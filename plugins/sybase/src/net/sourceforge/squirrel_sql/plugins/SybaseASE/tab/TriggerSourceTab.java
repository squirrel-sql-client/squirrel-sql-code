package net.sourceforge.squirrel_sql.plugins.SybaseASE.tab;
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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class will display the source for a Sybase trigger.
 *
 */
public class TriggerSourceTab extends FormattedSourceTab
{

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(TriggerSourceTab.class);
                
	/** SQL that retrieves the source of a stored procedure. */
	private static String SQL =
        "SELECT trigger_defs.text " +
        "FROM sysobjects tables , sysobjects triggers, syscomments trigger_defs " +
        "where triggers.type = 'TR' " +
        "and triggers.id = trigger_defs.id " +
        "and triggers.deltrig = tables.id " +
        "and tables.loginame = ? " +
        // TODO: figure out how to get then name of the table that the trigger 
        //       is on. 
        //"and tables.name = ? " +
        "and triggers.name = ? ";
        
	public TriggerSourceTab(String hint, String stmtSep)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL for View source tab: "+SQL);
            s_log.debug("Binding for param 1: "+doi.getCatalogName());
            s_log.debug("Binding for param 2: "+doi.getSimpleName());
        }
        
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getCatalogName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
