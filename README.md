# Smart Macro Automation Studio (Console Edition)

A DSA coursework project: a console-based macro recorder/editor/player built
around a **custom Doubly Linked List**.

## Build & Run

Requires JDK 17+ and Maven (this sandbox couldn't compile it - no JDK/network
here - so build it on your own machine):

```bash
mvn clean package
java -jar target/smart-macro-studio.jar
```

Or, without packaging a jar:

```bash
mvn clean compile exec:java
```

On first run it creates `./macros`, `./exports`, and `settings.json` next to
wherever you run the jar from.

## How "recording" works in a console app

There's no GUI window to hook into, so recording is done via a small typed
command language (see `help` inside the record prompt):

```
record> move 500 300
record> lclick
record> type "admin@gmail.com"
  (regex engine detected EMAIL -> stored as {{Email}})
record> wait 300
record> type "MyPassword1"
record> keydown ENTER
record> keyup ENTER
record> done
```

Every accepted line becomes one `AutomationAction` node appended to the
custom `ActionLinkedList`. If you want TRUE OS-level global capture (works
even when the terminal isn't focused), see the Javadoc in
`com.smartmacro.recorder.GlobalHookRecorder` for how to wire in JNativeHook
on a machine with internet access - no other code needs to change, since
everything downstream just walks the same `ActionLinkedList`.

## Playback

Playback uses `java.awt.Robot` to really move the mouse, click, and type.
If no display is available (e.g. a headless server), it automatically falls
back to a dry-run mode that just prints what it would have done.

## Where the coursework requirements live

| Requirement                                 | Where |
|----------------------------------------------|-------|
| Custom Doubly Linked List                     | `datastructure/ActionLinkedList.java` |
| AutomationAction node                         | `model/AutomationAction.java` |
| Regex dynamic variable detection              | `regex/RegexEngine.java`, `regex/DynamicVariableGenerator.java` |
| Recorder Engine                               | `recorder/RecorderEngine.java` |
| Timeline Editor                               | `editor/TimelineEditor.java` |
| Playback Engine                               | `playback/PlaybackEngine.java`, `playback/PlaybackController.java` |
| PowerShell Script Generator                   | `script/PowerShellScriptGenerator.java` |
| Macro Manager (JSON)                          | `macro/MacroManager.java`, `macro/MacroSerializer.java`, `util/Json.java` |
| Statistics Dashboard                          | `stats/StatisticsEngine.java` |
| Undo/Redo (command pattern + custom stack)    | `undoredo/` package, `datastructure/SimpleStack.java` |
| Settings Manager                              | `settings/` package |
| Console UI ("Dashboard" etc as menus)         | `ui/ConsoleUI.java`, `ui/MainApp.java` |

## Notes / honest limitations

- No `java.util.LinkedList`, `Deque`, or `Queue` are used anywhere for the
  timeline or undo/redo stacks - both are hand-rolled (`ActionLinkedList`,
  `SimpleStack`).
- JSON parsing/writing is hand-rolled (`util/Json.java`) so the project has
  **zero required external dependencies** to build and run the console app.
- Robot-based playback requires a real display (works fine on a normal
  desktop OS; not inside a headless CI/server, where it degrades to dry-run
  printing instead of failing).
- The generated `.ps1` scripts use `Add-Type` P/Invoke + `SendKeys`, which
  are standard techniques on Windows PowerShell / PowerShell 7 and need no
  extra modules installed.
