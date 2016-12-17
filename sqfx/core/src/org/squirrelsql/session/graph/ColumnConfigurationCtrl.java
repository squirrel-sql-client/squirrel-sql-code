package org.squirrelsql.session.graph;

import javafx.scene.layout.HBox;

public class ColumnConfigurationCtrl
{

   private HBox _currentPanel;

   public HBox createPanel(ColumnConfiguration columnConfiguration)
   {
      _currentPanel = new HBox();


      _currentPanel.setSpacing(3);

      AggregateFunctionPane aggregateFunctionPane = new AggregateFunctionPane(columnConfiguration.getAggregateFunctionData());

      GraphListCheckBoxWA graphListCheckBox = new GraphListCheckBoxWA(b -> aggregateFunctionPane.setEnabled(b));

      _currentPanel.getChildren().add(graphListCheckBox);
//      _currentPanel.getChildren().add(aggregateFunctionPane);
      _currentPanel.getChildren().add(aggregateFunctionPane);
      _currentPanel.getChildren().add(new FilterPane());
      _currentPanel.getChildren().add(new OrderByPane());
      return _currentPanel;
   }
}
