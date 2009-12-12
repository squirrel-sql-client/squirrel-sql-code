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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This class provides the necessary information to the parent tab to display the source for an DB2 stored 
 * procedure.
 */
public class ProcedureSourceTab extends FormattedSourceTab
{
	private static interface i18n
	{
		StringManager s_stringMgr = StringManagerFactory.getStringManager(ProcedureSourceTab.class);

		// i18n[ProcedureSourceTab.cLanguageProcMsg=This is a C-language routine. The
		// source code is unavailable.]
		String C_LANGUAGE_PROC_MSG = s_stringMgr.getString("ProcedureSourceTab.cLanguageProcMsg");
	}

	/** SQL that retrieves the source of a stored procedure. */
	private static String SQL =
		 "select " +
		 "    case " +
		 "        when language = 'C' then '" +i18n.C_LANGUAGE_PROC_MSG+"' " +
		 "        else text " +
		 "    end as text " +
		 "from SYSCAT.PROCEDURES " +
		 "where PROCSCHEMA = ? " +
		 "and PROCNAME = ? ";
	    
	 /** SQL that retrieves the source of a stored procedure on OS/400 */ 
	private static String OS_400_SQL =
		 "select routine_definition from qsys2.sysroutines " +
		 "where routine_schema = ? " +
		 "and routine_name = ? ";
	
	/** boolean to indicate whether or not this session is OS/400 */
	private boolean isOS400 = false;

	/**
	 * Constructor
	 * 
	 * @param hint
	 *        what the user sees on mouse-over tool-tip
	 * @param isOS400
	 *        whether or not the session is OS/400
	 */
	public ProcedureSourceTab(String hint, boolean isOS400, String stmtSep) {
		super(hint);
		super.setCompressWhitespace(false);
		super.setupFormatter(stmtSep, null);
		this.isOS400 = isOS400;
	}

	@Override
	protected String getSqlStatement()
	{
		String sql = SQL;
		if (isOS400)
		{
			sql = OS_400_SQL;
		}
		return sql;
	}
}
