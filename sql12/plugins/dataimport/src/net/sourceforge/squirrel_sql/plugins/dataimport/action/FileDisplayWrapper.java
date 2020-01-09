package net.sourceforge.squirrel_sql.plugins.dataimport.action;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.File;

public class FileDisplayWrapper
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FileDisplayWrapper.class);

   private final File _file;
   private final boolean _isTemporaryCsvFileFromClipboard;

   public FileDisplayWrapper(File file, boolean isTemporaryCsvFileFromClipboard)
   {
      _file = file;
      _isTemporaryCsvFileFromClipboard = isTemporaryCsvFileFromClipboard;
   }

   public File getFile()
   {
      return _file;
   }

   public boolean isTemporaryCsvFileFromClipboard()
   {
      return _isTemporaryCsvFileFromClipboard;
   }

   public String getDisplayPath()
   {
      if(_isTemporaryCsvFileFromClipboard)
      {
         return s_stringMgr.getString("FileDisplayWrapper.clipboard.content");
      }
      else
      {
         return _file.getAbsolutePath();
      }
   }

   public String getDisplayName()
   {
      if(_isTemporaryCsvFileFromClipboard)
      {
         return s_stringMgr.getString("FileDisplayWrapper.clipboard.content");
      }
      else
      {
         return _file.getName();
      }
   }
}
