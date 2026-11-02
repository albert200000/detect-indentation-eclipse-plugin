# Detect indentation Eclipse plugin

An Eclipse plugin that automatically detects and applies the indentation settings of the
active text editor.

When you open or activate a text editor, the plugin analyzes the file's indentation patterns and
applies the detected settings — tabs vs. spaces and the indentation width — to that editor.

## Build & test

The project is an Eclipse PDE plug-in with Java. Build and run tests from Eclipse (or use the
standard PDE build). The unit tests cover `IndentDetective` and `IndentResult`.
