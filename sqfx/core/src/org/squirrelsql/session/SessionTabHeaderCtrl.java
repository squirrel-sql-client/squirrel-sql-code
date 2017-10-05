package org.squirrelsql.session;

import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SessionTabHeaderCtrl
{
   private static final String SHOW_FILE_NAME = "SHOW_FILE_NAME";
   private static final String SHOW_FILE_PATH = "SHOW_FILE_PATH";
   
   private final Button _btnFileInfo;
   private final BorderPane _node;
   private final SessionTabContext _tabContext;
   private FileState _fileState;
   private File _file;

   private I18n _i18n = new I18n(getClass());
   private Pref _pref = new Pref(getClass());
   private CheckMenuItem _mnuShowFileName;
   private CheckMenuItem _mnuShowFilePath;
   private ContextMenu _buttonPopup;
   private Label _label;


   public SessionTabHeaderCtrl(SessionTabContext tabContext)
   {
      this(tabContext, null);
   }


   public SessionTabHeaderCtrl(SessionTabContext tabContext, ImageView icon)
   {
      _tabContext = tabContext;

      _node = new BorderPane();
      _label = new Label();

      if(null != icon)
      {
         _label.setGraphic(icon);
      }

      _node.setCenter(_label);

      _btnFileInfo = new Button(null, null);
      _btnFileInfo.setText(null);
      _btnFileInfo.setPadding(new Insets(0, 2, 0, 2));
      _node.setRight(_btnFileInfo);
      BorderPane.setMargin(_btnFileInfo, new Insets(3));

      setFileState(FileState.NO_FILE);

      _buttonPopup = createButtonPopup();

      _btnFileInfo.setOnAction(e -> showPopup());

      initLabelText();
   }

   private void initLabelText()
   {
      String title = SessionUtil.getSessionTabTitle(_tabContext);

      _label.setText(title);

      if(null == _file)
      {
         return;
      }


      if(_mnuShowFileName.isSelected())
      {
         _label.setText(title + "  " + _i18n.t("SessionTabHeader.file", _file.getName()));
      }
      else if(_mnuShowFilePath.isSelected())
      {
         _label.setText(title + "  " + _i18n.t("SessionTabHeader.path", _file.getPath()));
      }
   }

   private ContextMenu createButtonPopup()
   {
      ContextMenu ret = new ContextMenu();

      MenuItem mnuCopyFilePath = new MenuItem(_i18n.t("FileManager.open.file.path"));
      mnuCopyFilePath.setOnAction(e -> onOpenFilePath());
      ret.getItems().add(mnuCopyFilePath);

      MenuItem mnuOpenFilePath = new MenuItem(_i18n.t("FileManager.copy.file.path"));
      mnuOpenFilePath.setOnAction(e -> onCopyFilePath());
      ret.getItems().add(mnuOpenFilePath);

      ret.getItems().add(new SeparatorMenuItem());

      _mnuShowFileName = new CheckMenuItem(_i18n.t("FileManager.show.file.name"));
      _mnuShowFileName.setSelected(_pref.getBoolean(SHOW_FILE_NAME, false));
      _mnuShowFileName.setOnAction(e -> onShowFileName());
      ret.getItems().add(_mnuShowFileName);

      _mnuShowFilePath = new CheckMenuItem(_i18n.t("FileManager.show.file.path"));
      _mnuShowFilePath.setSelected(_pref.getBoolean(SHOW_FILE_PATH, false));
      _mnuShowFilePath.setOnAction(e -> onShowFilePath());
      ret.getItems().add(_mnuShowFilePath);

      MenuItem mnuRememberCheckBoxes = new MenuItem(_i18n.t("FileManager.remember.checkboxes"));
      mnuRememberCheckBoxes.setOnAction(e -> onRememberCheckBoxes());
      ret.getItems().add(mnuRememberCheckBoxes);

      return ret;
   }

   private void onShowFilePath()
   {
      _mnuShowFileName.setSelected(false);
      initLabelText();
   }

   private void onShowFileName()
   {
      _mnuShowFilePath.setSelected(false);
      initLabelText();
   }

   private void onRememberCheckBoxes()
   {
      _pref.set(SHOW_FILE_NAME, _mnuShowFileName.isSelected());
      _pref.set(SHOW_FILE_PATH, _mnuShowFilePath.isSelected());
   }

   private void onCopyFilePath()
   {
      if(null != _file)
      {
         final Clipboard clipboard = Clipboard.getSystemClipboard();
         final ClipboardContent content = new ClipboardContent();
         content.putString(_file.getAbsolutePath());
         clipboard.setContent(content);

      }
   }

   private void onOpenFilePath()
   {
      try
      {
         if(null != _file)
         {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(_file.getParentFile());
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }


   public BorderPane getTabHeader()
   {
      return _node;
   }

   public void setFileState(FileState fileState)
   {
      _fileState = fileState;

      switch (_fileState)
      {
         case NO_FILE:
            _btnFileInfo.setGraphic(null);
            _btnFileInfo.setVisible(false);
            break;

         case CLEAN:
            ImageView imgClean = new ImageView(new Props(getClass()).getImage("smallFile.gif"));
            _btnFileInfo.setGraphic(imgClean);
            _btnFileInfo.setVisible(true);
            break;

         case CHANGED:
            ImageView imgChanged = new ImageView(new Props(getClass()).getImage("smallFileChanged.gif"));
            _btnFileInfo.setGraphic(imgChanged);
            _btnFileInfo.setVisible(true);
            break;
      }
   }

   public FileState getFileState()
   {
      return _fileState;
   }


   private void showPopup()
   {
      _buttonPopup.show(_btnFileInfo, Side.BOTTOM, 0, -_btnFileInfo.getHeight());
   }

   public File getFile()
   {
      return _file;
   }

   public void setFile(File file)
   {
      _file = file;
      initLabelText();
   }
}
