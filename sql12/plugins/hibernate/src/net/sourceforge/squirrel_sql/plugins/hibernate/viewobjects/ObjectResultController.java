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

   public void displayObjects(HibernateConnection con, String hqlQuery)
   {

      int maxNumResults = -1;
      if (_session.getProperties().getSQLLimitRows())
      {
         maxNumResults = _session.getProperties().getSQLNbrRowsToShow();
      }

      QueryListCreatorListener queryListCreatorListener = new QueryListCreatorListener()
      {
         @Override
         public void listRead(QueryListCreator queryListCreator)
         {
            onListRead(queryListCreator);
         }
      };

      WaitPanel waitPanel = new WaitPanel(hqlQuery);
      _objectResultTabbedPane.addTab(waitPanel.getTitle(), waitPanel);
      _objectResultTabbedPane.setSelectedComponent(waitPanel);
      new QueryListCreator(queryListCreatorListener, hqlQuery, maxNumResults, con, _session, waitPanel).execute();

   }

   private void onListRead(QueryListCreator queryListCreator)
   {

      for (int i = 0; i < _objectResultTabbedPane.getTabCount(); i++)
      {
         if(_objectResultTabbedPane.getComponentAt(i) == queryListCreator.getWaitPanel())
         {
            _objectResultTabbedPane.removeTabAt(i);
            break;
         }
      }
         
      List list = queryListCreator.getList();

      if(null == list)
      {
         return;
      }

      ObjectResultTabControllerListener l = new ObjectResultTabControllerListener()
      {
         @Override
         public void closeTab(ObjectResultTabController toClose)
         {
            onCloseTab(toClose);
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


   private void onCloseTab(ObjectResultTabController toClose)
   {
      for (int i = 0; i < _objectResultTabbedPane.getTabCount(); i++)
      {
         if(_objectResultTabbedPane.getComponentAt(i) == toClose.getPanel())
         {
            _objectResultTabbedPane.removeTabAt(i);
            break;
         }
      }
   }
}
