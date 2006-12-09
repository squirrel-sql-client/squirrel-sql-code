package net.sourceforge.squirrel_sql.fw.gui;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class GUIUtilsTest extends TestCase {

    static final String LENGTH_10 = "123456789 ";
    static final String LENGTH_20 = LENGTH_10 + LENGTH_10;
    static final String LENGTH_40 = LENGTH_20 + LENGTH_20;
    static final String LENGTH_80 = LENGTH_40 + LENGTH_40;
    static final String LENGTH_160 = LENGTH_80 + LENGTH_80;

    public void testGetWrappedLine() {
        ApplicationArguments.initialize(new String[] {});
        String wrappedLine = GUIUtils.getWrappedLine(LENGTH_80, 40);
        String[] parts = wrappedLine.split("\\n");
        assertEquals("number of newlines in word-wrapped string" , 2, parts.length);
        
        wrappedLine = GUIUtils.getWrappedLine(LENGTH_80, 20);
        parts = wrappedLine.split("\\n");
        assertEquals("number of newlines in word-wrapped string" , 4, parts.length);
        assertEquals("length of parts[0]", 20, parts[0].length());
        assertEquals("length of parts[1]", 20, parts[1].length());
        assertEquals("length of parts[2]", 20, parts[2].length());
        assertEquals("length of parts[3]", 20, parts[3].length());
        
        
        wrappedLine = GUIUtils.getWrappedLine(LENGTH_20, 15);
        parts = wrappedLine.split("\\n");
        assertEquals("number of newlines in word-wrapped string" , 2, parts.length);
        assertEquals("length of parts[0]", 10, parts[0].length());
        assertEquals("length of parts[1]", 10, parts[1].length());
        
        wrappedLine = GUIUtils.getWrappedLine(LENGTH_20, 40);
        parts = wrappedLine.split("\\n");
        assertEquals("number of newlines in word-wrapped string" , 1, parts.length);
        assertEquals("length of parts[0]", 20, parts[0].length());
        
        wrappedLine = GUIUtils.getWrappedLine(LENGTH_160, 80);
        parts = wrappedLine.split("\\n");
        assertEquals("number of newlines in word-wrapped string" , 2, parts.length);
        assertEquals("length of parts[0]", 80, parts[0].length());
        assertEquals("length of parts[1]", 80, parts[1].length());
        
        wrappedLine = GUIUtils.getWrappedLine(LENGTH_10, 5);
        parts = wrappedLine.split("\\n");
        assertEquals("number of newlines in word-wrapped string" , 1, parts.length);
    }
}
