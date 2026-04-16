package com.example.terminalm3.screen.lazy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
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

data class LineTextAndColor(
    var text: String,
    var pairList: SnapshotStateList<PairTextAndColor>,
    var deleted: Boolean = false,
    val id: Long = Random.nextLong(),
    val remoteLineId: Long? = null
)

data class PairTextAndColor(
    val text: String,
    val colorText: Color,
    val colorBg: Color,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val flash: Boolean = false
)

class ConsoleMessage {
    val messages = mutableStateListOf<LineTextAndColor>()

    fun add(item: LineTextAndColor) {
        messages.add(item)
    }

    fun clear() {
        messages.clear()
    }
}

class Console {

    private data class PendingLocalMessage(
        val remoteLineId: Long,
        val line: LineTextAndColor
    )

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

    private val lineHeight get() = (fontSize.value * 1.25f).sp

    val messages = ConsoleMessage()

    private val recompose = MutableStateFlow(0)
    private var fontFamily = FontFamily(Font(R.font.jetbrains, FontWeight.Normal))
    private val consoleBackground = Color(0xFF090909)
    private var scrollToEndRequest by mutableIntStateOf(0)
    private val pendingLocalMessages = mutableListOf<PendingLocalMessage>()
    private var lastCompletedRemoteLineId = 0L

    /**
     * Принудительно дергает счетчик рекомпозиции, если нужно обновить список
     * без изменения его размера.
     */
    fun recompose() {
        recompose.value++
    }

    /**
     * Включает режим слежения за концом списка и инициирует прокрутку вниз.
     */
    fun requestScrollToEnd() {
        tracking = true
        scrollToEndRequest++
    }

    /**
     * Полностью очищает консоль и сбрасывает временное состояние,
     * связанное с незавершенными сетевыми строками.
     */
    fun clear() {
        messages.clear()
        pendingLocalMessages.clear()
        lastCompletedRemoteLineId = 0L
    }

    /**
     * Добавляет обычную локальную строку в конец консоли.
     */
    fun print(
        text: String,
        color: Color = Color.Green,
        bgColor: Color = Color.Black,
        flash: Boolean = false
    ) {
        messages.add(buildLine(text, color, bgColor, flash))
    }

    /**
     * Добавляет локальное сообщение после конкретной сетевой строки.
     * Если сетевой хвост еще не завершен, сообщение временно откладывается.
     */
    fun printLocalAfterRemoteLine(
        remoteLineId: Long,
        text: String,
        color: Color = Color.Green,
        bgColor: Color = Color.Black,
        flash: Boolean = false
    ) {
        val line = buildLine(text, color, bgColor, flash)
        if (remoteLineId <= lastCompletedRemoteLineId) {
            insertBeforeTrailingPlaceholder(line)
        } else {
            pendingLocalMessages.add(PendingLocalMessage(remoteLineId, line))
        }
    }

    /**
     * Обновляет содержимое уже известной сетевой строки или создает ее,
     * если эта строка еще не была добавлена в список.
     */
    fun updateRemoteLine(
        remoteLineId: Long,
        text: String,
        pairList: SnapshotStateList<PairTextAndColor>
    ) {
        val index = messages.messages.indexOfLast { it.remoteLineId == remoteLineId }
        val line = LineTextAndColor(
            text = text,
            pairList = pairList,
            remoteLineId = remoteLineId
        )

        if (index >= 0) {
            val current = messages.messages[index]
            messages.messages[index] = current.copy(
                text = text,
                pairList = pairList,
                remoteLineId = remoteLineId
            )
        } else {
            messages.add(line)
        }
    }

    /**
     * Помечает сетевую строку завершенной, создает placeholder для следующей
     * строки и вставляет все отложенные локальные сообщения, привязанные к ней.
     */
    fun completeRemoteLine(remoteLineId: Long, nextRemoteLineId: Long) {
        lastCompletedRemoteLineId = max(lastCompletedRemoteLineId, remoteLineId)
        ensureRemotePlaceholder(nextRemoteLineId)
        flushPendingLocalMessages(remoteLineId)
    }

    /**
     * Возвращает текущее содержимое консоли в порядке отображения.
     */
    fun getList(): List<LineTextAndColor> = messages.messages

