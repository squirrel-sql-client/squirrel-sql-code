package net.sourceforge.squirrel_sql.plugins.cache.tap;

import java.sql.ResultSet;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTabSimple;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class CacheViewSourceTab extends FormattedSourceTabSimple
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CacheViewSourceTab.class);

	private final static ILogger s_log = LoggerController.createLogger(CacheViewSourceTab.class);


	public CacheViewSourceTab(ISession session)
	{
		super(s_stringMgr.getString("CacheViewSourceTab.view.tab.tooltip"), session);
		setAppendSeparator(false);
	}

	@Override
	protected String getSourceCode(ISession session, IDatabaseObjectInfo databaseObjectInfo)
	{
		if( databaseObjectInfo.getDatabaseObjectType() != DatabaseObjectType.VIEW)
		{
			return s_stringMgr.getString("CacheViewSourceTab.cannot.load.sourcecode.for.db.object.type", databaseObjectInfo.getDatabaseObjectType());
		}

		String sql =
				"SELECT VIEW_DEFINITION " +
				"FROM INFORMATION_SCHEMA.VIEWS " +
				"  WHERE TABLE_SCHEMA = '" + databaseObjectInfo.getSchemaName() + "' " +
				"    AND TABLE_NAME = '" + databaseObjectInfo.getSimpleName() + "' ";

		try(Statement statement = session.getSQLConnection().createStatement();
			 ResultSet res = statement.executeQuery(sql))
		{
			if(false == res.next())
			{
				String errMsg = s_stringMgr.getString("CacheViewSourceTab.could.not.find.view.source", databaseObjectInfo.getQualifiedName());
				s_log.error(errMsg);
				return errMsg;
			}

			return res.getString("VIEW_DEFINITION");

		}
		catch(Exception e)
		{
			String errMsg = s_stringMgr.getString("CacheViewSourceTab.failed.to.load.view.source", databaseObjectInfo.getQualifiedName(), e.toString());
			s_log.error(errMsg, e);

			return errMsg;
		}
	}
}