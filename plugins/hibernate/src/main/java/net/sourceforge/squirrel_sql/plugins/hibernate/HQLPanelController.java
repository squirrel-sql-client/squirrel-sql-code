package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.plugins.hibernate.util.HqlQueryErrorUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class HQLPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(HQLPanelController.class);

   private static ILogger s_log = LoggerController.createLogger(HQLPanelController.class);

   private IHibernateTabController _hibernateTabController;
   private ISession _sess;
   private HibernatePluginResources _resource;
   private HibernateConnection _con;
   private AbstractAction _runHQL;
   private HQLEntryPanelManager _hqlEntryPanelManager;

   public HQLPanelController(IHibernateTabController hibernateTabController, ISession sess, HibernatePluginResources resource)
   {
      _hibernateTabController = hibernateTabController;
      _sess = sess;
      _resource = resource;

      _hqlEntryPanelManager = new HQLEntryPanelManager(_sess, resource, hibernateTabController.getHibernateConnectionProvider());

   }

   void initActions()
   {
      _runHQL = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRunHQL();
         }
      };


      _runHQL.putValue(AbstractAction.SMALL_ICON,  _resource.getIcon(HibernatePluginResources.IKeys.RUN_IMAGE));

      // i18n[hibernate.hqlToSqlLong=Run HQL]
      _runHQL.putValue(AbstractAction.NAME,  s_stringMgr.getString("hibernate.hqlToSqlLong"));

      // i18n[hibernate.hqlToSqlShort=Run HQL (ctrl + enter)]
      _runHQL.putValue(AbstractAction.SHORT_DESCRIPTION,  s_stringMgr.getString("hibernate.hqlToSqlShort"));


      _runHQL.setEnabled(false);

      _hibernateTabController.addToToolbar(_runHQL);

      KeyStroke ctrlEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK);
      _hqlEntryPanelManager.registerKeyboardAction(_runHQL, ctrlEnter);
   }


   private void onRunHQL()
   {
      if (false == _runHQL.isEnabled())
      {
         return;
      }

      String hql = _hqlEntryPanelManager.getEntryPanel().getSQLToBeExecuted();

      if (null == hql || 0 == hql.trim().length())
      {
         return;
      }

      final IQueryTokenizer queryTokenizer = _sess.getQueryTokenizer();
      final String statementSeparator = queryTokenizer.getSQLStatementSeparator();
      final String startOfLineComment = queryTokenizer.getLineCommentBegin();
      QueryTokenizer qt = new QueryTokenizer(statementSeparator, startOfLineComment, true);
      qt.setScriptToTokenize(hql);


      while (qt.hasQuery())
      {
         String hqlQuery = qt.nextQuery();
         doSQL(hqlQuery);

         if (_hibernateTabController.isDisplayObjects())
         {
            doObjects(hqlQuery);
         }
      }
   }


   private boolean doSQL(String hqlQuery)
   {
      ArrayList<String> sqls;

      long begin = System.currentTimeMillis();
      long duration;
      try
      {
         sqls = _con.generateSQL(hqlQuery);
         duration = System.currentTimeMillis() - begin;

         _hibernateTabController.displaySqls(sqls);

         // i18n[HQLPanelController.hqlToSqlSuccess=Generated {0} SQL(s) in {1} milliseconds.]
         _sess.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SQLPanelController.hqlToSqlSuccess", sqls.size(), duration));
         return true;
      }
      catch (Throwable e)
      {
         HqlQueryErrorUtil.handleHqlQueryError(e, _sess, true);
         return false;
      }
   }

   private void doObjects(String hqlQuery)
   {
      _hibernateTabController.displayObjects(_con, hqlQuery);
   }



   public void setConnection(HibernateConnection con)
   {
      _con = con;

      if(null == _con)
      {
         _runHQL.setEnabled(false);
      }
      else
      {
         _runHQL.setEnabled(true);
      }
   }

   public JComponent getComponent()
   {
      return _hqlEntryPanelManager.getComponent();
   }

   public void requestFocus()
   {
      _hqlEntryPanelManager.requestFocus();
   }
}
