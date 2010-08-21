package net.sourceforge.squirrel_sql.plugins.netezza.tab;

/*
 * Copyright (C) 2009 Rob Manning
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
import net.sourceforge.squirrel_sql.fw.codereformat.ICodeReformator;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This class provides the necessary information to the parent tab to display the source for a Netezza 
 * procedure.  It uses a custom formatter class (NetezzaProcedureFormator) that can handle formatting Netezza 
 * stored procedures (and not much else).
 */
public class ProcedureSourceTab extends FormattedSourceTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ProcedureSourceTab.class);
	
	public static interface i18n
	{
		// i18n[ProcedureSourceTab.hint=Shows the source of the selected procedure]
		String hint = s_stringMgr.getString("ProcedureSourceTab.hint");
	}	
	
	/**
	 * Constructor
	 * 
	 * @param stmtSep
	 *           the string to use to separate SQL statements
	 */
	public ProcedureSourceTab(String stmtSep)
	{
		super(i18n.hint);
		ICodeReformator formator = new NetezzaProcedureFormator(stmtSep);
		super.setupFormatter(formator, stmtSep, null);
		super.setCompressWhitespace(true);
		// Netezza procedure definitions include the statement separator.
		super.appendSeparator = false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PSFormattedSourceTab#getSqlStatement()
	 */
	@Override
	protected String getSqlStatement()
	{
		return 
		"SELECT " +
		"'create or replace procedure ' || proceduresignature || ' returns ' || returns || " +
		"' LANGUAGE NZPLSQL AS BEGIN_PROC ' || proceduresource || ' END_PROC;' " +
		"FROM _v_procedure " +
		"WHERE owner = ? " +
		"and procedure = ? ";
	}

	/**
    * Overridden as the super implementation binds schemaname rather than catalogname as is used
    * in Netezza.
    * 
    * @return a String array of bind variable values
    */
	@Override
   protected String[] getBindValues()
   {
   	final IDatabaseObjectInfo doi = getDatabaseObjectInfo();
   	return new String[] { doi.getSchemaName(), doi.getSimpleName() };		
   }
	
}
