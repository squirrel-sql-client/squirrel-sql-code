package net.sourceforge.squirrel_sql.plugins.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.DataSetTextArea;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class CacheStatsAndQueryPlanReader implements Runnable
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CacheStatsAndQueryPlanReader.class);

   private static final ILogger s_log = LoggerController.createLogger(CacheStatsAndQueryPlanReader.class);

   private final String _cleanedSQL;
   private final DataSetTextArea _dataSetTextArea;
   private Connection _con;
   private JScrollPane _scrollPane;
   private volatile boolean _disposed;

   public CacheStatsAndQueryPlanReader(String cleanedSQL, Connection con, DataSetTextArea dataSetTextArea, JScrollPane scrollPane)
   {
      _cleanedSQL = cleanedSQL;
      _con = con;
      _dataSetTextArea = dataSetTextArea;
      _scrollPane = scrollPane;
   }

   @Override
   public void run()
   {
      readCacheStatsAndQueryPlan();
   }

   private void readCacheStatsAndQueryPlan()
   {
      try(PreparedStatement pStat = _con.prepareStatement("SELECT DBUtilities.GetPlan('" + getNameSpaceName(_con) + "', ?)"))
      {
         pStat.setString(1, _cleanedSQL);
         ResultSet res = pStat.executeQuery();

         if(_disposed)
         {
            return;
         }

         if(false == res.next())
         {
            if(_disposed)
            {
               return;
            }

            String errMsg = s_stringMgr.getString("StatisticsAndQueryPlanAction.no.result");
            Main.getApplication().getMessageHandler().showErrorMessage(errMsg);
            s_log.error(new IllegalStateException(errMsg));

            displayText(errMsg);
         }
         else
         {
            if(_disposed)
            {
               return;
            }
            String statisticAndQueryPlan = res.getString(1);

            if(_disposed)
            {
               return;
            }

            displayText(statisticAndQueryPlan);
         }

      }
      catch(Exception e)
      {
         if(_disposed)
         {
            return;
         }

         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("StatisticsAndQueryPlanAction.failed.to.query.statistics", e));
         displayText(s_stringMgr.getString("StatisticsAndQueryPlanAction.failed.to.query.statistics", e.toString()));
         s_log.error(e);
      }

   }

   private void displayText(String statisticAndQueryPlan)
   {
      SwingUtilities.invokeLater(() -> _dataSetTextArea.setText(statisticAndQueryPlan));
      SwingUtilities.invokeLater(() -> GUIUtils.forceScrollToBegin(_scrollPane));
   }

   private String getNameSpaceName(Connection con)
   {
      try
      {
         String url = con.getMetaData().getURL();

         String[] splits = url.split("/");

         if(splits.length < 4)
         {
            throw new IllegalStateException("Expected 4th split(\"/\") of " + url + " to be the Intersystems Cache name space");
         }

         String nameSpace = splits[3];

         return nameSpace;
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public void setDisposed()
   {
      _disposed = true;
   }
}
