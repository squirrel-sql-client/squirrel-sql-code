package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class ExportController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportController.class);

   private ExportDlg _dlg;
   private final EditableComboBoxHandler _cboFileHandler;
   private final EditableComboBoxHandler _cboCommandHandler;

   private boolean _ok = false;

   private Window _owner;
   private ExportDialogType _exportDialogType;
   private TableExportSelectionPanelController _exportSelectionPanelController;
   private ExcelFontCtrl _excelFontCtrl = new ExcelFontCtrl();


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

      _cboFileHandler = new EditableComboBoxHandler(_dlg.cboFile, "fileexport.ExportDlg.cboFile");

      _cboCommandHandler = new EditableComboBoxHandler(_dlg.cboCommand, "fileexport.ExportDlg.cboCommand");


      initData();

      initListeners();

      _dlg.txtSeparatorChar.addKeyListener(new KeyAdapter()
      {
         public void keyTyped(KeyEvent e)
         {
            onSeparatorCharChanged(e);
         }
      });

      _cboFileHandler.addDocumentListener(new DocumentListener() {
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
      GUIUtils.initLocation(_dlg, 500, 880);
   }

   private void updateDestinationInfo()
   {
      _exportSelectionPanelController.updateExportDestinationInfo(_cboFileHandler.getItem(), _dlg.radFormatXLSX.isSelected() || _dlg.radFormatXLS.isSelected());
   }

   public void showDialog()
   {
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

      _dlg.btnChooseExcelFont.addActionListener(e -> onChooseExcelFont());

      _dlg.btnChooseExcelHeaderFont.addActionListener(e -> onChooseExcelHeaderFont());

      _dlg.chkSeparatorTab.addActionListener(e -> onFormat(false));

      _dlg.radUseGlobalPrefsFormating.addActionListener(e -> onAdjustEnableRenderGroupingSeparator());
      _dlg.radUseDefaultFormating.addActionListener(e -> onAdjustEnableRenderGroupingSeparator());

      _dlg.chkExecCommand.addActionListener(e -> onChkExecCommand());

      _dlg.btnFile.addActionListener(e -> onFile());

      _dlg.btnCommandFile.addActionListener(e -> onCommandFile());
   }

   private void onChooseExcelFont()
   {
      FontChooser fontChooser = new FontChooser(_dlg, true, true);
      Font font = fontChooser.showDialog(_excelFontCtrl.getFont());
      _excelFontCtrl.initFontLabel(_dlg.lblExcelFontName, font, fontChooser.isNoSelection());
   }

   private void onChooseExcelHeaderFont()
   {
      FontChooser fontChooser = new FontChooser(_dlg, true, true);
      Font font = fontChooser.showDialog(_excelFontCtrl.getHeaderFont());
      _excelFontCtrl.initHeaderFontLabel(_dlg.lblExcelHeaderFontName, font, fontChooser.isNoSelection());
   }

   private void onAdjustEnableRenderGroupingSeparator()
   {
      _dlg.chkRenderGroupingSeparator.setEnabled(_dlg.radUseGlobalPrefsFormating.isSelected());
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
         _dlg.chkExcelAutoFilter.setEnabled(false);
         _dlg.chkExcelFirstRowFrozen.setEnabled(false);
         _dlg.chkExcelFirstRowCentered.setEnabled(false);
         _dlg.chkExcelFirstRowBold.setEnabled(false);
         _dlg.btnChooseExcelFont.setEnabled(false);
         _dlg.btnChooseExcelHeaderFont.setEnabled(false);
         _dlg.lblExcelFontName.setEnabled(false);
         _dlg.lblExcelHeaderFontName.setEnabled(false);

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

         _dlg.chkExcelAutoFilter.setEnabled(true);
         _dlg.chkExcelFirstRowFrozen.setEnabled(true);
         _dlg.chkExcelFirstRowCentered.setEnabled(true);
         _dlg.chkExcelFirstRowBold.setEnabled(true);
         _dlg.btnChooseExcelFont.setEnabled(true);
         _dlg.btnChooseExcelHeaderFont.setEnabled(true);
         _dlg.lblExcelFontName.setEnabled(true);
         _dlg.lblExcelHeaderFontName.setEnabled(true);

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
         _dlg.chkExcelAutoFilter.setEnabled(false);
         _dlg.chkExcelFirstRowFrozen.setEnabled(false);
         _dlg.chkExcelFirstRowCentered.setEnabled(false);
         _dlg.chkExcelFirstRowBold.setEnabled(false);
         _dlg.btnChooseExcelFont.setEnabled(false);
         _dlg.btnChooseExcelHeaderFont.setEnabled(false);
         _dlg.lblExcelFontName.setEnabled(false);
         _dlg.lblExcelHeaderFontName.setEnabled(false);


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

      String file = _cboFileHandler.getItem();
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
      
      _cboFileHandler.addOrReplaceCurrentItem(newFile);
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
         _cboCommandHandler.addOrReplaceCurrentItem(chooser.getSelectedFile().getPath() + " %file");
      }
   }


   private void onFile()
   {
      JFileChooser chooser = null;

      String csvFileName = _cboFileHandler.getItem();
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
         _cboFileHandler.addOrReplaceCurrentItem(chooser.getSelectedFile().getPath());
      }

   }


   private void onOK()
   {
      if(warnIfExcel() == false)
      {
         return;
      }
	   
      String singleExportFileName = _cboFileHandler.getItem();
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
         String command = _cboCommandHandler.getItem();
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
      prefs.setFile(_cboFileHandler.getItem());
      _cboFileHandler.saveCurrentItem();

      prefs.setEncoding(_dlg.cboCharsets.getSelectedItem().toString());
      prefs.setWithHeaders(_dlg.chkWithHeaders.isSelected());
      prefs.setFormatCSV(_dlg.radFormatCSV.isSelected());

      prefs.setFormatXLS(_dlg.radFormatXLSX.isSelected());
      prefs.setUseColoring(_dlg.chkUseColoring.isSelected());
      prefs.setFormatXLSOld(_dlg.radFormatXLS.isSelected());
      prefs.setExcelAutoFilter(_dlg.chkExcelAutoFilter.isSelected());
      prefs.setExcelFirstRowFrozen(_dlg.chkExcelFirstRowFrozen.isSelected());
      prefs.setExcelFirstRowCentered(_dlg.chkExcelFirstRowCentered.isSelected());
      prefs.setExcelFirstRowBold(_dlg.chkExcelFirstRowBold.isSelected());
      _excelFontCtrl.writeToPrefs(prefs);


      prefs.setFormatXML(_dlg.radFormatXML.isSelected());
      prefs.setFormatJSON(_dlg.radFormatJSON.isSelected());

      prefs.setSeperatorTab(_dlg.chkSeparatorTab.isSelected());

      prefs.setSeperatorChar(_dlg.txtSeparatorChar.getText());

      prefs.setLineSeperator(((LineSeparator)_dlg.cboLineSeparators.getSelectedItem()).name());

      _exportSelectionPanelController.writeControlsToPrefs(prefs);

      prefs.setUseGlobalPrefsFormating(_dlg.radUseGlobalPrefsFormating.isSelected());
      prefs.setRenderGroupingSeparator(_dlg.chkRenderGroupingSeparator.isSelected());

      prefs.setExecuteCommand(_dlg.chkExecCommand.isSelected());

      prefs.setCommand(_cboCommandHandler.getItem());
      _cboCommandHandler.saveCurrentItem();
   }

	
   private void initData()
   {
      TableExportPreferences prefs = TableExportPreferencesDAO.loadPreferences();

      if (formatIsNewXlsx(prefs))
      {
         _cboFileHandler.addOrReplaceCurrentItem(replaceXlsByXlsx(prefs.getFile()));
      }
      else
      {
         _cboFileHandler.addOrReplaceCurrentItem(prefs.getFile());
      }
      _dlg.chkUseColoring.setSelected(prefs.isUseColoring());
      _dlg.chkExcelAutoFilter.setSelected(prefs.isExcelAutoFilter());
      _dlg.chkExcelFirstRowFrozen.setSelected(prefs.isExcelFirstRowFrozen());
      _dlg.chkExcelFirstRowCentered.setSelected(prefs.isExcelFirstRowCentered());
      _dlg.chkExcelFirstRowBold.setSelected(prefs.isExcelFirstRowBold());

      _dlg.cboCharsets.setSelectedItem(prefs.getEncoding());
      _dlg.chkWithHeaders.setSelected(prefs.isWithHeaders());

      _excelFontCtrl.initDataAndLabels(prefs, _dlg.lblExcelFontName, _dlg.lblExcelHeaderFontName);

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

      _dlg.chkRenderGroupingSeparator.setEnabled(_dlg.radUseGlobalPrefsFormating.isSelected());
      _dlg.chkRenderGroupingSeparator.setSelected(prefs.isRenderGroupingSeparator());

      _dlg.chkExecCommand.setSelected(prefs.isExecuteCommand());
      onChkExecCommand();

      _cboCommandHandler.addOrReplaceCurrentItem(prefs.getCommand());


      LineSeparator preferredLineSeparator = LineSeparator.valueOf(prefs.getLineSeperator());

      _dlg.cboLineSeparators.setSelectedItem(preferredLineSeparator);
   }

   private boolean formatIsNewXlsx(TableExportPreferences preferences)
   {
      return preferences.isFormatXLS();  // instead of preferences.isFormatXLSOld();
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
      _dlg.cboCommand.setEnabled(_dlg.chkExecCommand.isSelected());
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
      return new File(_cboFileHandler.getItem());
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
         return StringUtils.replace(_cboCommandHandler.getItem(), "%file", firstExportedFile.getAbsolutePath());
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
