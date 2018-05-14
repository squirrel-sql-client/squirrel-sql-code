package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TableExportCsvController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableExportCsvController.class);

   private TableExportCsvDlg _dlg;
   private boolean _ok = false;

   private JFrame _owner;


   TableExportCsvController(JFrame owner)
   {
      _owner = owner;
      _dlg = createDialog(owner);

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

   protected TableExportCsvDlg createDialog(JFrame owner) {
      return new TableExportCsvDlg(owner);
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

      _dlg.radFormatXLSX.addActionListener(new ActionListener()
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

      _dlg.radFormatXML.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFormat(true);
         }
      });

      _dlg.radFormatJSON.addActionListener(new ActionListener()
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
         _dlg.lblCharset.setEnabled(true);
         _dlg.charsets.setEnabled(true);
         _dlg.lblLineSeparator.setEnabled(true);
         _dlg._lineSeparators.setEnabled(true);

         if(_dlg.chkSeparatorTab.isSelected())
         {
            _dlg.txtSeparatorChar.setText(null);
            _dlg.txtSeparatorChar.setEnabled(false);
            _dlg.lblSeparator.setEnabled(false);
         }
         else
         {
            _dlg.txtSeparatorChar.setEnabled(true);
            _dlg.lblSeparator.setEnabled(true);
         }

         if(replaceEnding)
         {
            replaceFileEnding();
         }
      }
      else if (_dlg.radFormatXLSX.isSelected() || _dlg.radFormatXLS.isSelected())
      {
         _dlg.lblSeparator.setEnabled(false);
         _dlg.lblCharset.setEnabled(false);
         _dlg.chkSeparatorTab.setEnabled(false);
         _dlg.txtSeparatorChar.setEnabled(false);
         _dlg.charsets.setEnabled(false);
         _dlg.lblLineSeparator.setEnabled(false);
         _dlg._lineSeparators.setEnabled(false);
         if(replaceEnding)
         {
            replaceFileEnding();
         }
      }
      else if (_dlg.radFormatXML.isSelected() || _dlg.radFormatJSON.isSelected())
      {
         _dlg.lblSeparator.setEnabled(false);
         _dlg.lblCharset.setEnabled(false);
         _dlg.chkSeparatorTab.setEnabled(false);
         _dlg.txtSeparatorChar.setEnabled(false);
         _dlg.charsets.setEnabled(false);
         _dlg.lblLineSeparator.setEnabled(false);
         _dlg._lineSeparators.setEnabled(false);
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
      else if (_dlg.radFormatXLSX.isSelected())
      {
         newEnding = "xlsx";
      }
      else if (_dlg.radFormatXLS.isSelected())
      {
         newEnding = "xls";
      }
      else if (_dlg.radFormatXML.isSelected())
      {
         newEnding = "xml";
      }
      else if (_dlg.radFormatJSON.isSelected())
      {
         newEnding = "json";
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
	  if(warnIfExcel() == false){
		  return;
	  }
	   
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
      _ok = true;
      closeDlg();
   }




   /**
    * Warn the user if we should export the data into a Excel file.
    * Exporting a excel file may use a huge amount of memory and can cause some problems within MS Excel.
    * @return true, if the user wishes to continue.
    */
   private boolean warnIfExcel() {
	   if(this._dlg.radFormatXLS.isSelected() && shouldWarnIfExcel()){
		   // i18n[TableExportCsvController.warnIfExcel=Exporting a huge data set for MS Excel maybe use huge memory.]
		   String msg = s_stringMgr.getString("TableExportCsvController.warnIfExcel");
		   int option = JOptionPane.showConfirmDialog(_dlg, msg, null, JOptionPane.OK_CANCEL_OPTION);
		   if(option != JOptionPane.OK_OPTION){
			   return false;
		   }		
	   }
	   return true;
   }
   /**
    * Decide, if we want warn the user, if the choose the Excel export.
    * This default implementation returns always false.
    * @return true, if we should warn.
    */
   protected boolean shouldWarnIfExcel(){
	   return false;
   }

   protected void writePrefs()
   {
      TableExportPreferences tableExportPreferences = new TableExportPreferences();
      writeControlsToPrefs(tableExportPreferences);

      TableExportPreferencesDAO.savePreferences(tableExportPreferences);
   }
   protected void writeControlsToPrefs(TableExportPreferences prefs)
   {

      // Preferences.userRoot().put(PREF_KEY_CSV_FILE, );
      prefs.setCsvFile(_dlg.txtFile.getText());

      //Preferences.userRoot().put(PREF_KEY_CSV_ENCODING, _dlg.charsets.getSelectedItem().toString());
      prefs.setCsvEncoding(_dlg.charsets.getSelectedItem().toString());

      //Preferences.userRoot().putBoolean(PREF_KEY_WITH_HEADERS, _dlg.chkWithHeaders.isSelected());
      prefs.setWithHeaders(_dlg.chkWithHeaders.isSelected());

      //Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_CSV, _dlg.radFormatCSV.isSelected());
      prefs.setFormatCSV(_dlg.radFormatCSV.isSelected());

      //Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_XLS, _dlg.radFormatXLSX.isSelected());
      prefs.setFormatXLS(_dlg.radFormatXLSX.isSelected());

      //Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_XLS_OLD, _dlg.radFormatXLS.isSelected());
      prefs.setFormatXLSOld(_dlg.radFormatXLS.isSelected());

      //Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_XML, _dlg.radFormatXML.isSelected());
      prefs.setFormatXML(_dlg.radFormatXML.isSelected());

      //Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_XML, _dlg.radFormatXML.isSelected());
      prefs.setFormatJSON(_dlg.radFormatJSON.isSelected());

      //Preferences.userRoot().putBoolean(PREF_KEY_SEPERATOR_TAB, _dlg.chkSeparatorTab.isSelected());
      prefs.setSeperatorTab(_dlg.chkSeparatorTab.isSelected());

      //Preferences.userRoot().put(PREF_KEY_SEPERATOR_CHAR, _dlg.txtSeparatorChar.getText());
      prefs.setSeperatorChar(_dlg.txtSeparatorChar.getText());

      //Preferences.userRoot().put(PREF_KEY_LINE_SEPERATOR, ((LineSeparator)_dlg._lineSeparators.getSelectedItem()).name());
      prefs.setLineSeperator(((LineSeparator)_dlg._lineSeparators.getSelectedItem()).name());

      //Preferences.userRoot().putBoolean(PREF_KEY_EXPORT_COMPLETE, _dlg.radComplete.isSelected());
      prefs.setExportComplete(_dlg.radComplete.isSelected());

      //Preferences.userRoot().putBoolean(PREF_KEY_USE_GLOBAL_PREFS_FORMATING, _dlg.radUseGlobalPrefsFormating.isSelected());
      prefs.setUseGlobalPrefsFormating(_dlg.radUseGlobalPrefsFormating.isSelected());

      //Preferences.userRoot().putBoolean(PREF_KEY_EXECUTE_COMMAND, _dlg.chkExecCommand.isSelected());
      prefs.setExecuteCommand(_dlg.chkExecCommand.isSelected());

      // Preferences.userRoot().put(PREF_KEY_COMMAND, _dlg.txtCommand.getText());
      prefs.setCommand(_dlg.txtCommand.getText());
   }

	
   private void initDlg()
   {
      TableExportPreferences prefs = TableExportPreferencesDAO.loadPreferences();

      if (formatIsNewXlsx(prefs))
      {
         _dlg.txtFile.setText(replaceXlsByXlsx(prefs.getCsvFile()));
      }
      else
      {
         _dlg.txtFile.setText(prefs.getCsvFile());
      }

      _dlg.charsets.setSelectedItem(prefs.getCsvEncoding());
      _dlg.chkWithHeaders.setSelected(prefs.isWithHeaders());


      _dlg.chkSeparatorTab.setSelected(prefs.isSeperatorTab());

      if(false == _dlg.chkSeparatorTab.isSelected())
      {
         _dlg.txtSeparatorChar.setText(prefs.getSeperatorChar());
      }

      if(prefs.isFormatCSV())
      {
         _dlg.radFormatCSV.setSelected(true);
      }
      else if(formatIsNewXlsx(prefs))
      {
         _dlg.radFormatXLSX.setSelected(true);
      }
      else if(prefs.isFormatXLSOld())
      {
         _dlg.radFormatXLS.setSelected(true);
      }
      else if(prefs.isFormatXML())
      {
         _dlg.radFormatXML.setSelected(true);
      }
      else if(prefs.isFormatJSON())
      {
         _dlg.radFormatJSON.setSelected(true);
      }
      else
      {
         _dlg.radFormatCSV.setSelected(true);
      }

      onFormat(false);

      initSelectionPanel(prefs);

      if(prefs.isUseGlobalPrefsFormating())
      {
         _dlg.radUseGlobalPrefsFormating.setSelected(true);
      }
      else
      {
         _dlg.radUseDefaultFormating.setSelected(true);
      }


      _dlg.chkExecCommand.setSelected(prefs.isExecuteCommand());
      onChkExecCommand();

      _dlg.txtCommand.setText(prefs.getCommand());


      LineSeparator preferredLineSeparator = LineSeparator.valueOf(prefs.getLineSeperator());

      _dlg._lineSeparators.setSelectedItem(preferredLineSeparator);
   }

   private boolean formatIsNewXlsx(TableExportPreferences preferences)
   {
      return preferences.isFormatXLS();
   }

   private String replaceXlsByXlsx(String fileName)
   {
      String oldXlsSuffix = ".xls";
      if(null != fileName && fileName.toLowerCase().endsWith(oldXlsSuffix))
      {
         fileName = fileName.substring(0, fileName.length() - oldXlsSuffix.length()) + ".xlsx";
      }

      return fileName;
   }

   /**
    * Initialize the values for the selection panel from the saved properties.
    * @param userRoot the saved properties.
    */
   protected void initSelectionPanel(TableExportPreferences userRoot)
   {

	   if(userRoot.isExportComplete())
	   {
		   _dlg.radComplete.setSelected(true);
	   }
	   else
	   {
		   _dlg.radSelection.setSelected(true);
	   }
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
			private static final long serialVersionUID = 1L;

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

   public String getSeparatorChar()
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

   boolean exportComplete()
   {
      return _dlg.radComplete.isSelected();
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

   protected TableExportCsvDlg getDialog() {
	   return this._dlg;
   }

   public Frame getOwningFrame()
   {
      return _owner;
   }
}
