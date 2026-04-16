package com.example.terminalm3.screen.lazy

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

typealias ConsoleComposableContent = @Composable () -> Unit

sealed interface ConsoleItem {
    val id: Long
    val remoteLineId: Long?
}

data class ConsoleTextItem(
    val text: String,
    val pairList: SnapshotStateList<PairTextAndColor>,
    val deleted: Boolean = false,
    override val id: Long = Random.nextLong(),
    override val remoteLineId: Long? = null
) : ConsoleItem

data class ConsoleComposableItem(
    val content: ConsoleComposableContent,
    override val id: Long = Random.nextLong(),
    override val remoteLineId: Long? = null
) : ConsoleItem

typealias LineTextAndColor = ConsoleTextItem

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
    val messages = mutableStateListOf<ConsoleItem>()

    fun add(item: ConsoleItem) {
        messages.add(item)
    }

    fun clear() {
        messages.clear()
    }
}

class Console {

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

    val messages = ConsoleMessage()

    private val recompose = MutableStateFlow(0)
    private var fontFamily = FontFamily(Font(R.font.jetbrains, FontWeight.Normal))
    private val consoleBackground = Color(0xFF090909)
    private var scrollToEndRequest by mutableIntStateOf(0)
    private val pendingLocalItems = mutableListOf<PendingLocalItem>()
    private var lastCompletedRemoteLineId = 0L

    /**
     * Forces list recomposition when the caller updated item content
     * without changing list size.
     */
    fun recompose() {
        recompose.value++
    }

    /**
     * Re-enables auto-follow mode and requests scroll to the last item.
     */
    fun requestScrollToEnd() {
        tracking = true
        scrollToEndRequest++
    }

    /**
     * Clears the console and resets all pending remote/local item state.
     */
    fun clear() {
        messages.clear()
        pendingLocalItems.clear()
        lastCompletedRemoteLineId = 0L
    }

    /**
     * Adds any console item to the end of the list.
     */
    fun addItem(item: ConsoleItem) {
        messages.add(item)
    }

    /**
     * Inserts any console item after the specified remote line.
     * If that remote line is still being streamed, the item is queued.
     */
    fun addItemAfterRemoteLine(remoteLineId: Long, item: ConsoleItem) {
        if (remoteLineId <= lastCompletedRemoteLineId) {
            insertBeforeTrailingPlaceholder(item)
        } else {
            pendingLocalItems.add(PendingLocalItem(remoteLineId, item))
        }
    }

    /**
     * Adds a plain text line to the end of the list.
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
     * Adds a custom Compose item to the end of the list.
     */
    fun printComposable(content: ConsoleComposableContent) {
        addItem(ConsoleComposableItem(content = content))
    }

    /**
     * Inserts a plain text line after the specified remote line.
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
     * Inserts a custom Compose item after the specified remote line.
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
     * Updates a streamed remote text line or creates it if it does not exist yet.
     */
    fun updateRemoteLine(
        remoteLineId: Long,
        text: String,
        pairList: SnapshotStateList<PairTextAndColor>
    ) {
        val index = messages.messages.indexOfLast {
            it is ConsoleTextItem && it.remoteLineId == remoteLineId
        }
        val item = ConsoleTextItem(
            text = text,
            pairList = pairList,
            remoteLineId = remoteLineId
        )

        if (index >= 0) {
            messages.messages[index] = item
        } else {
            messages.add(item)
        }
    }

    /**
     * Marks the current remote line as complete, creates placeholder for the next
     * remote line, and flushes queued local items bound to the completed line.
     */
    fun completeRemoteLine(remoteLineId: Long, nextRemoteLineId: Long) {
        lastCompletedRemoteLineId = max(lastCompletedRemoteLineId, remoteLineId)
        ensureRemotePlaceholder(nextRemoteLineId)
        flushPendingLocalItems(remoteLineId)
    }

    /**
     * Returns the current list of console items.
     */
    fun getList(): List<ConsoleItem> = messages.messages

    /**
     * Renders the console as a LazyColumn and keeps autoscroll in sync.
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
     * Updates the font family used by text console items.
     */
    fun setFontFamily(fontFamily: GenericFontFamily) {
        this.fontFamily = fontFamily
    }

    /**
     * Updates the text size used by text console items.
     */
    fun setFontSize(size: Int) {
        fontSize = size.sp
    }

    /**
     * Draws one text item with blinking support.
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

        Text(
            text = buildAnnotatedText(item, index, blinkVisible),
            modifier = Modifier
                .fillMaxWidth()
                .background(if (select) Color.Cyan else Color.Transparent),
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontFamily = fontFamily
        )
    }

    /**
     * Draws one arbitrary Compose item inside the console list.
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
     * Builds AnnotatedString for one text item.
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
     * Creates one text item from plain string and style.
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
     * Ensures there is a placeholder for the next remote streamed line.
     */
    private fun ensureRemotePlaceholder(remoteLineId: Long) {
        val exists = messages.messages.any {
            it is ConsoleTextItem && it.remoteLineId == remoteLineId
        }
        if (exists) return

        messages.add(
            buildTextItem(
                text = " ",
                color = Color.Green,
                bgColor = Color.Black,
                flash = true,
                remoteLineId = remoteLineId
            ).copy(
                pairList = mutableStateListOf(
                    PairTextAndColor(
                        text = "\u2581",
                        colorText = Color.Green,
                        colorBg = Color.Black,
                        flash = true
                    )
                )
            )
        )
    }

    /**
     * Flushes queued local items after the given remote line is complete.
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
     * Inserts local item before the trailing remote placeholder so it is not
     * overwritten by future streamed text updates.
     */
    private fun insertBeforeTrailingPlaceholder(item: ConsoleItem) {
        val placeholderIndex = messages.messages.indexOfLast {
            it is ConsoleTextItem && it.remoteLineId != null && it.text == " "
        }

        if (placeholderIndex >= 0) {
            messages.messages.add(placeholderIndex, item)
        } else {
            messages.add(item)
        }
    }
}
