package net.sourceforge.squirrel_sql.plugins.sqlval;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;

public class ValidatorGlobalPreferencesTab implements IGlobalPreferencesPanel
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ValidatorGlobalPreferencesTab.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ValidatorGlobalPreferencesTab.class);
    
	/** Plugin preferences. */
	private final WebServicePreferences _prefs;

	/** Panel to display in the Global preferences dialog. */
	private PrefsPanel _myPanel;

	/** Application API. */
	private IApplication _app;
    
	/**
	 * Ctor.
	 *
	 * @param	prefs	Plugin preferences
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>LAFPlugin</TT>, or <TT>LAFRegister</TT> is <TT>null</TT>.
	 */
	public ValidatorGlobalPreferencesTab(WebServicePreferences prefs)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		_prefs = prefs;
	}

	/**
	 * Load panel with data from plugin preferences.
	 *
	 * @param	app	 Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>IApplication</TT> is <TT>null</TT>.
	 */
	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		((PrefsPanel)getPanelComponent()).loadData();
	}

   public void uninitialize(IApplication app)
   {
      
   }

   /**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return	the component to be displayed in the Preferences dialog.
	 */
	public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new PrefsPanel(_prefs);
		}
		return _myPanel;
	}

	/**
	 * User has pressed OK or Apply in the dialog so save data from
	 * panel.
	 */
	public void applyChanges()
	{
		_myPanel.save();
	}

	/**
	 * Return the title for this panel.
	 *
	 * @return	the title for this panel.
	 */
	public String getTitle()
	{
        // i18n[ValidatorGlobalPreferencesTab.title=SQL Validator]
		return s_stringMgr.getString("ValidatorGlobalPreferencesTab.title");
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return	the hint for this panel.
	 */
	public String getHint()
	{
        // i18n[ValidatorGlobalPreferencesTab.hint=Preferences for SQL validation]
		return s_stringMgr.getString("ValidatorGlobalPreferencesTab.hint");
	}

	static final class PrefsPanel extends JPanel
	{
		private AppPreferencesPanel _appPrefsPnl;

		private final WebServicePreferences _prefs;

		PrefsPanel(WebServicePreferences prefs)
		{
			super(new GridBagLayout());
			_prefs = prefs;
			createGUI(prefs);
		}

		private void loadData()
		{
			_appPrefsPnl.loadData();
		}

		private void save()
		{
			_appPrefsPnl.save();
		}

		private void createGUI(WebServicePreferences prefs)
		{
			_appPrefsPnl = new AppPreferencesPanel(prefs);

			setBorder(BorderFactory.createEmptyBorder());

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(1, 1, 1, 1);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			add(_appPrefsPnl, gbc);
		}
	}
}

