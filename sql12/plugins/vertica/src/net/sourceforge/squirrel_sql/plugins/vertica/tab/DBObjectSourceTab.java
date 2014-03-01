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
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This class will display the DDL used for Vertica Table and Projections.
 */
public class DBObjectSourceTab extends FormattedSourceTab
{
	// Internationalized strings for this class
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DBObjectSourceTab.class);

    // SQL to retrieve the source (i.e. DDL) of an object
	private static final String SQL = "SELECT export_objects('',?,false)";
	
	// This interface defines locale specific strings
	private interface i18n
	{
		String HINT = s_stringMgr.getString("DBObjectSourceTab.hint");
		String NOTAVAILABLE_SQL = "SELECT '" + s_stringMgr.getString("DBObjectSourceTab.notAvailable") + "'"; 
	}

    // ctor
	public DBObjectSourceTab()
    {
		super(i18n.HINT);
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
		PreparedStatement pstmt = null;
		
        if (doi.getDatabaseObjectType() == DatabaseObjectType.TABLE &&
            ((TableInfo)doi).getType().equals("SYSTEM TABLE"))
        {
			pstmt = conn.prepareStatement(i18n.NOTAVAILABLE_SQL);
		}
        else
        {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, doi.getSchemaName() + "." + doi.getSimpleName());
		}
		return pstmt;
	}


    protected String getSqlStatement()
    {
        return SQL;
    }
}
