package org.squirrelsql.workaround;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

import java.util.Timer;
import java.util.TimerTask;

public class SplitDividerWA
{
   private SplitPane _splt;
   private boolean _firstCall = true;

   public SplitDividerWA(SplitPane splt)
   {
      _splt = splt;
   }

   public void addItemAndAdjustDivider(Node node, final int divIx, final double divLoc)
   {
      _splt.getItems().add(divIx, node);

      _splt.setDividerPosition(divIx, divLoc);

      if (_firstCall)
      {
         final SimpleDoubleProperty doubleProperty = new SimpleDoubleProperty(divLoc);
         _splt.getDividers().get(divIx).positionProperty().bindBidirectional(doubleProperty);


         final Timer timer = new Timer();
         TimerTask timerTask = new TimerTask()
         {
            @Override
            public void run()
            {
               onTimerTick(timer, divLoc, divIx, doubleProperty);
            }
         };
         timer.schedule(timerTask,0, 100);

         doubleProperty.addListener(new ChangeListener<Number>()
         {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2)
            {
               forceDividerAdjust(timer, divLoc, divIx, doubleProperty);
            }
         });


         _firstCall = false;
      }
   }

   private void onTimerTick(final Timer timer, final double divLoc, final int divIx, final SimpleDoubleProperty doubleProperty)
   {
      Platform.runLater(new Runnable()
      {
         @Override
         public void run()
         {
            forceDividerAdjust(timer, divLoc, divIx, doubleProperty);
         }
      });
   }

   private void forceDividerAdjust(Timer timer, double divLoc, int divIx, SimpleDoubleProperty doubleProperty)
   {
      if(Math.abs(divLoc - _splt.getDividerPositions()[0]) > 0.0003)
      {
         System.out.println("_splt.getDividerPositions()[0] " + _splt.getDividerPositions()[0]);

         System.out.println("doubleProperty = " + doubleProperty.getValue());

         _splt.setDividerPosition(divIx, divLoc);
      }
      else
      {
         timer.cancel();
         timer.purge();
      }
   }
}
