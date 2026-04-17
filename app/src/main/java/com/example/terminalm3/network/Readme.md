## NetCommandDecoder

`NetCommandDecoder` принимает куски текста из `channelNetworkIn`, собирает из них строки и дальше разводит поток в две стороны:

1. В UI через `channelLastString`, чтобы консоль могла показывать:
   - промежуточную строку, пока `\n` еще не пришел;
   - завершенную строку, когда строка закрылась.
2. Во внутренний CLI-парсер, чтобы по завершенной строке можно было вызвать `addCmd(...)`.

### Поток данных

```mermaid
flowchart TD
    A["UDP / BT / другой источник"] -->|Channel<String>| B["NetCommandDecoder"]
    B -->|NetCommand(cmd, newString, lineId)| C["channelLastString"]
    C --> D["VM / Console"]
    B -->|CommandPacket(raw, lineId)| E["cliDecoder()"]
    E --> F["parse()"]
    F --> G["callback команды"]
```

### Что считается строкой

- Декодер завершает строку по `\n`.
- Если устройство присылает `\r\n`, то:
  - `\r` остается частью сырой команды;
  - `\n` закрывает строку;
  - при включенном `Global.isCheckUseCRLF` в UI рисуются маркеры `CR` и `LF`.

### Зачем два буфера

Внутри декодера есть два представления одной и той же строки:

- `rawLineBuilder`:
  - хранит исходный текст;
  - используется для `parse()`;
  - не должен содержать декоративных вставок для UI.

- `displayLineBuilder`:
  - хранит строку для консоли;
  - может содержать подсветку `CR` / `LF`;
  - отправляется в `channelLastString`.

Это сделано для того, чтобы красивое отображение служебных символов не ломало разбор команд.

### Как использовать

```kotlin
decoder.run()

decoder.addCmd("beep") { args, lineId ->
    // args  - аргументы после имени команды
    // lineId - id сетевой строки, после которой можно вставить локальный ответ в консоль
}
```

### Команды виджетов

Теперь декодер умеет принимать UI-команды, которые описывают Compose-виджет через обычные аргументы `key=value`.

Зарегистрированы две одинаковые команды:

- `ui`
- `widget`

Общий формат:

```text
ui type=<widgetType> key=value key=value ...
```

Если значение содержит пробелы, его можно заключить в кавычки:

```text
ui type=badge text="READY TO WORK" bg=#2E7D32 fg=#FFFFFF
```

### Поддерживаемые типы виджетов

#### `type=badge`

Плашка с текстом.

```text
ui type=badge text="READY" bg=#1F7A1F fg=#FFFFFF size=14
```

Параметры:

- `text` - текст плашки
- `bg` - цвет фона
- `fg` - цвет текста
- `size` - размер текста

#### `type=dot`

Круглый индикатор, можно с подписью.

```text
ui type=dot color=#00FF66 size=16 label="Link active"
```

Параметры:

- `color` - цвет круга
- `size` - диаметр в dp
- `label` - подпись справа
- `labelColor` - цвет подписи

#### `type=image`

Картинка из `res/drawable`.

```text
ui type=image name=info size=32
```

Параметры:

- `name` - имя drawable без расширения
- `size` - размер картинки в dp
- `desc` - contentDescription

#### `type=panel`

Карточка с заголовком, значением, подписью и опциональной иконкой.

```text
ui type=panel title="Motor 1" value=READY subtitle="24.3V" accent=#36C36B icon=info
```

Параметры:

- `title` - главный текст
- `value` - значение справа
- `subtitle` - вторая строка
- `accent` - цвет вертикальной полосы слева
- `bg` - фон карточки
- `border` - цвет рамки
- `icon` - drawable слева
- `titleColor` - цвет заголовка
- `valueColor` - цвет значения
- `subtitleColor` - цвет второй строки

### Что делает `lineId`

`lineId` связывает:

- входящую строку из сети;
- callback команды;
- локальную вставку в консоль, например `printLocalAfterRemoteLine(...)`
  или `printComposableAfterRemoteLine(...)`.

За счет этого локальный ответ появляется рядом именно с той строкой, которая вызвала команду.

### Основные методы

- `addCmd(name, cb)`
  - регистрирует команду;
  - имя нормализуется, поэтому можно передать и `"beep"`, и `"beep\r"`.

- `run()`
  - запускает декодер;
  - повторный вызов игнорируется, чтобы не запускать лишние корутины.

- `decodeScope()`
  - получает куски текста;
  - собирает их в строки;
  - отправляет промежуточные и завершенные данные в UI;
  - завершенные строки передает в CLI-парсер.

- `cliDecoder()`
  - берет только завершенные строки;
  - передает их в `parse()`.

- `parse()`
  - нормализует строку;
  - первое слово берет как имя команды;
  - остальные слова передает как аргументы callback.

### Additional widget types

#### `type=progress`

```text
ui type=progress label="Battery" value=72 max=100 fill=#36C36B display="72%"
```

Key params:
- `label`
- `value`
- `max`
- `display`
- `fill`
- `track`
- `bg`
- `border`

#### `type=2col`

```text
ui type=2col left="Voltage" right="24.3V"
```

Key params:
- `left`
- `right`
- `leftColor`
- `rightColor`
- `bg`
- `border`

#### `type=table`

```text
ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9"
```

Key params:
- `headers` - columns separated by `|`
- `rows` - rows separated by `;`, cells inside rows separated by `|`
- `headerBg`
- `headerColor`
- `cellColor`
- `bg`
- `border`

#### `type=switch`

```text
ui type=switch label="Pump enable" state=on subtitle="Remote mode"
```

Key params:
- `label`
- `state` or `checked` - `on/off`, `true/false`, `1/0`
- `subtitle`
- `onColor`
- `offColor`
- `thumb`
- `bg`
- `border`

#### `type=alarm-card`

```text
ui type=alarm-card title="Overheat" message="Motor 1: 92C" severity=critical time="12:41:03"
```

Key params:
- `title`
- `message`
- `severity` - `info`, `warn`, `error`, `critical`
- `time`
- `accent`
- `bg`
- `border`
- `icon`
