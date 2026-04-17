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

/**
 * Compose content block that can be inserted into the console list.
 */
typealias ConsoleComposableContent = @Composable () -> Unit

/**
 * Base type for any console item.
 */
sealed interface ConsoleItem {
    val id: Long
    val remoteLineId: Long?
}

/**
 * Text item stored in the console list.
 */
@Stable
class ConsoleTextItem(
    text: String,
    pairList: List<PairTextAndColor>,
    deleted: Boolean = false,
    override val id: Long = Random.nextLong(),
    override val remoteLineId: Long? = null,
    isPlaceholder: Boolean = false
) : ConsoleItem {
    var text by mutableStateOf(text)
    var pairList by mutableStateOf(pairList)
    var deleted by mutableStateOf(deleted)
    var isPlaceholder by mutableStateOf(isPlaceholder)
}

/**
 * Arbitrary Compose item stored in the console list.
 */
data class ConsoleComposableItem(
    val content: ConsoleComposableContent,
    override val id: Long = Random.nextLong(),
    override val remoteLineId: Long? = null
) : ConsoleItem

/**
 * Backward-compatible alias for text items.
 */
typealias LineTextAndColor = ConsoleTextItem

/**
 * One styled text fragment inside a text line.
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
 * Main console storage and renderer.
 *
 * It stores text items, arbitrary Compose items, streaming remote lines,
 * and the UI state needed for auto-follow and blinking text.
 */
class Console {

