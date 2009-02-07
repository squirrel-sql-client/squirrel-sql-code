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
package net.sourceforge.squirrel_sql.fw.dialects;

import java.util.HashMap;

import org.antlr.stringtemplate.StringTemplate;

/**
 * An extension that provides MySQL 5 support for views.
 * 
 * @author manningr
 */
public class MySQL5DialectExt extends MySQLDialectExt
{

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#getCreateViewSQL(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		final StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_ONE);

		// "CREATE VIEW $viewName$ " +
		// "AS $selectStatement$ $with$ $checkOptionType$ $checkOption$";
		final HashMap<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put(ST_VIEW_NAME_KEY, viewName);
		valuesMap.put(ST_SELECT_STATEMENT_KEY, definition);
		// check option not supported

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#getDialectType()
	 */
	@Override
	public DialectType getDialectType()
	{
		return DialectType.MYSQL5;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#getDropViewSQL(java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropViewSQL(viewName, cascade, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#getRenameViewSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final String renameClause = DialectUtils.RENAME_CLAUSE;
		final String commandPrefix = DialectUtils.ALTER_TABLE_CLAUSE;

		String renameViewSql =
			DialectUtils.getRenameViewSQL(commandPrefix, renameClause, oldViewName, newViewName, qualifier,
				prefs, this);

		renameViewSql = DialectUtils.stripQuotesFromIdentifier(this, newViewName, renameViewSql);

		return new String[] { renameViewSql };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#getViewDefinitionSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final StringBuilder result = new StringBuilder();
		result.append("SELECT view_definition ");
		result.append("FROM information_schema.views ");
		result.append("WHERE table_name = '");
		result.append(viewName);
		result.append("' ");
		result.append("AND table_schema = '");
		result.append(qualifier.getCatalog());
		result.append("'");
		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#supportsCreateView()
	 */
	@Override
	public boolean supportsCreateView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#supportsDropView()
	 */
	@Override
	public boolean supportsDropView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#supportsProduct(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null || databaseProductVersion == null) { return false; }
		if (!databaseProductName.trim().toLowerCase().startsWith("mysql")) { return false; }
		return databaseProductVersion.startsWith("5");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#supportsRenameView()
	 */
	@Override
	public boolean supportsRenameView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#supportsViewDefinition()
	 */
	@Override
	public boolean supportsViewDefinition()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#supportsCheckOptionsForViews()
	 */
	@Override
	public boolean supportsCheckOptionsForViews()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.MySQLDialectExt#getDisplayName()
	 */
	@Override
	public String getDisplayName()
	{
		return "MySQL5";
	}

}
