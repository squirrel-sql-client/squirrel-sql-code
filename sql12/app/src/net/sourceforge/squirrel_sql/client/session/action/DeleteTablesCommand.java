package net.sourceforge.squirrel_sql.client.session.action;
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
import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * @version 	$Id: DeleteTablesCommand.java,v 1.3 2007-02-20 00:05:47 manningr Exp $
 * @author		Rob Manning
 */
public class DeleteTablesCommand implements ICommand
{
    /** Logger for this class. */
    private final ILogger s_log =
        LoggerController.createLogger(DeleteTablesCommand.class);
    
    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DeleteTablesCommand.class);
    
    /** Current session. */
	private final ISession _session;

	/** Tables that have records to be deleted. */
	private final List<ITableInfo> _tables;
    
	/**
	 * Ctor.
	 *
	 * @param	session		Current session..
	 * @param	tables		Array of <TT>IDatabaseObjectInfo</TT> objects
	 * 						representing the tables to be deleted.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public DeleteTablesCommand(ISession session, List<ITableInfo> tables)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (tables == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo[] == null");
		}

		_session = session;
		_tables = tables;
	}

	/**
	 * Delete records from the selected tables in the object tree.
	 */
	public void execute()
	{
		final String sqlSep = _session.getProperties().getSQLStatementSeparator();
        final SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
        List<ITableInfo> orderedTables = _tables;
        String cascadeClause = null;
        try {
            orderedTables = SQLUtilities.getDeletionOrder(_tables);
        } catch (SQLException e) {
            s_log.error("Unexpected exception while attempting to order tables", e);
        }
        try {
            cascadeClause = md.getCascadeClause();
        } catch (SQLException e) {
            s_log.error("Unexpected exception while attempting to get cascade clause", e);
        }
		final StringBuffer buf = new StringBuffer();
        for (ITableInfo ti : orderedTables)
		{
            buf.append("DELETE FROM ").append(ti.getQualifiedName());
            if (cascadeClause != null && !cascadeClause.equals("")) {
                buf.append(" ").append(cascadeClause);
            }
            buf.append(sqlSep).append(" ").append('\n');
		}
        SQLExecuterTask executer = 
                        new SQLExecuterTask(_session, buf.toString(), null);
        
        // Execute the sql synchronously
		executer.run();
        
		// Use this to run asynch
		// _session.getApplication().getThreadPool().addTask(executer);
	}        
}
