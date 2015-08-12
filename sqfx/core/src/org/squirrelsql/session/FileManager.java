package org.squirrelsql.session;

import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.services.*;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.sql.SQLTextAreaServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager
{

   private I18n _i18n = new I18n(getClass());

   private final ImageView _fileImage;
   private SQLTextAreaServices _sqlTextAreaServices;
   private final SessionTabHeaderCtrl _sessionTabHeaderCtrl;
   private File _currentFile;
   private MessageHandler _mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);

   public FileManager(SQLTextAreaServices sqlTextAreaServices, SessionTabHeaderCtrl sessionTabHeaderCtrl)
   {
      _sqlTextAreaServices = sqlTextAreaServices;
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
      boolean askReplace = false;

      if(null == _currentFile)
      {
         askReplace = true;

         FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle(_i18n.t("FileManager.save.sql"));

         _currentFile = fileChooser.showSaveDialog(AppState.get().getPrimaryStage());

         if(null == _currentFile)
         {
            return;
         }

         if(false == _currentFile.getName().toUpperCase().endsWith(".SQL") &&  -1 == _currentFile.getName().toUpperCase().indexOf('.'))
         {
            _currentFile = new File(_currentFile.getPath() + ".sql");
         }
      }


      if (_currentFile.exists() && false == _currentFile.canWrite())
      {
         String msg = _i18n.t("FileManager.error.cannotwritefile", _currentFile.getAbsolutePath());

         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), msg);
         _currentFile = null;
         return;
      }


      FileOutputStream fos = null;
      try
      {
         fos = new FileOutputStream(_currentFile);

         String script = _sqlTextAreaServices.getTextArea().getText();

         fos.write(script.getBytes());

         String msg = _i18n.t("FileManager.savedfile", _currentFile.getAbsolutePath());
         _mh.info(msg);
      }
      catch (IOException ex)
      {
         _mh.error(ex);
      }
      finally
      {
         Utils.close(fos);
      }

      _sessionTabHeaderCtrl.setFileState(FileState.CLEAN);
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
