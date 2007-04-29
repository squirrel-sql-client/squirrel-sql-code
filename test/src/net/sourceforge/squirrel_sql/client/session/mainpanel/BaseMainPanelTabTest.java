package net.sourceforge.squirrel_sql.client.session.mainpanel;
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
import static org.easymock.EasyMock.createMock;

import java.awt.Component;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.client.session.ISession;


public class BaseMainPanelTabTest extends BaseSQuirreLTestCase {

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    // Test to ensure that we allow sessions that are ending to be GC'd.
    public void testSessionCleanup() {
        MyBaseMainPanelTab tab = new MyBaseMainPanelTab();
        ISession session1 = createMock(ISession.class);
        tab.setSession(session1);
        ISession session2 = createMock(ISession.class);
        tab.sessionEnding(session2);
        assertEquals(session1, tab.getSession());
        tab.sessionEnding(session1);
        assertEquals(null, tab.getSession());
    }
    
    private class MyBaseMainPanelTab extends BaseMainPanelTab {

        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab#refreshComponent()
         */
        @Override
        protected void refreshComponent() {
            
        }

        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab#getComponent()
         */
        public Component getComponent() {
            return null;
        }

        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab#getHint()
         */
        public String getHint() {
            return null;
        }

        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab#getTitle()
         */
        public String getTitle() {
            return null;
        }
        
    }
}
