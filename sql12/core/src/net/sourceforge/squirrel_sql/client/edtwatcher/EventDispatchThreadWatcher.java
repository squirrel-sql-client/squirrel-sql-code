package net.sourceforge.squirrel_sql.client.edtwatcher;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class EventDispatchThreadWatcher
{

   public EventDispatchThreadWatcher()
   {

      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run()
            {
               init();
            }
         });
   }

   private void init()
   {
      Timer t = new Timer(true);

      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            sendEventQueueWorkingCheck();
         }
      };

      t.schedule(task, 1000,1000);
   }

   private void sendEventQueueWorkingCheck()
   {
      SwingUtilities.invokeLater(new EventQueueWorkingCheck());
   }


}