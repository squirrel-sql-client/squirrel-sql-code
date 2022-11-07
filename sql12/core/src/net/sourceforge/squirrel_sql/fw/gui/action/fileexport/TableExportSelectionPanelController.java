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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableExportSelectionPanelController implements ExportSelectionPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableExportSelectionPanelController.class);


   private TableExportSelectionPanel _pnl;
   private MultipleSqlResultExportDestinationInfo _currentExportDestinationInfo;

   public TableExportSelectionPanelController()
   {
      _pnl = new TableExportSelectionPanel();

      _pnl.radComplete.addActionListener(e -> updateUI());
      _pnl.radSelection.addActionListener(e -> updateUI());
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

   @Override
   public void writeControlsToPrefs(TableExportPreferences prefs)
   {
      prefs.setExportMultipleSQLResults(_pnl.radMultipleSQLRes.isSelected());
      prefs.setExportComplete(prefs.isExportComplete());
   }

   @Override
   public void initPanel(TableExportPreferences prefs)
   {
      if(prefs.isExportComplete())
      {
         _pnl.radComplete.setSelected(true);
      }
      else
      {
         _pnl.radSelection.setSelected(true);
      }

      if(prefs.isExportMultipleSQLResults())
      {
         _pnl.radMultipleSQLRes.setSelected(true);
      }

      updateUI();
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


      if(_pnl.radMultipleSQLRes.isSelected())
      {
         List<SqlResultTabHandle> sqlResultTabHandel = Main.getApplication().getMultipleSqlResultExportChannel().getSqlResultTabHandles();

         int[] indexCounter = new int[1];
         final List<SqlResultListEntry> listEntries = sqlResultTabHandel.stream().map(h -> new SqlResultListEntry(h, ++indexCounter[0])).collect(Collectors.toList());

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
         _pnl.lstSQLResultsToExport.setListData(new SqlResultListEntry[0]);
      }
   }

   @Override
   public boolean isExportComplete()
   {
      return _pnl.radComplete.isSelected();
   }

   @Override
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

   @Override
   public JPanel getPanel()
   {
      return _pnl;
   }

   @Override
   public boolean isExportMultipleSqlResults()
   {
      return _pnl.radMultipleSQLRes.isSelected();
   }

   @Override
   public ExportDataInfoList getMultipleSqlResults()
   {
      if(false == _pnl.radMultipleSQLRes.isSelected())
      {
         return ExportDataInfoList.EMPTY;
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

      return new ExportDataInfoList(ret, _currentExportDestinationInfo);
   }
}
