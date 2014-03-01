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
package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This class displays the source of a INDEX.
 * @see http://www.dba-oracle.com/oracle_tips_dbms_metadata.htm
 * @author Stefan Willinger
 *
 */
public class IndexSourceTab extends FormattedSourceTab {
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexSourceTab.class);

	/** SQL that retrieves the source of a index
	 * @see http://www.dba-oracle.com/oracle_tips_dbms_metadata.htm 
	 */
	private static String SQL = "select dbms_metadata.get_ddl('INDEX',?,?) from dual";

	public IndexSourceTab()
	{
		// i18n[oracle.showIndexSource=Show index source]
		super(s_stringMgr.getString("oracle.showIndexSource"));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab#getSqlStatement()
	 */
	@Override
	protected String getSqlStatement() {
		return SQL;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab#getBindValues()
	 */
	@Override
	protected String[] getBindValues() {
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		return new String[]{doi.getSimpleName(), doi.getSchemaName()};
	}
	
	
}
