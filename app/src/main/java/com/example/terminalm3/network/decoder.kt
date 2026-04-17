package com.example.terminalm3.network

import com.example.terminalm3.Global
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Декодирует входящий поток текста из [channelIn] в два направления:
 *
 * 1. В UI через [channelOutNetCommand], чтобы консоль могла показывать как
 *    промежуточные части строки, так и уже завершенные строки.
 * 2. Во внутренний CLI-парсер, который вызывает callback зарегистрированной команды.
 *
 * У декодера два независимых представления текущей строки:
 * - raw-строка для парсинга команд;
 * - display-строка для консоли.
 *
 * Это важно, потому что для UI можно подменять `CR` / `LF` на цветные метки,
 * а для парсинга команд нужен исходный текст без декоративных вставок.
 */
class NetCommandDecoder(
    private val channelIn: Channel<String>,
    private val channelOutNetCommand: Channel<NetCommand>
) {

    /**
     * Зарегистрированные CLI-команды по нормализованному имени.
     *
     * Повторная регистрация того же имени заменяет прошлый callback, что
     * избавляет от дублирования обработчиков одной и той же команды.
     */
    private val cmdMap = linkedMapOf<String, CliCommand>()

    /**
     * Локальный канал завершенных строк, которые уже можно парсить как команды.
     */
    private val channelOutCommand = Channel<CommandPacket>(COMMAND_CHANNEL_CAPACITY)

    /**
     * Собственный scope декодера.
     *
     * Это безопаснее, чем `GlobalScope`, и не создает лишние фоновые корутины
     * при повторном вызове [run].
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Накопитель текущей строки в сыром виде.
     *
     * Используется только для CLI-парсинга.
     */
    private val rawLineBuilder = StringBuilder()

    /**
     * Накопитель текущей строки для UI.
     *
     * В него можно подставлять служебные маркеры `CR` / `LF`, не ломая сырой
     * текст, который разбирает CLI-парсер.
     */
    private val displayLineBuilder = StringBuilder()

    /**
     * Идентификатор текущей входящей строки.
     *
     * Нужен для связи сетевой строки и локального ответа в консоли.
     */
    private var currentLineId: Long = 1L

    /**
     * Защита от повторного запуска `run()`.
     */
    @Volatile
    private var isStarted = false

    /**
     * Регистрирует CLI-команду и callback для нее.
     *
     * Можно передать `"beep"` или `"beep\r"` - имя все равно будет
     * нормализовано перед сохранением.
     *
     * [cb] получает:
     * - список аргументов после имени команды;
     * - [lineId] той сетевой строки, из которой команда пришла.
     */
    fun addCmd(name: String, cb: (List<String>, Long) -> Unit = { _, _ -> }) {
        val normalizedName = normalizeCommandText(name)

        if (normalizedName.isEmpty()) {
            Timber.w("Попытка зарегистрировать пустую CLI-команду")
            return
        }

        cmdMap[normalizedName] = CliCommand(normalizedName, cb)
    }

    /**
     * Запускает декодер.
     *
     * Внутри поднимаются два фоновых цикла:
     * - [decodeScope] собирает строки из входящего потока;
     * - [cliDecoder] берет завершенные строки и разбирает их как команды.
     *
     * Повторный вызов не делает ничего, чтобы не плодить дублирующиеся корутины.
     */
    @Synchronized
    fun run() {
        if (isStarted) {
            Timber.w("NetCommandDecoder уже запущен, повторный run() пропущен")
            return
        }

        isStarted = true

        Timber.i("Запуск декодировщика команд")
        scope.launch { decodeScope() }
        scope.launch { cliDecoder() }
    }

    /**
     * Читает входящие куски текста и собирает их в строки.
     *
     * Линия считается завершенной только по `\n`.
     * Если устройство присылает `\r\n`, то:
     * - `\r` остается частью сырой строки;
     * - `\n` завершает строку;
     * - в UI по желанию показываются метки `CR` / `LF`.
     *
     * Пока `\n` не пришел, консоль все равно получает промежуточные обновления,
     * поэтому пользователь видит потоковый вывод без задержки до конца строки.
     */
    private suspend fun decodeScope() {
        val rawChunkBuffer = StringBuilder()
        val displayChunkBuffer = StringBuilder()

        while (true) {
            val rawChunk = channelIn.receive()

            if (rawChunk.isEmpty()) {
                continue
            }

            rawChunkBuffer.append(rawChunk)
            displayChunkBuffer.append(buildDisplayChunk(rawChunk))

            while (true) {
                val rawNewLineIndex = rawChunkBuffer.indexOf('\n')

                if (rawNewLineIndex == -1) {
                    emitPartialLine(rawChunkBuffer, displayChunkBuffer)
                    break
                }

                emitCompletedLine(
                    rawChunkBuffer = rawChunkBuffer,
                    displayChunkBuffer = displayChunkBuffer,
                    rawNewLineIndex = rawNewLineIndex
                )
            }
        }
    }

    /**
     * Полностью собранная строка для CLI-парсинга.
     *
     * [raw] не содержит завершающий `\n`, но может содержать `\r`, если он шел
     * перед переводом строки.
     */
    data class CommandPacket(
        val raw: String,
        val lineId: Long
    )

    /**
     * Описание одной зарегистрированной команды.
     */
    data class CliCommand(
        val name: String,
        val cb: (List<String>, Long) -> Unit
    )

    /**
     * Ждет завершенные строки из [channelOutCommand] и отправляет их в [parse].
     */
    private suspend fun cliDecoder() {
        while (true) {
            parse(channelOutCommand.receive())
        }
    }

    /**
     * Разбирает одну завершенную строку как простую CLI-команду.
     *
     * Правила очень простые:
     * - строка нормализуется: удаляются `\r`, `\n`, пробелы по краям;
     * - первое слово считается именем команды;
     * - остальные слова считаются аргументами.
     *
     * Если команда отсутствует, пишется предупреждение.
     * Если упал callback команды, логируется уже именно ошибка callback,
     * а не "команда не найдена".
     */
    private fun parse(packet: CommandPacket) {
        val normalizedLine = normalizeCommandText(packet.raw)

        if (normalizedLine.isEmpty()) {
            return
        }

        val parts = normalizedLine
            .split(ARGUMENT_SPLIT_REGEX)
            .filter { it.isNotEmpty() }

        val name = parts.firstOrNull() ?: return
        val arg = parts.drop(1)
        val command = cmdMap[name]

        if (command == null) {
            Timber.w("CLI команда не зарегистрирована: $name")
            return
        }

        try {
            command.cb.invoke(arg, packet.lineId)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка выполнения CLI-команды: $name")
        }
    }

    /**
     * Отправляет в UI промежуточное состояние строки, которая еще не завершена.
     *
     * Это позволяет консоли показывать потоковый вывод по мере прихода данных,
     * а не ждать обязательный `\n`.
     */
    private suspend fun emitPartialLine(
        rawChunkBuffer: StringBuilder,
        displayChunkBuffer: StringBuilder
    ) {
        if (rawChunkBuffer.isEmpty() && displayChunkBuffer.isEmpty()) {
            return
        }

        rawLineBuilder.append(rawChunkBuffer)
        displayLineBuilder.append(displayChunkBuffer)

        rawChunkBuffer.clear()
        displayChunkBuffer.clear()

        if (displayLineBuilder.isNotEmpty()) {
            channelOutNetCommand.send(
                NetCommand(
                    cmd = displayLineBuilder.toString(),
                    newString = false,
                    lineId = currentLineId
                )
            )
        }
    }

    /**
     * Завершает текущую строку по найденному `\n`.
     *
     * После этого:
     * - сырой текст отправляется в CLI-парсер;
     * - display-текст отправляется в UI как уже завершенная строка.
     */
    private suspend fun emitCompletedLine(
        rawChunkBuffer: StringBuilder,
        displayChunkBuffer: StringBuilder,
        rawNewLineIndex: Int
    ) {
        val displayNewLineIndex = displayChunkBuffer.indexOf('\n')

        if (displayNewLineIndex == -1) {
            Timber.w("Нарушена синхронизация буферов декодера, строка будет сброшена")
            rawChunkBuffer.clear()
            displayChunkBuffer.clear()
            rawLineBuilder.clear()
            displayLineBuilder.clear()
            return
        }

        rawLineBuilder.append(rawChunkBuffer.substring(0, rawNewLineIndex))
        displayLineBuilder.append(displayChunkBuffer.substring(0, displayNewLineIndex))

        rawChunkBuffer.delete(0, rawNewLineIndex + 1)
        displayChunkBuffer.delete(0, displayNewLineIndex + 1)

        if (Global.isCheckUseCRLF) {
            displayLineBuilder.append(LF_MARKER)
        }

        val lineId = currentLineId

        channelOutCommand.send(
            CommandPacket(
                raw = rawLineBuilder.toString(),
                lineId = lineId
            )
        )

        channelOutNetCommand.send(
            NetCommand(
                cmd = displayLineBuilder.toString(),
                newString = true,
                lineId = lineId
            )
        )

        rawLineBuilder.clear()
        displayLineBuilder.clear()
        currentLineId++
    }

    /**
     * Преобразует входящий кусок текста для UI.
     *
     * Здесь подменяется только `\r`, потому что `\n` используется как граница
     * завершения строки и обрабатывается отдельно в [emitCompletedLine].
     */
    private fun buildDisplayChunk(rawChunk: String): String {
        if (!Global.isCheckUseCRLF) {
            return rawChunk
        }

        return rawChunk.replace("\r", CR_MARKER)
    }

    /**
     * Приводит имя команды и строку для парсинга к одному виду.
     *
     * Это убирает путаницу между регистрацией `"beep\r"` и фактическим разбором
     * строки `"beep\r\n"` из сети.
     */
    private fun normalizeCommandText(value: String): String {
        return value
            .replace("\r", "")
            .replace("\n", "")
            .trim()
    }

    companion object {
        /**
         * Большая емкость внутреннего канала команд.
         *
         * Так прием сетевых данных не упирается в парсер слишком рано.
         */
        private const val COMMAND_CHANNEL_CAPACITY = 1_000_000

        /**
         * Последовательность пробельных символов считается разделителем аргументов.
         */
        private val ARGUMENT_SPLIT_REGEX = Regex("\\s+")

        /**
         * Визуальная метка символа `CR` для консоли.
         */
        private const val CR_MARKER = "\u001B[01;39;05;0;49;05;10mCR\u001B[2m"

        /**
         * Визуальная метка символа `LF` для консоли.
         */
        private const val LF_MARKER = "\u001B[01;39;05;15;49;05;27mLF\u001B[2m"
    }
}
