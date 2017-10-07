package org.squirrelsql.session.graph;

import javafx.scene.layout.HBox;

public class ColumnConfigurationCtrl
{

   private HBox _currentPanel;
   private QueryChannel _queryChannel;

   public ColumnConfigurationCtrl(QueryChannel queryChannel)
   {
      _queryChannel = queryChannel;
   }

   public HBox createPanel(GraphColumn column)
   {
      _currentPanel = new HBox();


      _currentPanel.setSpacing(3);

      ColumnConfigurationPersistence columnConfigurationPersistence = column.getColumnConfigurationPersistence();
      AggregateFunctionPersistence aggregateFunctionPersistence = columnConfigurationPersistence.getAggregateFunctionPersistence();

      AggregateFunctionPane aggregateFunctionPane = new AggregateFunctionPane(columnConfigurationPersistence, _queryChannel);
      GraphListCheckBoxWA graphListCheckBox = new GraphListCheckBoxWA(b -> aggregateFunctionPane.setEnabled(b));
      graphListCheckBox.setSelected(aggregateFunctionPersistence.isInSelect());


      _currentPanel.getChildren().add(graphListCheckBox);
//      _currentPanel.getChildren().add(aggregateFunctionPane);
      _currentPanel.getChildren().add(aggregateFunctionPane);
      _currentPanel.getChildren().add(new FilterPane(columnConfigurationPersistence.getFilterPersistence(), column.getColumnInfo(), _queryChannel));
      _currentPanel.getChildren().add(new OrderByPane(columnConfigurationPersistence.getOrderByPersistence(), _queryChannel));
      return _currentPanel;
   }
}
