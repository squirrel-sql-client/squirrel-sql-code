package org.squirrelsql.session.graph;

import javafx.scene.control.Tab;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.SessionTabContext;

public class GraphAccess
{
   public static Tab newQueryBuilder(SessionTabContext sessionTabContext)
   {
      Tab tab = new Tab();


      tab.setGraphic(new GraphTabHeaderCtrl(sessionTabContext.getSession()).getGraphTabHeader());
      tab.setContent(new GraphPaneCtrl().getPane());

      return tab;
   }
}
