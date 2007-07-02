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
package net.sourceforge.squirrel_sql.client.mainframe.action;

import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.awt.Component;
import java.awt.Frame;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.IDialogUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SavePreferencesCommandTest {

    Frame mockFrame = createMock(Frame.class);
    IApplication mockApplication = createMock(IApplication.class);
    IDialogUtils mockDialogUtils = createMock(IDialogUtils.class);
    
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    // Null tests
    
    @Test (expected = IllegalArgumentException.class)
    public final void testSavePreferencesCommandNullApp() {
        new SavePreferencesCommand(null, mockFrame);
    }

    @Test (expected = IllegalArgumentException.class)
    public final void testSavePreferencesCommandNullFrame() {
        new SavePreferencesCommand(mockApplication, null);
    }

    // Other tests
    
    @Test 
    public final void testExecute() {
        mockApplication.saveApplicationState();
        mockDialogUtils.showOk(isA(Component.class), isA(String.class));
        
        replay(mockFrame);
        replay(mockApplication);
        replay(mockDialogUtils);
        
        
        SavePreferencesCommand command = 
            new SavePreferencesCommand(mockApplication, mockFrame);
        command.setDialogUtils(mockDialogUtils);
        command.execute();
        
        verify(mockFrame);
        verify(mockApplication);
        verify(mockDialogUtils);
        
    }

}
