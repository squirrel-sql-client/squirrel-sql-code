package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;

public class StringUtilitiesTest extends BaseSQuirreLTestCase {

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
    
    public void testGetTokenBeginIndex() {
        String sql = "select valid_from from dealer";
        int idx = StringUtilities.getTokenBeginIndex(sql, "from");
        assertEquals(18, idx);
        sql = "select from_date from dealer";
        idx = StringUtilities.getTokenBeginIndex(sql, "from");
        assertEquals(17, idx);
    }
    
    public void testChop() {
        String toChop = "(1,2,3)";
        String expAfterChop = "(1,2,3";
        
        String afterChop = StringUtilities.chop(toChop);
        assertEquals(expAfterChop, afterChop);
    }
}
