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
package net.sourceforge.squirrel_sql.client;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApplicationArgumentsTest {

    ApplicationArguments applicationArgumentsUnderTest = null;
    
    String[] _rawArgs = new String[] { "1", "2", "3", "4" };
    
    @Before
    public void setUp() throws Exception {
        ApplicationArguments.reset();
        ApplicationArguments.initialize(_rawArgs);
        applicationArgumentsUnderTest = ApplicationArguments.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        ApplicationArguments.reset();
    }

    @Test
    public final void testGetRawArguments() {
        String[] rawArgs = applicationArgumentsUnderTest.getRawArguments();
        assertEquals(_rawArgs, rawArgs);
        assertEquals("1", rawArgs[0]);
        assertEquals("2", rawArgs[1]);
        assertEquals("3", rawArgs[2]);
        assertEquals("4", rawArgs[3]);
    }
    
}
