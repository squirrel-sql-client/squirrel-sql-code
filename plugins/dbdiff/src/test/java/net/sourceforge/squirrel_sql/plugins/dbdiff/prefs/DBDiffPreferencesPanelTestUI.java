/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbdiff.prefs;

import static org.easymock.EasyMock.expect;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.plugins.dbdiff.DBDiffPlugin;
import utils.EasyMockHelper;

public class DBDiffPreferencesPanelTestUI
{

	/**
	 * @param args
	 * @throws PluginException
	 */
	public static void main(String[] args) throws PluginException
	{
		ApplicationArguments.initialize(new String[] {});

		final EasyMockHelper mockHelper = new EasyMockHelper();

		// mocks
		final IApplication mockApplication = mockHelper.createMock(IApplication.class);
		final SquirrelPreferences mockPreferences = mockHelper.createMock(SquirrelPreferences.class);

		expect(mockApplication.getSquirrelPreferences()).andStubReturn(mockPreferences);

		mockHelper.replayAll();

		final JFrame frame = new JFrame("Test DBDiffPreferencesPanel");

		final DefaultPluginPreferencesManager manager = new DefaultPluginPreferencesManager();
		manager.initialize(new DBDiffPlugin(), DBDiffPreferenceBean.class);

		frame.getContentPane().add(new DBDiffPreferencesPanel(manager));
		frame.setSize(600, 600);
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
