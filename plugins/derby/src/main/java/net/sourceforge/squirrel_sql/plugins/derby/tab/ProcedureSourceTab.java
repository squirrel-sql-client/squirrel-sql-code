package net.sourceforge.squirrel_sql.plugins.derby.tab;

/*
 * Copyright (C) 2009 Glenn Hobbs
 * bassnfool2@users.sourceforge.net
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

public class ProcedureSourceTab  extends FormattedSourceTab {

	private static String SQL = 
		"SELECT " +
		"'CREATE PROCEDURE '||SCHEMAA.SCHEMANAME||'.'||ALIAS.ALIAS||'\n '|| SUBSTR " +
		"( " +
		"   CAST(ALIASINFO AS VARCHAR(4000)),LOCATE('(',CAST(ALIASINFO AS VARCHAR(4000))) " +
		") " +
		"||'\n '|| 'EXTERNAL NAME '''||ALIAS.JAVACLASSNAME||'.'||SUBSTR " +
		"( " +
		"   CAST(ALIASINFO AS VARCHAR(4000)), " +
		"   1, " +
		"   LOCATE('(',CAST(ALIASINFO AS VARCHAR(4000)))-1 " +
		") " +
		"||'''' " +
		"FROM SYS.SYSALIASES ALIAS, SYS.SYSSCHEMAS SCHEMAA " +
		"WHERE ALIAS.SCHEMAID = SCHEMAA.SCHEMAID " +
		"and SCHEMAA.SCHEMANAME = ? " +
		"AND ALIAS = ? ";	
	
	public ProcedureSourceTab(String hint, String stmtSep) {
		super(hint);
		super.setupFormatter(stmtSep, null);
	}

	@Override
	protected String getSqlStatement()
	{
		return SQL;
	}

}
