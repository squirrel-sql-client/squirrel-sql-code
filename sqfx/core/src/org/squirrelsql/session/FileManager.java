package org.squirrelsql.session;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.FileChooser;
import org.squirrelsql.AppState;
import org.squirrelsql.services.*;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.sql.SQLTextAreaServices;

import java.io.*;
import java.nio.file.Files;

public class FileManager
{

   private static final String LAST_DIRECTORY = "filemanager.last.directory";
   private I18n _i18n = new I18n(getClass());

   private SQLTextAreaServices _sqlTextAreaServices;
   private final SessionTabHeaderCtrl _sessionTabHeaderCtrl;
   private File _currentFile;
   private MessageHandler _mhPanel = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private MessageHandler _mhLog = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_LOG);
   private Pref _pref = new Pref(getClass());

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

      _sqlTextAreaServices.getTextArea().textProperty().addListener((observable, oldValue, newValue) -> onTextChanged());
   }

   private void onTextChanged()
   {
      if (FileState.CLEAN == _sessionTabHeaderCtrl.getFileState())
      {
         _sessionTabHeaderCtrl.setFileState(FileState.CHANGED);
      }
   }

   public boolean save()
   {
      if(null == _currentFile)
      {
         FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle(_i18n.t("FileManager.save.sql"));

         initInitialDir(fileChooser);

         _currentFile = fileChooser.showSaveDialog(AppState.get().getPrimaryStage());

         if(null == _currentFile)
         {
            return false;
         }

         if(false == _currentFile.getName().toUpperCase().endsWith(".SQL") &&  -1 == _currentFile.getName().toUpperCase().indexOf('.'))
         {
            File correctedFile = new File(_currentFile.getPath() + ".sql");
            if (false == correctedFile.exists())
            {
               _currentFile = correctedFile;
            }
         }
      }


      if (_currentFile.exists() && false == _currentFile.canWrite())
      {
         String msg = _i18n.t("FileManager.error.cannotwritefile", _currentFile.getAbsolutePath());

         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), msg);
         _currentFile = null;
         return false;
      }


      try
      {
         Files.write(_currentFile.toPath(), _sqlTextAreaServices.getTextArea().getText().getBytes());

         String msg = _i18n.t("FileManager.savedfile", _currentFile.getAbsolutePath());
         _mhPanel.info(msg);

         _sessionTabHeaderCtrl.setFileState(FileState.CLEAN);
         return true;
      }
      catch (IOException ex)
      {
         _mhPanel.error(ex);
         _mhLog.error(ex);
         return false;
      }

   }

   private void initInitialDir(FileChooser fileChooser)
   {
      File initialDir = new File(_pref.getString(LAST_DIRECTORY, System.getProperty("user.home")));
      if (initialDir.exists())
      {
         fileChooser.setInitialDirectory(initialDir);
      }
   }

   public void open()
   {
      if (false == questionSave())
      {
         return;
      }

      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle(_i18n.t("FileManager.open.sql"));

      initInitialDir(fileChooser);

      File file = fileChooser.showOpenDialog(AppState.get().getPrimaryStage());

      if(null == file)
      {
         return;
      }

      try
      {
         byte[] bytes = Files.readAllBytes(file.toPath());

         _sqlTextAreaServices.setText(new String(bytes));


         _currentFile = file;
         _sessionTabHeaderCtrl.setFileState(FileState.CLEAN);
      }
      catch (Exception e)
      {
         _mhPanel.error(e);
         _mhLog.error(e);
      }
   }

   private boolean questionSave()
   {
      if(FileState.CHANGED == _sessionTabHeaderCtrl.getFileState())
      {
         if( FXMessageBox.YES.equals(FXMessageBox.showYesNo(AppState.get().getPrimaryStage(), _i18n.t("Filemanager.save.question"))))
         {
            if(false == save())
            {
               return false;
            }
         }
      }
      return true;
   }

   public void saveAs()
   {
      File fileBuf = _currentFile;
      FileState fileStateBuf = _sessionTabHeaderCtrl.getFileState();

      _currentFile = null;
      _sessionTabHeaderCtrl.setFileState(FileState.NO_FILE);


      if(false == save())
      {
         _currentFile = fileBuf;
         _sessionTabHeaderCtrl.setFileState(fileStateBuf);
      }

   }

   public void newFile()
   {
      disconnect();
      _sqlTextAreaServices.clear();
   }

   private void append()
   {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle(_i18n.t("FileManager.append.sql"));

      initInitialDir(fileChooser);

      File file = fileChooser.showOpenDialog(AppState.get().getPrimaryStage());

      if(null == file)
      {
         return;
      }

      try
      {
         byte[] bytes = Files.readAllBytes(file.toPath());
         _sqlTextAreaServices.appendToEditor("\n\n" + new String(bytes));
      }
      catch (IOException e)
      {
         _mhPanel.error(e);
         _mhLog.error(e);
      }
   }

   public void disconnect()
   {
      if (false == questionSave())
      {
         return;
      }

      _currentFile = null;
      _sessionTabHeaderCtrl.setFileState(FileState.NO_FILE);
   }
}
