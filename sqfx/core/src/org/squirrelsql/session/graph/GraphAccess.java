package org.squirrelsql.session.graph;

import javafx.scene.control.Tab;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.SessionTabContext;

public class GraphAccess
{
   public static Tab newQueryBuilder(SessionTabContext sessionTabContext)
   {
      Tab tab = new Tab();

      GraphTableDndChannel graphTableDndChannel = new GraphTableDndChannel();

      tab.setGraphic(new GraphTabHeaderCtrl(graphTableDndChannel, sessionTabContext.getSession()).getGraphTabHeader());
      tab.setContent(new GraphPaneCtrl(graphTableDndChannel).getPane());

      return tab;
   }
}
