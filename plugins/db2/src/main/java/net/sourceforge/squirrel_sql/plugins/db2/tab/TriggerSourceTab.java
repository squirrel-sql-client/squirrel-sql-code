package net.sourceforge.squirrel_sql.plugins.db2.tab;
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
/**
 * This class provides the necessary information to the parent tab to  display the source for an DB2 trigger.
 */
public class TriggerSourceTab extends FormattedSourceTab
{
	/** SQL that retrieves the source of a trigger. */
	private final static String SQL =
        "select TEXT from SYSCAT.TRIGGERS " +
        "where TABSCHEMA = ? " +
        "and TRIGNAME = ? ";
 
	/** SQL that retrieves the source of a trigger on DB2 on OS/400. */
	private final static String OS2_400_SQL =
	    "select action_statement " +
	    "from qsys2.systriggers " +
	    "where trigger_schema = ? " +
	    "and trigger_name = ? ";
	
	/** a boolean value indicating whether or not this DB2 is on OS/400 */
	private boolean isOS2400 = false;

	/**
	 * Constructor
	 * 
	 * @param hint
	 *        what the user sees on mouse-over tool-tip
	 * @param isOS2400
	 *        a boolean value indicating whether or not this DB2 is on OS/400. 
	 * @param stmtSep        
	 *        the character that separates SQL statements
	 */
	public TriggerSourceTab(String hint, boolean isOS2400, String stmtSep)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null);
        this.isOS2400 = isOS2400;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PSFormattedSourceTab#getSqlStatement()
	 */
	@Override
   protected String getSqlStatement()
   {
		String sql = SQL;
		if (isOS2400) {
		    sql = OS2_400_SQL;
		}
	   return sql;
   }
}
