package org.squirrelsql.session.graph;

import javafx.scene.control.Tab;
import org.squirrelsql.services.Dao;
import org.squirrelsql.session.SessionTabAccess;
import org.squirrelsql.session.SessionTabContext;

public class GraphAccess
{
   private final SessionTabContext _sessionTabContext;
   private final SessionTabAccess _sessionTabAccess;

   public GraphAccess(SessionTabContext sessionTabContext, SessionTabAccess sessionTabAccess)
   {
      _sessionTabContext = sessionTabContext;
      _sessionTabAccess = sessionTabAccess;

      loadGraphTab(Dao.loadGraphPersistence());
   }

   public void onNewGraph()
   {
      loadGraphTab(new GraphPersistence());
   }

   private void loadGraphTab(GraphPersistence graphPersistence)
   {
      Tab tab = new Tab();

      GraphTableDndChannel graphTableDndChannel = new GraphTableDndChannel();

      tab.setGraphic(new GraphTabHeaderCtrl(graphTableDndChannel, graphPersistence.getTabTitle()).getGraphTabHeader());
      tab.setContent(new GraphPaneCtrl(graphTableDndChannel, _sessionTabContext.getSession(), graphPersistence).getPane());

      _sessionTabAccess.addAndSelectTab(tab);
   }
}
