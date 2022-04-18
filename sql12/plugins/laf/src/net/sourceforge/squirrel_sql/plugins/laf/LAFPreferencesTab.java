package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2001-2006 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * The Look and Feel panel for the Global Preferences dialog.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFPreferencesTab implements IGlobalPreferencesPanel
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(LAFPreferencesTab.class);

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(LAFPreferencesTab.class);

	/** The plugin. */
	private LAFPlugin _plugin;

	/** Plugin preferences object. */
	//private LAFPreferences _prefs;

	/** Look and Feel register. */
	private LAFRegister _lafRegister;

	/** LAF panel to display in the Global preferences dialog. */
	private LAFPreferencesPanel _myPanel;

	/**
	 * Ctor.
	 *
	 * @param	plugin			The LAF plugin.
	 * @param	lafRegister		Look and Feel register.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>LAFPlugin</TT>, or <TT>LAFRegister</TT> is <TT>null</TT>.
	 */
	public LAFPreferencesTab(LAFPlugin plugin, LAFRegister lafRegister)
	{
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null LAFPlugin passed");
		}
		if (lafRegister == null)
		{
			throw new IllegalArgumentException("Null LAFRegister passed");
		}
		_plugin = plugin;
		//_prefs = plugin.getLAFPreferences();
		_lafRegister = lafRegister;
	}

	/**
	 * Load panel with data from plugin preferences.
	 *
	 * @param	app	Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>IApplication</TT> is <TT>null</TT>.
	 */
	public void initialize(IApplication app)
	{
		getPanelComponent().loadData();
	}

   public void uninitialize(IApplication app)
   {
      
   }

   /**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return  the component to be displayed in the Preferences dialog.
	 */
	public LAFPreferencesPanel getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new LAFPreferencesPanel(_plugin, _lafRegister);
		}
		return _myPanel;
	}

	/**
	 * User has pressed OK or Apply in the dialog so save data from
	 * panel.
	 */
	public void applyChanges()
	{
		_myPanel.applyChanges();
	}

	/**
	 * Return the title for this panel.
	 *
	 * @return  the title for this panel.
	 */
	public String getTitle()
	{
		return LAFPreferencesPanel.LAFPreferencesPanelI18n.TAB_TITLE;
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return  the hint for this panel.
	 */
	public String getHint()
	{
		return LAFPreferencesPanel.LAFPreferencesPanelI18n.TAB_HINT;
	}

}
