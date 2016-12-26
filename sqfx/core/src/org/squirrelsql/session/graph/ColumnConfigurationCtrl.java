package org.squirrelsql.session.graph;

import javafx.scene.layout.HBox;

public class ColumnConfigurationCtrl
{

   private HBox _currentPanel;

   public HBox createPanel(GraphColumn column)
   {
      _currentPanel = new HBox();


      _currentPanel.setSpacing(3);

      AggregateFunctionPersistence aggregateFunctionPersistence = column.getColumnConfigurationPersistence().getAggregateFunctionPersistence();

      AggregateFunctionPane aggregateFunctionPane = new AggregateFunctionPane(aggregateFunctionPersistence);
      GraphListCheckBoxWA graphListCheckBox = new GraphListCheckBoxWA(b -> aggregateFunctionPane.setEnabled(b));
      graphListCheckBox.setSelected(aggregateFunctionPersistence.isInSelect());


      _currentPanel.getChildren().add(graphListCheckBox);
//      _currentPanel.getChildren().add(aggregateFunctionPane);
      _currentPanel.getChildren().add(aggregateFunctionPane);
      _currentPanel.getChildren().add(new FilterPane(column.getColumnConfigurationPersistence().getFilterPersistence()));
      _currentPanel.getChildren().add(new OrderByPane(column.getColumnConfigurationPersistence().getOrderByPersistence()));
      return _currentPanel;
   }
}
