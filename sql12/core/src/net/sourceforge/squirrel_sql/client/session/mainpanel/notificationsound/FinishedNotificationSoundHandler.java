package net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.SQLExecutionAdapter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JComponent;

public class FinishedNotificationSoundHandler
{
   private static final ILogger s_log = LoggerController.createLogger(FinishedNotificationSoundHandler.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FinishedNotificationSoundHandler.class);

   private boolean _playFinishedSoundChecked;

   private Long _playNotificationWhenMillisElapsed;

   public FinishedNotificationSoundHandler()
   {
      _playNotificationWhenMillisElapsed = SoundConfigDAO.playNotificationWhenMillisElapsed();
   }

   public void onPlayFinishedSoundChecked(boolean b)
   {
      _playFinishedSoundChecked = b;

      if(b == false)
      {
         _playNotificationWhenMillisElapsed = null;
      }
   }

   public void onConfigureFinishedSound(JComponent parent)
   {
      new ConfigureFinishedSoundCtrl(parent);
   }

   public ISQLExecutionListener getExecutionFinishedListener()
   {
      return new SQLExecutionAdapter()
      {
         @Override
         public void executionFinished()
         {
            onExecutionFinished();
         }
      };
   }

   private void onExecutionFinished()
   {
      if (_playFinishedSoundChecked)
      {
         Thread t = new Thread(()-> playFinishedSound());
         t.start();
      }
   }

   private void playFinishedSound()
   {
      try
      {
         SoundPlayer.PLAYER.quit();
         SoundPlayer.PLAYER.play(SoundConfigDAO.getSoundFile());
      }
      catch (Throwable e)
      {
         s_log.error("Error playing execution finished sound", e);
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("FinishedNotificationSoundHandler.error.playing.finished.sound", e.getMessage()));
      }
   }

   public boolean isToPlayNotificationSound(long elapsedMillis)
   {
      if(_playFinishedSoundChecked)
      {
         return true;
      }

      if(null == _playNotificationWhenMillisElapsed)
      {
         return false;
      }

      boolean ret = _playNotificationWhenMillisElapsed < elapsedMillis;

      if(ret)
      {
         _playFinishedSoundChecked = true;
      }

      return ret;
   }
}
