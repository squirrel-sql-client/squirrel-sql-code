package org.squirrelsql.session.graph;

import javafx.scene.control.Tab;
import org.squirrelsql.session.SessionTabContext;

public class GraphAccess
{
   public static Tab newQueryBuilder(SessionTabContext sessionTabContext)
   {
      Tab tab = new Tab();

      GraphTableDndChannel graphTableDndChannel = new GraphTableDndChannel();

      tab.setGraphic(new GraphTabHeaderCtrl(graphTableDndChannel).getGraphTabHeader());
      tab.setContent(new GraphPaneCtrl(graphTableDndChannel, sessionTabContext.getSession()).getPane());

      return tab;
   }
}
