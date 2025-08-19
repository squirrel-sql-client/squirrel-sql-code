package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;
import java.sql.Connection;
import java.util.Map;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Displays the driver properties that were active when the connection was opened.
 * See {@link Connection#getClientInfo()}
 */
public class ClientPropertiesTab extends BaseDataSetTab
{
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ClientPropertiesTab.class);
	private static final ILogger s_log = LoggerController.createLogger(ClientPropertiesTab.class);


	public String getTitle()
	{
		return s_stringMgr.getString("ClientPropertiesTab.title");
	}

	public String getHint()
	{
		return s_stringMgr.getString("ClientPropertiesTab.hint");
	}

	protected IDataSet createDataSet() throws DataSetException
	{
		MapDataSet mapDataSet;

      try
      {
         mapDataSet = new MapDataSet(getSession().getSQLConnection().getConnection().getClientInfo());
      }
      catch(Exception e)
      {
			String errMsg = "Failed to call java.sql.Connection.getClientInfo(): %s".formatted(Utilities.getToStringSave(e));
			s_log.warn(errMsg, e);
			mapDataSet = new MapDataSet(Map.of(errMsg, "" + null));
      }

      return mapDataSet;
	}
}
