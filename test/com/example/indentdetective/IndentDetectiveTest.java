package com.example.indentdetective;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.Document;
import org.junit.Test;

public class IndentDetectiveTest {

    @Test
    public void detectsTabsUsesDefaultWidth() {
        IndentResult result = IndentDetective.analyze(new Document("\tfoo\n\t\tbar"));
        assertEquals(false, result.isUseSpaces());
        assertEquals(4, result.getTabWidth());
    }

    @Test
    public void detectsFourSpaceWidth() {
        IndentResult result = IndentDetective.analyze(new Document("    foo\n        bar"));
        assertEquals(true, result.isUseSpaces());
        assertEquals(4, result.getTabWidth());
    }

    @Test
    public void detectsTwoSpaceWidth() {
        IndentResult result = IndentDetective.analyze(new Document("  foo\n    bar"));
        assertEquals(true, result.isUseSpaces());
        assertEquals(2, result.getTabWidth());
    }

    @Test
    public void detectsEightSpaceWidth() {
        IndentResult result = IndentDetective.analyze(new Document("        foo\n                bar"));
        assertEquals(true, result.isUseSpaces());
        assertEquals(8, result.getTabWidth());
    }

    @Test
    public void detectsThreeSpaceWidth() {
        IndentResult result = IndentDetective.analyze(new Document("   foo\n      bar"));
        assertEquals(true, result.isUseSpaces());
        assertEquals(3, result.getTabWidth());
    }

    @Test
    public void detectsSixSpaceWidth() {
        IndentResult result = IndentDetective.analyze(new Document("      foo\n            bar"));
        assertEquals(true, result.isUseSpaces());
        assertEquals(6, result.getTabWidth());
    }

    @Test
    public void skipsBlankLines() {
        IndentResult result = IndentDetective.analyze(new Document("\n\n  foo\n\n\n  bar\n\n"));
        assertEquals(true, result.isUseSpaces());
        assertEquals(2, result.getTabWidth());
    }

    @Test
    public void handlesLinesExceedingCap() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append("    line ").append(i).append('\n');
        }
        IndentResult result = IndentDetective.analyze(new Document(sb.toString()));
        assertEquals(true, result.isUseSpaces());
        assertEquals(4, result.getTabWidth());
    }

    @Test
    public void tieBreakFavorsSpaces() {
        // Equal space and tab line counts -> useSpaces is true (>= comparison)
        IndentResult result = IndentDetective.analyze(new Document("\tfoo\n    bar"));
        assertEquals(true, result.isUseSpaces());
    }

    @Test
    public void nonStandardDiffFallsBackToDefault() {
        // Only indent diffs of 5 -> not in POSSIBLE_INDENTATIONS -> default 4
        IndentResult result = IndentDetective.analyze(new Document("     foo\n          bar"));
        assertEquals(true, result.isUseSpaces());
        assertEquals(4, result.getTabWidth());
    }

    @Test
    public void allTabsReturnsTabs() {
        IndentResult result = IndentDetective.analyze(new Document("\tfoo\n\t\tbar\n\t\t\tbaz"));
        assertEquals(false, result.isUseSpaces());
    }

    @Test
    public void emptyDocumentDefaultsToSpacesDefaultWidth() {
        IndentResult result = IndentDetective.analyze(new Document(""));
        assertEquals(true, result.isUseSpaces());
        assertEquals(4, result.getTabWidth());
    }

    @Test
    public void reinforcesConsistentIndentLevel() {
        // First jump establishes diff=2, subsequent same-level jumps reinforce it.
        String content =
                "foo\n" +        // indent 0
                "  bar\n" +      // indent 2, diff 2
                "    baz\n" +    // indent 4, diff 2
                "      qux\n";   // indent 6, diff 2
        IndentResult result = IndentDetective.analyze(new Document(content));
        assertEquals(true, result.isUseSpaces());
        assertEquals(2, result.getTabWidth());
    }
}
