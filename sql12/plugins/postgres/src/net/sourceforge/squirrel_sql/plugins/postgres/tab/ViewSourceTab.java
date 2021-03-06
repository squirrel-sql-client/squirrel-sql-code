package net.sourceforge.squirrel_sql.plugins.postgres.tab;

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

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

/**
 * This class will display the source for a view.  This will work for databases that support the SQL standard
 * infomation_schema.views table.
 */
public class ViewSourceTab extends FormattedSourceTab
{
	/**
	 * Constructor
	 * 
	 * @param hint
	 *        what the user sees on mouse-over tool-tip
	 * @param stmtSep
	 *        the string to use to separate SQL statements
	 */
	public ViewSourceTab(String hint, String stmtSep)
	{
		super(hint);
		super.setCompressWhitespace(true);
		super.setupFormatter(stmtSep, null);
		super.setAppendSeparator(false);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PSFormattedSourceTab#getSqlStatement()
	 */
	@Override
   protected String getSqlStatement()
   {
   	// This was the version before bug #1306
//		return
//		"select view_definition " +
//		"from information_schema.views " +
//		"where table_schema = ? " +
//		"and table_name = ? ";

		return "select pg_get_viewdef(?::regclass::oid)";
   }

	@Override
	protected String[] getBindValues()
	{
		IDatabaseObjectInfo databaseObjectInfo = getDatabaseObjectInfo();

		//return new String[]{databaseObjectInfo.getSchemaName() + "." + databaseObjectInfo.getSimpleName()};
		return new String[]{databaseObjectInfo.getQualifiedName()};
	}
}
