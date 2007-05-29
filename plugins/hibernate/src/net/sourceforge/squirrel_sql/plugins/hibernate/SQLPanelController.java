package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.db.Utils;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import org.jdesktop.layout.LayoutStyle;

public class SQLPanelController
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SQLPanelController.class);


   private IHQLTabController _hqlTabController;
   private ISession _sess;
   private HibernatePluginResources _resource;
   private HibernateConnection _con;
   private AbstractAction _convertToSQL;
   private IHqlEntryPanelManager _hqlEntryPanelManager;

   public SQLPanelController(IHqlEntryPanelManager hqlEntryPanelManager, IHQLTabController hqlTabController, ISession sess, HibernatePluginResources resource)
   {
      _hqlEntryPanelManager = hqlEntryPanelManager;
      _hqlTabController = hqlTabController;
      _sess = sess;
      _resource = resource;

      _convertToSQL = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onConvertToHQL();
         }
      };
      //_convertToSQL.putValue(AbstractAction.SMALL_ICON, _resource.getIcon(HibernatePluginResources.IKeys.HQL_IMAGE));

      //_convertToSQL.putValue(AbstractAction.SHORT_DESCRIPTION,  "HQL to SQL");
      _convertToSQL.putValue(AbstractAction.NAME,  "HQL to SQL");

      _convertToSQL.setEnabled(false);

      _hqlTabController.addToToolbar(_convertToSQL);
   }

   private void onConvertToHQL()
   {
      String hql = _hqlEntryPanelManager.getEntryPanel().getSQLToBeExecuted();

      if(null != hql && 0 != hql.trim().length())
      {


         ArrayList<String> list = null;

         long begin = System.currentTimeMillis();
         long duration = 0;
         try
         {
            list = _con.generateSQL(hql);
            duration = System.currentTimeMillis() - begin;
         }
         catch (Exception e)
         {
            Throwable t = Utilities.getDeepestThrowable(e);
            _sess.getApplication().getMessageHandler().showErrorMessage(t);
            return;
         }

         StringBuffer sqls = new StringBuffer();

         String sep = _sess.getProperties().getSQLStatementSeparator();



         for (String sql : list)
         {
            sqls.append(sql);

            if(1 < sep.length())
            {
               sqls.append("\n");
            }
            sqls.append(sep).append("\n\n");
         }

         _hqlTabController.displaySQLs(sqls.toString());


         // i18n[SQLPanelController.hqlToSqlSuccess=Generated {0} SQL(s) in {1} milliseconds.]
         _sess.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SQLPanelController.hqlToSqlSuccess",list.size(), duration));

      }
   }

   public void setConnection(HibernateConnection con)
   {
      _con = con;

      if(null == _con)
      {
         _convertToSQL.setEnabled(false);
      }
      else
      {
         _convertToSQL.setEnabled(true);
      }
   }
}
