package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShowDistinctValuesCtrl
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShowDistinctValuesCtrl.class);
   private static final String PREF_DISTINCT_IN_COLUMN = "ShowDistinctValuesCtrl.optDistinctInColumn";
   private static final String PREF_DISTINCT_IN_SELECTION = "ShowDistinctValuesCtrl.optDistinctInSelection";
   private static final String PREF_DISTINCT_IN_TABLE = "ShowDistinctValuesCtrl.optDistinctInTable";

   private static final String PREF_DISTINCT_IN_ROWS = "ShowDistinctValuesCtrl.optDistinctInRows";

   private ShowDistinctValuesDlg _dlg;

   private javax.swing.Timer _distinctTableUpdateTrigger;

   public ShowDistinctValuesCtrl(JFrame owningFrame, DataSetViewerTable sourceTable, ISession session)
   {
      if(0 == sourceTable.getDataSetViewerTableModel().getRowCount())
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ShowDistinctValuesCtrl.emptyTable"));
         return;
      }

      int selectedColumnIx = sourceTable.getSelectedColumn();
      if(-1 == selectedColumnIx)
      {
         selectedColumnIx = 0;
      }

      TableColumn selectedColumn = sourceTable.getColumnModel().getColumn(selectedColumnIx);

      if (false == selectedColumn instanceof ExtTableColumn)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ShowDistinctValuesCtrl.unableToShowDistinctValuesForColumn", selectedColumn.getHeaderValue()));
         return;
      }

      _dlg = new ShowDistinctValuesDlg(owningFrame, ((ExtTableColumn) selectedColumn).getColumnDisplayDefinition().getColumnName());

      _dlg.optDistinctInColumn.setSelected(Props.getBoolean(PREF_DISTINCT_IN_COLUMN, true));
      _dlg.optDistinctInSelection.setSelected(Props.getBoolean(PREF_DISTINCT_IN_SELECTION, false));
      _dlg.optDistinctInTable.setSelected(Props.getBoolean(PREF_DISTINCT_IN_TABLE, false));


      _dlg.optDistinctInRows.setSelected(Props.getBoolean(PREF_DISTINCT_IN_ROWS, false));
      _dlg.optDistinctInColumns.setSelected(false == Props.getBoolean(PREF_DISTINCT_IN_ROWS, false));

      onDistinctInColumnOrSelectionChanged();

      _distinctTableUpdateTrigger = new Timer(300, e -> updateTable(sourceTable, (ExtTableColumn)selectedColumn, session));
      _distinctTableUpdateTrigger.setRepeats(false);
      triggerTableUpdate();

      _dlg.optDistinctInColumn.addActionListener(e -> onDistinctInColumnOrSelectionChanged());
      _dlg.optDistinctInSelection.addActionListener(e -> onDistinctInColumnOrSelectionChanged());
      _dlg.optDistinctInTable.addActionListener(e -> onDistinctInColumnOrSelectionChanged());

      _dlg.optDistinctInColumn.addActionListener(e -> triggerTableUpdate());
      _dlg.optDistinctInSelection.addActionListener(e -> triggerTableUpdate());
      _dlg.optDistinctInTable.addActionListener(e -> triggerTableUpdate());

      _dlg.optDistinctInRows.addActionListener(e -> triggerTableUpdate());
      _dlg.optDistinctInColumns.addActionListener(e -> triggerTableUpdate());

      GUIUtils.initLocation(_dlg, 500, 500);
      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.setVisible(true);
   }


   private void triggerTableUpdate()
   {
      Props.putBoolean(PREF_DISTINCT_IN_COLUMN,_dlg.optDistinctInColumn.isSelected());
      Props.putBoolean(PREF_DISTINCT_IN_SELECTION,_dlg.optDistinctInSelection.isSelected());
      Props.putBoolean(PREF_DISTINCT_IN_TABLE,_dlg.optDistinctInTable.isSelected());

      Props.putBoolean(PREF_DISTINCT_IN_ROWS, _dlg.optDistinctInRows.isSelected());

      _distinctTableUpdateTrigger.restart();
   }

   private void updateTable(DataSetViewerTable sourceTable, ExtTableColumn selectedColumn, ISession session)
   {
      _dlg.btnStatusBarInfoToolTip.setInfoText(s_stringMgr.getString("ShowDistinctValuesCtrl.noInformation"));
      _dlg.lblStatus.setText(null);

      if(_dlg.optDistinctInColumn.isSelected())
      {
         DistinctValuesHolder distinctValuesHolder = new DistinctValuesHolder();
         for (int i=0; i < sourceTable.getDataSetViewerTableModel().getRowCount(); ++i)
         {
            distinctValuesHolder.addDistinct(0, sourceTable.getValueAt(i, selectedColumn.getModelIndex()));
         }

         final List<Object[]> distinctRows = distinctValuesHolder.getDistinctRows();
         DataSetViewerTablePanel dataSetViewerTablePanel = DataSetViewerTablePanelUtil.createDataSetViewerTablePanel(distinctRows, Collections.singletonList(selectedColumn.getColumnDisplayDefinition()), session);
         _dlg.distinctTableScrollPane.setViewportView(dataSetViewerTablePanel.getComponent());

         _dlg.lblStatus.setText(s_stringMgr.getString("ShowDistinctValuesCtrl.numberOfDistinctValuesInColumn", selectedColumn.getColumnDisplayDefinition().getColumnName(), distinctRows.size()));
      }
      else if(_dlg.optDistinctInSelection.isSelected() )
      {
         // TODO if _dlg.optDistinctInColumns.isSelected()

         int[] selRows = sourceTable.getSelectedRows();
         int[] selCols = sourceTable.getSelectedColumns();

         ArrayList<ExtTableColumn> extTableColumns = new ArrayList<>();

         for (int colIdx : selCols)
         {
            TableColumn col = sourceTable.getColumnModel().getColumn(colIdx);

            if (col instanceof ExtTableColumn)
            {
               extTableColumns.add((ExtTableColumn) col);
            }
         }

         DistinctValuesHolder distinctValuesHolder = new DistinctValuesHolder();
         for (int selRowIx : selRows)
         {
            for (int i = 0; i < extTableColumns.size(); i++)
            {
               distinctValuesHolder.addDistinct(i, sourceTable.getValueAt(selRowIx, extTableColumns.get(i).getModelIndex()));
            }
         }

         List<ColumnDisplayDefinition> columnDisplayDefinitions = extTableColumns.stream().map(c -> c.getColumnDisplayDefinition()).collect(Collectors.toList());
         DataSetViewerTablePanel dataSetViewerTablePanel = DataSetViewerTablePanelUtil.createDataSetViewerTablePanel(distinctValuesHolder.getDistinctRows(), columnDisplayDefinitions, session);
         _dlg.distinctTableScrollPane.setViewportView(dataSetViewerTablePanel.getComponent());

         String statusBarText = null;

         for (int i = 0; i < extTableColumns.size(); i++)
         {
            ExtTableColumn extTableColumn = extTableColumns.get(i);

            String distinctValuesString = s_stringMgr.getString("ShowDistinctValuesCtrl.numberOfDistinctValuesInColumn", extTableColumn.getColumnDisplayDefinition().getColumnName(), distinctValuesHolder.getCountDistinctForColumn(i));
            if(null == statusBarText)
            {
               statusBarText = distinctValuesString;
            }
            else
            {
               statusBarText += "; " + distinctValuesString;
            }
         }

         _dlg.lblStatus.setText(statusBarText);
         _dlg.lblStatus.setCaretPosition(0);


         if(distinctValuesHolder.isNullsAdded())
         {
            _dlg.btnStatusBarInfoToolTip.setInfoText(s_stringMgr.getString("ShowDistinctValuesCtrl.information.nullsAdded"));
         }
      }
      else if(_dlg.optDistinctInTable.isSelected() )
      {
         ArrayList<ExtTableColumn> extTableColumns = new ArrayList<>();

         for (int colIdx = 0;  colIdx < sourceTable.getColumnCount(); ++colIdx)
         {
            TableColumn col = sourceTable.getColumnModel().getColumn(colIdx);

            if (col instanceof ExtTableColumn)
            {
               extTableColumns.add((ExtTableColumn) col);
            }
         }

         DistinctValuesHolder distinctValuesHolder = new DistinctValuesHolder();
         for (int rowIx = 0; rowIx < sourceTable.getRowCount(); ++rowIx)
         {
            for (int i = 0; i < extTableColumns.size(); i++)
            {
               distinctValuesHolder.addDistinct(i, sourceTable.getValueAt(rowIx, extTableColumns.get(i).getModelIndex()));
            }
         }

         List<ColumnDisplayDefinition> columnDisplayDefinitions = extTableColumns.stream().map(c -> c.getColumnDisplayDefinition()).collect(Collectors.toList());
         DataSetViewerTablePanel dataSetViewerTablePanel = DataSetViewerTablePanelUtil.createDataSetViewerTablePanel(distinctValuesHolder.getDistinctRows(), columnDisplayDefinitions, session);
         _dlg.distinctTableScrollPane.setViewportView(dataSetViewerTablePanel.getComponent());

         String statusBarText = null;

         for (int i = 0; i < extTableColumns.size(); i++)
         {
            ExtTableColumn extTableColumn = extTableColumns.get(i);

            String distinctValuesString = s_stringMgr.getString("ShowDistinctValuesCtrl.numberOfDistinctValuesInColumn", extTableColumn.getColumnDisplayDefinition().getColumnName(), distinctValuesHolder.getCountDistinctForColumn(i));
            if(null == statusBarText)
            {
               statusBarText = distinctValuesString;
            }
            else
            {
               statusBarText += "; " + distinctValuesString;
            }
         }

         _dlg.lblStatus.setText(statusBarText);
         _dlg.lblStatus.setCaretPosition(0);


         if(distinctValuesHolder.isNullsAdded())
         {
            _dlg.btnStatusBarInfoToolTip.setInfoText(s_stringMgr.getString("ShowDistinctValuesCtrl.information.nullsAdded"));
         }
      }
   }

   private void onDistinctInColumnOrSelectionChanged()
   {
      if(_dlg.optDistinctInColumn.isSelected())
      {
         _dlg.optDistinctInColumns.setSelected(true);
         _dlg.optDistinctInColumns.setEnabled(false);
         _dlg.optDistinctInRows.setEnabled(false);
      }
      else
      {
         _dlg.optDistinctInColumns.setEnabled(true);
         _dlg.optDistinctInRows.setEnabled(true);
      }
   }
}
