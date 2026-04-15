package com.example.terminalm3

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terminalm3.screen.lazy.PairTextAndColor
import com.example.terminalm3.network.channelLastString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class VM : ViewModel() {

    fun launchUIChanelReceive() {
        viewModelScope.launch(Dispatchers.Main) {
            receiveUILastString()
        }
    }

    //Создание списка pairTextAndColor из исходного текста
    private fun text_to_paitList(txt: String, mod : PairTextAndColor? = null): SnapshotStateList<PairTextAndColor> {
        val pair: SnapshotStateList<PairTextAndColor> = mutableStateListOf()// = arrayListOf()

        //замена [ на \u001C это и будет новый разделитель
        val str = txt.replace("\u001B", "\u001C\u001B")

        val list = str.split("\u001C") //Разделить по 1C чтобы сохранить 

        for (str1 in list) {
            if (str1 == "") {
                continue
            } else {
                //println("!text_to_paitList! split по ESC >>$str1")
                val p = stringcalculate(str1)
                pair.addAll(p)//Создаем список пар для одной строки

                if (mod != null)
                    pair.add(mod)

            }
        }
        return pair
    }


    /**
     * # ╔══════════════╗
     * # ║ 𝌴░░𝌴𝌴𝌴𝌴𝌴𝌴𝌴𝌴 ║▎▎▎▎
     * # ╚══════════════╝
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun receiveUILastString() {
        while (true) {

            while (!channelLastString.isEmpty)
            {
                val s = channelLastString.receive()
                if(s.cmd == "") continue

                //Отображение курсора, без записи в массив
                var mod: PairTextAndColor? = null
                if ((Global.isCheckUseCRLF) && ((!s.newString)))
                    mod = PairTextAndColor("▁", Color.Green, Color.Black, true, flash = true)

                    //s.cmd += '▁'//'⤵'▮ ▯ ▎


                //println("s.newString=${s.newString}")

                //mod = PairTextAndColor("▁", Color.Green, Color.Black, true, flash = true)

                if (console.messages.messages.isNotEmpty()) {
                    val pair = text_to_paitList(s.cmd, mod)
                    console.messages.messages.last().text = s.cmd
                    console.messages.messages.last().pairList = pair

                    //Если новая строка
                    if (s.newString) console.print("▁", flash = true)

                    console.recompose() //Для ручной композиции списка
                }
            }

            //withContext(Dispatchers.Main)
            //{
                //Timber.i("Ку ${channelLastString.isEmpty} ${colorline_and_text.size} ${colorline_and_text.last().text}")
               // console.recompose() //Для ручной композиции списка
            //}
            yield()

        }
    }

}