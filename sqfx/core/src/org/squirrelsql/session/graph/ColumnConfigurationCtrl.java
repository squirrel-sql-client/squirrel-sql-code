package org.squirrelsql.session.graph;

import javafx.scene.layout.HBox;

public class ColumnConfigurationCtrl
{

   private HBox _currentPanel;

   public HBox createPanel(ColumnConfiguration columnConfiguration)
   {
      _currentPanel = new HBox();


      _currentPanel.setSpacing(3);

      ColumnConfigurationListener columnConfigurationListener = () -> _currentPanel.requestLayout();

      AggregateFunctionPane aggregateFunctionPane = new AggregateFunctionPane(columnConfiguration.getAggregateFunctionData(), columnConfigurationListener);

      GraphListCheckBoxWA graphListCheckBox = new GraphListCheckBoxWA(b -> aggregateFunctionPane.setEnabled(b), columnConfigurationListener);

      _currentPanel.getChildren().add(graphListCheckBox);
//      _currentPanel.getChildren().add(aggregateFunctionPane);
      _currentPanel.getChildren().add(aggregateFunctionPane);
      _currentPanel.getChildren().add(new FilterPane(columnConfigurationListener));
      _currentPanel.getChildren().add(new OrderByPane(columnConfigurationListener));
      return _currentPanel;
   }
}
