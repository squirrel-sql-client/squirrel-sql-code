package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableExportSelectionPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableExportSelectionPanelController.class);
   private final ExportSourceAccess _exportSourceAccess;
   private final ExportDialogType _exportDialogType;


   private TableExportSelectionPanel _pnl;
   private MultipleSqlResultExportDestinationInfo _currentExportDestinationInfo;

   public TableExportSelectionPanelController(ExportSourceAccess exportSourceAccess, ExportDialogType exportDialogType)
   {
      _exportSourceAccess = exportSourceAccess;
      _exportDialogType = exportDialogType;
      _pnl = new TableExportSelectionPanel(exportDialogType);

      _pnl.radCompleteTableOrSingleFile.addActionListener(e -> updateUI());

      if(_exportDialogType == ExportDialogType.RESULT_SET_EXPORT)
      {
         _pnl.chkLimitRows.addActionListener(e -> updateUI());
      }
      else if(_exportDialogType == ExportDialogType.UI_TABLE_EXPORT)
      {
         _pnl.radSelectionInUiTable.addActionListener(e -> updateUI());
      }
      else
      {
         throw new IllegalStateException("Unknown ExportDialogType: " + _exportDialogType);
      }


      _pnl.radMultipleSQLRes.addActionListener(e -> updateUI());

      _pnl.btnUp.addActionListener(e -> onUp());
      _pnl.btnDown.addActionListener(e -> onDown());

      _pnl.btnEdit.addActionListener(e -> onEdit());
      _pnl.btnDelete.addActionListener(e -> onDelete());

      _pnl.btnSaveNames.addActionListener(e -> onSaveNames());
      _pnl.btnApplySavedNames.addActionListener(e -> onApplySavedNames());

      _pnl.lstSQLResultsToExport.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListClicked(e);
         }
      });
   }

   private void onApplySavedNames()
   {
      final List<String> savedNames = ExcelTabOrFileNamesDao.getSavedNames();

      if(savedNames.isEmpty())
      {
         JOptionPane.showConfirmDialog(GUIUtils.getOwningWindow(_pnl), s_stringMgr.getString("TableExportSelectionPanelController.no.names.saved"));
         return;
      }

      JPopupMenu menu = new JPopupMenu();

      for (String savedName : savedNames)
      {
         final JMenuItem menuItem = new JMenuItem(savedName);
         menu.add(menuItem);
         menuItem.addActionListener(e -> onNamesSelected(savedName));
      }

      menu.show(_pnl.btnApplySavedNames, 0,_pnl.btnApplySavedNames.getHeight());
   }

   private void onNamesSelected(String savedName)
   {
      List<String> excelTabOrFileNames = ExcelTabOrFileNamesDao.getExcelTabOrFileNames(savedName);

      for (int i = 0; i < Math.min(excelTabOrFileNames.size(), getListModel().size() ) ; i++)
      {
         getListModel().getElementAt(i).setUserEnteredSqlResultNameFileNormalized(excelTabOrFileNames.get(i));
      }

      _pnl.lstSQLResultsToExport.repaint();
   }

   private void onSaveNames()
   {
      if(getListModel().isEmpty())
      {
         return;
      }

      List<String> excelTabOrFileNames =
            GUIUtils.getListItems(getListModel()).stream().map(i -> i.getExportNameFileNormalized()).collect(Collectors.toList());

      ExcelTabOrFileNamesDao.addOrReplaceSavedName(String.join("/", excelTabOrFileNames), excelTabOrFileNames);

   }

   private DefaultListModel<SqlResultListEntry> getListModel()
   {
      return (DefaultListModel<SqlResultListEntry>) _pnl.lstSQLResultsToExport.getModel();
   }

   private void onUp()
   {
      final int selectedIndex = _pnl.lstSQLResultsToExport.getSelectedIndex();

      if(selectedIndex <= 0)
      {
         return;
      }

      final SqlResultListEntry entry = getListModel().remove(selectedIndex);
      final int newIndex = selectedIndex - 1;
      getListModel().add(newIndex, entry);
      _pnl.lstSQLResultsToExport.setSelectedIndex(newIndex);
   }

   private void onDown()
   {
      final int selectedIndex = _pnl.lstSQLResultsToExport.getSelectedIndex();

      final DefaultListModel<SqlResultListEntry> model = getListModel();

      if(selectedIndex < 0 || selectedIndex == model.size() - 1)
      {
         return;
      }

      final SqlResultListEntry entry = model.remove(selectedIndex);
      final int newIndex = selectedIndex + 1;
      model.add(newIndex, entry);
      _pnl.lstSQLResultsToExport.setSelectedIndex(newIndex);
   }


   private void onEdit()
   {
      final SqlResultListEntry selectedValue = _pnl.lstSQLResultsToExport.getSelectedValue();

      if(null == selectedValue)
      {
         final String msg = s_stringMgr.getString("TableExportSelectionPanelController.no.sql.result.selected");
         Main.getApplication().getMessageHandler().showWarningMessage(msg);
         return;
      }

      GUIUtils.getOwningWindow(_pnl);

      final EditExcelTabOrFileNameCtrl ctrl =
            new EditExcelTabOrFileNameCtrl(GUIUtils.getOwningWindow(_pnl), selectedValue.toString());

      if(ctrl.isOk())
      {
         selectedValue.setUserEnteredSqlResultNameFileNormalized(ctrl.getNewSqlResultNameFileNormalized());
      }

      _pnl.lstSQLResultsToExport.repaint();
   }

   private void onDelete()
   {
      final int selectedIndex = _pnl.lstSQLResultsToExport.getSelectedIndex();

      if(-1 == selectedIndex)
      {
         final String msg = s_stringMgr.getString("TableExportSelectionPanelController.no.sql.result.selected");
         Main.getApplication().getMessageHandler().showWarningMessage(msg);
         return;
      }
      ((DefaultListModel) _pnl.lstSQLResultsToExport.getModel()).remove(selectedIndex);

      if(0 == _pnl.lstSQLResultsToExport.getModel().getSize())
      {
         return;
      }


      if(selectedIndex < _pnl.lstSQLResultsToExport.getModel().getSize())
      {
         _pnl.lstSQLResultsToExport.setSelectedIndex(selectedIndex);
      }
      else
      {
         _pnl.lstSQLResultsToExport.setSelectedIndex(_pnl.lstSQLResultsToExport.getModel().getSize() - 1);
      }

   }


   private void onListClicked(MouseEvent e)
   {
      if( 1 != e.getButton())
      {
         return;
      }

      final SqlResultListEntry selEntry = _pnl.lstSQLResultsToExport.getSelectedValue();

      if(null == selEntry)
      {
         return;
      }

      if(false == selEntry.getHandle().isResultsVisible())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("TableExportSelectionPanelController.warn.result.tab.not.visible"));
         return;
      }


      if(1 == e.getClickCount())
      {

         if(null == selEntry)
         {
            return;
         }
         selEntry.getHandle().indicateTabComponent();
      }
      else if(2 == e.getClickCount())
      {

         if(null == selEntry)
         {
            return;
         }
         selEntry.getHandle().selectResultTab();
         selEntry.getHandle().indicateTabComponent();

      }
   }

   public void initPanel(TableExportPreferences prefs)
   {
      boolean exportCompleteTableOrSingleFile = prefs.isExportCompleteTableOrSingleFile();
      boolean exportMultipleSQLResults = prefs.isExportMultipleSQLResults();

      if(_exportDialogType == ExportDialogType.RESULT_SET_EXPORT)
      {
         if(1 < _exportSourceAccess.getOriginalSqlsToExport().size())
         {
            exportMultipleSQLResults = true;
            exportCompleteTableOrSingleFile = false;
         }
         else if( false == (prefs.isFormatXLS() || prefs.isFormatXLSOld()) )
         {
            exportMultipleSQLResults = false;
            exportCompleteTableOrSingleFile = true;
         }
      }
      else if(_exportDialogType == ExportDialogType.UI_TABLE_EXPORT)
      {
         if(   exportMultipleSQLResults
            && Main.getApplication().getMultipleSqlResultExportChannel().getSqlResultTabHandles().isEmpty())
         {
            exportMultipleSQLResults = false;
            exportCompleteTableOrSingleFile = true;
         }
      }
      else
      {
         throw new IllegalStateException("Unknown ExportDialogType: " + _exportDialogType);
      }


      if(exportMultipleSQLResults)
      {
         _pnl.radMultipleSQLRes.setSelected(true);
      }
      else if(exportCompleteTableOrSingleFile)
      {
         _pnl.radCompleteTableOrSingleFile.setSelected(true);
      }
      else
      {
         if(_exportDialogType == ExportDialogType.UI_TABLE_EXPORT)
         {
            _pnl.radSelectionInUiTable.setSelected(true);
         }
      }

      if(_exportDialogType == ExportDialogType.RESULT_SET_EXPORT)
      {
         _pnl.chkLimitRows.setSelected(prefs.isLimitRowsChecked());
         _pnl.txtLimitRows.setText(prefs.getRowsLimit());
      }

      updateUI();
   }

   public void writeControlsToPrefs(TableExportPreferences prefs)
   {
      if(_pnl.radMultipleSQLRes.isSelected())
      {
         prefs.setExportMultipleSQLResults(true);
         prefs.setExportComplete(false);
      }
      else if(_pnl.radCompleteTableOrSingleFile.isSelected())
      {
         prefs.setExportMultipleSQLResults(false);
         prefs.setExportComplete(true);
      }
      else
      {
         prefs.setExportMultipleSQLResults(false);
         prefs.setExportComplete(false);
      }

      if(_exportDialogType == ExportDialogType.RESULT_SET_EXPORT)
      {
         prefs.setLimitRowsChecked(_pnl.chkLimitRows.isSelected());
         prefs.setRowsLimit(_pnl.txtLimitRows.getText());
      }
   }

   private void updateUI()
   {
      _pnl.lstSQLResultsToExport.setEnabled(_pnl.radMultipleSQLRes.isSelected());
      _pnl.btnUp.setEnabled(_pnl.radMultipleSQLRes.isSelected());
      _pnl.btnDown.setEnabled(_pnl.radMultipleSQLRes.isSelected());
      _pnl.btnEdit.setEnabled(_pnl.radMultipleSQLRes.isSelected());
      _pnl.btnDelete.setEnabled(_pnl.radMultipleSQLRes.isSelected());
      _pnl.btnSaveNames.setEnabled(_pnl.radMultipleSQLRes.isSelected());
      _pnl.btnApplySavedNames.setEnabled(_pnl.radMultipleSQLRes.isSelected());
      _pnl.btnInfo.setEnabled(_pnl.radMultipleSQLRes.isSelected());

      _pnl.txtExportFileOrDir.setEnabled(_pnl.radMultipleSQLRes.isSelected());

      if(_exportDialogType == ExportDialogType.RESULT_SET_EXPORT)
      {
         _pnl.txtLimitRows.setEnabled(_pnl.chkLimitRows.isSelected());
      }

      if(_pnl.radMultipleSQLRes.isSelected())
      {
         final List<SqlResultListEntry> listEntries;

         int[] indexCounter = new int[1];
         if(_exportDialogType == ExportDialogType.UI_TABLE_EXPORT)
         {
            List<SqlResultTabHandle> sqlResultTabHandel = Main.getApplication().getMultipleSqlResultExportChannel().getSqlResultTabHandles();
            listEntries = sqlResultTabHandel.stream().map(h -> new SqlResultListEntry(h, ++indexCounter[0])).collect(Collectors.toList());
         }
         else
         {
            listEntries = _exportSourceAccess.getOriginalSqlsToExport().stream().map(sql -> new SqlResultListEntry(sql, ++indexCounter[0])).collect(Collectors.toList());
         }

         final SqlResultListEntry selEntry = _pnl.lstSQLResultsToExport.getSelectedValue();

         DefaultListModel<SqlResultListEntry> defaultListModel = new DefaultListModel<>();
         defaultListModel.addAll(listEntries);
         _pnl.lstSQLResultsToExport.setModel(defaultListModel);

         if(null != selEntry)
         {
            final Optional<SqlResultListEntry> match = listEntries.stream().filter(e -> e.toString().equals(selEntry.toString())).findFirst();

            if(match.isPresent())
            {
               _pnl.lstSQLResultsToExport.setSelectedValue(selEntry, true);
            }
         }
         else if(false == listEntries.isEmpty())
         {
            _pnl.lstSQLResultsToExport.setSelectedIndex(0);
         }

      }
      else
      {
         _pnl.lstSQLResultsToExport.setModel(new DefaultListModel<>());
      }
   }

   public void updateExportDestinationInfo(String exportFileNameText, boolean destinationIsExcel)
   {
      _pnl.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.location.unspecified"));
      _currentExportDestinationInfo = null;

      if(StringUtilities.isEmpty(exportFileNameText, true))
      {
         return;
      }

      final File file = new File(exportFileNameText);

      if(file.isDirectory())
      {
         if(false == destinationIsExcel)
         {
            _pnl.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.dir", file.getAbsolutePath()));
            _currentExportDestinationInfo = MultipleSqlResultExportDestinationInfo.createExportDir(file);
         }
      }
      else
      {
         if(false == destinationIsExcel )
         {
            if(null != file.getParentFile())
            {
               _pnl.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.dir", file.getParentFile().getAbsolutePath()));
               _currentExportDestinationInfo = MultipleSqlResultExportDestinationInfo.createExportDir(file.getParentFile());
            }
         }
         else
         {
            _pnl.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.file", file.getAbsolutePath()));
            _currentExportDestinationInfo = MultipleSqlResultExportDestinationInfo.createExcelExportFile(file);
         }
      }
   }

   public JPanel getPanel()
   {
      return _pnl;
   }

   public ExportSourceAccess getExportSourceAccess()
   {
      switch (_exportDialogType)
      {
         case UI_TABLE_EXPORT:
            _exportSourceAccess.prepareSqlResultDataSetViewersExport(getSqlResultDataSetViewersExportData(),
                                                                     _pnl.radSelectionInUiTable.isSelected(),
                                                                     _currentExportDestinationInfo,
                                                                     _pnl.radMultipleSQLRes.isSelected());
            return _exportSourceAccess;
         case RESULT_SET_EXPORT:
            _exportSourceAccess.prepareResultSetExport(getExportSqlsNamed(),
                                                       _pnl.radCompleteTableOrSingleFile.isSelected(),
                                                       _pnl.chkLimitRows.isSelected(),
                                                       _pnl.txtLimitRows.getInt(),
                                                       _currentExportDestinationInfo,
                                                       _pnl.radMultipleSQLRes.isSelected());
            return _exportSourceAccess;
         default:
            throw new IllegalStateException("Unknown ExportDialogType: " + _exportDialogType);
      }
   }

   private List<ExportSqlNamed> getExportSqlsNamed()
   {
      if(false == _pnl.radMultipleSQLRes.isSelected())
      {
         return Collections.emptyList();
      }

      ArrayList<ExportSqlNamed> ret = new ArrayList<>();

      for (int i = 0; i < getListModel().getSize(); i++)
      {
         final SqlResultListEntry listEntry = getListModel().getElementAt(i);
         ret.add(new ExportSqlNamed(listEntry.getSql(), listEntry.getExportNameFileNormalized()));
      }
      return ret;
   }

   private List<ExportDataInfo> getSqlResultDataSetViewersExportData()
   {
      if(false == _pnl.radMultipleSQLRes.isSelected())
      {
         return Collections.emptyList();
      }

      ArrayList<ExportDataInfo> ret = new ArrayList<>();

      for (int i = 0; i < getListModel().getSize(); i++)
      {
         final SqlResultListEntry listEntry = getListModel().getElementAt(i);
         final SqlResultTabHandle handle = listEntry.getHandle();
         final IDataSetViewer sqlResultDataSetViewer = handle.getSQLResultDataSetViewer();

         if(sqlResultDataSetViewer instanceof DataSetViewerTablePanel)
         {
            final DataSetViewerTable table = ((DataSetViewerTablePanel) sqlResultDataSetViewer).getTable();
            ret.add(new ExportDataInfo(new JTableExportData(table, true), listEntry.getExportNameFileNormalized()));
         }
      }

      return ret;
   }

}
