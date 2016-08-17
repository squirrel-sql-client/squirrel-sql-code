package org.squirrelsql.session.graph;

import javafx.scene.control.Tab;
import org.squirrelsql.services.Dao;
import org.squirrelsql.session.SessionTabAccess;
import org.squirrelsql.session.SessionTabContext;

import java.util.List;

public class GraphAccess
{
   private final SessionTabContext _sessionTabContext;
   private final SessionTabAccess _sessionTabAccess;

   public GraphAccess(SessionTabContext sessionTabContext, SessionTabAccess sessionTabAccess)
   {
      _sessionTabContext = sessionTabContext;
      _sessionTabAccess = sessionTabAccess;

      loadGraphTabs(Dao.loadGraphPersistences(_sessionTabContext.getSession().getAlias()));
   }

   public void onNewGraph()
   {
      loadGraphTab(new GraphPersistenceWrapper(), true);
   }

   private void loadGraphTabs(List<GraphPersistenceWrapper> graphPersistenceWrappers)
   {
      for (GraphPersistenceWrapper graphPersistenceWrapper : graphPersistenceWrappers)
      {
         loadGraphTab(graphPersistenceWrapper, false);
      }
   }

   private void loadGraphTab(GraphPersistenceWrapper graphPersistenceWrapper, boolean selectTab)
   {
      Tab tab = new Tab();

      GraphChannel graphChannel = new GraphChannel();

      tab.setGraphic(new GraphTabHeaderCtrl(graphChannel, graphPersistenceWrapper.getTabTitle(), () -> onSelectTab(tab)).getGraphTabHeader());
      tab.setContent(new GraphPaneCtrl(graphChannel, _sessionTabContext.getSession(), graphPersistenceWrapper).getPane());

      _sessionTabAccess.addTab(tab, selectTab);
   }

   private void onSelectTab(Tab tab)
   {
      tab.getTabPane().getSelectionModel().select(tab);
   }
}
