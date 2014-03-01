package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstituteRoot;

import javax.swing.*;
import java.util.List;

public class ObjectResultController
{
   private static ILogger s_log = LoggerController.createLogger(ObjectResultController.class);
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectResultController.class);


   private JTabbedPane _objectResultTabbedPane;
   private ISession _session;
   private HibernatePluginResources _resource;

   public ObjectResultController(ISession session, HibernatePluginResources resource)
   {
      _session = session;
      _resource = resource;
      final SessionProperties props = session.getProperties();
      _objectResultTabbedPane = UIFactory.getInstance().createTabbedPane(props.getSQLExecutionTabPlacement());

   }

   public JTabbedPane getPanel()
   {
      return _objectResultTabbedPane;
   }

   public void displayObjects(HibernateConnection con, String hqlQuery, boolean limitObjectCount, int limitObjectCountVal, boolean useSessionConnection)
   {
      int maxNumResults = -1;
      if (limitObjectCount)
      {
         maxNumResults = limitObjectCountVal;
      }

      QueryListCreatorListener queryListCreatorListener = new QueryListCreatorListener()
      {
         @Override
         public void queryExecuted(QueryListCreator queryListCreator)
         {
            onQueryExecuted(queryListCreator);
         }
      };

      WaitPanelListener waitPanelListener = new WaitPanelListener()
      {
         @Override
         public void removeWaitPanel(WaitPanel waitPanel)
         {
            onCloseTab(waitPanel);
         }
      };

      WaitPanel waitPanel = new WaitPanel(hqlQuery, _resource, waitPanelListener);
      _objectResultTabbedPane.addTab(waitPanel.getTitle(), waitPanel);
      _objectResultTabbedPane.setSelectedComponent(waitPanel);
      new QueryListCreator(queryListCreatorListener, hqlQuery, maxNumResults, useSessionConnection, con, _session, waitPanel).execute();

   }

   private void onQueryExecuted(QueryListCreator queryListCreator)
   {
      removeOldErrorPanel(queryListCreator.getWaitPanel());

      if (queryListCreator.getWaitPanel().isDisplayingError())
      {
         return;
      }

      for (int i = 0; i < _objectResultTabbedPane.getTabCount(); i++)
      {
         if (_objectResultTabbedPane.getComponentAt(i) == queryListCreator.getWaitPanel())
         {
            _objectResultTabbedPane.removeTabAt(i);
            break;
         }
      }

      List<ObjectSubstituteRoot> list = queryListCreator.getList();

      if (null == list)
      {
         return;
      }

      ObjectResultTabControllerListener l = new ObjectResultTabControllerListener()
      {
         @Override
         public void closeTab(ObjectResultTabController toClose)
         {
            onCloseTab(toClose.getPanel());
         }
      };


      String hqlQuery = queryListCreator.getHqlQuery();
      int maxNumResults = queryListCreator.getMaxNumResults();
      HibernateConnection con = queryListCreator.getConnection();

      ObjectResultTabController ortc = new ObjectResultTabController(list, maxNumResults, con, hqlQuery, _resource, l, _session);
      int titelLen = Math.min(hqlQuery.length(), 14);
      String title = hqlQuery.trim().substring(0, titelLen).replaceAll("\n", " ");
      _objectResultTabbedPane.addTab(title, ortc.getPanel());
      _objectResultTabbedPane.setSelectedComponent(ortc.getPanel());
   }

   private void removeOldErrorPanel(WaitPanel currentWaitPanel)
   {
      for (int i = 0; i < _objectResultTabbedPane.getTabCount(); i++)
      {
         if (currentWaitPanel != _objectResultTabbedPane.getComponentAt(i)
               && _objectResultTabbedPane.getComponentAt(i) instanceof WaitPanel
               && ((WaitPanel) _objectResultTabbedPane.getComponentAt(i)).isDisplayingError())
         {
            _objectResultTabbedPane.removeTabAt(i);
            break;
         }
      }
   }


   private void onCloseTab(JPanel panel)
   {
      for (int i = 0; i < _objectResultTabbedPane.getTabCount(); i++)
      {
         if(_objectResultTabbedPane.getComponentAt(i) == panel)
         {
            _objectResultTabbedPane.removeTabAt(i);
            break;
         }
      }
   }
}
