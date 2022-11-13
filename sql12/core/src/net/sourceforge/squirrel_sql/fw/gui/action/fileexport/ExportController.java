package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class ExportController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportController.class);

   private ExportDlg _dlg;
   private boolean _ok = false;

   private Window _owner;
   private ExportDialogType _exportDialogType;
   private TableExportSelectionPanelController _exportSelectionPanelController;


   /**
    * @param tableExportDlgFinishedListener when null the dialog is modal.
    * @param exportSourceAccess
    */
   ExportController(ExportSourceAccess exportSourceAccess, Window owner, ExportDialogType exportDialogType)
   {
      _owner = owner;
      _exportDialogType = exportDialogType;
      _exportSelectionPanelController = new TableExportSelectionPanelController(exportSourceAccess, exportDialogType);

      _dlg = new ExportDlg(owner, _exportSelectionPanelController.getPanel(), _exportDialogType);

      initData();

      initListeners();

      _dlg.txtSeparatorChar.addKeyListener(new KeyAdapter()
      {
         public void keyTyped(KeyEvent e)
         {
            onSeparatorCharChanged(e);
         }
      });

      _dlg.txtFile.getDocument().addDocumentListener(new DocumentListener() {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            updateDestinationInfo();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            updateDestinationInfo();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            updateDestinationInfo();
         }
      });

      updateDestinationInfo();

      _dlg.getRootPane().setDefaultButton(_dlg.btnOk);
      GUIUtils.enableCloseByEscape(_dlg);
   }

   private void updateDestinationInfo()
   {
      _exportSelectionPanelController.updateExportDestinationInfo(_dlg.txtFile.getText(), _dlg.radFormatXLSX.isSelected() || _dlg.radFormatXLS.isSelected());
   }

   public void showDialog()
   {
      _dlg.pack();
      GUIUtils.centerWithinParent(_dlg);
      _dlg.setVisible(true);
   }


   private void onSeparatorCharChanged(KeyEvent e)
   {
      SwingUtilities.invokeLater(() ->
      {
         String text = _dlg.txtSeparatorChar.getText();
         if(null != text && 1 < text.length())
         {
            _dlg.txtSeparatorChar.setText(text.substring(0,1));
            Toolkit.getDefaultToolkit().beep();
         }
      });

   }

   private void initListeners()
   {
      _dlg.btnOk.addActionListener(e -> onOK());

      _dlg.btnCancel.addActionListener(e -> closeDlg());

      _dlg.radFormatCSV.addActionListener(e -> onFormat(true));

      _dlg.radFormatXLSX.addActionListener(e -> onFormat(true));

      _dlg.radFormatXLS.addActionListener(e -> onFormat(true));

      _dlg.radFormatXML.addActionListener(e -> onFormat(true));

      _dlg.radFormatJSON.addActionListener(e -> onFormat(true));


      _dlg.chkSeparatorTab.addActionListener(e -> onFormat(false));


      _dlg.chkExecCommand.addActionListener(e -> onChkExecCommand());

      _dlg.btnFile.addActionListener(e -> onFile());

      _dlg.btnCommandFile.addActionListener(e -> onCommandFile());
   }

   private void onFormat(boolean replaceEnding)
   {

      if (_dlg.radFormatCSV.isSelected())
      {
         _dlg.lblSeparator.setEnabled(true);
         _dlg.chkSeparatorTab.setEnabled(true);
         _dlg.txtSeparatorChar.setEnabled(true);
         _dlg.lblCharset.setEnabled(true);
         _dlg.cboCharsets.setEnabled(true);
         _dlg.lblLineSeparator.setEnabled(true);
         _dlg.cboLineSeparators.setEnabled(true);

         _dlg.chkUseColoring.setEnabled(false);
         _dlg.btnUseColoringInfo.setEnabled(false);


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
         _dlg.cboCharsets.setEnabled(false);
         _dlg.chkSeparatorTab.setEnabled(false);
         _dlg.txtSeparatorChar.setEnabled(false);
         _dlg.lblLineSeparator.setEnabled(false);
         _dlg.cboLineSeparators.setEnabled(false);
         _dlg.chkUseColoring.setEnabled(_dlg.radFormatXLSX.isSelected());
         _dlg.btnUseColoringInfo.setEnabled(_dlg.radFormatXLSX.isSelected());


         if(replaceEnding)
         {
            replaceFileEnding();
         }
      }
      else if (_dlg.radFormatXML.isSelected() || _dlg.radFormatJSON.isSelected())
      {
         _dlg.lblSeparator.setEnabled(false);
         _dlg.lblCharset.setEnabled(true);
         _dlg.cboCharsets.setEnabled(true);
         _dlg.chkSeparatorTab.setEnabled(false);
         _dlg.txtSeparatorChar.setEnabled(false);
         _dlg.lblLineSeparator.setEnabled(false);
         _dlg.cboLineSeparators.setEnabled(false);

         _dlg.chkUseColoring.setEnabled(false);
         _dlg.btnUseColoringInfo.setEnabled(false);


         if(replaceEnding)
         {
            replaceFileEnding();
         }
      }
      else
      {
         throw new IllegalStateException("No valid output format");
      }

      updateDestinationInfo();
   }

   private void replaceFileEnding()
   {
      String newEnding;

      if (_dlg.radFormatCSV.isSelected())
      {
         newEnding = FileEndings.CSV.get();
      }
      else if (_dlg.radFormatXLSX.isSelected())
      {
         newEnding = FileEndings.XLSX.get();
      }
      else if (_dlg.radFormatXLS.isSelected())
      {
         newEnding = FileEndings.XLS.get();
      }
      else if (_dlg.radFormatXML.isSelected())
      {
         newEnding = FileEndings.XML.get();
      }
      else if (_dlg.radFormatJSON.isSelected())
      {
         newEnding = FileEndings.JSON.get();
      }
      else
      {
         throw new IllegalStateException("No valid output format");
      }

      String file = _dlg.txtFile.getText();
      if(null == file || 0 == file.trim().length() || file.toUpperCase().endsWith("." + newEnding.toUpperCase()))
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
         if (FileEndings.fileEndsWithOneOf(file))
         {
            // We replace the ending only if it is a known ending, see bug #1474
            newFile = file.substring(0, file.lastIndexOf(".")) + "." + newEnding;
         }
         else
         {
            newFile = file + "." + newEnding;
         }
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
      if(warnIfExcel() == false)
      {
         return;
      }
	   
      String singleExportFileName = _dlg.txtFile.getText();
      if(StringUtilities.isEmpty(singleExportFileName, true))
      {
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

      if(    new File(singleExportFileName).exists()
          && false == isExportingMultipleFiles() // For now in case of multiple export files these files will be replaced silently.
        )
      {
         // i18n[TableExportCsvController.replaceFile=The export file already exisits. Would you like to replace it?]
         String msg = s_stringMgr.getString("TableExportCsvController.replaceFile");
         if(JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(_dlg, msg))
         {
            return;
         }
      }

      TableExportPreferences tableExportPreferences = new TableExportPreferences();
      writeControlsToPrefs(tableExportPreferences);
      TableExportPreferencesDAO.savePreferences(tableExportPreferences);

      _ok = true;
      closeDlg();
   }

   private boolean isExportingMultipleFiles()
   {
      return _exportSelectionPanelController.isExportMultipleSQLResults()
             && false == (_dlg.radFormatXLS.isSelected() || _dlg.radFormatXLSX.isSelected()); // Multiple exports to MS Excel means one Excel file with multiple tabs.
   }


   /**
    * Warn the user if we should export the data into a Excel file.
    * Exporting a excel file may use a huge amount of memory and can cause some problems within MS Excel.
    * @return true, if the user wishes to continue.
    */
   private boolean warnIfExcel()
   {
      if(this._dlg.radFormatXLS.isSelected() && _exportDialogType.isWarnIfExcel())
      {
         // i18n[TableExportCsvController.warnIfExcel=Exporting a huge data set for MS Excel maybe use huge memory.]
         String msg = s_stringMgr.getString("TableExportCsvController.warnIfExcel");
         int option = JOptionPane.showConfirmDialog(_dlg, msg, null, JOptionPane.OK_CANCEL_OPTION);
         if(option != JOptionPane.OK_OPTION)
         {
            return false;
         }
      }
      return true;
   }

   private void writeControlsToPrefs(TableExportPreferences prefs)
   {
      // Preferences.put(PREF_KEY_CSV_FILE, );
      prefs.setFile(_dlg.txtFile.getText());

      //Preferences.put(PREF_KEY_CSV_ENCODING, _dlg.charsets.getSelectedItem().toString());
      prefs.setEncoding(_dlg.cboCharsets.getSelectedItem().toString());

      //Preferences.putBoolean(PREF_KEY_WITH_HEADERS, _dlg.chkWithHeaders.isSelected());
      prefs.setWithHeaders(_dlg.chkWithHeaders.isSelected());

      //Preferences.putBoolean(PREF_KEY_FORMAT_CSV, _dlg.radFormatCSV.isSelected());
      prefs.setFormatCSV(_dlg.radFormatCSV.isSelected());

      //Preferences.putBoolean(PREF_KEY_FORMAT_XLS, _dlg.radFormatXLSX.isSelected());
      prefs.setFormatXLS(_dlg.radFormatXLSX.isSelected());

      prefs.setUseColoring(_dlg.chkUseColoring.isSelected());

      //Preferences.putBoolean(PREF_KEY_FORMAT_XLS_OLD, _dlg.radFormatXLS.isSelected());
      prefs.setFormatXLSOld(_dlg.radFormatXLS.isSelected());

      //Preferences.putBoolean(PREF_KEY_FORMAT_XML, _dlg.radFormatXML.isSelected());
      prefs.setFormatXML(_dlg.radFormatXML.isSelected());

      //Preferences.putBoolean(PREF_KEY_FORMAT_XML, _dlg.radFormatXML.isSelected());
      prefs.setFormatJSON(_dlg.radFormatJSON.isSelected());

      //Preferences.putBoolean(PREF_KEY_SEPERATOR_TAB, _dlg.chkSeparatorTab.isSelected());
      prefs.setSeperatorTab(_dlg.chkSeparatorTab.isSelected());

      //Preferences.put(PREF_KEY_SEPERATOR_CHAR, _dlg.txtSeparatorChar.getText());
      prefs.setSeperatorChar(_dlg.txtSeparatorChar.getText());

      //Preferences.put(PREF_KEY_LINE_SEPERATOR, ((LineSeparator)_dlg._lineSeparators.getSelectedItem()).name());
      prefs.setLineSeperator(((LineSeparator)_dlg.cboLineSeparators.getSelectedItem()).name());

      _exportSelectionPanelController.writeControlsToPrefs(prefs);

      prefs.setUseGlobalPrefsFormating(_dlg.radUseGlobalPrefsFormating.isSelected());

      prefs.setExecuteCommand(_dlg.chkExecCommand.isSelected());

      prefs.setCommand(_dlg.txtCommand.getText());
   }

	
   private void initData()
   {
      TableExportPreferences prefs = TableExportPreferencesDAO.loadPreferences();

      if (formatIsNewXlsx(prefs))
      {
         _dlg.txtFile.setText(replaceXlsByXlsx(prefs.getFile()));
      }
      else
      {
         _dlg.txtFile.setText(prefs.getFile());
      }
      _dlg.chkUseColoring.setSelected(prefs.isUseColoring());

      _dlg.cboCharsets.setSelectedItem(prefs.getEncoding());
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

      _exportSelectionPanelController.initPanel(prefs);

      onFormat(false);

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

      _dlg.cboLineSeparators.setSelectedItem(preferredLineSeparator);
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

   private void onChkExecCommand()
   {
      _dlg.txtCommand.setEnabled(_dlg.chkExecCommand.isSelected());
      _dlg.btnCommandFile.setEnabled(_dlg.chkExecCommand.isSelected());
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

   File getSingleExportTargetFile()
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

   String getCommand(File firstExportedFile)
   {
      if(_dlg.chkExecCommand.isSelected())
      {
         return _dlg.txtCommand.getText().replaceAll("%file", firstExportedFile.getAbsolutePath());
      }
      else
      {
         return null;
      }
   }

   public Window getOwningWindow()
   {
      return _owner;
   }

   public boolean isUITableMissingBlobData()
   {
      return _exportSelectionPanelController.getExportSourceAccess().isUITableMissingBlobData(getSeparatorChar());
   }

   public ExportSourceAccess getExportSourceAccess()
   {
      return _exportSelectionPanelController.getExportSourceAccess();
   }
}
