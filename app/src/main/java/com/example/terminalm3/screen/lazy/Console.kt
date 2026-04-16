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
import kotlin.random.Random

data class LineTextAndColor(
    var text: String, //Строка вообще
    var pairList: SnapshotStateList<PairTextAndColor>, //То что будет определено в этой строке
    var deleted: Boolean = false,
    val id: Long = Random.nextLong(),
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

//var manual_recomposeLazy = mutableStateOf(0)

//println("Индекс первого видимого элемента = " + lazyListState.firstVisibleItemIndex.toString())
//println("Смещение прокрутки первого видимого элемента = " + lazyListState.firstVisibleItemScrollOffset.toString())
//println("Количество строк выведенных на экран lastIndex = " + lazyListState.layoutInfo.visibleItemsInfo.lastIndex.toString())

//➕️ ✅️✏️⛏️ $${\color{red}Red}$$ 📥 📤  📃  📑 📁 📘 🇷🇺 🆗 ✳️

class ConsoleMessage {

    val messages = mutableStateListOf<LineTextAndColor>()

    fun add(item: LineTextAndColor) { messages.add(item) }

    fun clear() { messages.clear() }
}

class Console {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(700L)
                update.value = !update.value
            }
        }
    }


    //PUBLIC
    val update = MutableStateFlow(true)   //для мигания
    var lineVisible by mutableStateOf(false) //Отображение номер строки
    var tracking by mutableStateOf(true) //Слежение за последним полем
    var lastCount by mutableIntStateOf(0) //Количество записей
    var fontSize by mutableStateOf(12.sp) //Размер шрифта


    private val lineHeight get() = (fontSize.value * 1.25f).sp

    val messages = ConsoleMessage()//mutableStateListOf<LineTextAndColor>()


    //PRIVATE
    private val recompose = MutableStateFlow(0)
    private var fontFamily = FontFamily(
        Font(
            R.font.jetbrains, FontWeight.Normal
        )
    ) //FontFamily.Monospace //Используемый шрифт
    private val consoleBackground = Color(0xFF090909)
    private var scrollToEndRequest by mutableIntStateOf(0)


    //PUBLIC METHOD
    /**
     * ⛏️ Рекомпозиция списка
     */
    fun recompose() { recompose.value++ }

    fun requestScrollToEnd() {
        tracking = true
        scrollToEndRequest++
    }

    /**
     * Очистка списка
     */
    fun clear() {
        messages.clear()
        messages.add( LineTextAndColor( " ",  mutableStateListOf(PairTextAndColor("▁", Color.Green, Color.Black, flash = true)) ) )
        //recompose()
    }

    /**
     * Добавить запись
     */
    fun print(
        text: String,
        color: Color = Color.Green,
        bgColor: Color = Color.Black,
        flash: Boolean = false
    ) {
        if ((messages.messages.isNotEmpty()) && (messages.messages.last().text == " ")) {

            messages.messages.removeAt(messages.messages.lastIndex)
            messages.add(
                LineTextAndColor(
                    text, mutableStateListOf(PairTextAndColor(text = text, color, bgColor, flash = flash))
                )
            )
        } else {
            messages.add(
                LineTextAndColor(
                    text, mutableStateListOf(PairTextAndColor(text = text, color, bgColor, flash = flash))
                )
            )
        }
    }

    /**
     * Получить экземпляр списка
     */
    fun getList(): List<LineTextAndColor> = messages.messages


    @Composable
    fun lazy(modifier: Modifier = Modifier) {

        //val _update = update.collectAsStateWithLifecycle().value
        val _recompose = recompose.collectAsStateWithLifecycle().value

        val list = getList() //: List<LineTextAndColor> = messages.toList().map { it }
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

        //var update by remember { mutableStateOf(true) }  //для мигания

        //val lazyListState: LazyListState = rememberLazyListState()

        //println("Последний видимый индекс = $lastVisibleItemIndex")

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

            //Анимация (плавная прокрутка) к последнему элементу.
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
                    lazyListState,
                    horizontal = false, //countCorrection = 0,
                    hiddenAlpha = 0f
                ), state = lazyListState
        ) {

            itemsIndexed(list, key = { index, item -> item.id }) { index, item ->
                ScriptItemDraw(item = item, index = index, select = false)
            }

        }
    }

    /**
     * 🔧 Установка типа шрифта
     * 📥 **FontFamily(Font(R.font.jetbrains, FontWeight.Normal))**
     */
    fun setFontFamily(fontFamily: GenericFontFamily) {
        this.fontFamily = fontFamily
    }

    /**
     *  🔧 Установка размера шрифта
     */
    fun setFontSize(size: Int) {
        fontSize = size.sp
    }

    //==================================================
    //PRIVATE METHOD
    //==================================================

    @Composable
    private fun ScriptItemDraw(
        item: LineTextAndColor, index: Int, select: Boolean
    ) { //println("Draw  $index")

        val blinkVisible = if (item.pairList.any { it.flash }) {
            update.collectAsStateWithLifecycle().value
        } else {
            update.value
        }

        val x = convertStringToAnnotatedString(item = item, index = index, blinkVisible = blinkVisible)

        Text(
            x,
            modifier = Modifier
                .fillMaxWidth() //.padding(top = 0.dp)
                .background(if (select) Color.Cyan else Color.Transparent),

            fontSize = fontSize,
            lineHeight = lineHeight,
            fontFamily = fontFamily,
        )

    }

    private fun convertStringToAnnotatedString(
        item: LineTextAndColor,
        index: Int,
        blinkVisible: Boolean
    ): AnnotatedString {
        return buildAnnotatedString {
            if (lineVisible) withStyle(style = SpanStyle(color = Color.Gray)) {
                append("${index}>")
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
                        fontStyle = if (part.italic) FontStyle.Italic else null,
                    )
                ) { append(part.text) }
            }
        }
    }

}
