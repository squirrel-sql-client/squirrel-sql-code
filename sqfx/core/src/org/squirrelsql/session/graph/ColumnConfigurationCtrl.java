package org.squirrelsql.session.graph;

import javafx.scene.layout.HBox;

public class ColumnConfigurationCtrl
{
   private final HBox _panel;

   public ColumnConfigurationCtrl()
   {
      _panel = new HBox();
      _panel.setSpacing(3);

      AggregateFunctionCtrl aggregateFunctionCtrl = new AggregateFunctionCtrl(false);

      GraphListCheckBoxWA graphListCheckBox = new GraphListCheckBoxWA(b -> aggregateFunctionCtrl.setEnabled(b));

      _panel.getChildren().add(graphListCheckBox);
      _panel.getChildren().add(aggregateFunctionCtrl);
      _panel.getChildren().add(new FilterCtrl());
   }

   public HBox getPanel()
   {
      return _panel;
   }
}
