# detect-indentation-eclipse-plugin

An Eclipse plugin that automatically detects and applies the indentation settings of the
active text editor.

When you open or activate a text editor, the plugin analyzes the file's indentation patterns and
applies the detected settings — tabs vs. spaces and the indentation width — to that editor.

## How it works

The plugin registers a workbench listener that fires on editor activation and on window opening.
For every active `ITextEditor` it runs the core heuristic (`IndentDetective`) against the editor's
document and writes the result into the shared `EditorsUI` preference store:

- **`IndentDetective`** — the core detection logic. It scans up to 150 non-empty lines, counting
  tab-indented vs. space-indented lines and voting on the space-indent *diff* (jump between
  consecutive indent levels). Standard widths (2, 3, 4, 6, 8) are considered. The result is a
  `IndentResult` holding `useSpaces` and the detected `tabWidth`.
- **`IndentListener`** — `IPartListener2` that runs the analysis when a part is activated. Results
  are cached per editor input so switching back to an already-analyzed tab reuses the previous
  result instead of re-analyzing.
- **`Activator`** — the plugin singleton that wires up the window/part listeners.
- **`IndentStartup`** — an `IStartup` hook that registers the listeners once the workbench is ready.
