package net.sourceforge.squirrel_sql.plugins.oracle.tab;

/*
 * Copyright (C) 2002-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class will display the source for an Oracle object.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectSourceTab extends BaseSourceTab
{
	private static final ILogger s_log = LoggerController.createLogger(ObjectSourceTab.class);

	private final String _columnData;

	public ObjectSourceTab(String columnData, String hint)
	{
		this(columnData, null, hint);
	}

	public ObjectSourceTab(String columnData, String title, String hint)
	{
		super(title, hint);
		if (columnData == null) { throw new IllegalArgumentException("Column Data is null"); }
		_columnData = columnData;
	}


	@Override
	protected PreparedStatement createStatement() throws SQLException
	{
		return null;
	}

	@Override
	protected String getSourceCode(ISession session, PreparedStatement stmt)
	{
		try
		{
			ISQLConnection conn = session.getSQLConnection();
			Statement stat = conn.createStatement();

			IDatabaseObjectInfo doi = getDatabaseObjectInfo();

			ResultSet res = null;

			boolean read_sys_dba_source_failed = false;
			try
			{
				String sql1 = "select text from sys.dba_source " +
						"where type = '" + _columnData + "' " +
						"and owner = '" + doi.getSchemaName() + "' " +
						"and name = '" + doi.getSimpleName() + "' " +
						"order by line";

				res = stat.executeQuery(sql1);
			}
			catch (Exception sys_dba_source_exc)
			{
				read_sys_dba_source_failed = true;
				s_log.warn("Failed to read source from sys.dba_source. Will try sys.all_source next", sys_dba_source_exc);
			}

			if(read_sys_dba_source_failed || false == res.next())
			{
				if (false == read_sys_dba_source_failed)
				{
					res.close();
				}

				String sql2 = "select text from sys.all_source " +
						"where type = '" + _columnData + "' " +
						"and owner = '" + doi.getSchemaName() + "' " +
						"and name = '" + doi.getSimpleName() + "' " +
						"order by line";

				res = stat.executeQuery(sql2);

				if(false == res.next())
				{
					return null;
				}
			}

			String ret = res.getString(1);

			while(res.next())
			{
				ret += res.getString(1);
			}

			SQLUtilities.closeResultSet(res);
			SQLUtilities.closeStatement(stat);

			return ret;
		}
		catch (SQLException e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}
}
