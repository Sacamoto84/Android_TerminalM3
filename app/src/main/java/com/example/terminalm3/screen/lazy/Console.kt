package com.example.terminalm3.screen.lazy

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.terminalm3.R
import com.example.terminalm3.console
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


data class PairTextAndColor(
    var text: String,
    var colorText: Color,
    var colorBg: Color,
    var bold: Boolean = false,
    var italic: Boolean = false,
    var underline: Boolean = false,
    var flash: Boolean = false
)

data class LineTextAndColor(
    var text: String, //Строка вообще
    var pairList: List<PairTextAndColor>, //То что будет определено в этой строке
    var deleted: Boolean = false
)

//var manual_recomposeLazy = mutableStateOf(0)

//println("Индекс первого видимого элемента = " + lazyListState.firstVisibleItemIndex.toString())
//println("Смещение прокрутки первого видимого элемента = " + lazyListState.firstVisibleItemScrollOffset.toString())
//println("Количество строк выведенных на экран lastIndex = " + lazyListState.layoutInfo.visibleItemsInfo.lastIndex.toString())

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("MutableCollectionMutableState")
class Console {

    init {
        GlobalScope.launch {
            while (true) {
                delay(700L)
                update.value = !update.value
            }
        }
    }

    val messages = mutableListOf<LineTextAndColor>()

    val update = MutableStateFlow(true)   //для мигания

    private val recompose = MutableStateFlow(0)



    var lineVisible by mutableStateOf(false)

    var tracking by mutableStateOf(true) //Слежение за последним полем
    var lastCount by mutableIntStateOf(0)




    /**
     *  # Настройка шрифтов
     *  ### Размер шрифта
     */
    var fontSize by mutableStateOf(12.sp)

    /**
     * ### Используемый шрифт
     */
    private var fontFamily = FontFamily(Font(R.font.jetbrains, FontWeight.Normal))
    //FontFamily.Monospace


    //val messages = mutableStateListOf<LineTextAndColor>()


    //val messages = MutableStateFlow(emptyList<LineTextAndColor>().toMutableList())


    /**
     * # ⛏️ Рекомпозиция списка
     */
    fun recompose() {
        recompose.value++
    }

    var lastVisibleItemIndex = 0

    private val lazyListState: LazyListState = LazyListState()

    fun clear() {

        messages.clear()

        messages.forEach {
            it.deleted = true
        }

        messages.add(
            LineTextAndColor(
                "...",
                listOf(PairTextAndColor("///", Color.Red, Color.Green))
            )
        )
        recompose()
    }


    @Composable
    fun lazy(modifier: Modifier = Modifier) {

        update.collectAsState().value
        recompose.collectAsState().value

        val list = messages

        //var update by remember { mutableStateOf(true) }  //для мигания

        //val lazyListState: LazyListState = rememberLazyListState()


        //println("Последний видимый индекс = $lastVisibleItemIndex")

        //LaunchedEffect(key1 = messagesR) {
        //lastVisibleItemIndex =
        //    lazyListState.layoutInfo.visibleItemsInfo.lastIndex + lazyListState.firstVisibleItemIndex
        //println("lazy lastVisibleItemIndex $lastVisibleItemIndex")
        //}


//        LaunchedEffect(key1 = list) {
//            while (true) {
//                delay(700L)
//                //update = !update
//                //recompose.value++
//                //////////////////////telnetWarning.value = (telnetSlegenie.value == false) && (messages.size > lastCount)
//            }
//        }

//        LaunchedEffect(key1 = lastVisibleItemIndex) {
//            while (true) {
//                delay(200L)
//                val s = messagesR.size
//                if ((s > 20) && tracking) {
//                    lazyListState.scrollToItem(index = messagesR.size - 1) //Анимация (плавная прокрутка) к данному элементу.
//                }
//            }
//        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                //.background(Color(0xFF090909))
                .background(Color(0xFFFF0909))
                .then(modifier),

//                        .scrollbar(
//                            count = list.count { it.pairList.isNotEmpty() },
//                            lazyListState,
//                            horizontal = false,
//                            countCorrection = 0,
//                            hiddenAlpha = 0f
//                        )
            //state = lazyListState
        )
        {


            itemsIndexed(list)
            { index, item ->
                if (!item.deleted)
                    ScriptItemDraw({ item }, { index }, { false })
            }


            //if (messages.value.isNotEmpty())
//            itemsIndexed(messages.value)
//            { index, item ->
//                //update.collectAsState().value
//                //recompose.collectAsState().value
//                //ScriptItemDraw({ item }, { index }, { false })
//            }

        }
    }

    fun consoleAdd(
        text: String,
        color: Color = Color.Green,
        bgColor: Color = Color.Black,
        flash: Boolean = false
    ) {
        if ((messages.size > 0) && (messages.last().text == " ")) {
            messages.removeAt(messages.lastIndex)
            messages.add(
                LineTextAndColor(
                    text,
                    listOf(PairTextAndColor(text = text, color, bgColor, flash = flash))
                )
            )
        } else {
            messages.add(
                LineTextAndColor(
                    text,
                    listOf(PairTextAndColor(text = text, color, bgColor, flash = flash))
                )
            )
        }
    }


    //➕️ ✅️✏️⛏️ $${\color{red}Red}$$ 📥 📤  📃  📑 📁 📘 🇷🇺 🆗 ✳️


    /**
     * # -------------------------------------------------------------------
     * ## 🔧 Установка типа шрифта
     * 📥 **FontFamily(Font(R.font.jetbrains, FontWeight.Normal))**
     */
    fun setFontFamily(fontFamily: GenericFontFamily) {
        this.fontFamily = fontFamily
    }

    /**
     * # -------------------------------------------------------------------
     * ## 🔧 Установка размера шрифта
     */
    fun setFontSize(size: Int) {
        fontSize = size.sp
    }



    @Composable
    fun ScriptItemDraw(item: () -> LineTextAndColor, index: () -> Int, select: () -> Boolean) {
        //println("Draw  ${index()}")
        val x = convertStringToAnnotatedString(item(), index())
        Text( x, modifier = Modifier
            .fillMaxWidth()
            //.padding(top = 0.dp)
            .background(if (select()) Color.Cyan else Color.Transparent),

            fontSize = console.fontSize,
            fontFamily = FontFamily(Font(R.font.jetbrains, FontWeight.Normal)),
            //lineHeight = console.fontSize * 1.2f
        )

    }

    private fun convertStringToAnnotatedString(item: LineTextAndColor, index: Int): AnnotatedString {


        val s = item.pairList.size

        //lateinit var x : AnnotatedString
        var x = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Gray)) {
                append("${index}>")
            }
        }

        for (i in 0 until s) {

            x += buildAnnotatedString {
                withStyle(
                    style = SpanStyle(

                        color = if (!item.pairList[i].flash)
                            item.pairList[i].colorText
                        else
                            if (update.value)
                                item.pairList[i].colorText
                            else
                                Color(0xFF090909),

                        background = if (!item.pairList[i].flash)
                            item.pairList[i].colorBg
                        else
                            if (update.value)
                                item.pairList[i].colorBg
                            else Color(0xFF090909),
                        fontFamily = FontFamily(Font(R.font.jetbrains)),

                        textDecoration = if (item.pairList[i].underline) TextDecoration.Underline else null,
                        fontWeight = if (item.pairList[i].bold) FontWeight.Bold else null,
                        fontStyle = if (item.pairList[i].italic) FontStyle.Italic else null,
                    )
                )
                { append(item.pairList[i].text) }
            }

        }

        return x
    }


}










