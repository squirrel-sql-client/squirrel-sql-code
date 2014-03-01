package net.sourceforge.squirrel_sql.plugins.h2.tab;
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
 * This class will display the details for an H2 sequence.
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
        "SELECT SEQUENCE_CATALOG,SEQUENCE_SCHEMA, " +
        "CURRENT_VALUE,INCREMENT,IS_GENERATED,REMARKS " +
        "FROM INFORMATION_SCHEMA.SEQUENCES " +
        "WHERE SEQUENCE_SCHEMA = ? " +
        "AND SEQUENCE_NAME = ? ";
    
	public SequenceDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Sequence details SQL: "+SQL);
            s_log.debug("Sequence schema: "+doi.getSchemaName());
            s_log.debug("Sequence name: "+doi.getSimpleName());
        }

		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
        pstmt.setString(1, doi.getSchemaName());
        pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
    
}
