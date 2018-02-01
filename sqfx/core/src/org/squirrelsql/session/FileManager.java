package org.squirrelsql.session;

import javafx.event.Event;
import javafx.stage.FileChooser;
import org.squirrelsql.AppState;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.sql.SQLTextAreaServices;
import org.squirrelsql.session.sql.TabHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileManager
{

   private static final String LAST_DIRECTORY = "filemanager.last.directory";
   private I18n _i18n = new I18n(getClass());

   private SQLTextAreaServices _sqlTextAreaServices;
   private final SessionTabHeaderCtrl _sessionTabHeaderCtrl;
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
      if(null == _sessionTabHeaderCtrl.getFile())
      {
         FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle(_i18n.t("FileManager.save.sql"));

         initInitialDir(fileChooser);

         _sessionTabHeaderCtrl.setFile(fileChooser.showSaveDialog(AppState.get().getPrimaryStage()));

         if(null == _sessionTabHeaderCtrl.getFile())
         {
            return false;
         }

         if(false == _sessionTabHeaderCtrl.getFile().getName().toUpperCase().endsWith(".SQL") &&  -1 == _sessionTabHeaderCtrl.getFile().getName().toUpperCase().indexOf('.'))
         {
            File correctedFile = new File(_sessionTabHeaderCtrl.getFile().getPath() + ".sql");
            if (false == correctedFile.exists())
            {
               _sessionTabHeaderCtrl.setFile(correctedFile);
            }
         }
      }


      if (_sessionTabHeaderCtrl.getFile().exists() && false == _sessionTabHeaderCtrl.getFile().canWrite())
      {
         String msg = _i18n.t("FileManager.error.cannotwritefile", _sessionTabHeaderCtrl.getFile().getAbsolutePath());

         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), msg);
         _sessionTabHeaderCtrl.setFile(null);
         return false;
      }


      try
      {
         Files.write(_sessionTabHeaderCtrl.getFile().toPath(), _sqlTextAreaServices.getTextArea().getText().getBytes());

         String msg = _i18n.t("FileManager.savedfile", _sessionTabHeaderCtrl.getFile().getAbsolutePath());
         _mhPanel.info(msg);

         _sessionTabHeaderCtrl.setFileState(FileState.CLEAN);

         _pref.set(LAST_DIRECTORY, _sessionTabHeaderCtrl.getFile().getParent());
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

         String text = new String(bytes);

         text = normalizeText(text);

         _sqlTextAreaServices.setText(text);
         _sqlTextAreaServices.getTextArea().moveTo(0);


         _sessionTabHeaderCtrl.setFile(file);
         _sessionTabHeaderCtrl.setFileState(FileState.CLEAN);
         _pref.set(LAST_DIRECTORY, _sessionTabHeaderCtrl.getFile().getParent());

      }
      catch (Exception e)
      {
         _mhPanel.error(e);
         _mhLog.error(e);
      }
   }

   private String normalizeText(String text)
   {
      return text.replaceAll("\t", TabHandler.TAB_SPACES);
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
      File fileBuf = _sessionTabHeaderCtrl.getFile();
      FileState fileStateBuf = _sessionTabHeaderCtrl.getFileState();

      _sessionTabHeaderCtrl.setFile(null);
      _sessionTabHeaderCtrl.setFileState(FileState.NO_FILE);


      if(false == save())
      {
         _sessionTabHeaderCtrl.setFile(fileBuf);
         _sessionTabHeaderCtrl.setFileState(fileStateBuf);
      }

   }

   public void newFile()
   {
      if(null == _sessionTabHeaderCtrl.getFile())
      {
         return;
      }


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
         _pref.set(LAST_DIRECTORY, file.getParent());
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

      _sessionTabHeaderCtrl.setFile(null);
      _sessionTabHeaderCtrl.setFileState(FileState.NO_FILE);

      _sqlTextAreaServices.requestFocus();
   }

   public void closeRequest(Event e)
   {
      if(false == questionSave())
      {
         e.consume();
      }
   }
}
