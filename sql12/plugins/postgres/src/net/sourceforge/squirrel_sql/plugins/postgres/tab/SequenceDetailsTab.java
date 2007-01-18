package net.sourceforge.squirrel_sql.plugins.postgres.tab;
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
 * This class will display the details for an DB2 sequence.
 *
 * @author manningr
 */
public class SequenceDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SequenceDetailsTab.class);

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(SequenceDetailsTab.class);
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[SequenceDetailsTab.title=Details]
		String TITLE = s_stringMgr.getString("SequenceDetailsTab.title");
		// i18n[SequenceDetailsTab.hint=Display sequence details]
		String HINT = s_stringMgr.getString("SequenceDetailsTab.hint");
	}

	/** SQL that retrieves the data. */
	private static final String SQL =
        "SELECT last_value, max_value, min_value, cache_value, increment_by, is_cycled " +
        "FROM  ";
    
	public SequenceDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        String sql = getSQL();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Sequence details SQL: "+sql);
            s_log.debug("Sequence schema: "+doi.getSchemaName());
            s_log.debug("Sequence name: "+doi.getSimpleName());
        }

		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
		return pstmt;
	}
    
    private String getSQL() {
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        StringBuffer result = new StringBuffer();
        result.append(SQL);
        result.append(doi.getSchemaName());
        result.append(".");
        result.append(doi.getSimpleName());
        return result.toString();
    }
}
