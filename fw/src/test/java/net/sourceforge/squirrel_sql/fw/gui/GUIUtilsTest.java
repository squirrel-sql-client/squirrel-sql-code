package net.sourceforge.squirrel_sql.fw.gui;

import junit.framework.TestCase;

public class GUIUtilsTest extends TestCase {

    static final String LENGTH_10 = "123456789 ";
    static final String LENGTH_20 = LENGTH_10 + LENGTH_10;
    static final String LENGTH_40 = LENGTH_20 + LENGTH_20;
    static final String LENGTH_80 = LENGTH_40 + LENGTH_40;
    static final String LENGTH_160 = LENGTH_80 + LENGTH_80;

    public void testGetWrappedLine() {
        assertEquality(LENGTH_80, 40, 2, trimLength(LENGTH_40));
        assertEquality(LENGTH_80, 20, 4, trimLength(LENGTH_20));
        assertEquality(LENGTH_20, 15, 2, trimLength(LENGTH_10));
        // Here the max line length == original line size - so it doesn't get 
        // trimmed of white space.
        assertEquality(LENGTH_20, 40, 1, LENGTH_20.length());
        assertEquality(LENGTH_160, 80, 2, trimLength(LENGTH_80));
        assertEquality(LENGTH_10, 5, 1, -1);
    }
    
    // Helpers

    private void assertEquality(String origLine, 
                                int maxLineSize,
                                int expPartsArrayLength,
                                int expPartLenth) 
    {
        String wrappedLine = GUIUtils.getWrappedLine(origLine, maxLineSize);
        String[] parts = wrappedLine.split("\\n");
        assertEquals("number of newlines in word-wrapped string" , 
                     expPartsArrayLength, 
                     parts.length);
        if (expPartLenth != -1) {
            for (int i = 0; i < parts.length; i++) {
                assertEquals("length of parts["+i+"]", 
                             expPartLenth , 
                             parts[0].length());
            }
        }
    }
    
    private int trimLength(String s) {
        return s.trim().length();
    }
}
