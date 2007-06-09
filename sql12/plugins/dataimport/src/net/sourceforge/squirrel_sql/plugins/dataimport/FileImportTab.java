package net.sourceforge.squirrel_sql.plugins.dataimport;
/*
 * Copyright (C) 2001 Like Gao
 * lgao@gmu.edu
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
import java.awt.Component;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

@Deprecated
public class FileImportTab extends BaseMainPanelTab {
	 /**
	  * This interface defines locale specific strings. This should be
	  * replaced with a property file.
	  */

	 private static final StringManager s_stringMgr =
		 StringManagerFactory.getStringManager(FileImportTab.class);


	 private interface i18n {
		 // i18n[dataimport.importData=Import Data]
		 String TITLE = s_stringMgr.getString("dataimport.importData");
		 // i18n[dataimport.importCsv=Import csv files into database]
		  String DESC = s_stringMgr.getString("dataimport.importCsv");
	 }

	FileImportTab(ISession session) {
		super();
		setSession(session);
	}

	/**
	 * @see BaseMainPanelTab#refreshComponent()
	 */
	protected void refreshComponent() {
	}
	/**
	 * @see IMainPanelTab#getTitle()
	 */
	public String getTitle() {
		return i18n.TITLE;
	}

	/**
	 * @see IMainPanelTab#getHint()
	 */
	public String getHint() {
		return i18n.DESC;
	}

	/**
	 * @see IMainPanelTab#getComponent()
	 */
	public Component getComponent() {
		return new FileImport(getSession());
	}
}

