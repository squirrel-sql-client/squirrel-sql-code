package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.syntax.KeywordMap;
import org.gjt.sp.jedit.syntax.SQLTokenMarker;
import org.gjt.sp.jedit.syntax.Token;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class JeditSQLTokenMarker extends SQLTokenMarker
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(JeditSQLTokenMarker.class);

	private KeywordMap _keywords;

	public JeditSQLTokenMarker()
	{
		super(createDummyKeywordMap());
	}

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

	private static KeywordMap createDummyKeywordMap()
	{
		KeywordMap keywords = new KeywordMap(true);

		addKeywords(null, keywords);

		keywords.add("VARCHAR", Token.DATA_TYPE);
		keywords.add("DATETIME", Token.DATA_TYPE);

		keywords.add("TABLE1", Token.TABLE);
		keywords.add("TABLE2", Token.TABLE);

		keywords.add("COL1", Token.COLUMN);
		keywords.add("COL2", Token.COLUMN);

		keywords.add("AVG", Token.FUNCTION);

		return keywords;
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
		keywords.add("ABSOLUTE", Token.KEYWORD);
		keywords.add("ACTION", Token.KEYWORD);
		keywords.add("ADD", Token.KEYWORD);
		keywords.add("ALL", Token.KEYWORD);
		keywords.add("ALTER", Token.KEYWORD);
		keywords.add("AND", Token.KEYWORD);
		keywords.add("AS", Token.KEYWORD);
		keywords.add("ASC", Token.KEYWORD);
		keywords.add("ASSERTION", Token.KEYWORD);
		keywords.add("AUTHORIZATION", Token.KEYWORD);
		keywords.add("AVG", Token.KEYWORD);
		keywords.add("BETWEEN", Token.KEYWORD);
		keywords.add("BY", Token.KEYWORD);
		keywords.add("CASCADE", Token.KEYWORD);
		keywords.add("CASCADED", Token.KEYWORD);
		keywords.add("CATALOG", Token.KEYWORD);
		keywords.add("CHARACTER", Token.KEYWORD);
		keywords.add("CHECK", Token.KEYWORD);
		keywords.add("COLLATE", Token.KEYWORD);
		keywords.add("COLLATION", Token.KEYWORD);
		keywords.add("COLUMN", Token.KEYWORD);
		keywords.add("COMMIT", Token.KEYWORD);
		keywords.add("COMMITTED", Token.KEYWORD);
		keywords.add("CONNECT", Token.KEYWORD);
		keywords.add("CONNECTION", Token.KEYWORD);
		keywords.add("CONSTRAINT", Token.KEYWORD);
		keywords.add("COUNT", Token.KEYWORD);
		keywords.add("CORRESPONDING", Token.KEYWORD);
		keywords.add("CREATE", Token.KEYWORD);
		keywords.add("CROSS", Token.KEYWORD);
		keywords.add("CURRENT", Token.KEYWORD);
		keywords.add("CURSOR", Token.KEYWORD);
		keywords.add("DECLARE", Token.KEYWORD);
		keywords.add("DEFAULT", Token.KEYWORD);
		keywords.add("DEFERRABLE", Token.KEYWORD);
		keywords.add("DEFERRED", Token.KEYWORD);
		keywords.add("DELETE", Token.KEYWORD);
		keywords.add("DESC", Token.KEYWORD);
		keywords.add("DIAGNOSTICS", Token.KEYWORD);
		keywords.add("DISCONNECT", Token.KEYWORD);
		keywords.add("DISTINCT", Token.KEYWORD);
		keywords.add("DOMAIN", Token.KEYWORD);
		keywords.add("DROP", Token.KEYWORD);
		keywords.add("ESCAPE", Token.KEYWORD);
		keywords.add("EXCEPT", Token.KEYWORD);
		keywords.add("EXISTS", Token.KEYWORD);
		keywords.add("EXTERNAL", Token.KEYWORD);
		keywords.add("FALSE", Token.KEYWORD);
		keywords.add("FETCH", Token.KEYWORD);
		keywords.add("FIRST", Token.KEYWORD);
		keywords.add("FOREIGN", Token.KEYWORD);
		keywords.add("FROM", Token.KEYWORD);
		keywords.add("FULL", Token.KEYWORD);
		keywords.add("GET", Token.KEYWORD);
		keywords.add("GLOBAL", Token.KEYWORD);
		keywords.add("GRANT", Token.KEYWORD);
		keywords.add("GROUP", Token.KEYWORD);
		keywords.add("HAVING", Token.KEYWORD);
		keywords.add("IDENTITY", Token.KEYWORD);
		keywords.add("IMMEDIATE", Token.KEYWORD);
		keywords.add("IN", Token.KEYWORD);
		keywords.add("INITIALLY", Token.KEYWORD);
		keywords.add("INNER", Token.KEYWORD);
		keywords.add("INSENSITIVE", Token.KEYWORD);
		keywords.add("INSERT", Token.KEYWORD);
		keywords.add("INTERSECT", Token.KEYWORD);
		keywords.add("INTO", Token.KEYWORD);
		keywords.add("IS", Token.KEYWORD);
		keywords.add("ISOLATION", Token.KEYWORD);
		keywords.add("JOIN", Token.KEYWORD);
		keywords.add("KEY", Token.KEYWORD);
		keywords.add("LAST", Token.KEYWORD);
		keywords.add("LEFT", Token.KEYWORD);
		keywords.add("LEVEL", Token.KEYWORD);
		keywords.add("LIKE", Token.KEYWORD);
		keywords.add("LOCAL", Token.KEYWORD);
		keywords.add("MATCH", Token.KEYWORD);
		keywords.add("MAX", Token.KEYWORD);
		keywords.add("MIN", Token.KEYWORD);
		keywords.add("NAMES", Token.KEYWORD);
		keywords.add("NEXT", Token.KEYWORD);
		keywords.add("NO", Token.KEYWORD);
		keywords.add("NOT", Token.KEYWORD);
		keywords.add("NULL", Token.KEYWORD);
		keywords.add("OF", Token.KEYWORD);
		keywords.add("ON", Token.KEYWORD);
		keywords.add("ONLY", Token.KEYWORD);
		keywords.add("OPEN", Token.KEYWORD);
		keywords.add("OPTION", Token.KEYWORD);
		keywords.add("OR", Token.KEYWORD);
		keywords.add("ORDER", Token.KEYWORD);
		keywords.add("OUTER", Token.KEYWORD);
		keywords.add("OVERLAPS", Token.KEYWORD);
		keywords.add("PARTIAL", Token.KEYWORD);
		keywords.add("PRESERVE", Token.KEYWORD);
		keywords.add("PRIMARY", Token.KEYWORD);
		keywords.add("PRIOR", Token.KEYWORD);
		keywords.add("PRIVILIGES", Token.KEYWORD);
		keywords.add("PUBLIC", Token.KEYWORD);
		keywords.add("READ", Token.KEYWORD);
		keywords.add("REFERENCES", Token.KEYWORD);
		keywords.add("RELATIVE", Token.KEYWORD);
		keywords.add("REPEATABLE", Token.KEYWORD);
		keywords.add("RESTRICT", Token.KEYWORD);
		keywords.add("REVOKE", Token.KEYWORD);
		keywords.add("RIGHT", Token.KEYWORD);
		keywords.add("ROLLBACK", Token.KEYWORD);
		keywords.add("ROWS", Token.KEYWORD);
		keywords.add("SCHEMA", Token.KEYWORD);
		keywords.add("SCROLL", Token.KEYWORD);
		keywords.add("SELECT", Token.KEYWORD);
		keywords.add("SERIALIZABLE", Token.KEYWORD);
		keywords.add("SESSION", Token.KEYWORD);
		keywords.add("SET", Token.KEYWORD);
		keywords.add("SIZE", Token.KEYWORD);
		keywords.add("SOME", Token.KEYWORD);
		keywords.add("SUM", Token.KEYWORD);
		keywords.add("TABLE", Token.KEYWORD);
		keywords.add("TEMPORARY", Token.KEYWORD);
		keywords.add("THEN", Token.KEYWORD);
		keywords.add("TIME", Token.KEYWORD);
		keywords.add("TO", Token.KEYWORD);
		keywords.add("TRANSACTION", Token.KEYWORD);
		keywords.add("TRIGGER", Token.KEYWORD);
		keywords.add("TRUE", Token.KEYWORD);
		keywords.add("UNCOMMITTED", Token.KEYWORD);
		keywords.add("UNION", Token.KEYWORD);
		keywords.add("UNIQUE", Token.KEYWORD);
		keywords.add("UNKNOWN", Token.KEYWORD);
		keywords.add("UPDATE", Token.KEYWORD);
		keywords.add("USAGE", Token.KEYWORD);
		keywords.add("USER", Token.KEYWORD);
		keywords.add("USING", Token.KEYWORD);
		keywords.add("VALUES", Token.KEYWORD);
		keywords.add("VIEW", Token.KEYWORD);
		keywords.add("WHERE", Token.KEYWORD);
		keywords.add("WITH", Token.KEYWORD);
		keywords.add("WORK", Token.KEYWORD);
		keywords.add("WRITE", Token.KEYWORD);
		keywords.add("ZONE", Token.KEYWORD);

		// Not actually in the std.
		keywords.add("INDEX", Token.KEYWORD);

		// Extra keywords that this DBMS supports.
		if (dmd != null)
		{
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
				keywords.add(strtok.nextToken().trim(), Token.KEYWORD);
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
	}

	private static void addTableNames(DatabaseMetaData dmd, KeywordMap keywords)
	{
		try
		{
			// all table types, everything. TODO:
			ResultSet rs = dmd.getTables(null, null, null,
					new String[] { "TABLE" });

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
		try
		{
			ResultSet rs = dmd.getTypeInfo();

			try
			{
				while (rs.next())
				{
					keywords.add(rs.getString(1).trim(), Token.DATA_TYPE);
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
			keywords.add(strtok.nextToken().trim(), Token.FUNCTION);
		}
	}

	private static void addSingleKeyword(String keyword, KeywordMap keywords)
	{
		if (keyword != null)
		{
			keyword = keyword.trim();

			if (keyword.length() > 0)
			{
				keywords.add(keyword, Token.KEYWORD);
			}
		}
	}
}