    /**
     * Рисует консоль как `LazyColumn`, управляет автопрокруткой
     * и обновляет служебное состояние списка.
     */
    @Composable
    fun lazy(modifier: Modifier = Modifier) {
        val _recompose = recompose.collectAsStateWithLifecycle().value
        val list = getList()
        val lazyListState = rememberLazyListState()
        val isAtEnd by remember(lazyListState, list) {
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

        LaunchedEffect(_recompose, list.size) {
            lastCount = list.size
        }

        LaunchedEffect(lazyListState.isScrollInProgress, isAtEnd) {
            if (lazyListState.isScrollInProgress && tracking && !isAtEnd) {
                tracking = false
            } else if (!lazyListState.isScrollInProgress && isAtEnd && !tracking) {
                tracking = true
            }
        }

        LaunchedEffect(list.size, tracking, scrollToEndRequest) {
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
                ScriptItemDraw(item = item, index = index, select = false)
            }
        }
    }

    /**
     * Меняет семейство шрифта, используемое при отрисовке консоли.
     */
    fun setFontFamily(fontFamily: GenericFontFamily) {
        this.fontFamily = fontFamily
    }

    /**
     * Меняет размер шрифта консоли в `sp`.
     */
    fun setFontSize(size: Int) {
        fontSize = size.sp
    }

    /**
     * Отрисовывает одну строку консоли с учетом мигающих сегментов.
     */
    @Composable
    private fun ScriptItemDraw(
        item: LineTextAndColor,
        index: Int,
        select: Boolean
    ) {
        val blinkVisible = if (item.pairList.any { it.flash }) {
            update.collectAsStateWithLifecycle().value
        } else {
            update.value
        }

        val text = convertStringToAnnotatedString(item, index, blinkVisible)

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
     * Собирает `AnnotatedString` из цветных сегментов строки и,
     * при необходимости, добавляет номер строки слева.
     */
    private fun convertStringToAnnotatedString(
        item: LineTextAndColor,
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
     * Создает одну строку консоли из простого текста и базовых параметров оформления.
     */
    private fun buildLine(
        text: String,
        color: Color,
        bgColor: Color,
        flash: Boolean,
        remoteLineId: Long? = null
    ): LineTextAndColor {
        return LineTextAndColor(
            text = text,
            pairList = mutableStateListOf(
                PairTextAndColor(
                    text = text,
                    colorText = color,
                    colorBg = bgColor,
                    flash = flash
                )
            ),
            remoteLineId = remoteLineId
        )
    }

    /**
     * Гарантирует, что для следующей сетевой строки существует placeholder,
     * в который можно безопасно дописывать приходящие данные.
     */
    private fun ensureRemotePlaceholder(remoteLineId: Long) {
        val exists = messages.messages.any { it.remoteLineId == remoteLineId }
        if (exists) return

        messages.add(
            buildLine(
                text = " ",
                color = Color.Green,
                bgColor = Color.Black,
                flash = true,
                remoteLineId = remoteLineId
            ).copy(
                pairList = mutableStateListOf(
                    PairTextAndColor(
                        text = "▁",
                        colorText = Color.Green,
                        colorBg = Color.Black,
                        flash = true
                    )
                )
            )
        )
    }

    /**
     * Вставляет все локальные сообщения, которые ждали завершения
     * указанной сетевой строки.
     */
    private fun flushPendingLocalMessages(remoteLineId: Long) {
        val iterator = pendingLocalMessages.iterator()
        while (iterator.hasNext()) {
            val pending = iterator.next()
            if (pending.remoteLineId == remoteLineId) {
                insertBeforeTrailingPlaceholder(pending.line)
                iterator.remove()
            }
        }
    }

    /**
     * Вставляет локальную строку перед завершающим placeholder сетевого хвоста,
     * чтобы она не затиралась последующими обновлениями входящих данных.
     */
    private fun insertBeforeTrailingPlaceholder(line: LineTextAndColor) {
        val placeholderIndex = messages.messages.indexOfLast {
            it.remoteLineId != null && it.text == " "
        }

        if (placeholderIndex >= 0) {
            messages.messages.add(placeholderIndex, line)
        } else {
            messages.add(line)
        }
    }
}
