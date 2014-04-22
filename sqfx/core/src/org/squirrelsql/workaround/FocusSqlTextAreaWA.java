package org.squirrelsql.workaround;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.fxmisc.richtext.CodeArea;

import java.util.Timer;
import java.util.TimerTask;

public class FocusSqlTextAreaWA
{
   public static void forceFocus(CodeArea sqlTextArea)
   {
      sqlTextArea.requestFocus();

      final Timer timer = new Timer();
      TimerTask timerTask = new TimerTask()
      {
         @Override
         public void run()
         {
            onTimerTick(timer, sqlTextArea);
         }
      };
      timer.schedule(timerTask, 0, 100);

   }

   private static void onTimerTick(Timer timer, CodeArea sqlTextArea)
   {
      Platform.runLater(() -> forceFocus(timer, sqlTextArea));
   }

   private static void forceFocus(Timer timer, CodeArea sqlTextArea)
   {
      //System.out.println("org.squirrelsql.workaround.FocusSqlTextAreaWA.forceFocus");

      if (false == sqlTextArea.isFocused())
      {
         sqlTextArea.requestFocus();
         return;
      }



      timer.cancel();
      timer.purge();
   }
}
