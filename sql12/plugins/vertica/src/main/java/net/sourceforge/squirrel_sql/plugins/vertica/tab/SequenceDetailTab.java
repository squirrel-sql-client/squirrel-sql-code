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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This tab shows the primary key info for the currently selected table.
 */
public class SequenceDetailTab extends BasePreparedStatementTab
{
	// Internationalized strings for this class
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SequenceDetailTab.class);

  	// This interface defines locale specific strings
	private interface i18n
	{
		String HINT  = s_stringMgr.getString("SequenceDetailTab.hint");
		String TITLE = s_stringMgr.getString("SequenceDetailTab.title"); 
	}

	private static final String SQL =
		"SELECT SEQUENCE_NAME," +
            "OWNER_NAME AS OWNER," +
            "CURRENT_VALUE," +
            "MINIMUM," +
		    "MAXIMUM," +
		    "INCREMENT_BY," +
		    "ALLOW_CYCLE AS CYCLE," +
		    "SESSION_CACHE_COUNT AS CACHE " +
		"FROM V_CATALOG.SEQUENCES " +
		"WHERE SEQUENCE_SCHEMA=? AND SEQUENCE_NAME=?";

	
	public SequenceDetailTab()
    {
        super(i18n.TITLE, i18n.HINT, true);
	}
	
    /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab#createStatement()
     */
    @Override	
	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}

}
