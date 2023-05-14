package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

class GeneralPreferencesPanel implements IGlobalPreferencesPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GeneralPreferencesPanel.class);

	/** Panel to be displayed in preferences dialog. */
	private GeneralPreferencesGUI _myPanel;
	private JScrollPane _myScrollPane;


	/**
	 * Default ctor.
	 */
	public GeneralPreferencesPanel()
	{
	}

	@Override
	public void initialize(IApplication app)
	{
		getPanelComponent();
      _myPanel.loadData(app.getSquirrelPreferences());

   }

   @Override
   public void uninitialize(IApplication app)
   {
   }

   @Override
   public Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new GeneralPreferencesGUI();
         _myScrollPane = new JScrollPane(_myPanel);

			GUIUtils.forceScrollToBegin(_myScrollPane,1);
      }
		return _myScrollPane;
	}

	@Override
	public void applyChanges()
	{
		_myPanel.applyChanges(Main.getApplication().getSquirrelPreferences());
	}

	@Override
	public String getTitle()
	{
		return s_stringMgr.getString("GeneralPreferencesPanel.tabtitle");
	}

	@Override
	public String getHint()
	{
		return s_stringMgr.getString("GeneralPreferencesPanel.tabhint");
	}

}
