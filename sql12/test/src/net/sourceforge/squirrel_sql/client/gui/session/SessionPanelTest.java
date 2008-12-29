/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.client.gui.session;


import static org.easymock.EasyMock.expect;

import java.awt.Font;

import javax.swing.ImageIcon;

import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;

public class SessionPanelTest extends AbstractSerializableTest
{
	
	ISession mockSession = mockHelper.createMock(ISession.class);
	IApplication mockApplication = mockHelper.createMock(IApplication.class);
	IIdentifier mockIdentifier = mockHelper.createMock(IIdentifier.class);
	SquirrelPreferences mockPreferences = mockHelper.createMock(SquirrelPreferences.class);
	SessionProperties mockSessionProperties = mockHelper.createMock(SessionProperties.class);
	FontInfo mockFontInfo = mockHelper.createMock(FontInfo.class);
	Font mockFont = mockHelper.createMock(Font.class);
	SquirrelResources mockSquirrelResources = mockHelper.createMock(SquirrelResources.class);
	ImageIcon mockImageIcon = mockHelper.createMock(ImageIcon.class);
	IMainPanelFactory mockMainPanelFactory = mockHelper.createMock(IMainPanelFactory.class);
	
	@Before
	public void setUp() throws Exception
	{
		// mockSession
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockIdentifier);
		expect(mockSession.getProperties()).andStubReturn(mockSessionProperties);
		
		// mockPreferences
		expect(mockPreferences.getUseScrollableTabbedPanes()).andStubReturn(false);
		
		// mockSessionProperties
		expect(mockSessionProperties.getFontInfo()).andStubReturn(mockFontInfo);
		
		// mockFontInfo
		expect(mockFontInfo.createFont()).andStubReturn(mockFont);
		
		// mockApplication
		expect(mockApplication.getResources()).andStubReturn(mockSquirrelResources);
		
		// mockSquirrelResources
		expect(mockSquirrelResources.getIcon(EasyMock.isA(String.class))).andStubReturn(mockImageIcon);
		
		mockHelper.replayAll();
		UIFactory.initialize(mockPreferences, mockApplication);
		
		serializableToTest = new SessionPanel(mockSession);
		((SessionPanel)serializableToTest).setMainPanelFactory(mockMainPanelFactory);
	}

	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
		mockHelper.verifyAll();
	}
		
}
