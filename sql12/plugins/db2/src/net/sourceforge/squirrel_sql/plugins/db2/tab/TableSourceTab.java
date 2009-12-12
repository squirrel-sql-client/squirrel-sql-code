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
import static java.util.Arrays.asList;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class will display the source for a DB2 table (including MQTs).
 * 
 * @author manningr
 */
public class TableSourceTab extends FormattedSourceTab
{
	/** SQL that retrieves the source of a MQT. */
	private static final String MQT_SQL = 
		"SELECT text " + 
		"FROM SYSCAT.VIEWS " + 
		"WHERE viewschema = ? " +
		"and viewname = ? ";

	/** SQL that retrieves the source of a MQT on OS/400 */
	private static final String OS400_MQT_SQL = 
		"select mqt_definition " + 
		"from qsys2.systables " + 
		"where table_schema = ? " + 
		"and table_name = ? ";

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(ViewSourceTab.class);

	/** boolean to indicate whether or not this session is OS/400 */
	private boolean isOS400 = false;

	/**
	 * Constructor
	 * 
	 * @param hint
	 *        what the user sees on mouse-over tool-tip
	 * @param stmtSep
	 *        the string to use to separate SQL statements
	 * @param isOS400
	 *        whether or not we are connected to an OS/400 system
	 */
	public TableSourceTab(String hint, String stmtSep, boolean isOS400) {
		super(hint);
		super.setCompressWhitespace(true);
		super.setupFormatter(stmtSep, null);
		this.isOS400 = isOS400;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab#createStatement()
	 */
	@Override
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
		String sql = MQT_SQL;
		if (isOS400)
		{
			sql = OS400_MQT_SQL;
		}
		boolean isMQT = isMQT();
		if (!isMQT)
		{
			sql = getTableSelectSql((ITableInfo) doi);

			// we may have more than one statement in sql at this point
			super.appendSeparator = false;
		} else
		{
			// MQTs only ever have one sql statement
			super.appendSeparator = true;
		}
		if (s_log.isDebugEnabled())
		{
			s_log.debug("Running SQL for table source tab: " + sql);
			s_log.debug("schema=" + doi.getSchemaName());
			s_log.debug("view name=" + doi.getSimpleName());
		}
		PreparedStatement pstmt = conn.prepareStatement(sql);
		if (isMQT)
		{
			pstmt.setString(1, doi.getSchemaName());
			pstmt.setString(2, doi.getSimpleName());
		}
		return pstmt;
	}

	private boolean isMQT()
	{
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		boolean isMQT = false;

		if (doi.getDatabaseObjectType() == DatabaseObjectType.TABLE)
		{
			ITableInfo info = (ITableInfo) doi;
			if (info.getType().startsWith("MATERIALIZED"))
			{
				isMQT = true;
				if (s_log.isDebugEnabled())
				{
					s_log.debug("Table " + doi.getSimpleName() + " appears to be an MQT");
				}
			} else
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("Table " + doi.getSimpleName() + " appears to be a regular table");
				}
			}
		}

		return isMQT;
	}

	private String getTableSelectSql(ITableInfo ti)
	{
		String sql = getRegularTableSelectSql(ti);
		if (sql == null)
		{
			sql = MQT_SQL;
		}
		return sql;
	}

	/**
	 * This builds a create statement
	 * 
	 * @param ti
	 * @return
	 */
	private String getRegularTableSelectSql(ITableInfo ti)
	{
		StringBuilder tmp = new StringBuilder();
		tmp.append("select '");

		ISQLDatabaseMetaData md = getSession().getMetaData();
		try
		{
			HibernateDialect dialect = DialectFactory.getDialect(md);
			List<ITableInfo> tableList = asList(new ITableInfo[]
				{ ti });
			CreateScriptPreferences prefs = new CreateScriptPreferences();
			List<String> sqls = dialect.getCreateTableSQL(tableList, md, prefs, false);
			for (String sql : sqls)
			{
				tmp.append(sql);
				tmp.append(statementSeparator);
				tmp.append("\n");
				tmp.append("\n");
			}
		} catch (SQLException e)
		{
			s_log.error("createStatement: Unexpected exception while " + "constructing SQL for table("
			      + ti.getSimpleName() + "): " + e.getMessage(), e);
			return null;
		}
		tmp.append("' from sysibm.sysdummy1");
		return tmp.toString();
	}

	protected String getSqlStatement()
   {
	   return null;
   }
}
