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

}
