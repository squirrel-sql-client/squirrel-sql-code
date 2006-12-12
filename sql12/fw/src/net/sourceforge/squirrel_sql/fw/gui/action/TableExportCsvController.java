package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.io.File;
import java.awt.event.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class TableExportCsvController
{
   private static final String PREF_KEY_CSV_FILE = "SquirrelSQL.csvexport.csvfile";
   private static final String PREF_KEY_WITH_HEADERS = "SquirrelSQL.csvexport.withColumnHeaders";
   private static final String PREF_KEY_SEPERATOR_TAB = "SquirrelSQL.csvexport.sepearatorTab";
   private static final String PREF_KEY_SEPERATOR_CHAR = "SquirrelSQL.csvexport.sepearatorChar";
   private static final String PREF_KEY_EXPORT_COMPLETE = "SquirrelSQL.csvexport.exportcomplete";
   private static final String PREF_KEY_USE_GLOBAL_PREFS_FORMATING = "SquirrelSQL.csvexport.useGlobalPrefsFomating";
   private static final String PREF_KEY_EXECUTE_COMMAND = "SquirrelSQL.csvexport.executeCommand";
   private static final String PREF_KEY_COMMAND = "SquirrelSQL.csvexport.commandString";
   private static final String PREF_KEY_FORMAT_CSV = "SquirrelSQL.csvexport.formatCSV";
   private static final String PREF_KEY_FORMAT_XLS = "SquirrelSQL.csvexport.formatXLS";

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(TableExportCsvController.class);



   private TableExportCsvDlg _dlg;
   private boolean _ok = false;
   public static final int EXPORT_FORMAT_CSV = 0;
   public static final int EXPORT_FORMAT_XLS = 1;

   TableExportCsvController()
   {
      _dlg = new TableExportCsvDlg();

      initDlg();

      initListeners();

      _dlg.txtSeparatorChar.addKeyListener(new KeyAdapter()
      {
         public void keyTyped(KeyEvent e)
         {
            onSeparatorCharChanged(e);
         }
      });

      _dlg.getRootPane().setDefaultButton(_dlg.btnOk);
      installEscapeClose();

      _dlg.pack();

      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);

   }

   private void onSeparatorCharChanged(KeyEvent e)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            String text = _dlg.txtSeparatorChar.getText();
            if(null != text && 1 < text.length())
            {
               _dlg.txtSeparatorChar.setText(text.substring(0,1));
               Toolkit.getDefaultToolkit().beep();
            }
         }
      });

   }

   private void initListeners()
   {
      _dlg.btnOk.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      _dlg.btnCancel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            closeDlg();
         }
      });

      _dlg.radFormatCSV.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFormat(true);
         }
      });

      _dlg.radFormatXLS.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFormat(true);
         }
      });


      _dlg.chkSeparatorTab.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFormat(false);
         }
      });


      _dlg.chkExecCommand.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onChkExecCommand();
         }
      });

      _dlg.btnFile.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFile();
         }

      });

      _dlg.btnCommandFile.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCommandFile();
         }
      });
   }

   private void onFormat(boolean replaceEnding)
   {

      if (_dlg.radFormatCSV.isSelected())
      {
         _dlg.lblSeparator.setEnabled(true);
         _dlg.chkSeparatorTab.setEnabled(true);
         _dlg.txtSeparatorChar.setEnabled(true);

         if(_dlg.chkSeparatorTab.isSelected())
         {
            _dlg.txtSeparatorChar.setText(null);
            _dlg.txtSeparatorChar.setEnabled(false);
         }
         else
         {
            _dlg.txtSeparatorChar.setEnabled(true);
         }

         if(replaceEnding)
         {
            replaceFileEnding();
         }
      }
      else if (_dlg.radFormatXLS.isSelected())
      {
         _dlg.lblSeparator.setEnabled(false);
         _dlg.chkSeparatorTab.setEnabled(false);
         _dlg.txtSeparatorChar.setEnabled(false);
         if(replaceEnding)
         {
            replaceFileEnding();
         }
      }
      else
      {
         throw new IllegalStateException("No valid output format");
      }
   }

   private void replaceFileEnding()
   {
      String newEnding;

      if (_dlg.radFormatCSV.isSelected())
      {
         newEnding = "csv";
      }
      else if (_dlg.radFormatXLS.isSelected())
      {
         newEnding = "xls";
      }
      else
      {
         throw new IllegalStateException("No valid output format");
      }

      String file = _dlg.txtFile.getText();
      if(null == file ||
         0 == file.trim().length() ||
         file.toUpperCase().endsWith("." + newEnding.toUpperCase()))
      {
         return;
      }

      file = file.trim();

      String newFile;

      if(-1 == file.lastIndexOf(".") || file.lastIndexOf(".") < file.lastIndexOf(File.separator))
      {
         newFile = file + "." + newEnding;
      }
      else if(file.lastIndexOf(".") > file.lastIndexOf(File.separator))
      {
         newFile = file.substring(0, file.lastIndexOf(".")) + "." + newEnding;
      }
      else
      {
         newFile = file;
      }
      
      _dlg.txtFile.setText(newFile);
   }

   private void onCommandFile()
   {
      JFileChooser chooser = new JFileChooser(System.getProperties().getProperty("user.home"));
      chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

      // i18n[TableExportCsvController.commandChooserTitel=Choose command executable]
      chooser.setDialogTitle(s_stringMgr.getString("TableExportCsvController.commandChooserTitel"));

      // i18n[TableExportCsvController.commandChooserButton=Choose]
      if(JFileChooser.APPROVE_OPTION != chooser.showDialog(_dlg, s_stringMgr.getString("TableExportCsvController.commandChooserButton")))
      {
         return;
      }

      if(null != chooser.getSelectedFile())
      {
         _dlg.txtCommand.setText(chooser.getSelectedFile().getPath() + " %file");
      }
   }


   private void onFile()
   {
      JFileChooser chooser = null;

      String csvFileName = _dlg.txtFile.getText();
      if(null != csvFileName && 0 < csvFileName.trim().length())
      {
         File csvFile = new File(csvFileName);

         File parentFile = csvFile.getParentFile();
         if(null != parentFile && parentFile.exists())
         {
            chooser = new JFileChooser(parentFile);
         }
      }

      if(null == chooser)
      {
         chooser = new JFileChooser(System.getProperties().getProperty("user.home"));
      }


      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

      // i18n[TableExportCsvController.fileChooserTitel=Choose export file]
      chooser.setDialogTitle(s_stringMgr.getString("TableExportCsvController.fileChooserTitel"));

      // i18n[TableExportCsvController.fileChooserButton=Choose]
      if(JFileChooser.APPROVE_OPTION != chooser.showDialog(_dlg, s_stringMgr.getString("TableExportCsvController.fileChooserButton")))
      {
         return;
      }


      if(null != chooser.getSelectedFile())
      {
         _dlg.txtFile.setText(chooser.getSelectedFile().getPath());
      }

   }


   private void onOK()
   {
      String csvFileName = _dlg.txtFile.getText();
      if(null == csvFileName || 0 == csvFileName.trim().length())
      {
         // i18n[TableExportCsvController.noFile=You must provide a export file name.]
         String msg = s_stringMgr.getString("TableExportCsvController.noFile");
         JOptionPane.showMessageDialog(_dlg, msg);
         return;
      }

      if(false == _dlg.chkSeparatorTab.isSelected())
      {
         String sepChar = _dlg.txtSeparatorChar.getText();
         if(null == sepChar || 1 != sepChar.trim().length())
         {
            // i18n[TableExportCsvController.invalidSeparator=You must provide a single separator character or check "Use tab" to use the tab character.]
            String msg = s_stringMgr.getString("TableExportCsvController.invalidSeparator");
            JOptionPane.showMessageDialog(_dlg, msg);
            return;
         }
      }


      if(_dlg.chkExecCommand.isSelected())
      {
         String command = _dlg.txtCommand.getText();
         if(null == command || 0 == command.trim().length())
         {
            // i18n[TableExportCsvController.noCommand=You must provide a command string or uncheck "Execute command".]
            String msg = s_stringMgr.getString("TableExportCsvController.noCommand");
            JOptionPane.showMessageDialog(_dlg, msg);
            return;
         }
      }

      if(new File(csvFileName).exists())
      {
         // i18n[TableExportCsvController.replaceFile=The export file already exisits. Would you like to replace it?]
         String msg = s_stringMgr.getString("TableExportCsvController.replaceFile");
         if(JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(_dlg, msg))
         {
            return;
         }
      }

      writePrefs();
      closeDlg();
      _ok = true;
   }




   private void writePrefs()
   {
      Preferences.userRoot().put(PREF_KEY_CSV_FILE, _dlg.txtFile.getText());
      Preferences.userRoot().putBoolean(PREF_KEY_WITH_HEADERS, _dlg.chkWithHeaders.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_CSV, _dlg.radFormatCSV.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_XLS, _dlg.radFormatXLS.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_SEPERATOR_TAB, _dlg.chkSeparatorTab.isSelected());
      Preferences.userRoot().put(PREF_KEY_SEPERATOR_CHAR, _dlg.txtSeparatorChar.getText());
      Preferences.userRoot().putBoolean(PREF_KEY_EXPORT_COMPLETE, _dlg.radComplete.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_USE_GLOBAL_PREFS_FORMATING, _dlg.radUseGlobalPrefsFormating.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_EXECUTE_COMMAND, _dlg.chkExecCommand.isSelected());
      Preferences.userRoot().put(PREF_KEY_COMMAND, _dlg.txtCommand.getText());
   }


   private void initDlg()
   {
      _dlg.txtFile.setText(Preferences.userRoot().get(PREF_KEY_CSV_FILE, null));
      _dlg.chkWithHeaders.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_WITH_HEADERS, true));


      _dlg.chkSeparatorTab.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_SEPERATOR_TAB, false));

      if(false == _dlg.chkSeparatorTab.isSelected())
      {
         _dlg.txtSeparatorChar.setText(Preferences.userRoot().get(PREF_KEY_SEPERATOR_CHAR, ","));
      }

      if(Preferences.userRoot().getBoolean(PREF_KEY_FORMAT_CSV, true))
      {
         _dlg.radFormatCSV.setSelected(true);
      }
      else if(Preferences.userRoot().getBoolean(PREF_KEY_FORMAT_XLS, false))
      {
         _dlg.radFormatXLS.setSelected(true);
      }
      else
      {
         _dlg.radFormatCSV.setSelected(true);
      }


      onFormat(false);



      if(Preferences.userRoot().getBoolean(PREF_KEY_EXPORT_COMPLETE, true))
      {
         _dlg.radComplete.setSelected(true);
      }
      else
      {
         _dlg.radSelection.setSelected(true);
      }

      if(Preferences.userRoot().getBoolean(PREF_KEY_USE_GLOBAL_PREFS_FORMATING, true))
      {
         _dlg.radUseGlobalPrefsFormating.setSelected(true);
      }
      else
      {
         _dlg.radUseDefaultFormating.setSelected(true);
      }


      _dlg.chkExecCommand.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_EXECUTE_COMMAND, false));
      onChkExecCommand();

      _dlg.txtCommand.setText(Preferences.userRoot().get(PREF_KEY_COMMAND, "openoffice.org-2.0 -calc %file"));
   }

   private void onChkExecCommand()
   {
      _dlg.txtCommand.setEnabled(_dlg.chkExecCommand.isSelected());
      _dlg.btnCommandFile.setEnabled(_dlg.chkExecCommand.isSelected());
   }


   private void installEscapeClose()
   {
      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            closeDlg();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      _dlg.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      _dlg.getRootPane().getActionMap().put("CloseAction", closeAction);
   }

   private void closeDlg()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   boolean isOK()
   {
      return _ok;
   }

   File getFile()
   {
      return new File(_dlg.txtFile.getText());
   }

   String getSeparatorChar()
   {
      if(_dlg.chkSeparatorTab.isSelected())
      {
         return "\t";
      }
      else
      {
         return _dlg.txtSeparatorChar.getText();
      }
   }

   boolean includeHeaders()
   {
      return _dlg.chkWithHeaders.isSelected();
   }

   boolean exportComplete()
   {
      return _dlg.radComplete.isSelected();
   }

   boolean useGloablPrefsFormatting()
   {
      return _dlg.radUseGlobalPrefsFormating.isSelected();
   }

   String getCommand()
   {
      if(_dlg.chkExecCommand.isSelected())
      {
         // Copied from Java Doc Matcher.replaceAll:
         //
         // Note that backslashes (\) and dollar signs ($) in the replacement string
         // may cause the results to be different than if it
         // were being treated as a literal replacement string.
         // Dollar signs may be treated as references to
         // captured subsequences as described above, and
         // backslashes are used to escape literal characters in the replacement string.
         return _dlg.txtCommand.getText().replaceAll("%file", _dlg.txtFile.getText().replaceAll("\\\\","\\\\\\\\"));
      }
      else
      {
         return null;
      }
   }

   public int getExportFormat()
   {
      if(_dlg.radFormatCSV.isSelected())
      {
         return EXPORT_FORMAT_CSV;
      }
      else if(_dlg.radFormatXLS.isSelected())
      {
         return EXPORT_FORMAT_XLS;
      }
      else
      {
         throw new IllegalStateException("No valid output format");
      }

   }
}
