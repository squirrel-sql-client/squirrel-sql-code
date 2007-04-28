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
 * This class will display the details for an Oracle user.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class UserDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(UserDetailsTab.class);

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[oracle.userDetails=Details]
		String TITLE = s_stringMgr.getString("oracle.userDetails");
		// i18n[oracle.displayUserDetails=Display User details]
		String HINT = s_stringMgr.getString("oracle.displayUserDetails");
	}
    /** SQL that is used to see if the session has access to query this info */
    private static final String SQL_CHECK_ACCESS = 
        "select username, user_id,"
            + " account_status, lock_date, expiry_date, default_tablespace,"
            + " temporary_tablespace, created, initial_rsrc_consumer_group,"
            + " external_name from dba_users";        
    
	/** SQL that retrieves the data. */
	private static final String SQL_ADMIN =
		"select username, user_id,"
			+ " account_status, lock_date, expiry_date, default_tablespace,"
			+ " temporary_tablespace, created, initial_rsrc_consumer_group,"
			+ " external_name from dba_users"
			+ " where username = ?";
    
	/** SQL that retrieves the data. */
	private static final String SQL_USER =
		"select username, user_id,"
			+ " account_status, lock_date, expiry_date, default_tablespace,"
			+ " temporary_tablespace, created, initial_rsrc_consumer_group,"
			+ " external_name from user_users"
			+ " where username = ?";

	/** Is user can access to dba_users. */
	protected boolean isAdmin;
	
	public UserDetailsTab(final ISession session)
	{
		super(i18n.TITLE, i18n.HINT, true);
        session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                isAdmin=OraclePlugin.checkObjectAccessible(session, SQL_CHECK_ACCESS);
            }
        });
		
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();

		final PreparedStatement pstmt = session.getSQLConnection().prepareStatement(isAdmin?SQL_ADMIN:SQL_USER);
		
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
