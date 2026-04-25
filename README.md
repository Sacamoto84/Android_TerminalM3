# TerminalM3

`TerminalM3` - Android-приложение-терминал для ESP32 и других микроконтроллеров. Оно принимает поток текстовых данных по сети, отображает ANSI/ESC-разметку, умеет рисовать Compose-виджеты из строковых команд и отправляет команды обратно на ESP32.

Проект состоит из нескольких связанных частей:

- Android-приложение `TerminalM3`.
- Система консольных Compose-виджетов.
- C++ библиотека `TimberWidget` для Arduino / PlatformIO, которая формирует команды `ui type=...`.
- Сетевой протокол для связки Android и ESP32.

## Возможности Android

- Прием живого потока логов от ESP32 по `TCP 8888`.
- Отправка команд на ESP32 по `TCP 8900`; команды завершаются `\n` и рассчитаны на обработку через `SimpleCLI`.
- Отображение ANSI / ESC-последовательностей: цвет текста, фон, bold, italic, underline, flash.
- Многоканальная консоль `0..3` и общий канал `ALL`.
- Маршрутизация строки в канал через transport-prefix, например `@2 hello`.
- Отображение виджетов из команд `ui ...` / `widget ...`.
- Закрепленные виджеты по slot/index, чтобы обновлять карточку на месте.
- Настройка показа исходной строки команды виджета.
- Если исходная строка виджета скрыта, длинное удержание на виджете показывает или скрывает ее.
- Компактный индикатор TCP-состояния в виде цветного кружка.
- Автопоиск ESP32 через `esp.local` или ручной ввод IP.
- UDP heartbeat для контроля живости соединения.
- Встроенное открытие web-портала ESP32.

## Схема Связи

Основной сценарий сейчас такой:

```text
ESP32 UART / Serial data
        |
        v
ESP32 TCP server :8888  --->  Android TerminalM3 console

Android TerminalM3      --->  ESP32 TCP server :8900  --->  SimpleCLI

Android / ESP32         <-->  UDP :8888 heartbeat
```

Порты:

| Порт | Протокол | Назначение |
| --- | --- | --- |
| `8888` | TCP | Основной поток данных от ESP32 в Android. |
| `8900` | TCP | Команды из Android в ESP32 для `SimpleCLI`. |
| `8888` | UDP | Heartbeat ping/pong и служебная проверка связи. |

Важно: порт `8889` больше не используется. Для команд оставлен только `TCP 8900`.

## Виджеты

Android понимает строки вида:

```text
ui type=badge text="READY" st=ok
ui type=panel title="Motor 1" value=READY subtitle="24.3V"
ui type=progress label="Battery" value=72 max=100 display="72%"
```

Команда приходит как обычная строка консоли, парсится и превращается в Compose-виджет. В настройках можно выбрать, показывать ли исходную строку `ui/widget ...` над виджетом.

Если показ исходной строки выключен:

- строка команды скрывается из консольного потока;
- сам виджет остается на экране;
- длинное удержание на виджете показывает эту строку;
- повторное длинное удержание скрывает ее обратно.

Подробная спецификация виджетов находится здесь:

- [README консольных виджетов Android](app/src/main/java/com/example/terminalm3/console/README.md)
- [README библиотеки TimberWidget](TimberWidget/README.md)

## Настройки В Приложении

В нижней панели доступны:

- показ номеров строк;
- показ визуальных меток `CR` / `LF`;
- показ или скрытие строк, которые породили виджеты;
- выбор IP ESP32: авто через `esp.local` или ручной адрес;
- отправка `SimpleCLI` команд на `TCP 8900`;
- открытие web-портала ESP32;
- размер шрифта консоли.

## Сборка Android

Требования:

- Android Studio.
- JDK 17.
- Android 6.0+ (`minSdk 23`).

Сборка из командной строки:

```powershell
.\gradlew :app:assembleDebug
```

Запуск в Android Studio:

1. Открыть проект.
2. Дождаться синхронизации Gradle.
3. Запустить конфигурацию `app` на устройстве или эмуляторе.

## Скриншоты

<p align="center">
  <img src="img/1.jpg" width="30%" alt="Главный экран TerminalM3" />
  <img src="img/2.jpg" width="30%" alt="Консоль с входящими сообщениями" />
  <img src="img/3.jpg" width="30%" alt="Нижняя панель настроек" />
</p>

<p align="center">
  <img src="img/4.jpg" width="30%" alt="Экран со справкой по цветам" />
  <img src="img/5.jpg" width="30%" alt="Встроенный web-портал устройства" />
</p>

## Документация

- [Console widgets README](app/src/main/java/com/example/terminalm3/console/README.md)
- [Network decoder README](app/src/main/java/com/example/terminalm3/network/Readme.md)
- [TimberWidget README](TimberWidget/README.md)

## Стек

- Kotlin
- Jetpack Compose
- Material 3
- Hilt
- Timber
- C++ / Arduino / PlatformIO для библиотеки `TimberWidget`
