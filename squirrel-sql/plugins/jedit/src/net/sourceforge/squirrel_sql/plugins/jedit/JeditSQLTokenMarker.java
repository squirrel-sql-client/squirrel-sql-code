package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.KeywordMap;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SQLTokenMarker;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.Token;

// :TODO binary, array

public class JeditSQLTokenMarker extends SQLTokenMarker
{
	private static void addKeywords(DatabaseMetaData dmd, KeywordMap keywords)
	{
		keywords.add("ALL", Token.KEYWORD1);
		keywords.add("ALTER", Token.KEYWORD1);
		keywords.add("AND", Token.KEYWORD1);
		keywords.add("AS", Token.KEYWORD1);
		keywords.add("ASC", Token.KEYWORD1);
		keywords.add("BETWEEN", Token.KEYWORD1);
		keywords.add("BY", Token.KEYWORD1);
		keywords.add("CASCADE", Token.KEYWORD1);
		keywords.add("COUNT", Token.KEYWORD1);
		keywords.add("CREATE", Token.KEYWORD1);
		keywords.add("DEFAULT", Token.KEYWORD1);
		keywords.add("DELETE", Token.KEYWORD1);
		keywords.add("DESC", Token.KEYWORD1);
		keywords.add("DISTINCT", Token.KEYWORD1);
		keywords.add("DROP", Token.KEYWORD1);
		keywords.add("EXISTS", Token.KEYWORD1);
		keywords.add("FROM", Token.KEYWORD1);
		keywords.add("GRANT", Token.KEYWORD1);
		keywords.add("GROUP", Token.KEYWORD1);
		keywords.add("HAVING", Token.KEYWORD1);
		keywords.add("IN", Token.KEYWORD1);
		keywords.add("INDEX", Token.KEYWORD1);
		keywords.add("INSERT", Token.KEYWORD1);
		keywords.add("INTO", Token.KEYWORD1);
		keywords.add("IS", Token.KEYWORD1);
		keywords.add("LIKE", Token.KEYWORD1);
		keywords.add("NULL", Token.KEYWORD1);
		keywords.add("OR", Token.KEYWORD1);
		keywords.add("ORDER", Token.KEYWORD1);
		keywords.add("OUTER", Token.KEYWORD1);
		keywords.add("SELECT", Token.KEYWORD1);
		keywords.add("TABLE", Token.KEYWORD1);
		keywords.add("WHERE", Token.KEYWORD1);
		keywords.add("UNIQUE", Token.KEYWORD1);
		keywords.add("UNION", Token.KEYWORD1);
		keywords.add("UPDATE", Token.KEYWORD1);
		StringBuffer buf = new StringBuffer();
		try
		{
			buf.append(dmd.getSQLKeywords());
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}
		StringTokenizer strtok = new StringTokenizer(buf.toString(), ",");
		while (strtok.hasMoreTokens())
		{
			keywords.add(strtok.nextToken().trim(), Token.KEYWORD1);
		}
		try
		{
			addSingleKeyword(dmd.getCatalogTerm(), keywords);
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}
		try
		{
			addSingleKeyword(dmd.getSchemaTerm(), keywords);
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}
		try
		{
			addSingleKeyword(dmd.getProcedureTerm(), keywords);
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}
	}

	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(JeditSQLTokenMarker.class);

	private KeywordMap _keywords = new KeywordMap(true);

	// Keyword 1 = keywords
	// Keyword 2 = data types
	// Keyword 3 = functions.

	public JeditSQLTokenMarker(SQLConnection conn)
	{
		super(createKeywordMap(conn), false);
		_keywords = SQUIRREL_getKeywordMap();
		try
		{
			_dmd = conn.getMetaData();
		}
		catch (Exception ex)
		{
			s_log.error("Error retrieving metadata", ex);
		}
	}

	private static KeywordMap createKeywordMap(SQLConnection conn)
	{
		KeywordMap keywords = new KeywordMap(true);
		try
		{
			final DatabaseMetaData dmd = conn.getMetaData();
			addKeywords(dmd, keywords);
			addDataTypes(dmd, keywords);
			addFunctions(dmd, keywords);

			// TODO: should be optional for LARGE databases??
			addTableNames(dmd, keywords);
		}
		catch (Throwable ex)
		{
			s_log.error("Error occured creating keyword map", ex);
		}
		return keywords;
	}

	private static void addTableNames(DatabaseMetaData dmd, KeywordMap keywords)
	{
		try
		{
			ResultSet rs = dmd.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next())
			{
				keywords.add(rs.getString(3), Token.TABLE);
			}
			rs.close();
		}
		catch (Exception e)
		{
			s_log.debug("failed to load table names into the keywordmap", e);
		}
	}

	private static void addDataTypes(DatabaseMetaData dmd, KeywordMap keywords)
	{
		keywords.add("BIT", Token.KEYWORD2);
		keywords.add("BLOB", Token.KEYWORD2);
		keywords.add("BOOLEAN", Token.KEYWORD2);
		keywords.add("CHAR", Token.KEYWORD2);
		keywords.add("CHARACTER", Token.KEYWORD2);
		keywords.add("CLOB", Token.KEYWORD2);
		keywords.add("DATE", Token.KEYWORD2);
		keywords.add("DECIMAL", Token.KEYWORD2);
		keywords.add("DOUBLE", Token.KEYWORD2);
		keywords.add("FLOAT", Token.KEYWORD2);
		keywords.add("INTEGER", Token.KEYWORD2);
		keywords.add("INTERVAL", Token.KEYWORD2);
		keywords.add("NCHAR", Token.KEYWORD2);
		keywords.add("NCLOB", Token.KEYWORD2);
		keywords.add("NUMERIC", Token.KEYWORD2);
		keywords.add("PRECISION", Token.KEYWORD2);
		keywords.add("REAL", Token.KEYWORD2);
		keywords.add("SMALLINT", Token.KEYWORD2);
		keywords.add("TIME", Token.KEYWORD2);
		keywords.add("TIMESTAMP", Token.KEYWORD2);
		keywords.add("VARCHAR", Token.KEYWORD2);
		keywords.add("VARYING", Token.KEYWORD2);

		try
		{
			ResultSet rs = dmd.getTypeInfo();
			try
			{
				while (rs.next())
				{
					keywords.add(rs.getString(1).trim(), Token.KEYWORD2);
				}
			}
			finally
			{
				rs.close();
			}
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}
	}

	private static void addFunctions(DatabaseMetaData dmd, KeywordMap keywords)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			buf.append(dmd.getNumericFunctions());
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}
		buf.append(",");
		try
		{
			buf.append(dmd.getStringFunctions());
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}
		buf.append(",");
		try
		{
			buf.append(dmd.getTimeDateFunctions());
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}

		StringTokenizer strtok = new StringTokenizer(buf.toString(), ",");
		while (strtok.hasMoreTokens())
		{
			keywords.add(strtok.nextToken().trim(), Token.KEYWORD3);
		}
	}

	private static void addSingleKeyword(String keyword, KeywordMap keywords)
	{
		if (keyword != null)
		{
			keyword = keyword.trim();
			if (keyword.length() > 0)
			{
				keywords.add(keyword, Token.KEYWORD1);
			}
		}
	}
}