package net.sourceforge.squirrel_sql.plugins.db2.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql;

/**
 * This class will display the Check constraints for an DB2 table.
 */
public class DB2CheckConstraintsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DB2CheckConstraintsTab.class);

	private final static ILogger s_log = LoggerController.createLogger(DB2CheckConstraintsTab.class);

	/** Object that contains methods for retrieving SQL that works for each DB2 platform */
	private final DB2Sql db2Sql;

	public DB2CheckConstraintsTab(DB2Sql db2Sql)
	{
		super(s_stringMgr.getString("DB2CheckConstraintsTab.title"), s_stringMgr.getString("DB2CheckConstraintsTab.hint"), false);
		this.db2Sql = db2Sql;
	}


	@Override
	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		String sql = db2Sql.getCheckConstraintSql();

		if(StringUtilities.isEmpty(sql, true))
		{
			return null;
		}

		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
