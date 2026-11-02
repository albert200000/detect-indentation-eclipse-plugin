package org.albert200000.detectindentation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IndentResultTest {

    @Test
    public void returnsConstructorValues() {
        IndentResult result = new IndentResult(true, 2);
        assertEquals(true, result.isUseSpaces());
        assertEquals(2, result.getTabWidth());
    }

    @Test
    public void tabStyleToString() {
        IndentResult result = new IndentResult(false, 8);
        assertEquals("Tabs (width: 8)", result.toString());
    }

    @Test
    public void spacesStyleToString() {
        IndentResult result = new IndentResult(true, 4);
        assertEquals("Spaces: 4)", result.toString());
    }
}
