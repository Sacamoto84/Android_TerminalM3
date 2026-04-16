package com.example.terminalm3.network

import com.example.terminalm3.Global
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber

class NetCommandDecoder(
    private val channelIn: Channel<String>,               //Входной канал от bt и wifi
    private val channelOutNetCommand: Channel<NetCommand> //Для Отображения списка текст.новая строка
) {

    /**
     * # Добавить команду
     */
    fun addCmd(name: String, cb: (List<String>) -> Unit = { }) = cmdList.add(CliCommand(name, cb))


    private var lastString: String = "" //Прошлая строка
    private val channelRoute = Channel<String>(1000000)

    @OptIn(DelicateCoroutinesApi::class)
    fun run() {
        Timber.i("Запуск декодировщика")
        GlobalScope.launch(Dispatchers.IO) { decodeScope() }
        GlobalScope.launch(Dispatchers.IO) { cliDecoder() }
    }

    private suspend fun decodeScope() {

        val bigStr: StringBuilder =
                StringBuilder() //Большая строка в которую и складируются данные с канала

        while (true) {

            var string =  channelIn.receive() //Получить строку с канала, может содежать несколько строк

            if (Global.isCheckUseCRLF) string =
                    string.replace("\r", "\u001B[01;39;05;0;49;05;10mCR\u001B[2m")

            //Timber.e( "in>>>${string.length} "+string )

            if (string.isEmpty()) continue

            bigStr.append(string) //Захерячиваем в большую строку

            //MARK: Будем сами делить на строки
            while (true) { //Индекс \n
                val indexN = bigStr.indexOf('\n')

                if (indexN != -1) { //Область полюбому имеет конец строки
                    //MARK: Чета есть, копируем в подстроку
                    val stringDoN = bigStr.substring(0, indexN)
                    bigStr.delete(0, bigStr.indexOf('\n') + 1)

                    lastString += stringDoN

                    if (Global.isCheckUseCRLF) lastString += "\u001B[01;39;05;15;49;05;27mLF\u001B[2m"

                    channelOutCommand.send(lastString)
                    channelOutNetCommand.send(
                        NetCommand(
                            lastString, true
                        )
                    ) //Timber.i( "out>>>${lastString.length} "+lastString )
                    lastString = ""


                } else { //Конец строки не найден
                    //MARK: Тут для дополнения прошлой строки
                    //Получить полную запись посленней строки
                    lastString += bigStr
                    if (lastString.isNotEmpty()) {
                        channelOutNetCommand.send(
                            NetCommand(
                                lastString, false
                            )
                        ) //Timber.w( "out>>>${lastString.length} "+lastString )
                    }
                    bigStr.clear() //Он отжил свое)
                    break
                }

            }


        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val channelOutCommand = Channel<String>(1000000) //Готовые команды из пакета

    data class CliCommand(var name: String, var cb: (List<String>) -> Unit)

    //Перевод на сет
    private val cmdList = mutableListOf<CliCommand>() //Список команд


    private suspend fun cliDecoder() {
        while (true) {
            val s = channelOutCommand.receive()
            parse(s)
        }
    }

    private fun parse(str: String) {
        if (str.isEmpty()) return
        val l = str.split(' ').toMutableList()
        val name = l.first()
        l.removeFirst()
        val arg: List<String> = l.filter { it.isNotEmpty() }
        try {
            val command: CliCommand = cmdList.first { it.name == name }
            command.cb.invoke(arg)
        } catch (e: Exception) {
            Timber.e("CLI отсутствует команда $name")
        }

    }


}