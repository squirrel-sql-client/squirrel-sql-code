package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.Timer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Watches and notifies file changes.
 */
public class FileNotifier
{
   private static ILogger s_log = LoggerController.createLogger(FileNotifier.class);

   private ArrayList<FileNotifierListener> _fileNotifierListeners = new ArrayList<>();

   private final Timer _swingTimer;
   private HashSet<File> _filesBeingWritten = new HashSet<>();

   private HashSet<FileWatch> _fileWatches = new HashSet<>();
   private boolean _inOnTimerTriggered;

   public FileNotifier()
   {
      _swingTimer = new Timer(2000, e -> onTimerTriggered());
      _swingTimer.setRepeats(true);
      _swingTimer.start();
   }

   private void onTimerTriggered()
   {
      if (false == Main.getApplication().getSquirrelPreferences().isNotifyExternalFileChanges())
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
         FileNotifierListener[] listeners = null;

         HashSet<FileWatch> updatedWatches = new HashSet<>();

         final FileWatch[] fileWatches = _fileWatches.toArray(new FileWatch[0]);

         for (FileWatch fileWatch : fileWatches)
         {
            if( false == fileWatch.changedExternally() || _filesBeingWritten.contains(fileWatch.getFile()))
            {
               continue;
            }

            if (null == listeners)
            {
               listeners = _fileNotifierListeners.toArray(new FileNotifierListener[0]);
            }

            for (FileNotifierListener listener : listeners)
            {
               try
               {
                  listener.fileChanged(fileWatch.getFile());
               }
               catch (Exception e)
               {
                  s_log.error(e);
               }
            }
            updatedWatches.add(new FileWatch(fileWatch.getFile())); // We fire a detected change only once.
         }

         _fileWatches.removeAll(updatedWatches); // Due to implementation of HastSet.add(...) and FileWatch.hashCode()/equals()
         _fileWatches.addAll(updatedWatches);
      }
      finally
      {
         _inOnTimerTriggered = false;
      }
   }

   public void watchFile(File file)
   {
      _fileWatches.remove(new FileWatch(file)); // Due to implementation of HastSet.add(...) and FileWatch.hashCode()/equals()
      _fileWatches.add(new FileWatch(file));
   }

   public void unwatchFile(File file)
   {
      _fileWatches.remove(new FileWatch(file));
   }

   public void addFileNotifierListener(FileNotifierListener fileNotifierListener)
   {
      removeFileNotifierListener(fileNotifierListener);
      _fileNotifierListeners.add(fileNotifierListener);
   }

   public void removeFileNotifierListener(FileNotifierListener fileNotifierListener)
   {
      _fileNotifierListeners.remove(fileNotifierListener);
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
      _filesBeingWritten.add(file);
   }

   public void endFileWrite(File file)
   {
      // This makes file unchanged.
      _fileWatches.remove(new FileWatch(file));
      _fileWatches.add(new FileWatch(file)); // Due to implementation of HastSet.add(...) and FileWatch.hashCode()/equals()

      _filesBeingWritten.remove(file);
   }
}
