package com.example.terminalm3.console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.GenericFontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.terminalm3.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import libs.modifier.scrollbar
import kotlin.math.max
import kotlin.random.Random

/**
 * Compose-блок, который можно вставить в список консоли как отдельный элемент.
 */
typealias ConsoleComposableContent = @Composable () -> Unit

/**
 * Базовый тип любого элемента консоли.
 *
 * [remoteLineId] связывает локальные элементы с конкретной входящей сетевой строкой,
 * а [channelId] определяет, в каком логическом канале элемент хранится и отображается.
 */
sealed interface ConsoleItem {
    val id: Long
    val remoteLineId: Long?
    val channelId: Int
    val slotIndex: Int?
}

/**
 * Текстовый элемент, который хранится внутри канала консоли.
 */
@Stable
class ConsoleTextItem(
    text: String,
    pairList: List<PairTextAndColor>,
    deleted: Boolean = false,
    override val id: Long = Random.nextLong(),
    override val remoteLineId: Long? = null,
    override val channelId: Int = 0,
    override val slotIndex: Int? = null,
    isPlaceholder: Boolean = false
) : ConsoleItem {
    var text by mutableStateOf(text)
    var pairList by mutableStateOf(pairList)
    var deleted by mutableStateOf(deleted)
    var isPlaceholder by mutableStateOf(isPlaceholder)
}

/**
 * Произвольный Compose-элемент, который хранится внутри канала консоли.
 */
@Stable
class ConsoleComposableItem(
    content: ConsoleComposableContent,
    override val id: Long = Random.nextLong(),
    override val remoteLineId: Long? = null,
    override val channelId: Int = 0,
    override val slotIndex: Int? = null
) : ConsoleItem {
    var content by mutableStateOf(content)
}

/**
 * Обратная совместимость со старым именем текстовой строки консоли.
 */
typealias LineTextAndColor = ConsoleTextItem

/**
 * Один стилизованный фрагмент внутри текстовой строки.
 */
data class PairTextAndColor(
    val text: String,
    val colorText: Color,
    val colorBg: Color,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val flash: Boolean = false
)

/**
 * Основное хранилище и renderer консоли.
 *
 * Внутри один экземпляр [Console] держит `4` независимых буфера каналов и одну
 * общую агрегированную ленту `ALL`, поэтому
 * внешнему коду не нужно управлять несколькими Console. UI просто переключает
 * [activeChannel], а методы `print(...)`, `updateRemoteLine(...)` и вставка виджетов
 * работают поверх нужного канала.
 */
class Console {

    /**
     * Отложенный локальный элемент, который должен появиться после завершения
     * конкретной входящей сетевой строки.
     */
    private data class PendingLocalItem(
        val remoteLineId: Long,
        val item: ConsoleItem
    )

    /**
     * Внутренний буфер одного логического канала консоли.
     *
     * Хранит историю элементов, временные placeholder-строки для потокового вывода
     * и локальные элементы, которые ждут завершения сетевой строки.
     */
    private class ChannelBuffer {
        val messages = mutableStateListOf<ConsoleItem>()
        val pendingLocalItems = mutableListOf<PendingLocalItem>()
        val remoteTextItems = mutableMapOf<Long, ConsoleTextItem>()
        val slottedItems = sortedMapOf<Int, ConsoleItem>()
        var lastCompletedRemoteLineId = 0L

