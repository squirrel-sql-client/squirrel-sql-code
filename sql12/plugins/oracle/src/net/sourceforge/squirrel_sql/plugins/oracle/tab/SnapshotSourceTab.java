package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SnapshotSourceTab extends OracleSourceTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SnapshotSourceTab.class);


	/** SQL that retrieves the data. */
	private static final String SQL =
        "SELECT 'CREATE MATERIALIZED VIEW ' || TABLE_NAME || ' AS ', QUERY " +
        "FROM SYS.ALL_SNAPSHOTS " +
        "WHERE OWNER = ? AND TABLE_NAME = ? ";
    
	public SnapshotSourceTab()
	{
		// i18n[oracle.displaySnapshotDetails=Display materialized view details]
		super(s_stringMgr.getString("oracle.displaySnapshotDetails"));
        super.sourceType = OracleSourceTab.TABLE_TYPE;
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
