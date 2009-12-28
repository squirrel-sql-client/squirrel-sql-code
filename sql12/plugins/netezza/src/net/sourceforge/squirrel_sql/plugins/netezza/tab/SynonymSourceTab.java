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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This class provides the necessary information to the parent tab to display the source for a Netezza
 * synonym.
 */
public class SynonymSourceTab extends FormattedSourceTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SynonymSourceTab.class);
	
	public static interface i18n
	{
		// i18n[SynonymSourceTab.hint=Shows the source of the selected synonym]
		String hint = s_stringMgr.getString("SynonymSourceTab.hint");
	}
	
	/**
	 * Constructor
	 * 
	 * @param hint
	 *           what the user sees on mouse-over tool-tip
	 * @param stmtSep
	 *           the string to use to separate SQL statements
	 */
	public SynonymSourceTab(String stmtSep)
	{
		super(i18n.hint);
		super.setupFormatter(stmtSep, null);
		super.setCompressWhitespace(true);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PSFormattedSourceTab#getSqlStatement()
	 */
	@Override
	protected String getSqlStatement()
	{
		return 
		"SELECT " +
		"'create synonym ' || synonym_name || ' for ' || refobjname " +
		"FROM _v_synonym " +
		"where refdatabase = ? " +
		"and refschema = ? " +
		"and synonym_name like ? ";
	}

	/**
	 * Overridden as the super implementation binds just schemaname rather than both catalogname and schemaName
	 * as is used in Netezza.
	 * 
	 * @return a String array of bind variable values
	 */
	@Override
	protected String[] getBindValues()
	{
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		return new String[] { doi.getCatalogName(), doi.getSchemaName(), doi.getSimpleName() };
	}
	
}