        fun clear() {
            messages.clear()
            pendingLocalItems.clear()
            remoteTextItems.clear()
            slottedItems.clear()
            lastCompletedRemoteLineId = 0L
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(700L)
                update.value = !update.value
            }
        }
    }

    val update = MutableStateFlow(true)
    var lineVisible by mutableStateOf(false)
    var tracking by mutableStateOf(true)
    var lastCount by mutableIntStateOf(0)
    var fontSize by mutableStateOf(12.sp)
    var activeChannel by mutableIntStateOf(ALL_CHANNEL)
        private set
    var defaultOutputChannel by mutableIntStateOf(DEFAULT_CHANNEL)
        private set

    private val lineHeight get() = (fontSize.value * 1.25f).sp
    private val channelBuffers = List(CHANNEL_COUNT) { ChannelBuffer() }
    private val allMessages = mutableStateListOf<ConsoleItem>()
    private val recompose = MutableStateFlow(0)
    private val unreadCounts = List(CHANNEL_COUNT) { mutableIntStateOf(0) }
    private val unreadAllCount = mutableIntStateOf(0)
    private var fontFamily = FontFamily(Font(R.font.jetbrains, FontWeight.Normal))
    private val consoleBackground = Color(0xFF090909)
    private var scrollToEndRequest by mutableIntStateOf(0)

    /**
     * Обратная совместимость со старым прямым доступом к `messages`.
     *
     * Возвращает список элементов активного канала.
     *
     * Если пользователь находится на вкладке `ALL`, прямые операции через `messages`
     * направляются в [defaultOutputChannel], потому что `ALL` не является реальным
     * буфером для записи.
     */
    val messages
        get() = channelBuffer(
            if (activeChannel == ALL_CHANNEL) defaultOutputChannel else activeChannel
        ).messages

    /**
     * Принудительно дергает рекомпозицию списка, не меняя его размер.
     */
    fun recompose() {
        recompose.value++
    }

    /**
     * Переключает канал, который сейчас отображается на экране.
     *
     * История других каналов при этом не теряется, меняется только активное представление.
     * После старта консоль открывается на вкладке [ALL_CHANNEL], чтобы сразу видеть
     * весь входящий поток.
     */
    fun selectChannel(channelId: Int) {
        if (channelId == ALL_CHANNEL) {
            unreadAllCount.intValue = 0
            if (activeChannel == ALL_CHANNEL) return

            activeChannel = ALL_CHANNEL
            lastCount = allMessages.size
            tracking = true
            scrollToEndRequest++
            return
        }

        val normalized = normalizeChannel(channelId)
        unreadCounts[normalized].intValue = 0
        if (activeChannel == normalized) return

        activeChannel = normalized
        lastCount = channelBuffer(normalized).messages.size
        tracking = true
        scrollToEndRequest++
    }

    /**
     * Меняет канал по умолчанию для локальных вызовов `print(...)`,
     * `printComposable(...)` и других методов, где канал явно не указан.
     */
    fun selectDefaultOutputChannel(channelId: Int) {
        defaultOutputChannel = normalizeChannel(channelId)
    }

    /**
     * Снова включает автослежение и запрашивает прокрутку к концу активного канала.
     */
    fun requestScrollToEnd() {
        tracking = true
        scrollToEndRequest++
    }

    /**
     * Очищает только активный канал.
     */
    fun clear() {
        if (activeChannel == ALL_CHANNEL) {
            clearAll()
        } else {
            clearChannel(activeChannel)
        }
    }

    /**
     * Очищает конкретный канал по номеру.
     */
    fun clearChannel(channelId: Int) {
        val normalized = normalizeChannel(channelId)
        Snapshot.withMutableSnapshot {
            channelBuffer(normalized).clear()
            rebuildAllMessagesWithoutChannel(normalized)
            unreadCounts[normalized].intValue = 0
            if (activeChannel == normalized) {
                lastCount = 0
            } else if (activeChannel == ALL_CHANNEL) {
                lastCount = allMessages.size
            }
            unreadAllCount.intValue = unreadAllCount.intValue.coerceAtMost(allMessages.size)
        }
    }

    /**
     * Очищает все каналы консоли сразу.
     */
    fun clearAll() {
        Snapshot.withMutableSnapshot {
            channelBuffers.forEach(ChannelBuffer::clear)
            allMessages.clear()
            unreadCounts.forEach { it.intValue = 0 }
            unreadAllCount.intValue = 0
            lastCount = 0
        }
    }

    /**
     * Добавляет любой элемент в конец буфера того канала, которому он принадлежит.
     */
    fun addItem(item: ConsoleItem) {
        Snapshot.withMutableSnapshot {
            channelBuffer(item.channelId).messages.add(item)
            allMessages.add(item)
            recordNewItem(item.channelId)
        }
    }

    /**
     * Вставляет элемент после конкретной сетевой строки в рамках его канала.
     *
     * Если входящая строка еще не завершена, элемент не вставляется сразу, а кладется
     * в отложенную очередь этого же канала и появится после [completeRemoteLine].
     */
    fun addItemAfterRemoteLine(remoteLineId: Long, item: ConsoleItem) {
        val buffer = channelBuffer(item.channelId)
        if (remoteLineId <= buffer.lastCompletedRemoteLineId) {
            insertBeforeTrailingPlaceholder(buffer, item)
            if (item is ConsoleComposableItem) {
                Snapshot.withMutableSnapshot {
                    removeTrailingPlaceholder(buffer)
                }
            }
        } else {
            buffer.pendingLocalItems.add(PendingLocalItem(remoteLineId, item))
        }
    }

    /**
     * Добавляет новую текстовую строку в указанный канал.
     *
     * Если [channelId] не передан, используется [defaultOutputChannel].
     */
    fun print(
        text: String,
        color: Color = Color.Green,
        bgColor: Color = Color.Black,
        flash: Boolean = false,
        channelId: Int = defaultOutputChannel
    ) {
        addItem(buildTextItem(text, color, bgColor, flash, channelId = channelId))
    }

    /**
     * Создает или обновляет текстовую строку в стабильном slot/index канала.
     */
    fun printAt(
        slotIndex: Int,
        text: String,
        color: Color = Color.Green,
        bgColor: Color = Color.Black,
        flash: Boolean = false,
        channelId: Int = defaultOutputChannel
    ) {
        setItemAt(
            slotIndex = slotIndex,
            item = buildTextItem(
                text = text,
                color = color,
                bgColor = bgColor,
                flash = flash,
                channelId = channelId,
                slotIndex = slotIndex
            )
        )
    }

    /**
     * Дописывает текст в последнюю обычную текстовую строку указанного канала.
     *
     * Если подходящей строки нет, метод сам создаст новую через [print].
     */
    fun appendToLastTextLine(
        text: String,
        color: Color? = null,
        bgColor: Color? = null,
        flash: Boolean? = null,
        channelId: Int = defaultOutputChannel
    ) {
        val buffer = channelBuffer(channelId)
        val target = buffer.messages
            .asReversed()
            .firstOrNull { item ->
                item is ConsoleTextItem && !item.isPlaceholder
            } as? ConsoleTextItem

        if (target == null) {
            print(
                text = text,
                color = color ?: Color.Green,
                bgColor = bgColor ?: Color.Black,
                flash = flash ?: false,
                channelId = channelId
            )
            return
        }

        val lastPart = target.pairList.lastOrNull()
        val appendedPart = PairTextAndColor(
            text = text,
            colorText = color ?: lastPart?.colorText ?: Color.Green,
            colorBg = bgColor ?: lastPart?.colorBg ?: Color.Black,
            bold = lastPart?.bold ?: false,
            italic = lastPart?.italic ?: false,
            underline = lastPart?.underline ?: false,
            flash = flash ?: lastPart?.flash ?: false
        )

        target.text += text
        target.pairList = target.pairList + appendedPart
    }

    /**
     * Добавляет произвольный Compose-элемент в конец указанного канала.
     */
    fun printComposable(
        channelId: Int = defaultOutputChannel,
        content: ConsoleComposableContent
    ) {
        addItem(ConsoleComposableItem(content = content, channelId = normalizeChannel(channelId)))
    }

    /**
     * Создает или обновляет произвольный Compose-элемент в стабильном slot/index канала.
     */
    fun printComposableAt(
        slotIndex: Int,
        channelId: Int = defaultOutputChannel,
        content: ConsoleComposableContent
    ) {
        setItemAt(
            slotIndex = slotIndex,
            item = ConsoleComposableItem(
                content = content,
                channelId = normalizeChannel(channelId),
                slotIndex = normalizeSlot(slotIndex)
            )
        )
    }

    /**
     * Добавляет локальную текстовую строку после указанной сетевой строки
     * в заданном канале.
     */
    fun printLocalAfterRemoteLine(
        remoteLineId: Long,
        text: String,
        color: Color = Color.Green,
        bgColor: Color = Color.Black,
        flash: Boolean = false,
        channelId: Int = defaultOutputChannel
    ) {
        addItemAfterRemoteLine(
            remoteLineId = remoteLineId,
            item = buildTextItem(text, color, bgColor, flash, channelId = channelId)
        )
    }

    /**
     * Добавляет Compose-элемент после указанной сетевой строки в заданном канале.
     */
    fun printComposableAfterRemoteLine(
        remoteLineId: Long,
        channelId: Int = defaultOutputChannel,
        content: ConsoleComposableContent
    ) {
        addItemAfterRemoteLine(
            remoteLineId = remoteLineId,
            item = ConsoleComposableItem(content = content, channelId = normalizeChannel(channelId))
        )
    }

    /**
     * Обновляет потоковую входящую строку внутри ее канала.
     *
     * Если строка уже существует, меняется ее текст и стили. Если строки еще нет,
     * создается новая запись, которая будет дальше обновляться тем же `remoteLineId`.
     */
    fun updateRemoteLine(
        remoteLineId: Long,
        text: String,
        pairList: List<PairTextAndColor>,
        channelId: Int = DEFAULT_CHANNEL
    ) {
        val normalizedChannel = normalizeChannel(channelId)
        val buffer = channelBuffer(normalizedChannel)
        val item = buffer.remoteTextItems[remoteLineId]
        Snapshot.withMutableSnapshot {
            if (item != null) {
                item.text = text
                item.pairList = pairList
                item.isPlaceholder = false
                return@withMutableSnapshot
            }

            val newItem = ConsoleTextItem(
                text = text,
                pairList = pairList,
                remoteLineId = remoteLineId,
                channelId = normalizedChannel
            )
            buffer.remoteTextItems[remoteLineId] = newItem
            buffer.messages.add(newItem)
            allMessages.add(newItem)
            recordNewItem(normalizedChannel)
        }
    }

    /**
     * Завершает входящую сетевую строку в указанном канале.
     *
     * После этого:
     * - канал помнит, что [remoteLineId] уже завершен;
     * - создается placeholder для следующей потоковой строки [nextRemoteLineId];
     * - выгружаются локальные элементы, которые ждали завершения этой строки.
     */
    fun completeRemoteLine(
        remoteLineId: Long,
        nextRemoteLineId: Long,
        channelId: Int = DEFAULT_CHANNEL
    ) {
        val normalizedChannel = normalizeChannel(channelId)
        val buffer = channelBuffer(normalizedChannel)
        Snapshot.withMutableSnapshot {
            buffer.lastCompletedRemoteLineId = max(buffer.lastCompletedRemoteLineId, remoteLineId)
            ensureRemotePlaceholder(buffer, nextRemoteLineId, normalizedChannel)
            flushPendingLocalItems(buffer, remoteLineId)
        }
    }

    /**
     * Возвращает содержимое одного канала в порядке отрисовки.
     *
     * По умолчанию берется активный канал. Для [ALL_CHANNEL] возвращается
     * агрегированная лента всех сообщений из всех каналов.
     */
    fun getList(channelId: Int = activeChannel): List<ConsoleItem> =
        if (channelId == ALL_CHANNEL) allMessages else channelBuffer(channelId).messages

    /**
     * Возвращает количество элементов, которые сейчас хранятся в одном канале.
     * Для [ALL_CHANNEL] это размер общей агрегированной ленты.
     */
    fun getChannelItemCount(channelId: Int): Int =
        if (channelId == ALL_CHANNEL) allMessages.size else channelBuffer(channelId).messages.size

    /**
     * Возвращает количество новых элементов, пришедших в канал с момента,
     * когда пользователь последний раз его открывал.
     *
     * Для [ALL_CHANNEL] возвращается число новых элементов по общей ленте.
     */
    fun getUnreadCount(channelId: Int): Int =
        if (channelId == ALL_CHANNEL) unreadAllCount.intValue
        else unreadCounts[normalizeChannel(channelId)].intValue

    /**
     * Отрисовывает активный канал как `LazyColumn`.
     *
     * Внутри следит за автопрокруткой, пользовательским скроллом и переключением каналов.
     */
    @Composable
    fun lazy(modifier: Modifier = Modifier) {
        recompose.collectAsStateWithLifecycle().value
        val list = getList(activeChannel)
        val lazyListState = rememberLazyListState()
        val isAtEnd by remember(lazyListState, list, activeChannel) {
            derivedStateOf {
                val lastIndex = list.lastIndex
                if (lastIndex <= 0) {
                    true
                } else {
                    val layoutInfo = lazyListState.layoutInfo
                    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                    val viewportEnd = layoutInfo.viewportEndOffset - layoutInfo.afterContentPadding

                    lastVisibleItem?.index == lastIndex &&
                        (lastVisibleItem.offset + lastVisibleItem.size) <= viewportEnd
                }
            }
        }

        LaunchedEffect(list.size, activeChannel) {
            lastCount = list.size
        }

        LaunchedEffect(lazyListState.isScrollInProgress, isAtEnd) {
            if (lazyListState.isScrollInProgress && tracking && !isAtEnd) {
                tracking = false
            } else if (!lazyListState.isScrollInProgress && isAtEnd && !tracking) {
                tracking = true
            }
        }

        LaunchedEffect(list.size, tracking, scrollToEndRequest, activeChannel) {
            val lastIndex = list.lastIndex
            if (lastIndex >= 0 && tracking && !isAtEnd) {
                lazyListState.scrollToItem(index = lastIndex, scrollOffset = 0)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(consoleBackground)
                .then(modifier)
                .scrollbar(
                    count = list.size,
                    state = lazyListState,
                    horizontal = false,
                    hiddenAlpha = 0f
                ),
            state = lazyListState
        ) {
            itemsIndexed(list, key = { _, item -> item.id }) { index, item ->
                when (item) {
                    is ConsoleTextItem -> drawTextItem(item = item, index = index, select = false)
                    is ConsoleComposableItem -> drawComposableItem(
                        item = item,
                        index = index,
                        select = false
                    )
                }
            }
        }
    }

    /**
     * Меняет шрифт текстовых строк консоли.
     */
    fun setFontFamily(fontFamily: GenericFontFamily) {
        this.fontFamily = fontFamily
    }

    /**
     * Меняет размер шрифта текстовых строк консоли.
     */
    fun setFontSize(size: Int) {
        fontSize = size.sp
    }

    /**
     * Рисует одну текстовую строку.
     */
    @Composable
    private fun drawTextItem(
        item: ConsoleTextItem,
        index: Int,
        select: Boolean
    ) {
        val blinkVisible = if (item.pairList.any { it.flash }) {
            update.collectAsStateWithLifecycle().value
        } else {
            update.value
        }

        val text = remember(
            item.text,
            item.pairList,
            blinkVisible,
            index,
            lineVisible,
            fontFamily
        ) {
            buildAnnotatedText(item, index, blinkVisible)
        }

        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .background(if (select) Color.Cyan else Color.Transparent),
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontFamily = fontFamily
        )
    }

    /**
     * Рисует один произвольный Compose-элемент внутри списка консоли.
     */
    @Composable
    private fun drawComposableItem(
        item: ConsoleComposableItem,
        index: Int,
        select: Boolean
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (select) Color.Cyan else Color.Transparent)
        ) {
            if (lineVisible) {
                Text(
                    text = "${index}>",
                    color = Color.Gray,
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    fontFamily = fontFamily
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                item.content()
            }
        }
    }

    /**
     * Собирает `AnnotatedString` из сегментов с цветами и стилями.
     */
    private fun buildAnnotatedText(
        item: ConsoleTextItem,
        index: Int,
        blinkVisible: Boolean
    ): AnnotatedString {
        return buildAnnotatedString {
            if (lineVisible) {
                withStyle(style = SpanStyle(color = Color.Gray)) {
                    append("${index}>")
                }
            }

            item.pairList.forEach { part ->
                val textColor = if (!part.flash || blinkVisible) {
                    part.colorText
                } else {
                    consoleBackground
                }

                val backgroundColor = if (!part.flash || blinkVisible) {
                    part.colorBg
                } else {
                    consoleBackground
                }

                withStyle(
                    style = SpanStyle(
                        color = textColor,
                        background = backgroundColor,
                        fontFamily = fontFamily,
                        textDecoration = if (part.underline) TextDecoration.Underline else null,
                        fontWeight = if (part.bold) FontWeight.Bold else null,
                        fontStyle = if (part.italic) FontStyle.Italic else null
                    )
                ) {
                    append(part.text)
                }
            }
        }
    }

    /**
     * Создает текстовый элемент консоли из строки и набора базовых стилей.
     */
    private fun buildTextItem(
        text: String,
        color: Color,
        bgColor: Color,
        flash: Boolean,
        remoteLineId: Long? = null,
        channelId: Int = defaultOutputChannel,
        slotIndex: Int? = null
    ): ConsoleTextItem {
        return ConsoleTextItem(
            text = text,
            pairList = listOf(
                PairTextAndColor(
                    text = text,
                    colorText = color,
                    colorBg = bgColor,
                    flash = flash
                )
            ),
            remoteLineId = remoteLineId,
            channelId = normalizeChannel(channelId),
            slotIndex = slotIndex?.let(::normalizeSlot)
        )
    }

    /**
     * Гарантирует наличие placeholder-строки для следующей входящей сетевой строки
     * внутри одного канала.
     */
    private fun ensureRemotePlaceholder(
        buffer: ChannelBuffer,
        remoteLineId: Long,
        channelId: Int
    ) {
        if (buffer.remoteTextItems.containsKey(remoteLineId)) return

        val placeholder = ConsoleTextItem(
            text = " ",
            pairList = listOf(
                PairTextAndColor(
                    text = "\u2581",
                    colorText = Color.Green,
                    colorBg = Color.Black,
                    flash = true
                )
            ),
            remoteLineId = remoteLineId,
            channelId = channelId,
            isPlaceholder = true
        )

        buffer.remoteTextItems[remoteLineId] = placeholder
        buffer.messages.add(placeholder)
        allMessages.add(placeholder)
    }

    /**
     * Вставляет отложенные локальные элементы, которые ждали завершения
     * конкретной сетевой строки в этом канале.
     */
    private fun flushPendingLocalItems(buffer: ChannelBuffer, remoteLineId: Long) {
        val iterator = buffer.pendingLocalItems.iterator()
        while (iterator.hasNext()) {
            val pending = iterator.next()
            if (pending.remoteLineId == remoteLineId) {
                insertBeforeTrailingPlaceholder(buffer, pending.item)
                if (pending.item is ConsoleComposableItem) {
                    removeTrailingPlaceholder(buffer)
                }
                iterator.remove()
            }
        }
    }

    /**
     * Вставляет локальный элемент перед хвостовым placeholder того же канала,
     * если такой placeholder уже существует.
     */
    private fun insertBeforeTrailingPlaceholder(buffer: ChannelBuffer, item: ConsoleItem) {
        Snapshot.withMutableSnapshot {
            val lastItem = buffer.messages.lastOrNull()
            if (lastItem is ConsoleTextItem && lastItem.isPlaceholder) {
                buffer.messages.add(buffer.messages.lastIndex, item)
            } else {
                buffer.messages.add(item)
            }
            allMessages.add(item)
            recordNewItem(item.channelId)
        }
    }

    private fun removeTrailingPlaceholder(buffer: ChannelBuffer) {
        val lastItem = buffer.messages.lastOrNull() as? ConsoleTextItem ?: return
        if (!lastItem.isPlaceholder) return

        buffer.messages.removeAt(buffer.messages.lastIndex)
        val allIndex = allMessages.indexOfLast { it.id == lastItem.id }
        if (allIndex >= 0) {
            allMessages.removeAt(allIndex)
        }
        lastItem.remoteLineId?.let { remoteLineId ->
            val mapped = buffer.remoteTextItems[remoteLineId]
            if (mapped?.id == lastItem.id) {
                buffer.remoteTextItems.remove(remoteLineId)
            }
        }
    }

    private fun setItemAt(slotIndex: Int, item: ConsoleItem) {
        val normalizedSlot = normalizeSlot(slotIndex)
        val normalizedChannel = normalizeChannel(item.channelId)
        val buffer = channelBuffer(normalizedChannel)
        val existing = buffer.slottedItems[normalizedSlot]

        Snapshot.withMutableSnapshot {
            if (existing == null) {
                val insertIndex = buffer.slottedItems.keys.count { it < normalizedSlot }
                    .coerceIn(0, buffer.messages.size)

                buffer.slottedItems[normalizedSlot] = item
                buffer.messages.add(insertIndex, item)
                allMessages.add(item)
                recordNewItem(normalizedChannel)
                return@withMutableSnapshot
            }

            buffer.slottedItems[normalizedSlot] = when {
                existing is ConsoleTextItem && item is ConsoleTextItem -> {
                    existing.text = item.text
                    existing.pairList = item.pairList
                    existing.deleted = item.deleted
                    existing.isPlaceholder = item.isPlaceholder
                    existing
                }

                existing is ConsoleComposableItem && item is ConsoleComposableItem -> {
                    existing.content = item.content
                    existing
                }

                else -> {
                    replaceItemReferences(buffer, existing, item)
                    item
                }
            }
        }
    }

    private fun replaceItemReferences(
        buffer: ChannelBuffer,
        oldItem: ConsoleItem,
        newItem: ConsoleItem
    ) {
        val channelIndex = buffer.messages.indexOfFirst { it.id == oldItem.id }
        if (channelIndex >= 0) {
            buffer.messages[channelIndex] = newItem
        }

        val allIndex = allMessages.indexOfFirst { it.id == oldItem.id }
        if (allIndex >= 0) {
            allMessages[allIndex] = newItem
        }
    }

    private fun recordNewItem(channelId: Int) {
        val normalized = normalizeChannel(channelId)
        if (normalized != activeChannel) {
            unreadCounts[normalized].intValue++
        }
        if (activeChannel != ALL_CHANNEL) {
            unreadAllCount.intValue++
        }
    }

    private fun rebuildAllMessagesWithoutChannel(channelId: Int) {
        val filtered = allMessages.filterNot { it.channelId == channelId }
        if (filtered.size == allMessages.size) return

        Snapshot.withMutableSnapshot {
            allMessages.clear()
            allMessages.addAll(filtered)
        }
    }

    private fun channelBuffer(channelId: Int): ChannelBuffer = channelBuffers[normalizeChannel(channelId)]

    private fun normalizeSlot(slotIndex: Int): Int = slotIndex.coerceAtLeast(0)

    private fun normalizeChannel(channelId: Int): Int = channelId.coerceIn(0, CHANNEL_COUNT - 1)

    companion object {
        const val ALL_CHANNEL = -1
        const val CHANNEL_COUNT = 4
        const val DEFAULT_CHANNEL = 0
    }
}
