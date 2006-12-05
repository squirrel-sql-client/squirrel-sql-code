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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This class will display the details for an Informix sequence.
 *
 * @author manningr
 */
public class SequenceDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SequenceDetailsTab.class);

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
        "SELECT  T2.owner     AS sequence_owner, " +
        "       T2.tabname   AS sequence_name, " +
        "       T1.min_val   AS min_value, " +
        "       T1.max_val   AS max_value, " +
        "       T1.inc_val   AS increment_by, " +
        "       case T1.cycle " +
        "         when '0' then 'NOCYCLE' " +
        "         else 'CYCLE' " +
        "       end AS cycle_flag, " +
        "       case T1.order " +
        "         when '0' then 'NOORDER' " +
        "         else 'ORDER' " +
        "        end AS order_flag, " +
        "       T1.cache     AS cache_size " +
        "FROM    syssequences AS T1, " +
        "       systables    AS T2 " +
        "WHERE   T2.tabid     = T1.tabid " +
        "and T2.owner = ? " +        
        "and T2.tabname = ? ";
    
	public SequenceDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
