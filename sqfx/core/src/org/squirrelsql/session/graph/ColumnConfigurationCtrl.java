package org.squirrelsql.session.graph;

import javafx.scene.layout.HBox;

public class ColumnConfigurationCtrl
{

   public HBox getPanel()
   {
      HBox panel;
      panel = new HBox();
      panel.setSpacing(3);

      AggregateFunctionPane aggregateFunctionPane = new AggregateFunctionPane(false);

      GraphListCheckBoxWA graphListCheckBox = new GraphListCheckBoxWA(b -> aggregateFunctionPane.setEnabled(b));

      panel.getChildren().add(graphListCheckBox);
      panel.getChildren().add(aggregateFunctionPane);
      panel.getChildren().add(new FilterPane());
      panel.getChildren().add(new OrderByPane());
      return panel;
   }
}
