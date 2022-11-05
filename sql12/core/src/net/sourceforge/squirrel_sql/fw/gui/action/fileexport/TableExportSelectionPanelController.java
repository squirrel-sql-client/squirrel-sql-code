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
import javax.swing.JPanel;
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


   private TableExportSelectionPanel _exportSelectionPanel;
   private MultipleSqlResultExportDestinationInfo _currentExportDestinationInfo;

   public TableExportSelectionPanelController()
   {
      _exportSelectionPanel = new TableExportSelectionPanel();

      _exportSelectionPanel.radComplete.addActionListener(e -> updateUI());
      _exportSelectionPanel.radSelection.addActionListener(e -> updateUI());
      _exportSelectionPanel.radMultipleSQLRes.addActionListener(e -> updateUI());

      _exportSelectionPanel.btnUp.addActionListener(e -> onUp());
      _exportSelectionPanel.btnDown.addActionListener(e -> onDown());

      _exportSelectionPanel.btnEdit.addActionListener(e -> onEdit());
      _exportSelectionPanel.btnDelete.addActionListener(e -> onDelete());

      _exportSelectionPanel.lstSQLResultsToExport.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListClicked(e);
         }
      });
   }

   private void onUp()
   {
      final int selectedIndex = _exportSelectionPanel.lstSQLResultsToExport.getSelectedIndex();

      if(selectedIndex <= 0)
      {
         return;
      }

      final DefaultListModel<SqlResultListEntry> model = (DefaultListModel<SqlResultListEntry>) _exportSelectionPanel.lstSQLResultsToExport.getModel();

      final SqlResultListEntry entry = model.remove(selectedIndex);
      final int newIndex = selectedIndex - 1;
      model.add(newIndex, entry);
      _exportSelectionPanel.lstSQLResultsToExport.setSelectedIndex(newIndex);
   }

   private void onDown()
   {
      final int selectedIndex = _exportSelectionPanel.lstSQLResultsToExport.getSelectedIndex();

      final DefaultListModel<SqlResultListEntry> model = (DefaultListModel<SqlResultListEntry>) _exportSelectionPanel.lstSQLResultsToExport.getModel();

      if(selectedIndex < 0 || selectedIndex == model.size() - 1)
      {
         return;
      }

      final SqlResultListEntry entry = model.remove(selectedIndex);
      final int newIndex = selectedIndex + 1;
      model.add(newIndex, entry);
      _exportSelectionPanel.lstSQLResultsToExport.setSelectedIndex(newIndex);
   }


   private void onEdit()
   {
      final SqlResultListEntry selectedValue = _exportSelectionPanel.lstSQLResultsToExport.getSelectedValue();

      if(null == selectedValue)
      {
         final String msg = s_stringMgr.getString("TableExportSelectionPanelController.no.sql.result.selected");
         Main.getApplication().getMessageHandler().showWarningMessage(msg);
         return;
      }

      GUIUtils.getOwningWindow(_exportSelectionPanel);

      final EditExcelTabOrFileNameCtrl ctrl =
            new EditExcelTabOrFileNameCtrl(GUIUtils.getOwningWindow(_exportSelectionPanel), selectedValue.toString());

      if(ctrl.isOk())
      {
         selectedValue.setUserEnteredSqlResultNameFileNormalized(ctrl.getNewSqlResultNameFileNormalized());
      }

      _exportSelectionPanel.lstSQLResultsToExport.repaint();
   }

   private void onDelete()
   {
      final int selectedIndex = _exportSelectionPanel.lstSQLResultsToExport.getSelectedIndex();

      if(-1 == selectedIndex)
      {
         final String msg = s_stringMgr.getString("TableExportSelectionPanelController.no.sql.result.selected");
         Main.getApplication().getMessageHandler().showWarningMessage(msg);
         return;
      }
      ((DefaultListModel)_exportSelectionPanel.lstSQLResultsToExport.getModel()).remove(selectedIndex);

      if(0 == _exportSelectionPanel.lstSQLResultsToExport.getModel().getSize())
      {
         return;
      }


      if(selectedIndex < _exportSelectionPanel.lstSQLResultsToExport.getModel().getSize())
      {
         _exportSelectionPanel.lstSQLResultsToExport.setSelectedIndex(selectedIndex);
      }
      else
      {
         _exportSelectionPanel.lstSQLResultsToExport.setSelectedIndex(_exportSelectionPanel.lstSQLResultsToExport.getModel().getSize() - 1);
      }

   }


   private void onListClicked(MouseEvent e)
   {
      if( 1 != e.getButton())
      {
         return;
      }

      final SqlResultListEntry selEntry = _exportSelectionPanel.lstSQLResultsToExport.getSelectedValue();

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
      prefs.setExportMultipleSQLResults(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      prefs.setExportComplete(prefs.isExportComplete());
   }

   @Override
   public void initPanel(TableExportPreferences prefs)
   {
      if(prefs.isExportComplete())
      {
         _exportSelectionPanel.radComplete.setSelected(true);
      }
      else
      {
         _exportSelectionPanel.radSelection.setSelected(true);
      }

      if(prefs.isExportMultipleSQLResults())
      {
         _exportSelectionPanel.radMultipleSQLRes.setSelected(true);
      }

      updateUI();
   }

   private void updateUI()
   {
      _exportSelectionPanel.lstSQLResultsToExport.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnUp.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnDown.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnEdit.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnDelete.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnInfo.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());

      _exportSelectionPanel.txtExportFileOrDir.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());


      if(_exportSelectionPanel.radMultipleSQLRes.isSelected())
      {
         List<SqlResultTabHandle> sqlResultTabHandel = Main.getApplication().getMultipleSqlResultExportChannel().getSqlResultTabHandles();

         int[] indexCounter = new int[1];
         final List<SqlResultListEntry> listEntries = sqlResultTabHandel.stream().map(h -> new SqlResultListEntry(h, ++indexCounter[0])).collect(Collectors.toList());

         final SqlResultListEntry selEntry = _exportSelectionPanel.lstSQLResultsToExport.getSelectedValue();

         DefaultListModel<SqlResultListEntry> defaultListModel = new DefaultListModel<>();
         defaultListModel.addAll(listEntries);
         _exportSelectionPanel.lstSQLResultsToExport.setModel(defaultListModel);

         if(null != selEntry)
         {
            final Optional<SqlResultListEntry> match = listEntries.stream().filter(e -> e.toString().equals(selEntry.toString())).findFirst();

            if(match.isPresent())
            {
               _exportSelectionPanel.lstSQLResultsToExport.setSelectedValue(selEntry, true);
            }
         }
         else if(false == listEntries.isEmpty())
         {
            _exportSelectionPanel.lstSQLResultsToExport.setSelectedIndex(0);
         }

      }
      else
      {
         _exportSelectionPanel.lstSQLResultsToExport.setListData(new SqlResultListEntry[0]);
      }
   }

   @Override
   public boolean isExportComplete()
   {
      return _exportSelectionPanel.radComplete.isSelected();
   }

   @Override
   public void updateExportDestinationInfo(String exportFileNameText, boolean destinationIsExcel)
   {
      _exportSelectionPanel.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.location.unspecified"));
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
            _exportSelectionPanel.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.dir", file.getAbsolutePath()));
            _currentExportDestinationInfo = MultipleSqlResultExportDestinationInfo.createExportDir(file);
         }
      }
      else
      {
         if(false == destinationIsExcel )
         {
            if(null != file.getParentFile())
            {
               _exportSelectionPanel.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.dir", file.getParentFile().getAbsolutePath()));
               _currentExportDestinationInfo = MultipleSqlResultExportDestinationInfo.createExportDir(file.getParentFile());
            }
         }
         else
         {
            _exportSelectionPanel.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.file", file.getAbsolutePath()));
            _currentExportDestinationInfo = MultipleSqlResultExportDestinationInfo.createExcelExportFile(file);
         }
      }
   }

   @Override
   public JPanel getPanel()
   {
      return _exportSelectionPanel;
   }

   @Override
   public boolean isExportMultipleSqlResults()
   {
      return _exportSelectionPanel.radMultipleSQLRes.isSelected();
   }

   @Override
   public ExportDataInfoList getMultipleSqlResults()
   {
      if(false == _exportSelectionPanel.radMultipleSQLRes.isSelected())
      {
         return ExportDataInfoList.EMPTY;
      }

      final DefaultListModel<SqlResultListEntry> model = (DefaultListModel<SqlResultListEntry>) _exportSelectionPanel.lstSQLResultsToExport.getModel();

      ArrayList<ExportDataInfo> ret = new ArrayList<>();

      for (int i = 0; i < model.getSize(); i++)
      {
         final SqlResultListEntry listEntry = model.getElementAt(i);
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