    /**
     * Delayed local item that should appear after a specific remote line finishes.
     */
    private data class PendingLocalItem(
        val remoteLineId: Long,
        val item: ConsoleItem
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

    /**
     * Observable list of all console items.
     *
     * This is intentionally exposed directly on `Console` so callers can work with
     * the list without going through an extra wrapper class.
     */
    val messages = mutableStateListOf<ConsoleItem>()

    private val recompose = MutableStateFlow(0)
    private var fontFamily = FontFamily(Font(R.font.jetbrains, FontWeight.Normal))
    private val consoleBackground = Color(0xFF090909)
    private var scrollToEndRequest by mutableIntStateOf(0)
    private val pendingLocalItems = mutableListOf<PendingLocalItem>()
    private val remoteTextItems = mutableMapOf<Long, ConsoleTextItem>()
    private var lastCompletedRemoteLineId = 0L

    /**
     * Forces list recomposition without changing list size.
     */
    fun recompose() {
        recompose.value++
    }

    /**
     * Re-enables auto-follow and requests scroll to the last item.
     */
    fun requestScrollToEnd() {
        tracking = true
        scrollToEndRequest++
    }

    /**
     * Clears the console and resets temporary state.
     */
    fun clear() {
        messages.clear()
        pendingLocalItems.clear()
        remoteTextItems.clear()
        lastCompletedRemoteLineId = 0L
    }

    /**
     * Adds any item to the end of the console.
     */
    fun addItem(item: ConsoleItem) {
        messages.add(item)
    }

    /**
     * Inserts an item after a specific remote line.
     *
     * If that line has not finished yet, the item is queued.
     */
    fun addItemAfterRemoteLine(remoteLineId: Long, item: ConsoleItem) {
        if (remoteLineId <= lastCompletedRemoteLineId) {
            insertBeforeTrailingPlaceholder(item)
        } else {
            pendingLocalItems.add(PendingLocalItem(remoteLineId, item))
        }
    }

    /**
     * Adds a plain text line to the end of the console.
     */
    fun print(
        text: String,
        color: Color = Color.Green,
        bgColor: Color = Color.Black,
        flash: Boolean = false
    ) {
        addItem(buildTextItem(text, color, bgColor, flash))
    }

    /**
     * Appends text to the last non-placeholder text line.
     *
     * If there is no suitable text line yet, it falls back to `print(...)`.
     * By default the appended fragment inherits styling from the last fragment
     * of the target line.
     */
    fun appendToLastTextLine(
        text: String,
        color: Color? = null,
        bgColor: Color? = null,
        flash: Boolean? = null
    ) {
        val target = messages
            .asReversed()
            .firstOrNull { item ->
                item is ConsoleTextItem && !item.isPlaceholder
            } as? ConsoleTextItem

        if (target == null) {
            print(
                text = text,
                color = color ?: Color.Green,
                bgColor = bgColor ?: Color.Black,
                flash = flash ?: false
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
     * Adds arbitrary Compose content to the end of the console.
     */
    fun printComposable(content: ConsoleComposableContent) {
        addItem(ConsoleComposableItem(content = content))
    }

    /**
     * Adds a local text line after the specified remote line.
     */
    fun printLocalAfterRemoteLine(
        remoteLineId: Long,
        text: String,
        color: Color = Color.Green,
        bgColor: Color = Color.Black,
        flash: Boolean = false
    ) {
        addItemAfterRemoteLine(
            remoteLineId = remoteLineId,
            item = buildTextItem(text, color, bgColor, flash)
        )
    }

    /**
     * Adds arbitrary Compose content after the specified remote line.
     */
    fun printComposableAfterRemoteLine(
        remoteLineId: Long,
        content: ConsoleComposableContent
    ) {
        addItemAfterRemoteLine(
            remoteLineId = remoteLineId,
            item = ConsoleComposableItem(content = content)
        )
    }

    /**
     * Updates text of a streamed remote line by `remoteLineId`.
     *
     * If the line does not exist yet, it is created.
     */
    fun updateRemoteLine(
        remoteLineId: Long,
        text: String,
        pairList: List<PairTextAndColor>
    ) {
        val item = remoteTextItems[remoteLineId]
        if (item != null) {
            item.text = text
            item.pairList = pairList
            item.isPlaceholder = false
            return
        }

        val newItem = ConsoleTextItem(
            text = text,
            pairList = pairList,
            remoteLineId = remoteLineId
        )
        remoteTextItems[remoteLineId] = newItem
        messages.add(newItem)
    }

    /**
     * Marks a remote line as complete, creates a placeholder for the next one,
     * and flushes delayed local items.
     */
    fun completeRemoteLine(remoteLineId: Long, nextRemoteLineId: Long) {
        lastCompletedRemoteLineId = max(lastCompletedRemoteLineId, remoteLineId)
        ensureRemotePlaceholder(nextRemoteLineId)
        flushPendingLocalItems(remoteLineId)
    }

    /**
     * Returns the current console content in render order.
     */
    fun getList(): List<ConsoleItem> = messages

    /**
     * Renders the console as a LazyColumn.
     */
    @Composable
    fun lazy(modifier: Modifier = Modifier) {
        recompose.collectAsStateWithLifecycle().value
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

        LaunchedEffect(list.size) {
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
     * Sets font family for text lines.
     */
    fun setFontFamily(fontFamily: GenericFontFamily) {
        this.fontFamily = fontFamily
    }

    /**
     * Sets font size for text lines.
     */
    fun setFontSize(size: Int) {
        fontSize = size.sp
    }

    /**
     * Draws one text item.
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
     * Draws one arbitrary Compose item.
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
     * Builds AnnotatedString from styled text fragments.
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
     * Creates a text item from plain text and style.
     */
    private fun buildTextItem(
        text: String,
        color: Color,
        bgColor: Color,
        flash: Boolean,
        remoteLineId: Long? = null
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
            remoteLineId = remoteLineId
        )
    }

    /**
     * Ensures there is a placeholder for the next remote text line.
     */
    private fun ensureRemotePlaceholder(remoteLineId: Long) {
        if (remoteTextItems.containsKey(remoteLineId)) return

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
            isPlaceholder = true
        )

        remoteTextItems[remoteLineId] = placeholder
        messages.add(placeholder)
    }

    /**
     * Flushes delayed local items for a completed remote line.
     */
    private fun flushPendingLocalItems(remoteLineId: Long) {
        val iterator = pendingLocalItems.iterator()
        while (iterator.hasNext()) {
            val pending = iterator.next()
            if (pending.remoteLineId == remoteLineId) {
                insertBeforeTrailingPlaceholder(pending.item)
                iterator.remove()
            }
        }
    }

    /**
     * Inserts local item before the trailing placeholder if it exists.
     */
    private fun insertBeforeTrailingPlaceholder(item: ConsoleItem) {
        val lastItem = messages.lastOrNull()
        if (lastItem is ConsoleTextItem && lastItem.isPlaceholder) {
            messages.add(messages.lastIndex, item)
        } else {
            messages.add(item)
        }
    }
}
