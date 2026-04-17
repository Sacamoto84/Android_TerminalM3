# Команды консольных виджетов

Гайд по командам, которые понимает консольный декодер и через которые можно
создавать Compose-виджеты прямо из сообщений микроконтроллера.

Файлы, на которые опирается этот гайд:

- `ConsoleWidgetProtocol.kt`
- `ConsoleWidgetProtocolExtras.kt`
- `widgets/*.kt`

## Зарегистрированные команды

### `beep`

Служебная команда. Проигрывает звуковой сигнал на телефоне и вставляет локальный
элемент в консоль.

Пример:

```text
beep
```

### `ui`

Основная команда для создания виджетов в консоли.

### `widget`

Полный алиас команды `ui`. Работает точно так же.

Пример:

```text
ui type=badge text="READY"
widget type=badge text="READY"
```

## Общий формат

```text
ui type=<widgetType> key=value key=value ...
```

Примеры:

```text
ui type=badge text="READY" bg=#1F7A1F fg=#FFFFFF size=14
ui type=panel title="Motor 1" value=READY subtitle="24.3V" accent=#36C36B icon=info
ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9"
```

## Правила синтаксиса

1. Команда должна приходить завершенной строкой.
2. Завершение по `\n`, формат `\r\n` тоже подходит.
3. Аргументы передаются как `key=value`.
4. Значения с пробелами нужно брать в кавычки:

```text
ui type=panel title="Motor 1" subtitle="24.3V 1.8A"
```

5. Поддерживаются одинарные и двойные кавычки.
6. Булевы значения можно передавать как:
   `on/off`, `true/false`, `1/0`, `yes/no`.
7. Цвета можно передавать как:
   `#RRGGBB`, `#AARRGGBB`, либо именами `black`, `white`, `red`, `green`,
   `blue`, `yellow`, `cyan`, `magenta`, `gray`, `orange`.

## Быстрые примеры

```text
ui type=badge text="READY" bg=#1F7A1F fg=#FFFFFF size=14
ui type=dot color=#00E676 size=16 label="Link active"
ui type=image name=info size=40 desc="Info icon"
ui type=panel title="Motor 1" value=READY subtitle="24.3V 1.8A" accent=#36C36B icon=info
ui type=progress label="Battery" value=72 max=100 fill=#36C36B display="72%"
ui type=2col left="Voltage" right="24.3V"
ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9;M3|ALARM|91.8"
ui type=switch label="Pump enable" state=on subtitle="Remote mode"
ui type=alarm-card title="Overheat" message="Motor 1 temperature reached 92C" severity=critical time="12:41:03" icon=warn2
```

## Виджеты

### `type=badge`

Короткая округлая плашка для статуса.

Пример:

```text
ui type=badge text="READY" bg=#1F7A1F fg=#FFFFFF size=14
```

Параметры:

- `text` - текст на плашке
- `bg` - фон
- `fg` - цвет текста
- `size` - размер текста

### `type=dot`

Круглый индикатор, можно с подписью справа.

Пример:

```text
ui type=dot color=#00E676 size=16 label="Link active"
```

Параметры:

- `color` - цвет круга
- `size` - размер круга в `dp`
- `label` - подпись справа
- `labelColor` - цвет подписи

### `type=image`

Картинка из `res/drawable`.

Пример:

```text
ui type=image name=info size=40 desc="Info icon"
```

Параметры:

- `name` - имя drawable без расширения
- `size` - размер в `dp`
- `desc` - `contentDescription`

### `type=panel`

Карточка состояния с акцентной полосой, заголовком, значением и подписью.

Пример:

```text
ui type=panel title="Motor 1" value=READY subtitle="24.3V 1.8A" accent=#36C36B icon=info
```

Параметры:

- `title` - заголовок карточки
- `value` - значение справа
- `subtitle` - вторая строка
- `accent` - цвет вертикальной полосы слева
- `icon` - drawable слева
- `bg` - фон карточки
- `border` - цвет рамки
- `titleColor` - цвет заголовка
- `valueColor` - цвет значения
- `subtitleColor` - цвет второй строки

### `type=progress`

Карточка с полосой прогресса.

Пример:

```text
ui type=progress label="Battery" value=72 max=100 fill=#36C36B display="72%"
```

Параметры:

- `label` - подпись слева
- `value` - текущее значение
- `max` - максимальное значение
- `display` - текст справа, например `72%`
- `fill` - цвет заполнения
- `track` - цвет полосы-фона
- `bg` - фон карточки
- `border` - цвет рамки
- `labelColor` - цвет подписи
- `valueColor` - цвет текста справа

### `type=2col`

Строка из двух колонок в формате `ключ -> значение`.

Пример:

```text
ui type=2col left="Voltage" right="24.3V"
```

Параметры:

- `left` - левый текст
- `right` - правый текст
- `leftColor` - цвет левой части
- `rightColor` - цвет правой части
- `bg` - фон
- `border` - цвет рамки

### `type=table`

Таблица с заголовками и несколькими строками.

Пример:

```text
ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9;M3|ALARM|91.8"
```

Параметры:

- `headers` - заголовки колонок через `|`
- `rows` - строки через `;`
- ячейки внутри каждой строки разделяются через `|`
- `headerBg` - фон строки заголовков
- `headerColor` - цвет текста заголовков
- `cellColor` - цвет текста ячеек
- `bg` - общий фон таблицы
- `border` - цвет рамки

### `type=switch`

Визуальный `ON/OFF`-переключатель без интерактива.

Пример:

```text
ui type=switch label="Pump enable" state=on subtitle="Remote mode"
```

Параметры:

- `label` - основной текст
- `state` или `checked` - `on/off`, `true/false`, `1/0`
- `subtitle` - вторая строка
- `onColor` - цвет включенного состояния
- `offColor` - цвет выключенного состояния
- `thumb` - цвет бегунка
- `bg` - фон
- `border` - цвет рамки
- `labelColor` - цвет заголовка
- `subtitleColor` - цвет второй строки

### `type=alarm-card`

Карточка аварии / предупреждения.

Пример:

```text
ui type=alarm-card title="Overheat" message="Motor 1 temperature reached 92C" severity=critical time="12:41:03" icon=warn2
```

Параметры:

- `title` - заголовок события
- `message` - описание
- `severity` - `info`, `warn`, `error`, `critical`
- `time` - время или служебная meta-строка
- `icon` - drawable слева
- `accent` - цвет акцента
- `bg` - фон
- `border` - цвет рамки
- `titleColor` - цвет заголовка
- `messageColor` - цвет описания
- `metaColor` - цвет времени

## Алиасы и заметки

- `widget` полностью эквивалентен `ui`
- `dot`, `circle` - одно и то же
- `image`, `icon` - одно и то же
- `panel`, `card` - одно и то же
- `progress`, `bar` - одно и то же
- `2col`, `twocol`, `pair` - одно и то же
- `table`, `grid` - одно и то же
- `switch`, `toggle` - одно и то же
- `alarm-card`, `alarm`, `alert` - одно и то же

## Где смотреть реализацию

- Протокол и `spec`: [ConsoleWidgetProtocol.kt](./ConsoleWidgetProtocol.kt)
- Разбор сложных команд: [ConsoleWidgetProtocolExtras.kt](./ConsoleWidgetProtocolExtras.kt)
- Compose-виджеты: [`widgets/`](./widgets)
