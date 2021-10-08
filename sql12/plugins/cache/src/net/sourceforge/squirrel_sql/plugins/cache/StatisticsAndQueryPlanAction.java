package net.sourceforge.squirrel_sql.plugins.cache;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.custompanel.CustomResultPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.DataSetTextArea;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;


public class StatisticsAndQueryPlanAction extends SquirrelAction implements ISQLPanelAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(StatisticsAndQueryPlanAction.class);

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
         }

         // Needed to remove comments
         IQueryTokenizer queryTokenizer = _sqlPanelAPI.getSession().getQueryTokenizer();
         queryTokenizer.setScriptToTokenize(sql);

         Connection con = _sqlPanelAPI.getSession().getSQLConnection().getConnection();
         PreparedStatement pStat = con.prepareStatement("SELECT DBUtilities.GetPlan('" + getNameSpaceName(con) + "', ?)");
         pStat.setString(1, queryTokenizer.nextQuery().getQuery());

         ResultSet res = pStat.executeQuery();
         if(false == res.next())
         {
            Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("StatisticsAndQueryPlanAction.no.result"));
            return;
         }

         String statisticAndQueryPlan = res.getString(1);

         CustomResultPanel resultPanel = new CustomResultPanel(new GridLayout(1,1));
         resultPanel.setDisposeListener(() -> onDispose());

         DataSetTextArea dataSetTextArea = new DataSetTextArea(statisticAndQueryPlan);
         resultPanel.add(new JScrollPane(dataSetTextArea));
         SwingUtilities.invokeLater(() -> dataSetTextArea.scrollRectToVisible(new Rectangle(0, 0)));


         ImageIcon icon = _resources.getIcon(CachePluginResources.IKeys.INTERSYSTEMS_CAC);
         _sqlPanelAPI.getSQLResultExecuter().addCustomResult(resultPanel, s_stringMgr.getString("StatisticsAndQueryPlanAction.tab.title"), icon);
      }
      catch(Exception ex)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("StatisticsAndQueryPlanAction.failed.to.query.statistics", ex));
         throw Utilities.wrapRuntime(ex);
      }
   }

   private void onDispose()
   {
      System.out.println("StatisticsAndQueryPlanAction.onDispose");
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

   @Override
   public void setSQLPanel(ISQLPanelAPI sqlPanelAPI)
   {
      _sqlPanelAPI = sqlPanelAPI;
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
