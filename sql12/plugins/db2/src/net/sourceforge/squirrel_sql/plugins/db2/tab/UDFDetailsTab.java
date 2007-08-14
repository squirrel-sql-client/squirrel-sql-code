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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class will display the details for an DB2 user-defined function.
 *
 * @author manningr
 */
public class UDFDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TriggerDetailsTab.class);

	/**
	 * This interface defines locale specific strings. 
	 */
	private interface i18n
	{
		// i18n[UdfDetailsTab.title=Details]
		String TITLE = s_stringMgr.getString("UdfDetailsTab.title");
		// i18n[UdfDetailsTab.hint=Display UDF details]
		String HINT = s_stringMgr.getString("UdfDetailsTab.hint");
	}

	/** SQL that retrieves the data. */
	private static String SQL =
	    "select " +
	    "name, " +
	    "schema, " +
	    "definer, " +
	    "function_id, " +
	    "parm_count, " +
	    "side_effects, " +
	    "fenced, " +
	    "language, " +
	    "contains_sql, " +
	    "result_cols, " +
	    "class, " +
	    "jar_id " +
	    "from sysibm.SYSFUNCTIONS " +
	    "where schema = ? " +
	    "and name = ? ";
	
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerDetailsTab.class);

	public UDFDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        if (s_log.isDebugEnabled()) {
            s_log.debug("UDF details SQL: "+SQL);
            s_log.debug("UDF schema: "+doi.getSchemaName());
            s_log.debug("UDF name: "+doi.getSimpleName());
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
