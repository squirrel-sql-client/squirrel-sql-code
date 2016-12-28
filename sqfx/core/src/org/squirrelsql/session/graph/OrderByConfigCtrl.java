package org.squirrelsql.session.graph;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class OrderByConfigCtrl
{
   public OrderByConfigCtrl(GraphPersistenceWrapper graphPersistenceWrapper)
   {


   }

   public Pane getPane()
   {
      return new BorderPane(new Label("onOrderBy"));
   }
}
