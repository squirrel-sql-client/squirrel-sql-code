package org.squirrelsql.session.graph;

import javafx.scene.control.Tab;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.SessionTabContext;

public class GraphAccess
{
   public static Tab newQueryBuilder(SessionTabContext sessionTabContext)
   {
      Tab tab = new Tab(new I18n(GraphAccess.class).t("graph.new.graph.title"));

      tab.setContent(new GraphPaneCtrl().getPane());

      tab.setClosable(false);

      return tab;
   }
}
