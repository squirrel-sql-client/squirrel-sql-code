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

public class JeditSQLTokenMarker extends SQLTokenMarker
{
	// Keyword 1 = keywords
	// Keyword 2 = data types
	// Keyword 3 = functions.

	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(JeditSQLTokenMarker.class);

	private KeywordMap _keywords = new KeywordMap(true);

	public JeditSQLTokenMarker(SQLConnection conn)
	{
		super(createKeywordMap(conn), false);
		_keywords = SQUIRREL_getKeywordMap();
		try
		{
			_dmd = conn.getSQLMetaData().getJDBCMetaData();
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
			final DatabaseMetaData dmd = conn.getSQLMetaData().getJDBCMetaData();
			addKeywords(dmd, keywords);
			addDataTypes(dmd, keywords);
			addFunctions(dmd, keywords);

			// TODO: should be optional for LARGE databases?? Perhaps run in a
			// background thread?
			addTableNames(dmd, keywords);
		}
		catch (Throwable ex)
		{
			s_log.error("Error occured creating keyword map", ex);
		}
		return keywords;
	}

	private static void addKeywords(DatabaseMetaData dmd, KeywordMap keywords)
	{
		// ANSI std keywords.
//		keywords.add("=", Token.KEYWORD1);
//		keywords.add("<>", Token.KEYWORD1);
//		keywords.add("<", Token.KEYWORD1);
//		keywords.add("<=", Token.KEYWORD1);
//		keywords.add(">", Token.KEYWORD1);
//		keywords.add(">=", Token.KEYWORD1);
		keywords.add("ABSOLUTE", Token.KEYWORD1);
		keywords.add("ACTION", Token.KEYWORD1);
		keywords.add("ADD", Token.KEYWORD1);
		keywords.add("ALL", Token.KEYWORD1);
		keywords.add("ALTER", Token.KEYWORD1);
		keywords.add("AND", Token.KEYWORD1);
		keywords.add("AS", Token.KEYWORD1);
		keywords.add("ASC", Token.KEYWORD1);
		keywords.add("ASSERTION", Token.KEYWORD1);
		keywords.add("AUTHORIZATION", Token.KEYWORD1);
		keywords.add("AVG", Token.KEYWORD1);
		keywords.add("BETWEEN", Token.KEYWORD1);
		keywords.add("BY", Token.KEYWORD1);
		keywords.add("CASCADE", Token.KEYWORD1);
		keywords.add("CASCADED", Token.KEYWORD1);
		keywords.add("CATALOG", Token.KEYWORD1);
		keywords.add("CHARACTER", Token.KEYWORD1);
		keywords.add("CHECK", Token.KEYWORD1);
		keywords.add("COLLATE", Token.KEYWORD1);
		keywords.add("COLLATION", Token.KEYWORD1);
		keywords.add("COLUMN", Token.KEYWORD1);
		keywords.add("COMMIT", Token.KEYWORD1);
		keywords.add("COMMITTED", Token.KEYWORD1);
		keywords.add("CONNECT", Token.KEYWORD1);
		keywords.add("CONNECTION", Token.KEYWORD1);
		keywords.add("CONSTRAINT", Token.KEYWORD1);
		keywords.add("COUNT", Token.KEYWORD1);
		keywords.add("CORRESPONDING", Token.KEYWORD1);
		keywords.add("CREATE", Token.KEYWORD1);
		keywords.add("CROSS", Token.KEYWORD1);
		keywords.add("CURRENT", Token.KEYWORD1);
		keywords.add("CURSOR", Token.KEYWORD1);
		keywords.add("DECLARE", Token.KEYWORD1);
		keywords.add("DEFAULT", Token.KEYWORD1);
		keywords.add("DEFERRABLE", Token.KEYWORD1);
		keywords.add("DEFERRED", Token.KEYWORD1);
		keywords.add("DELETE", Token.KEYWORD1);
		keywords.add("DESC", Token.KEYWORD1);
		keywords.add("DIAGNOSTICS", Token.KEYWORD1);
		keywords.add("DISCONNECT", Token.KEYWORD1);
		keywords.add("DISTINCT", Token.KEYWORD1);
		keywords.add("DOMAIN", Token.KEYWORD1);
		keywords.add("DROP", Token.KEYWORD1);
		keywords.add("ESCAPE", Token.KEYWORD1);
		keywords.add("EXCEPT", Token.KEYWORD1);
		keywords.add("EXISTS", Token.KEYWORD1);
		keywords.add("EXTERNAL", Token.KEYWORD1);
		keywords.add("FALSE", Token.KEYWORD1);
		keywords.add("FETCH", Token.KEYWORD1);
		keywords.add("FIRST", Token.KEYWORD1);
		keywords.add("FOREIGN", Token.KEYWORD1);
		keywords.add("FROM", Token.KEYWORD1);
		keywords.add("FULL", Token.KEYWORD1);
		keywords.add("GET", Token.KEYWORD1);
		keywords.add("GLOBAL", Token.KEYWORD1);
		keywords.add("GRANT", Token.KEYWORD1);
		keywords.add("GROUP", Token.KEYWORD1);
		keywords.add("HAVING", Token.KEYWORD1);
		keywords.add("IDENTITY", Token.KEYWORD1);
		keywords.add("IMMEDIATE", Token.KEYWORD1);
		keywords.add("IN", Token.KEYWORD1);
		keywords.add("INITIALLY", Token.KEYWORD1);
		keywords.add("INNER", Token.KEYWORD1);
		keywords.add("INSENSITIVE", Token.KEYWORD1);
		keywords.add("INSERT", Token.KEYWORD1);
		keywords.add("INTERSECT", Token.KEYWORD1);
		keywords.add("INTO", Token.KEYWORD1);
		keywords.add("IS", Token.KEYWORD1);
		keywords.add("ISOLATION", Token.KEYWORD1);
		keywords.add("JOIN", Token.KEYWORD1);
		keywords.add("KEY", Token.KEYWORD1);
		keywords.add("LAST", Token.KEYWORD1);
		keywords.add("LEFT", Token.KEYWORD1);
		keywords.add("LEVEL", Token.KEYWORD1);
		keywords.add("LIKE", Token.KEYWORD1);
		keywords.add("LOCAL", Token.KEYWORD1);
		keywords.add("MATCH", Token.KEYWORD1);
		keywords.add("MAX", Token.KEYWORD1);
		keywords.add("MIN", Token.KEYWORD1);
		keywords.add("NAMES", Token.KEYWORD1);
		keywords.add("NEXT", Token.KEYWORD1);
		keywords.add("NO", Token.KEYWORD1);
		keywords.add("NOT", Token.KEYWORD1);
		keywords.add("NULL", Token.KEYWORD1);
		keywords.add("OF", Token.KEYWORD1);
		keywords.add("ON", Token.KEYWORD1);
		keywords.add("ONLY", Token.KEYWORD1);
		keywords.add("OPEN", Token.KEYWORD1);
		keywords.add("OPTION", Token.KEYWORD1);
		keywords.add("OR", Token.KEYWORD1);
		keywords.add("ORDER", Token.KEYWORD1);
		keywords.add("OUTER", Token.KEYWORD1);
		keywords.add("OVERLAPS", Token.KEYWORD1);
		keywords.add("PARTIAL", Token.KEYWORD1);
		keywords.add("PRESERVE", Token.KEYWORD1);
		keywords.add("PRIMARY", Token.KEYWORD1);
		keywords.add("PRIOR", Token.KEYWORD1);
		keywords.add("PRIVILIGES", Token.KEYWORD1);
		keywords.add("PUBLIC", Token.KEYWORD1);
		keywords.add("READ", Token.KEYWORD1);
		keywords.add("REFERENCES", Token.KEYWORD1);
		keywords.add("RELATIVE", Token.KEYWORD1);
		keywords.add("REPEATABLE", Token.KEYWORD1);
		keywords.add("RESTRICT", Token.KEYWORD1);
		keywords.add("REVOKE", Token.KEYWORD1);
		keywords.add("RIGHT", Token.KEYWORD1);
		keywords.add("ROLLBACK", Token.KEYWORD1);
		keywords.add("ROWS", Token.KEYWORD1);
		keywords.add("SCHEMA", Token.KEYWORD1);
		keywords.add("SCROLL", Token.KEYWORD1);
		keywords.add("SELECT", Token.KEYWORD1);
		keywords.add("SERIALIZABLE", Token.KEYWORD1);
		keywords.add("SESSION", Token.KEYWORD1);
		keywords.add("SET", Token.KEYWORD1);
		keywords.add("SIZE", Token.KEYWORD1);
		keywords.add("SOME", Token.KEYWORD1);
		keywords.add("SUM", Token.KEYWORD1);
		keywords.add("TABLE", Token.KEYWORD1);
		keywords.add("TEMPORARY", Token.KEYWORD1);
		keywords.add("THEN", Token.KEYWORD1);
		keywords.add("TIME", Token.KEYWORD1);
		keywords.add("TO", Token.KEYWORD1);
		keywords.add("TRANSACTION", Token.KEYWORD1);
		keywords.add("TRIGGER", Token.KEYWORD1);
		keywords.add("TRUE", Token.KEYWORD1);
		keywords.add("UNCOMMITTED", Token.KEYWORD1);
		keywords.add("UNION", Token.KEYWORD1);
		keywords.add("UNIQUE", Token.KEYWORD1);
		keywords.add("UNKNOWN", Token.KEYWORD1);
		keywords.add("UPDATE", Token.KEYWORD1);
		keywords.add("USAGE", Token.KEYWORD1);
		keywords.add("USER", Token.KEYWORD1);
		keywords.add("USING", Token.KEYWORD1);
		keywords.add("VALUES", Token.KEYWORD1);
		keywords.add("VIEW", Token.KEYWORD1);
		keywords.add("WHERE", Token.KEYWORD1);
		keywords.add("WITH", Token.KEYWORD1);
		keywords.add("WORK", Token.KEYWORD1);
		keywords.add("WRITE", Token.KEYWORD1);
		keywords.add("ZONE", Token.KEYWORD1);

		// Not actually in the std.
		keywords.add("INDEX", Token.KEYWORD1);

		// Extra keywords that this DBMS supports.
		StringBuffer buf = new StringBuffer();
		try
		{
			buf.append(dmd.getSQLKeywords());
		}
		catch (Throwable ex)
		{
			s_log.error("Error retrieving DBMS keywords", ex);
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

	private static void addTableNames(DatabaseMetaData dmd, KeywordMap keywords)
	{
		try
		{
// all table types, everything. TODO:
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
//		keywords.add("BIT", Token.KEYWORD2);
//		keywords.add("BLOB", Token.KEYWORD2);
//		keywords.add("BOOLEAN", Token.KEYWORD2);
//		keywords.add("CHAR", Token.KEYWORD2);
//		keywords.add("CHARACTER", Token.KEYWORD2);
//		keywords.add("CLOB", Token.KEYWORD2);
//		keywords.add("DATE", Token.KEYWORD2);
//		keywords.add("DECIMAL", Token.KEYWORD2);
//		keywords.add("DOUBLE", Token.KEYWORD2);
//		keywords.add("FLOAT", Token.KEYWORD2);
//		keywords.add("INTEGER", Token.KEYWORD2);
//		keywords.add("INTERVAL", Token.KEYWORD2);
//		keywords.add("NCHAR", Token.KEYWORD2);
//		keywords.add("NCLOB", Token.KEYWORD2);
//		keywords.add("NUMERIC", Token.KEYWORD2);
//		keywords.add("PRECISION", Token.KEYWORD2);
//		keywords.add("REAL", Token.KEYWORD2);
//		keywords.add("SMALLINT", Token.KEYWORD2);
//		keywords.add("TIME", Token.KEYWORD2);
//		keywords.add("TIMESTAMP", Token.KEYWORD2);
//		keywords.add("VARCHAR", Token.KEYWORD2);
//		keywords.add("VARYING", Token.KEYWORD2);
//
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
