package net.sourceforge.squirrel_sql.client.gui.titlefilepath;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TitleFilePathHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TitleFilePathHandler.class);

   private static final String PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_FILE_NAME = "Squirrel.TitleFilePathHandler.showFileName";
   private static final String PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_PATH_NAME = "Squirrel.TitleFilePathHandler.showPathName";

   private File _sqlFile;
   private TitleFilePathHandlerListener _titleFilePathHandlerListener;
   private SmallTabButton _smallTabButton;
   private JPopupMenu _popUp;
   private JCheckBoxMenuItem _chkMnuShowFileName;
   private JCheckBoxMenuItem _chkMnuShowFilePath;


   public TitleFilePathHandler(TitleFilePathHandlerListener titleFilePathHandlerListener)
   {
      SquirrelResources resources = Main.getApplication().getResources();
      _titleFilePathHandlerListener = titleFilePathHandlerListener;
      String msg = s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.tooltip");
      _smallTabButton = new SmallTabButton(msg, resources.getIcon(SquirrelResources.IImageNames.SMALL_FILE));

      _smallTabButton.addActionListener(e -> showPopup());

      _popUp = new JPopupMenu();

      JMenuItem mnuOpenFilePath = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.openFilePath"));
      _popUp.add(mnuOpenFilePath);
      mnuOpenFilePath.addActionListener(e -> onOpenFilePath());

      JMenuItem mnuCopyFilePath = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.copyFilePath"));
      _popUp.add(mnuCopyFilePath);
      mnuCopyFilePath.addActionListener(e -> onCopyFilePath());

      JMenuItem mnuCopyFileName = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.copyFileName"));
      _popUp.add(mnuCopyFileName);
      mnuCopyFileName.addActionListener(e -> onCopyFileName());


      _popUp.addSeparator();

      _chkMnuShowFileName = new JCheckBoxMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.showFileName"));
      _popUp.add(_chkMnuShowFileName);
      _chkMnuShowFileName.addActionListener(e -> onChkMnuShowFileName());

      _chkMnuShowFilePath = new JCheckBoxMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.showFilePath"));
      _popUp.add(_chkMnuShowFilePath);
      _chkMnuShowFilePath.addActionListener(e -> onChkMnuShowFilePath());


      JMenuItem mnuRememberCheckboxes = new JMenuItem(s_stringMgr.getString("desktopcontainer.TitleFilePathHandler.rememberCheckBoxes"));
      _popUp.add(mnuRememberCheckboxes);
      mnuRememberCheckboxes.addActionListener(e -> onRememberCheckboxes());

      _chkMnuShowFileName.setSelected(Props.getBoolean(PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_FILE_NAME, false));
      _chkMnuShowFilePath.setSelected(Props.getBoolean(PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_PATH_NAME, false));
   }

   private void onRememberCheckboxes()
   {
      Props.putBoolean(PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_FILE_NAME, _chkMnuShowFileName.isSelected());
      Props.putBoolean(PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_PATH_NAME, _chkMnuShowFilePath.isSelected());
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
      _popUp.show(_smallTabButton, 0, 0);
   }

   public void setSqlFile(File sqlFile)
   {
      _sqlFile = sqlFile;
      if(null == _sqlFile)
      {
         _smallTabButton.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_FILE));
         _smallTabButton.setToolTipText("");
      }
      else
      {
         _smallTabButton.setToolTipText(_sqlFile.getAbsolutePath());
      }

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
         _smallTabButton.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_FILE_CHANGED));
      }
      else
      {
         _smallTabButton.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_FILE));
      }
   }
}
