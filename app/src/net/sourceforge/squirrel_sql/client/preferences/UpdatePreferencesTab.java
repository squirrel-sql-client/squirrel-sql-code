package net.sourceforge.squirrel_sql.client.preferences;
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
import java.awt.Component;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class UpdatePreferencesTab implements IGlobalPreferencesPanel
{

   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(UpdatePreferencesTab.class);
   
   static interface i18n {
      
      // i18n[UpdatePreferencesTab.title=Update]
      String TITLE = s_stringMgr.getString("UpdatePreferencesTab.title");

      // i18n[UpdatePreferencesTab.hint=Software Update Settings]
      String HINT = s_stringMgr.getString("UpdatePreferencesTab.hint");

   }   
   
	/** Panel to be displayed in preferences dialog. */
	private UpdatePreferencesPanel _myPanel;

	/** Application API. */
	private IApplication _app;
	
	
	/**
	 * Default ctor.
	 */
	public UpdatePreferencesTab()
	{
		super();
	}

	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		UpdatePreferencesPanel updatePanel = (UpdatePreferencesPanel)getPanelComponent();
		updatePanel.setApplication(_app);
		updatePanel.loadData(_app.getSquirrelPreferences());
	}

   public void uninitialize(IApplication app)
   {
      
   }

   public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new UpdatePreferencesPanel();
		}
		return _myPanel;
	}

	public synchronized void applyChanges()
	{
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle()
	{
		return i18n.TITLE;
	}

	public String getHint()
	{
		return i18n.HINT;
	}


}
