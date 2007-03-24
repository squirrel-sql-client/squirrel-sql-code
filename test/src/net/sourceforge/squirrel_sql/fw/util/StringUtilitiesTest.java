package net.sourceforge.squirrel_sql.fw.util;

import junit.framework.TestCase;

public class StringUtilitiesTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testJoin() {
        String[] parts = new String[] {"foo", "bar", "baz"};
        
        String joinedParts = StringUtilities.join(parts, null);
        
        assertEquals("foobarbaz", joinedParts);
        
        joinedParts = StringUtilities.join(parts, "|");
        
        assertEquals("foo|bar|baz", joinedParts);
        
        joinedParts = StringUtilities.join(parts, "");
        
        assertEquals("foobarbaz", joinedParts);
    }

    public void testSegment() {
        String[] segments = null;
        String longString = "part1part2part3part4";
        segments = StringUtilities.segment(longString, 1);
        assertEquals(segments.length, 20);
        segments = StringUtilities.segment(longString, 5);
        assertEquals(segments.length, 4);
        segments = StringUtilities.segment(longString, 9);
        assertEquals(segments.length, 3);
        segments = StringUtilities.segment(longString, 11);
        assertEquals(segments.length, 2);
        segments = StringUtilities.segment(longString, 20);
        assertEquals(segments.length, 1);
        /*
        for (int i = 0; i < segments.length; i++) {
            String string = segments[i];
            System.out.println("segment: |"+string+"|");
        }
        */
    }
}
