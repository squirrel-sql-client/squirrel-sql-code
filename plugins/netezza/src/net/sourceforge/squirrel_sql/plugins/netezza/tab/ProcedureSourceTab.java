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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

/**
 * This class provides the necessary information to the parent tab to display the source for a Netezza 
 * procedure.
 */
public class ProcedureSourceTab extends FormattedSourceTab
{
	/**
	 * Constructor
	 * 
	 * @param hint
	 *           what the user sees on mouse-over tool-tip
	 * @param stmtSep
	 *           the string to use to separate SQL statements
	 */
	public ProcedureSourceTab(String hint, String stmtSep)
	{
		super(hint);
		super.setupFormatter(stmtSep, null);
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

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab#processResult(java.lang.StringBuilder)
	 */
	@Override
	protected String processResult(StringBuilder buf)
	{
		return buf.toString().replace("/n", "\\n");
	}
	
}
