package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.AppState;

public class AggregateFunctionPane extends BorderPane
{
   private ColumnConfigurationPersistence _columnConfigurationPersistence;
   private QueryChannel _queryChannel;


   public AggregateFunctionPane(ColumnConfigurationPersistence columnConfigurationPersistence, QueryChannel queryChannel)
   {
      _columnConfigurationPersistence = columnConfigurationPersistence;
      _queryChannel = queryChannel;

      updateGraphics();

      addEventHandler(MouseEvent.MOUSE_PRESSED, e -> showPopup());
   }

   private void showPopup()
   {
      if(false == _columnConfigurationPersistence.getAggregateFunctionPersistence().isInSelect())
      {
         return;
      }

      MenuItem[]  menuItems = new MenuItem[AggregateFunction.values().length];

      for (int i = 0; i < AggregateFunction.values().length; i++)
      {
         AggregateFunction agg = AggregateFunction.values()[i];
         menuItems[i] = new MenuItem(agg.getTitle(), agg.createImage());
         menuItems[i].setOnAction(e -> onFctSelected(agg));
      }

      ContextMenu popup = new ContextMenu(menuItems);

      Point2D localToScene = localToScreen(0, 0);

      popup.show(AppState.get().getPrimaryStage(), localToScene.getX(), localToScene.getY());
   }

   private void onFctSelected(AggregateFunction agg)
   {
      _columnConfigurationPersistence.getAggregateFunctionPersistence().setAggregateFunction(agg);
      _setCenter(agg.createImage());
   }

   private void updateGraphics()
   {
      if(_columnConfigurationPersistence.getAggregateFunctionPersistence().isInSelect())
      {
         _setCenter(_columnConfigurationPersistence.getAggregateFunctionPersistence().getAggregateFunction().createImage());
      }
      else
      {
         _setCenter(AggregateFunction.createDisabledImage());
      }
   }

   private void _setCenter(ImageView image)
   {
      setCenter(image);
      layoutChildren();
   }


   public void setEnabled(boolean b)
   {
      _columnConfigurationPersistence.getAggregateFunctionPersistence().setInSelect(b);

      if(false == b)
      {
         _columnConfigurationPersistence.getAggregateFunctionPersistence().setAggregateFunction(AggregateFunction.NONE);
         _columnConfigurationPersistence.setSelectPosition(-1);
      }

      updateGraphics();

      _queryChannel.fireChanged();
   }
}
