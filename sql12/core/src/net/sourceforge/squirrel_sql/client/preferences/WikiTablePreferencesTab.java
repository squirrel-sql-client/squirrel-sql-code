/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.preferences;

import java.awt.Component;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Preferences for WIKI table configurations.
 * @author Stefan Willinger
 * 
 */
public class WikiTablePreferencesTab implements IGlobalPreferencesPanel {

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(WikiTablePreferencesTab.class);

	static interface i18n {

		// i18n[WikiTablePreferencesTab.title=Wiki Table]
		String TITLE = s_stringMgr.getString("WikiTablePreferencesTab.title");

		// i18n[WikiTablePreferencesTab.hint=Configure exports for a Wiki table]
		String HINT = s_stringMgr.getString("WikiTablePreferencesTab.hint");

	}

	private WikiTablePreferencesPanel myPanel;
	private IApplication app;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
	 */
	@Override
	public void applyChanges() {
		myPanel.applyChanges();
		app.savePreferences(PreferenceType.WIKI_CONFIGURATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getTitle()
	 */
	@Override
	public String getTitle() {
		return i18n.TITLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getHint()
	 */
	@Override
	public String getHint() {
		return i18n.HINT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.squirrel_sql.client.util.IOptionPanel#getPanelComponent()
	 */
	@Override
	public Component getPanelComponent() {
		if (myPanel == null){
			myPanel = new WikiTablePreferencesPanel();
		}
		return myPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel
	 * #initialize(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	@Override
	public void initialize(IApplication app) {
		if (app == null){
			throw new IllegalArgumentException("Null IApplication passed");
		}

		this.app = app;

		WikiTablePreferencesPanel panel = (WikiTablePreferencesPanel)getPanelComponent();
		panel.setApplication(app);
		panel.loadData(app.getSquirrelPreferences());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel
	 * #uninitialize(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	@Override
	public void uninitialize(IApplication app) {
		// nothing

	}

}
