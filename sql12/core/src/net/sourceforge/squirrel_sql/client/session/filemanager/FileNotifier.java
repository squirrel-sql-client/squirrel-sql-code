package net.sourceforge.squirrel_sql.client.session.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


/**
 * Watches and notifies file changes.
 */
public class FileNotifier
{
   private final Map<File, FileNotifierImpl> _defaultFileNotifierImpls = new HashMap<>();;
   private final Map<File, FileNotifierImpl> _customFileNotifierImpls = new HashMap<>();

   private final FileNotifierListener _internalDefaultFileNotifierListener = file -> onDefaultFileChanged(file);
   private List<FileNotifierListener> _externalDefaultFileNotifierListeners = new ArrayList<>();

   public FileNotifier()
   {
   }

   private void onDefaultFileChanged(File file)
   {
      FileNotifierListener[] listeners = _externalDefaultFileNotifierListeners.toArray(new FileNotifierListener[0]);
      Stream.of(listeners).forEach(l -> l.fileChanged(file));
   }

   public void watchFileDefaultIfNotAlreadyWatchedCustom(File file)
   {
      if(_defaultFileNotifierImpls.containsKey(file))
      {
         // Already being watched
         return;
      }

      if(_customFileNotifierImpls.containsKey(file))
      {
         // Is already being watched custom
         return;
      }

      FileNotifierImpl removed = _customFileNotifierImpls.remove(file);
      if(null != removed)
      {
         removed.dispose();
      }


      FileNotifierImpl notifier = new FileNotifierImpl(true, FileNotifierImpl.DEFAULT_DELAY, _internalDefaultFileNotifierListener);
      _defaultFileNotifierImpls.put(file, notifier);
      notifier.watchFile(file);
   }

   public void unwatchFileDefault(File file)
   {
      FileNotifierImpl fileNotifier = _defaultFileNotifierImpls.remove(file);
      if(null == fileNotifier)
      {
         return;
      }

      fileNotifier.dispose();
   }

   public void watchFileCustom(File file, int delay, FileNotifierListener listener)
   {
      if(_customFileNotifierImpls.containsKey(file))
      {
         // Already being watched
         return;
      }

      FileNotifierImpl removed = _defaultFileNotifierImpls.remove(file);
      if(null != removed)
      {
         removed.dispose();
      }


      FileNotifierImpl customFileNotifier = new FileNotifierImpl(false, delay, listener);
      _customFileNotifierImpls.put(file, customFileNotifier);
      customFileNotifier.watchFile(file);
   }


   public void unwatchFileCustom(File file)
   {
      FileNotifierImpl notifier = _customFileNotifierImpls.remove(file);

      if(null == notifier)
      {
         // Not being watched
         return;
      }

      notifier.dispose();

      FileNotifierImpl defaultFileNotifier = new FileNotifierImpl(true, FileNotifierImpl.DEFAULT_DELAY, _internalDefaultFileNotifierListener);
      defaultFileNotifier.watchFile(file);
      _defaultFileNotifierImpls.put(file, defaultFileNotifier);
   }


   public void addDefaultFileNotifierListener(FileNotifierListener fileNotifierListener)
   {
      _externalDefaultFileNotifierListeners.remove(fileNotifierListener);
      _externalDefaultFileNotifierListeners.add(fileNotifierListener);
   }

   public void removeDefaultFileNotifierListener(FileNotifierListener fileNotifierListener)
   {
      _externalDefaultFileNotifierListeners.remove(fileNotifierListener);
   }

   public void setNotifyDefaultExternalFileChanges(boolean b)
   {
      _defaultFileNotifierImpls.values().forEach(n -> n.setNotifyExternalFileChanges(b));
   }

   public void beginFileWrite(File file)
   {
      FileNotifierImpl fileNotifier = _defaultFileNotifierImpls.get(file);

      if(null == fileNotifier)
      {
         fileNotifier = _customFileNotifierImpls.get(file);
      }

      if(null != fileNotifier)
      {
         fileNotifier.beginFileWrite(file);
      }

   }

   public void endFileWrite(File file)
   {
      FileNotifierImpl fileNotifier = _defaultFileNotifierImpls.get(file);

      if(null == fileNotifier)
      {
         fileNotifier = _customFileNotifierImpls.get(file);
      }


      if(null != fileNotifier)
      {
         fileNotifier.endFileWrite(file);
      }
   }
}
