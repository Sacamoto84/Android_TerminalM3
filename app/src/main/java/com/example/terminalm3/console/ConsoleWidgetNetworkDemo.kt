package com.example.terminalm3.console

import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.SendChannel

/**
 * Набор демонстрационных сетевых команд для всех поддерживаемых консольных виджетов.
 *
 * Эти строки специально оформлены так же, как реальные команды от микроконтроллера,
 * поэтому их можно отправлять напрямую в [NetCommandDecoder] через `Channel<String>`.
 */
val consoleWidgetDemoNetworkCommands: List<String> = listOf(
    "ui type=badge text=\"READY\" st=ok",
    "ui type=dot color=#00E676 size=16 label=\"Link active\"",
    "ui type=image name=info size=40 desc=\"Info icon\"",
    "ui type=panel title=\"Motor 1\" value=READY subtitle=\"24.3V 1.8A\" accent=#36C36B icon=info",
    "ui type=progress label=\"Battery\" value=72 max=100 fill=#36C36B display=\"72%\"",
    "ui type=2col left=\"Voltage\" right=\"24.3V\"",
    "ui type=table headers=\"Name|State|Temp\" rows=\"M1|READY|24.3;M2|WAIT|22.9;M3|ALARM|91.8\"",
    "ui type=switch label=\"Pump enable\" state=on subtitle=\"Remote mode\"",
    "ui type=alarm-card title=\"Overheat\" message=\"Motor 1 temperature reached 92C\" severity=critical time=\"12:41:03\" icon=warn2",
    "ui type=sparkline label=\"Temp\" values=\"21,22,22,23,24,23,25\" min=18 max=28 color=#36C36B display=\"25C\" points=on",
    "ui type=bar-group title=\"Motors\" labels=\"M1|M2|M3\" values=\"20|45|80\" max=100 colors=\"#36C36B|#4FC3F7|#FFB300\"",
    "ui type=gauge label=\"CPU\" value=72 max=100 unit=\"%\" color=#36C36B",
    "ui type=battery label=\"Battery A\" value=78 max=100 charging=true voltage=4.08",
    "ui type=led-row title=\"Links\" items=\"NET:#00E676|MQTT:#00E676|ERR:#FF5252|GPS:off\"",
    "ui type=stats-card title=\"RPM\" value=1450 unit=\"rpm\" delta=\"+12\" subtitle=\"Motor 1\" accent=#36C36B",
    "ui type=kv-grid title=\"Motor 1\" items=\"Voltage:24.3V|Current:1.8A|Temp:62C|State:READY\" columns=2",
    "ui type=pin-bank title=\"GPIO\" items=\"D1:on|D2:off|D3:warn|A0:adc|PWM1:pwm\"",
    "ui type=timeline title=\"Boot\" items=\"12:01 Boot|12:03 WiFi connected|12:05 MQTT online\"",
    "ui type=line-chart title=\"Voltage\" values=\"24.1,24.2,24.0,24.3,24.4\" labels=\"T1|T2|T3|T4|T5\" min=23 max=25 color=#4FC3F7",
    "ui type=bitfield label=\"STATUS\" value=0xB38F bits=16",
    "ui type=hex-dump title=\"RX Buffer\" data=\"48 65 6C 6C 6F 20 57 6F 72 6C 64\" width=8 addr=0x1000 ascii=on",
    "ui type=register-table title=\"Holding Registers\" rows=\"0000|0x1234|Status;0001|0x00A5|Flags;0002|0x03E8|Speed\"",
    "ui type=modbus-frame direction=request preset=rtu data=\"01 03 00 10 00 02 C5 CE\"",
    "ui type=can-frame title=\"Motor CAN\" direction=rx id=0x18FF50E5 ext=true data=\"11 22 33 44 55 66 77 88\" channel=can0",
    "ui type=uart-frame title=\"UART RX\" direction=rx channel=UART1 baud=115200 data=\"AA 55 10 02 01 02 34\" fields=\"0-1|Sync|AA55|Preamble;2|Cmd|10|Command;3|Len|02|Payload length;4-5|Payload|0102|Data;6|CRC|34|Checksum\"",
    "ui type=packet-frame title=\"Binary Packet\" protocol=CUSTOM direction=tx data=\"7E A1 02 10 FF 55\" ascii=on"
)

/**
 * Отправляет весь демо-набор виджетов в сетевой канал так,
 * как будто команды пришли с внешнего устройства.
 *
 * [delayMs] нужен только для более "живого" появления карточек в консоли.
 */
suspend fun emitConsoleWidgetNetworkDemo(
    channel: SendChannel<String>,
    delayMs: Long = 40L
) {
    consoleWidgetDemoNetworkCommands.forEachIndexed { index, command ->
        channel.send("$command\n")

        if (delayMs > 0L && index != consoleWidgetDemoNetworkCommands.lastIndex) {
            delay(delayMs)
        }
    }
}
