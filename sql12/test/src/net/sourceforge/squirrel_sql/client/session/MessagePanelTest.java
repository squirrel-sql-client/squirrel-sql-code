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
package net.sourceforge.squirrel_sql.client.session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for MessagePanel
 * 
 * @author manningr 
 */
public class MessagePanelTest {

    MessagePanel messagePanelUnderTest = null;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        messagePanelUnderTest = new MessagePanel();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        messagePanelUnderTest = null;
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.client.session.MessagePanel#addToMessagePanelPopup(javax.swing.Action)}.
     */
    @Test (expected = IllegalArgumentException.class)
    public final void testAddToMessagePanelPopup() {
        messagePanelUnderTest.addToMessagePanelPopup(null);
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.client.session.MessagePanel#showMessage(java.lang.Throwable, net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)}.
     */
    @Test (expected = IllegalArgumentException.class)
    public final void testShowMessageThrowableExceptionFormatter() {
        messagePanelUnderTest.showMessage(null, null);
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.client.session.MessagePanel#showErrorMessage(java.lang.Throwable, net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)}.
     */
    @Test (expected = IllegalArgumentException.class)
    public final void testShowErrorMessageThrowableExceptionFormatter() {
        messagePanelUnderTest.showErrorMessage(null, null);
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.client.session.MessagePanel#showMessage(java.lang.String)}.
     */
    @Test (expected = IllegalArgumentException.class)
    public final void testShowMessageString() {
        messagePanelUnderTest.showMessage(null);
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.client.session.MessagePanel#showWarningMessage(java.lang.String)}.
     */
    @Test (expected = IllegalArgumentException.class)
    public final void testShowWarningMessage() {
        messagePanelUnderTest.showWarningMessage(null);
    }

    /**
     * Test method for {@link net.sourceforge.squirrel_sql.client.session.MessagePanel#showErrorMessage(java.lang.String)}.
     */
    @Test (expected = IllegalArgumentException.class)
    public final void testShowErrorMessageString() {
        messagePanelUnderTest.showErrorMessage(null);
    }

}
