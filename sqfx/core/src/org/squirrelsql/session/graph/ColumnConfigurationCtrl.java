package org.squirrelsql.session.graph;

import javafx.scene.layout.HBox;

public class ColumnConfigurationCtrl
{
   private final HBox _panel;

   public ColumnConfigurationCtrl()
   {
      _panel = new HBox();
      _panel.setSpacing(3);

      AggregateFunctionPane aggregateFunctionPane = new AggregateFunctionPane(false);

      GraphListCheckBoxWA graphListCheckBox = new GraphListCheckBoxWA(b -> aggregateFunctionPane.setEnabled(b));

      _panel.getChildren().add(graphListCheckBox);
      _panel.getChildren().add(aggregateFunctionPane);
      _panel.getChildren().add(new FilterPane());
      _panel.getChildren().add(new OrderByPane());
   }

   public HBox getPanel()
   {
      return _panel;
   }
}
