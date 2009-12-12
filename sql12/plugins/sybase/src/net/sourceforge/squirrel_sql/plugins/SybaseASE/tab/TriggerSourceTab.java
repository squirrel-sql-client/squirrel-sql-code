package net.sourceforge.squirrel_sql.plugins.SybaseASE.tab;
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
 * This class provides the necessary information to the parent tab to display the source for a Sybase trigger.
 */
public class TriggerSourceTab extends FormattedSourceTab
{        
	/**
	 * Constructor
	 * 
	 * @param hint
	 *        what the user sees on mouse-over tool-tip
	 * @param stmtSep
	 *        the string to use to separate SQL statements
	 */
	public TriggerSourceTab(String hint, String stmtSep) {
		super(hint);
		super.setCompressWhitespace(true);
		super.setupFormatter(stmtSep, null);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PSFormattedSourceTab#getSqlStatement()
	 */
	@Override
   protected String getSqlStatement()
   {
		return
      "SELECT trigger_defs.text " +
      "FROM sysobjects tables , sysobjects triggers, syscomments trigger_defs " +
      "where triggers.type = 'TR' " +
      "and triggers.id = trigger_defs.id " +
      "and triggers.deltrig = tables.id " +
      "and tables.loginame = ? " +
      // TODO: figure out how to get the name of the table that the trigger 
      //       is on. 
      //"and tables.name = ? " +
      "and triggers.name = ? ";
   }
	
	/**
    * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PSFormattedSourceTab#getBindValues()
    */
   @Override
   protected String[] getBindValues()
   {
   	final IDatabaseObjectInfo doi = getDatabaseObjectInfo();
   	return new String[] { doi.getCatalogName(), doi.getSimpleName() };
   }
}
