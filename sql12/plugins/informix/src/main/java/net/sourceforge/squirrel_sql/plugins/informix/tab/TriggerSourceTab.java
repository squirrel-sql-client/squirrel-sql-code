package net.sourceforge.squirrel_sql.plugins.informix.tab;
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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This class will display the source for an Informix trigger.
 *
 * @author manningr
 */
public class TriggerSourceTab extends InformixSourceTab
{
	/** 
	 * SQL that retrieves the source of a stored procedure.
	 *  
     * Note: datakey and seqno are technically not needed to display the source 
     * for the trigger source code, however, on some versions of Informix it 
     * is an error to order by a column that is not in the select list, 
     * yielding this exception:
     * 
     *      Error: ORDER BY column (datakey) must be in SELECT list. 
     *      SQLState: IX000
     *      ErrorCode: -309
	 */
	private static String SQL =
	    "SELECT  T2.data, T2.datakey, T2.seqno " +
	    "FROM    informix.systriggers AS T1, informix.systrigbody AS T2 " +
	    "WHERE   trigname = ? " +
	    "AND     T2.trigid = T1.trigid " +
	    "AND     datakey IN ('D', 'A') " +
	    "ORDER   BY datakey DESC, seqno ";
    
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerSourceTab.class);

	public TriggerSourceTab(String hint)
	{
		super(hint);
        sourceType = TRIGGER_TYPE;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab#createStatement()
	 */
	@Override
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL: "+SQL);
            s_log.debug("trigname="+doi.getSimpleName());
        }
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
