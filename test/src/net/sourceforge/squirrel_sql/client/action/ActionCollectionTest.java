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
package net.sourceforge.squirrel_sql.client.action;

import static org.easymock.classextension.EasyMock.replay;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.client.IApplication;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActionCollectionTest extends BaseSQuirreLJUnit4TestCase {

    ActionCollection actionCollectionUnderTest = null;
    IApplication mockApplication = null;
    Action mockAction = null;
    
    @Before
    public void setUp() throws Exception {
        mockApplication = AppTestUtil.getEasyMockApplication(false, true, null);
        mockAction = EasyMock.createMock(Action.class);
        
        replay(mockAction);
        
        actionCollectionUnderTest = new ActionCollection(mockApplication);
    }

    @After
    public void tearDown() throws Exception {
        actionCollectionUnderTest = null;
    }

    // Null tests 

    @Test (expected = IllegalArgumentException.class )
    public final void testAddNull() {
        actionCollectionUnderTest.add(null);            
    }
    
    @Test (expected = IllegalArgumentException.class )
    public final void testEnableAction() {
        actionCollectionUnderTest.enableAction(null, true);            
    }
    
}
