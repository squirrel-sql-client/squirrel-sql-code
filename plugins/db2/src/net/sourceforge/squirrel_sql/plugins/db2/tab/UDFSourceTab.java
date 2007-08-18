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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
/**
 * This class will display the source for an DB2 User-Defined function.
 *
 * @author manningr
 */
public class UDFSourceTab extends FormattedSourceTab
{
	/** SQL that retrieves the source of a user-defined function. */
	private static String SQL =
	    "SELECT " +
	    "case " +
	    "    when body is null then 'No source available' " +
	    "    else body " +
	    "end " + 	    
	    "FROM SYSIBM.SYSFUNCTIONS " +
	    "WHERE schema = ? " +
	    "AND name = ? " +
	    "AND implementation is null ";
	
	/** SQL that retrieves the source of a user-defined function on OS/400 */
	private static final String OS_400_SQL = 
	    "select " +
	    "case " +
	    "    when body = 'SQL' and routine_definition is not null then routine_definition " +
	    "    when body = 'SQL' and routine_definition is null then 'no source available' " +
	    "    when body = 'EXTERNAL' and external_name is not null then external_name " +
	    "    when body = 'EXTERNAL' and external_name is null then 'system-generated function' " +
	    "end as definition " +
	    "from QSYS2.SYSFUNCS " +
	    "where routine_schema = ? " +
	    "and routine_name = ? ";	    
	
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(UDFSourceTab.class);

    /** whether or not we are connected to OS/400 */
    private boolean isOS400 = false;	
	
    /**
     * Constructor
     * 
     * @param hint
     * @param stmtSep
     * @param isOS400 whether or not we are connected to OS/400
     */
	public UDFSourceTab(String hint, String stmtSep, boolean isOS400)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null);
        this.isOS400 = isOS400;
	}

    /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab#createStatement()
     */
    @Override	
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        String sql = SQL;
        if (isOS400) {
            sql = OS_400_SQL;
        }		
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL: "+sql);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("udf name="+doi.getSimpleName());
        }
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
