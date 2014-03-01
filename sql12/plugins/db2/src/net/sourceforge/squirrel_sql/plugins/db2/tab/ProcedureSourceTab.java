package net.sourceforge.squirrel_sql.plugins.db2.tab;

/*
 * Copyright (C) 2006 Rob Manning
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
import net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql;

/**
 * This class provides the necessary information to the parent tab to display the source for an DB2 stored 
 * procedure.
 */
public class ProcedureSourceTab extends FormattedSourceTab
{
	
	/** Object that contains methods for retrieving SQL that works for each DB2 platform */
	private final DB2Sql db2Sql;

	/**
	 * Constructor
	 * 
	 * @param hint
	 *        what the user sees on mouse-over tool-tip
	 * @param db2Sql
	 *           Object that contains methods for retrieving SQL that works for each DB2 platform
	 */
	public ProcedureSourceTab(String hint, String stmtSep, DB2Sql db2Sql) {
		super(hint);
		super.setCompressWhitespace(false);
		super.setupFormatter(stmtSep, null);
		this.db2Sql = db2Sql;
	}

	@Override
	protected String getSqlStatement()
	{
		return db2Sql.getProcedureSourceSql();
	}
}
