package org.squirrelsql.session;

import org.squirrelsql.session.action.StdActionCfg;

public class FileManager
{
   public FileManager(SessionTabContext sessionTabContext)
   {
      StdActionCfg.FILE_SAVE.setAction(() -> save());
      StdActionCfg.FILE_OPEN.setAction(() -> open());
      StdActionCfg.FILE_APPEND.setAction(() -> append());
      StdActionCfg.FILE_SAVE_AS.setAction(() -> saveAs());
      StdActionCfg.FILE_NEW.setAction(() -> newFile());
      StdActionCfg.FILE_DISCONNECT.setAction(() -> disconnect());
   }

   private void append()
   {
      System.out.println("FileManager.append");
   }

   public void save()
   {
      System.out.println("FileManager.save");
   }

   public void open()
   {
      System.out.println("FileManager.open");
   }

   public void saveAs()
   {
      System.out.println("FileManager.saveAs");
   }

   public void newFile()
   {
      System.out.println("FileManager.newFile");
   }

   public void disconnect()
   {
      System.out.println("FileManager.disconnect");
   }
}
