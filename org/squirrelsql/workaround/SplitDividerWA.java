package org.squirrelsql.workaround;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

import java.util.Timer;
import java.util.TimerTask;

public class SplitDividerWA
{

   public static void addItemAndAdjustDivider(final SplitPane splt, Node node, final int divIx, final double divLoc)
   {
      splt.getItems().add(divIx, node);

      splt.setDividerPosition(divIx, divLoc);

      final Timer timer = new Timer();
      TimerTask timerTask = new TimerTask()
      {
         @Override
         public void run()
         {
            onTimerTick(splt, timer, divLoc, divIx);
         }
      };
      timer.schedule(timerTask, 0, 100);

   }

   private static void onTimerTick(final SplitPane splt, final Timer timer, final double divLoc, final int divIx)
   {
      Platform.runLater(new Runnable()
      {
         @Override
         public void run()
         {
            forceDividerAdjust(splt, timer, divLoc, divIx);
         }
      });
   }

   private static void forceDividerAdjust(SplitPane splt, Timer timer, double divLoc, int divIx)
   {
      if (0 == splt.getDividerPositions().length)
      {
         return;
      }

      if (Math.abs(divLoc - splt.getDividerPositions()[0]) > 0.01)
      {
         splt.setDividerPosition(divIx, divLoc);
      }
      else
      {
         timer.cancel();
         timer.purge();
      }
   }
}
