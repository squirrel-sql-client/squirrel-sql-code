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
   private AggregateFunctionData _aggregateFunctionData;
   private ColumnConfigurationListener _columnConfigurationListener;


   public AggregateFunctionPane(AggregateFunctionData aggregateFunctionData, ColumnConfigurationListener columnConfigurationListener)
   {
      _aggregateFunctionData = aggregateFunctionData;
      _columnConfigurationListener = columnConfigurationListener;

      updateGraphics();

      addEventHandler(MouseEvent.MOUSE_PRESSED, e -> showPopup());
   }

   private void showPopup()
   {
      if(false == _aggregateFunctionData.isInSelect())
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
      _aggregateFunctionData.setAggregateFunction(agg);
      _setCenter(agg.createImage());
   }

   private void updateGraphics()
   {
      if(_aggregateFunctionData.isInSelect())
      {
         _setCenter(_aggregateFunctionData.getAggregateFunction().createImage());
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
      _columnConfigurationListener.requestLayout();
   }


   public void setEnabled(boolean b)
   {
      _aggregateFunctionData.setInSelect(b);

      if(false == b)
      {
         _aggregateFunctionData.setAggregateFunction(AggregateFunction.NONE);
      }

      updateGraphics();
   }
}
