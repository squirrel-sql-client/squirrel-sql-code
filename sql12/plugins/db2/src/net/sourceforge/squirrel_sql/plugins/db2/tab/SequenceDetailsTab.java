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
/**
 * This class will display the details for an DB2 sequence.
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
        "SELECT  T1.OWNER     AS sequence_owner, " +
        "        T1.DEFINER   AS sequence_definer, " +
        "       T1.SEQNAME   AS sequence_name, " +
        "       T2.TYPENAME AS data_type, " +
        "       T1.MINVALUE   AS min_value, " +
        "       T1.MAXVALUE   AS max_value, " +
        "       T1.INCREMENT   AS increment_by, " +
        "       case T1.CYCLE " +
        "         when 'Y' then 'CYCLE' " +
        "         else 'NOCYCLE' " +
        "       end AS cycle_flag, " +
        "       case T1.ORDER " +
        "         when 'Y' then 'ORDERED' " +
        "         else 'UNORDERED' " +
        "        end AS order_flag, " +
        "       T1.CACHE AS cache_size, " +
        "       T1.CREATE_TIME AS create_time, " +
        "       T1.ALTER_TIME AS last_alter_time, " +
        "       case T1.ORIGIN " +
        "         when 'U' then 'User' " +
        "         when 'S' then 'System' " +
        "       end AS origin, " +
        "       T1.REMARKS AS comment " +
        "FROM    SYSCAT.SEQUENCES AS T1, " +
        "        SYSCAT.DATATYPES AS T2 " +
        "WHERE T1.DATATYPEID = T2.TYPEID " +
        "and T1.SEQSCHEMA = ? " +
        "and T1.SEQNAME = ? ";
    
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
