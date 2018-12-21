package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import java.io.File;

public class RecentFileWrapper
{
   private File _file;
   private boolean _openAtSessionStart;

   public RecentFileWrapper(File file)
   {
      _file = file;
   }

   @Override
   public String toString()
   {
      return _file.toString();
   }

   public File getFile()
   {
      return _file;
   }

   public void setOpenAtSessionStart(boolean openAtSessionStart)
   {
      _openAtSessionStart = openAtSessionStart;
   }

   public boolean isOpenAtSessionStart()
   {
      return _openAtSessionStart;
   }
}
