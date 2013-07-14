/*
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

package net.sourceforge.squirrel_sql.plugins.vertica.tab;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.BaseTableTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This tab shows the primary key info for the currently selected table.
 */
public class ProjectionTab extends BaseTableTab
{
	// Internationalized strings for this class
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProjectionTab.class);

  	// This interface defines locale specific strings
	private interface i18n
	{
		String HINT  = s_stringMgr.getString("ProjectionTab.hint");
		String TITLE = s_stringMgr.getString("ProjectionTab.title"); 
	}

	private static final String SQL =
		"SELECT PROJECTION_NAME," +
            "OWNER_NAME AS OWNER," +
		    "NODE_NAME," +
            "IS_PREJOIN," +
            "CREATED_EPOCH," +
		    "CREATE_TYPE," +
            "VERIFIED_FAULT_TOLERANCE AS K_SAFETY_VALUE," +
            "IS_UP_TO_DATE," +
            "HAS_STATISTICS " +
		"FROM V_CATALOG.PROJECTIONS " +
		"WHERE PROJECTION_SCHEMA=? AND ANCHOR_TABLE_NAME=?";

	
	public ProjectionTab() {
	}
	
	public String getTitle()
	{
		return i18n.TITLE;
	}

	public String getHint()
	{
		return i18n.HINT;
	}

	// Create the <TT>IDataSet</TT> to be displayed in this tab
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
		SQLDatabaseMetaData md = conn.getSQLMetaData();
		ResultSet rs = null;

		String sql = SQL;
		final PreparedStatement pstmt;
		try
        {
			pstmt = conn.prepareStatement(sql);
		}
		catch (SQLException e)
		{
			throw new DataSetException(e);
		}
		
		try
		{
			ITableInfo ti = getTableInfo();
			pstmt.setString(1, ti.getSchemaName());
			pstmt.setString(2, ti.getSimpleName());
			rs = pstmt.executeQuery();
			ResultSetDataSet rsds = new ResultSetDataSet();
			rsds.setResultSet(rs, DialectFactory.getDialectType(md));
			return rsds;

		}
		catch (SQLException e)
		{
			throw new DataSetException(e);
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
			SQLUtilities.closeStatement(pstmt);
		}
	}

}
