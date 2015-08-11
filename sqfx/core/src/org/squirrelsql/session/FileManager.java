package org.squirrelsql.session;

import javafx.scene.image.ImageView;
import org.squirrelsql.Props;
import org.squirrelsql.session.action.StdActionCfg;

public class FileManager
{

   private final ImageView _fileImage;
   private final SessionTabHeaderCtrl _sessionTabHeaderCtrl;

   public FileManager(SessionTabContext sessionTabContext, SessionTabHeaderCtrl sessionTabHeaderCtrl)
   {
      _sessionTabHeaderCtrl = sessionTabHeaderCtrl;
      StdActionCfg.FILE_SAVE.setAction(() -> save());
      StdActionCfg.FILE_OPEN.setAction(() -> open());
      StdActionCfg.FILE_APPEND.setAction(() -> append());
      StdActionCfg.FILE_SAVE_AS.setAction(() -> saveAs());
      StdActionCfg.FILE_NEW.setAction(() -> newFile());
      StdActionCfg.FILE_DISCONNECT.setAction(() -> disconnect());

      _fileImage = new ImageView(new Props(SessionUtil.class).getImage("smallFile.gif"));


   }

   private void append()
   {
      _sessionTabHeaderCtrl.setFileState(FileState.CHANGED);
      System.out.println("FileManager.append");
   }

   public void save()
   {
      _sessionTabHeaderCtrl.setFileState(FileState.CLEAN);

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
      _sessionTabHeaderCtrl.setFileState(FileState.NO_FILE);
      System.out.println("FileManager.disconnect");
   }
}
