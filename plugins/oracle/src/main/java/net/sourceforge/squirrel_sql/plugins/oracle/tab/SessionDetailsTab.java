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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;
/**
 * This class will display the details for an Oracle session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionDetailsTab.class);


	/**
	 *
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[oracle.sessionDetails=Details]
		String TITLE = s_stringMgr.getString("oracle.sessionDetails");
		// i18n[oracle.displaySessionDetails=Display session details]
		String HINT = s_stringMgr.getString("oracle.displaySessionDetails");
	}

	/** SQL that retrieves the data. */
	private static String SQL =
		"select sid, user#, username, status, server, schemaname, osuser,"
			+ " machine, terminal, program"
			+ " from sys.v_$session"
			+ " where sid = ?";

    /** SQL that is used to see if the session has access to query this info */
    private static String CHECK_ACCESS_SQL =
        "select sid, user#, username, status, server, schemaname, osuser,"
            + " machine, terminal, program"
            + " from sys.v_$session";
    
	public SessionDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	/**
	 */
	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        String[] parts = doi.getSimpleName().split("\\s+");
		pstmt.setLong(1, Long.parseLong(parts[0]));
		return pstmt;
	}
	
	/**
	 * Check if data accessible from current connection.
	 * 
	 * @param session session 
	 * @return true if data accessible
	 */
	public static boolean isAccessible(final ISession session) {
		return OraclePlugin.checkObjectAccessible(session, CHECK_ACCESS_SQL);
	}
}
