package net.sourceforge.squirrel_sql.client.gui.titlefilepath;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionUtil;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

public class TitleFilePathHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TitleFilePathHandler.class);

   public static final String PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_FILE_NAME = "Squirrel.TitleFilePathHandler.showFileName";
   public static final String PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_PATH_NAME = "Squirrel.TitleFilePathHandler.showPathName";
   public static final String PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_INTERNALLY_SAVED_SESSION_FILE_NAME= "Squirrel.TitleFilePathHandler.showInternallySavedSessionFileName";
   public static final String PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_INTERNALLY_SAVED_SESSION_PATH_NAME= "Squirrel.TitleFilePathHandler.showInternallySavedSessionPathName";
   
   
   private File _sqlFile;
   private TitleFilePathHandlerListener _titleFilePathHandlerListener;
   private SmallTabButton _smallTabButton;
   private JCheckBoxMenuItem _chkMnuShowFileName;
   private JCheckBoxMenuItem _chkMnuShowFilePath;

   private TitleFileCheckBoxesInitializer _titleFileCheckBoxesInitializer = new TitleFileCheckBoxesInitializer();


   public TitleFilePathHandler(TitleFilePathHandlerListener titleFilePathHandlerListener)
   {
      SquirrelResources resources = Main.getApplication().getResources();
      _titleFilePathHandlerListener = titleFilePathHandlerListener;
      String msg = s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.tooltip");
      _smallTabButton = new SmallTabButton(msg, resources.getIcon(SquirrelResources.IImageNames.SMALL_FILE));

      _smallTabButton.addActionListener(e -> showPopup());
      _chkMnuShowFileName = new JCheckBoxMenuItem();
      _chkMnuShowFilePath = new JCheckBoxMenuItem();
   }

   private JPopupMenu createPopupMenu()
   {
      JPopupMenu popUp = new JPopupMenu();

      JMenuItem mnuOpenFilePath;
      if(SavedSessionUtil.isInSavedSessionsDir(_sqlFile))
      {
         mnuOpenFilePath = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.openInternallySavedSessionFilePath"));
      }
      else
      {
         mnuOpenFilePath = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.openFilePath"));
      }
      popUp.add(mnuOpenFilePath);
      mnuOpenFilePath.addActionListener(e -> onOpenFilePath());

      JMenuItem mnuCopyFilePath;
      if(SavedSessionUtil.isInSavedSessionsDir(_sqlFile))
      {
         mnuCopyFilePath = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.copyInternallySavedSessionFilePath"));
      }
      else
      {
         mnuCopyFilePath = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.copyFilePath"));
      }
      popUp.add(mnuCopyFilePath);
      mnuCopyFilePath.addActionListener(e -> onCopyFilePath());

      JMenuItem mnuCopyFileName;
      if(SavedSessionUtil.isInSavedSessionsDir(_sqlFile))
      {
         mnuCopyFileName = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.copyInternallySavedSessionFileName"));
      }
      else
      {
         mnuCopyFileName = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.copyFileName"));
      }
      popUp.add(mnuCopyFileName);
      mnuCopyFileName.addActionListener(e -> onCopyFileName());


      popUp.addSeparator();

      if(SavedSessionUtil.isInSavedSessionsDir(_sqlFile))
      {
         _chkMnuShowFileName.setText(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.showInternallySavedSessionFileName"));
      }
      else
      {
         _chkMnuShowFileName.setText(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.showFileName"));
      }
      popUp.add(_chkMnuShowFileName);
      _chkMnuShowFileName.addActionListener(e -> onChkMnuShowFileName());

      if(SavedSessionUtil.isInSavedSessionsDir(_sqlFile))
      {
         _chkMnuShowFilePath.setText(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.showInternallySavedSessionFilePath"));
      }
      else
      {
         _chkMnuShowFilePath.setText(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.showFilePath"));
      }
      popUp.add(_chkMnuShowFilePath);
      _chkMnuShowFilePath.addActionListener(e -> onChkMnuShowFilePath());


      JMenuItem mnuRememberCheckboxes = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.rememberCheckBoxes"));
      popUp.add(mnuRememberCheckboxes);
      mnuRememberCheckboxes.addActionListener(e -> onRememberCheckboxes());

      return popUp;
   }

   private void onRememberCheckboxes()
   {
      if(SavedSessionUtil.isInSavedSessionsDir(_sqlFile))
      {
         Props.putBoolean(PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_INTERNALLY_SAVED_SESSION_FILE_NAME, _chkMnuShowFileName.isSelected());
         Props.putBoolean(PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_INTERNALLY_SAVED_SESSION_PATH_NAME, _chkMnuShowFilePath.isSelected());
      }
      else
      {
         Props.putBoolean(PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_FILE_NAME, _chkMnuShowFileName.isSelected());
         Props.putBoolean(PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_PATH_NAME, _chkMnuShowFilePath.isSelected());
      }
   }

   private void onChkMnuShowFilePath()
   {
      _chkMnuShowFileName.setSelected(false);
      _titleFilePathHandlerListener.refreshFileDisplay();
   }

   private void onChkMnuShowFileName()
   {
      _chkMnuShowFilePath.setSelected(false);
      _titleFilePathHandlerListener.refreshFileDisplay();
   }

   private void onCopyFilePath()
   {
      _onCopyFile(true);
   }

   private void onCopyFileName()
   {
      _onCopyFile(false);
   }

   private void _onCopyFile(boolean fullPath)
   {
      if(hasFile())
      {
         Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringSelection data;

         if (fullPath)
         {
            data = new StringSelection(_sqlFile.getAbsolutePath());
         }
         else
         {
            data = new StringSelection(_sqlFile.getName());
         }

         clip.setContents(data, data);
      }
   }

   private void onOpenFilePath()
   {
      try
      {
         if(hasFile())
         {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(_sqlFile.getParentFile());
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void showPopup()
   {
      createPopupMenu().show(_smallTabButton, 0, 0);
   }

   public void setSqlFile(File sqlFile)
   {
      _sqlFile = sqlFile;
      if(null == _sqlFile)
      {
         _smallTabButton.setIcon(null);
         _smallTabButton.setToolTipText("");
      }
      else
      {
         if(SavedSessionUtil.isInSavedSessionsDir(_sqlFile))
         {
            _smallTabButton.setToolTipText(s_stringMgr.getString("TitleFilePathHandler.internallySavedFile", _sqlFile.getAbsolutePath()));
         }
         else
         {
            _smallTabButton.setToolTipText(_sqlFile.getAbsolutePath());
         }
      }

      _titleFileCheckBoxesInitializer.init(_chkMnuShowFileName, _chkMnuShowFilePath, _sqlFile);

      _titleFilePathHandlerListener.refreshFileDisplay();
   }

   public boolean hasFile()
   {
      return null != _sqlFile;
   }

   public String getSqlFile()
   {

      if (_chkMnuShowFilePath.isSelected())
      {
         return "  " + s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.pathDisplayPrefix", _sqlFile.getAbsolutePath());
      }
      else if (_chkMnuShowFileName.isSelected())
      {
         return "  " + s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.fileDisplayPrefix", _sqlFile.getName());
      }
      else
      {
         return "";
      }
   }

   public SmallTabButton getFileMenuSmallButton()
   {
      return _smallTabButton;
   }

   public void setUnsavedEdits(boolean unsavedEdits)
   {
      if (unsavedEdits)
      {
         if(SavedSessionUtil.isInSavedSessionsDir(_sqlFile))
         {
            _smallTabButton.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_FILE_INTERNAL_CHANGED));
         }
         else
         {
            _smallTabButton.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_FILE_CHANGED));
         }
      }
      else
      {
         if(SavedSessionUtil.isInSavedSessionsDir(_sqlFile))
         {
            _smallTabButton.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_FILE_INTERNAL));
         }
         else
         {
            _smallTabButton.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_FILE));
         }
      }
   }
}
