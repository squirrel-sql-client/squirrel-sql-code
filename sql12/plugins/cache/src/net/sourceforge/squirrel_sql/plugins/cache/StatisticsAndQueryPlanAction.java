package net.sourceforge.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.custompanel.CustomResultPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.DataSetTextArea;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StatisticsAndQueryPlanAction extends SquirrelAction implements ISQLPanelAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(StatisticsAndQueryPlanAction.class);

   private static final ILogger s_log = LoggerController.createLogger(StatisticsAndQueryPlanAction.class);

   private final ExecutorService _executorService = Executors.newCachedThreadPool();

   private ISQLPanelAPI _sqlPanelAPI;
   private CachePluginResources _resources;


   public StatisticsAndQueryPlanAction(CachePluginResources resources)
   {
      super(Main.getApplication(), resources);
      _resources = resources;
   }

   public void actionPerformed(ActionEvent e)
   {
      try
      {
         if(null == _sqlPanelAPI)
         {
            return;
         }

         String sql = _sqlPanelAPI.getSQLEntryPanel().getSQLToBeExecuted();

         if( StringUtilities.isEmpty(sql,true) )
         {
            Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("StatisticsAndQueryPlanAction.empty.sql"));
            return;
         }

         // Needed to remove comments
         IQueryTokenizer queryTokenizer = _sqlPanelAPI.getSession().getQueryTokenizer();
         queryTokenizer.setScriptToTokenize(sql);
         String cleanedSQL = queryTokenizer.nextQuery().getQuery();

         CustomResultPanel resultPanel = new CustomResultPanel(new GridLayout(1,1));

         ImageIcon icon = _resources.getIcon(CachePluginResources.IKeys.INTERSYSTEMS_CAC);
         _sqlPanelAPI.getSQLResultExecuter().addCustomResult(resultPanel, s_stringMgr.getString("StatisticsAndQueryPlanAction.tab.title"), icon);

         DataSetTextArea dataSetTextArea = new DataSetTextArea(s_stringMgr.getString("StatisticsAndQueryPlanAction.reading.stats.for.sql", cleanedSQL));

         JScrollPane scrollPane = new JScrollPane(dataSetTextArea);
         resultPanel.add(scrollPane);
         SwingUtilities.invokeLater(() -> dataSetTextArea.scrollRectToVisible(new Rectangle(0, 0)));

         Connection con = _sqlPanelAPI.getSession().getSQLConnection().getConnection();
         CacheStatsAndQueryPlanReader readerTask = new CacheStatsAndQueryPlanReader(cleanedSQL, con, dataSetTextArea, scrollPane);
         _executorService.submit(readerTask);

         resultPanel.setDisposeListener(() -> readerTask.setDisposed());
      }
      catch(Exception ex)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("StatisticsAndQueryPlanAction.failed.to.query.statistics", ex));
         throw Utilities.wrapRuntime(ex);
      }
   }

   @Override
   public void setSQLPanel(ISQLPanelAPI sqlPanelAPI)
   {
      _sqlPanelAPI = sqlPanelAPI;
      setEnabled(null != _sqlPanelAPI);
   }



   public static void main(String[] args)
   {
      //String url = "jdbc:Cache://srv-cache-dev:1972/SHD_MAE_MWE/dsfdfdf/sfsdfsd";
      String url = "jdbc:Cache://srv-cache-dev:1972/SHD_MAE_MWE";

      String[] splits = "jdbc:Cache://srv-cache-dev:1972/SHD_MAE_MWE".split("/");

      if(splits.length < 4)
      {
         throw new IllegalStateException("Expected 4th split(\"/\") of " + url + " to be the Intersystems Cache name space");
      }

      String nameSpace = splits[3];

      System.out.println("nameSpace = " + nameSpace);

   }
}
