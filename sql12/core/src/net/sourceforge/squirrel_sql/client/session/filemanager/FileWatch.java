package net.sourceforge.squirrel_sql.client.session.filemanager;

import java.io.File;

public class FileWatch
{
   private final File _file;
   private final long _lastModified;

   public FileWatch(File file)
   {
      _file = file;
      _lastModified = _file.lastModified();
   }

   public File getFile()
   {
      return _file;
   }

   public long getLastModified()
   {
      return _lastModified;
   }

   @Override
   public int hashCode()
   {
      return _file.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if(false == obj instanceof FileWatch)
      {
         return false;
      }

      return _file.equals(((FileWatch)obj)._file);
   }

   public boolean changedExternally()
   {
      return _lastModified < _file.lastModified();
   }
}
