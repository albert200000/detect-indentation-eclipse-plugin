package com.example.indentdetective;

/**
 * Immutable result of the indentation analysis.
 * Holds the detected indentation style (spaces vs. tabs) and the
 * detected indentation width (tab width).
 */
public class IndentResult {

    private final boolean useSpaces;
    private final int tabWidth;

    public IndentResult(boolean useSpaces, int tabWidth) {
        this.useSpaces = useSpaces;
        this.tabWidth = tabWidth;
    }

    public boolean isUseSpaces() { return useSpaces; }

    public int getTabWidth() { return tabWidth; }

    @Override
    public String toString() {
        return (useSpaces ? "Spaces: " : "Tabs (width: ") + tabWidth + ")";
    }
}
