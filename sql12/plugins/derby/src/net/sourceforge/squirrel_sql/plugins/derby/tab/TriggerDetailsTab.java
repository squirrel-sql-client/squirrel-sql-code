package net.sourceforge.squirrel_sql.plugins.derby.tab;
/*
 * Copyright (C) 2006 Rob Manning
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
 * This class will display the details for a Derby trigger.
 *
 * @author manningr
 */
public class TriggerDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TriggerDetailsTab.class);


	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[TriggerDetailsTab.title=Details]
		String TITLE = s_stringMgr.getString("TriggerDetailsTab.title");
		// i18n[TriggerDetailsTab.hint=Display trigger details]
		String HINT = s_stringMgr.getString("TriggerDetailsTab.hint");
	}

	/** SQL that retrieves the data. */
	private static String SQL =
        "select tr.TRIGGERNAME       AS name, " +
        "       sc.SCHEMANAME        AS schemaname, " +
        "       tr.CREATIONTIMESTAMP AS createtime, " +
        "       CASE " +
        "         WHEN tr.EVENT='U' THEN 'UPDATE' " +
        "         WHEN tr.EVENT='D' THEN 'DELETE' " +
        "         WHEN tr.EVENT='I' THEN 'INSERT' " +
        "       END AS event, " +
        "       CASE " +
        "         WHEN tr.FIRINGTIME='B' THEN 'BEFORE' " +
        "         WHEN tr.FIRINGTIME='A' THEN 'AFTER' " +
        "       END AS firingtime, " +
        "       CASE " +
        "         WHEN tr.TYPE='R' THEN 'ROW' " +
        "         WHEN tr.TYPE='S' THEN 'STATEMENT' " +
        "       END AS type, " +
        "       t.TABLENAME AS TABLENAME " +
        "from SYS.SYSTRIGGERS tr, SYS.SYSSCHEMAS sc, SYS.SYSTABLES t " +
        "where TRIGGERNAME = ? " +
        "and sc.SCHEMANAME = ? " +
        "and tr.SCHEMAID = sc.SCHEMAID " +
        "and tr.TABLEID = t.TABLEID ";
    
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerDetailsTab.class);

	public TriggerDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Trigger details SQL: "+SQL);
            s_log.debug("Trigger name: "+doi.getSimpleName());
            s_log.debug("Trigger schema: "+doi.getSchemaName());
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
        pstmt.setString(2, doi.getSchemaName());
		return pstmt;
	}
}
