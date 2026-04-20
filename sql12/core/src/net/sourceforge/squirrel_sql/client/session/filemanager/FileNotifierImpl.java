package net.sourceforge.squirrel_sql.client.session.filemanager;

import java.io.File;
import javax.swing.Timer;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Watches and notifies file changes.
 */
public class FileNotifierImpl
{
   public static final int DEFAULT_DELAY = 2000;

   private static ILogger s_log = LoggerController.createLogger(FileNotifier.class);

   private final boolean _defaultNotifier;
   private Timer _swingTimer;
   private FileNotifierListener _listener;
   private boolean _fileIsBeingWritten;

   private FileWatch _fileWatch;
   private boolean _inOnTimerTriggered;

   public FileNotifierImpl(boolean defaultNotifier, int delay, FileNotifierListener listener)
   {
      _defaultNotifier = defaultNotifier;
      _swingTimer = new Timer(delay, e -> onTimerTriggered());
      _listener = listener;
      _swingTimer.setRepeats(true);
      _swingTimer.start();
   }

   private void onTimerTriggered()
   {
      if (_defaultNotifier && false == Main.getApplication().getSquirrelPreferences().isNotifyExternalFileChanges())
      {
         _swingTimer.stop();
         return;
      }

      if(_inOnTimerTriggered)
      {
         return;
      }

      try
      {
         _inOnTimerTriggered = true;

         if( null == _fileWatch || false == _fileWatch.changedExternally() || _fileIsBeingWritten)
         {
            return;
         }

         try
         {
            _listener.fileChanged(_fileWatch.getFile());
         }
         catch (Exception e)
         {
            s_log.error(e);
         }

         _fileWatch = new FileWatch(_fileWatch.getFile());
      }
      finally
      {
         _inOnTimerTriggered = false;
      }
   }

   public void watchFile(File file)
   {
      _fileWatch = new FileWatch(file);
   }


   public void setNotifyExternalFileChanges(boolean b)
   {
      if (false == b)
      {
         _swingTimer.stop();
      }

      Main.getApplication().getSquirrelPreferences().setNotifyExternalFileChanges(b);
   }

   public void beginFileWrite(File file)
   {
      _fileIsBeingWritten = true;
   }

   public void endFileWrite(File file)
   {
      _fileWatch = new FileWatch(file); // Due to implementation of HastSet.add(...) and FileWatch.hashCode()/equals()

      _fileIsBeingWritten = false;
   }

   public void dispose()
   {
      _swingTimer.stop();
      _swingTimer = null;
      _listener = null;
   }
}
