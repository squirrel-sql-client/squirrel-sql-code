package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.gui.table.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DistinctValuesTableUpdater
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DistinctValuesTableUpdater.class);

   public final static ILogger s_log = LoggerController.createLogger(DistinctValuesTableUpdater.class);


   private ShowDistinctValuesDlg _dlg;

   public DistinctValuesTableUpdater(ShowDistinctValuesDlg dlg)
   {
      _dlg = dlg;
   }

   void updateTable(DataSetViewerTable sourceTable, ExtTableColumn selectedColumn)
   {
      _dlg.btnStatusBarInfoToolTip.setInfoText(s_stringMgr.getString("ShowDistinctValuesCtrl.noInformation"));
      _dlg.lblStatus.setText(null);

      if(_dlg.optDistinctInColumn.isSelected())
      {
         long begMillis = System.currentTimeMillis();

         DistinctValuesHolder distinctValuesHolder = new DistinctValuesHolder();
         for (int i=0; i < sourceTable.getDataSetViewerTableModel().getRowCount(); ++i)
         {
            distinctValuesHolder.addDistinct(0, sourceTable.getValueAt(i, getViewColumnIndex(sourceTable, selectedColumn)));
         }

         final List<Object[]> distinctRows = distinctValuesHolder.getDistinctRows();
         fillTablePanel(distinctRows, distinctValuesHolder.maybeAppendCountColumn(Collections.singletonList(selectedColumn.getColumnDisplayDefinition())));

         long distinctValuesTime = System.currentTimeMillis() - begMillis;
         s_log.info("Building distinct values took " + distinctValuesTime + " Milli seconds");

         _dlg.lblStatus.setText(s_stringMgr.getString("ShowDistinctValuesCtrl.numberOfDistinctValuesInColumn", selectedColumn.getColumnDisplayDefinition().getColumnName(), distinctRows.size()));
      }
      else if(_dlg.optDistinctInSelection.isSelected() )
      {
         int[] selRows = sourceTable.getSelectedRows();
         ArrayList<ExtTableColumn> extTableColumns = getSelectedExtTableColumns(sourceTable);

         if (_dlg.optDistinctInColumns.isSelected())
         {
            long begMillis = System.currentTimeMillis();
            DistinctValuesHolder distinctValuesHolder = new DistinctValuesHolder();
            for (int selRowIx : selRows)
            {
               for (int i = 0; i < extTableColumns.size(); i++)
               {
                  distinctValuesHolder.addDistinct(i, sourceTable.getValueAt(selRowIx, getViewColumnIndex(sourceTable, extTableColumns.get(i))));
               }
            }

            List<ColumnDisplayDefinition> columnDisplayDefinitions = extTableColumns.stream().map(c -> c.getColumnDisplayDefinition()).collect(Collectors.toList());
            fillTablePanel(distinctValuesHolder.getDistinctRows(), distinctValuesHolder.maybeAppendCountColumn(columnDisplayDefinitions));

            long distinctValuesTime = System.currentTimeMillis() - begMillis;
            s_log.info("Building distinct values took " + distinctValuesTime + " Milli seconds");

            writeDistinctInColumnsStatusBar(extTableColumns, distinctValuesHolder);
         }
         else
         {
            doDistinctRows(sourceTable, sourceTable.getSelectedModelRows(), extTableColumns);
         }
      }
      else if(_dlg.optDistinctInSelectedRows.isSelected() )
      {
         int[] selRows = sourceTable.getSelectedRows();
         ArrayList<ExtTableColumn> extTableColumns = getAllExtTableColumns(sourceTable);

         if (_dlg.optDistinctInColumns.isSelected())
         {
            long begMillis = System.currentTimeMillis();
            DistinctValuesHolder distinctValuesHolder = new DistinctValuesHolder();
            for (int selRowIx : selRows)
            {
               for (int i = 0; i < extTableColumns.size(); i++)
               {
                  distinctValuesHolder.addDistinct(i, sourceTable.getValueAt(selRowIx, getViewColumnIndex(sourceTable, extTableColumns.get(i))));
               }
            }

            List<ColumnDisplayDefinition> columnDisplayDefinitions = extTableColumns.stream().map(c -> c.getColumnDisplayDefinition()).collect(Collectors.toList());
            fillTablePanel(distinctValuesHolder.getDistinctRows(), distinctValuesHolder.maybeAppendCountColumn(columnDisplayDefinitions));

            long distinctValuesTime = System.currentTimeMillis() - begMillis;
            s_log.info("Building distinct values took " + distinctValuesTime + " Milli seconds");

            writeDistinctInColumnsStatusBar(extTableColumns, distinctValuesHolder);
         }
         else
         {
            doDistinctRows(sourceTable, sourceTable.getSelectedModelRows(), extTableColumns);
         }
      }
      else if(_dlg.optDistinctInTable.isSelected() )
      {
         ArrayList<ExtTableColumn> extTableColumns = getAllExtTableColumns(sourceTable);

         if (_dlg.optDistinctInColumns.isSelected())
         {
            long begMillis = System.currentTimeMillis();
            DistinctValuesHolder distinctValuesHolder = new DistinctValuesHolder();
            for (int rowIx = 0; rowIx < sourceTable.getRowCount(); ++rowIx)
            {
               for (int i = 0; i < extTableColumns.size(); i++)
               {
                  distinctValuesHolder.addDistinct(i, sourceTable.getValueAt(rowIx, getViewColumnIndex(sourceTable, extTableColumns.get(i))));
               }
            }

            List<ColumnDisplayDefinition> columnDisplayDefinitions = extTableColumns.stream().map(c -> c.getColumnDisplayDefinition()).collect(Collectors.toList());
            fillTablePanel(distinctValuesHolder.getDistinctRows(), distinctValuesHolder.maybeAppendCountColumn(columnDisplayDefinitions));

            long distinctValuesTime = System.currentTimeMillis() - begMillis;
            s_log.info("Building distinct values took " + distinctValuesTime + " Milli seconds");

            writeDistinctInColumnsStatusBar(extTableColumns, distinctValuesHolder);
         }
         else
         {
            doDistinctRows(sourceTable, null, extTableColumns);
         }
      }
   }

   private void fillTablePanel(List<Object[]> distinctRows, List<ColumnDisplayDefinition> columnDisplayDefinitions)
   {
      DataSetViewerTablePanel dataSetViewerTablePanel = DataSetViewerTablePanelUtil.createDataSetViewerTablePanel(distinctRows, columnDisplayDefinitions);
      _dlg.distinctTableScrollPane.setViewportView(dataSetViewerTablePanel.getComponent());
   }

   private int getViewColumnIndex(DataSetViewerTable sourceTable, ExtTableColumn column)
   {
      return sourceTable.getButtonTableHeader().getViewColumnIndex(column.getModelIndex());
   }

   private void doDistinctRows(DataSetViewerTable sourceTable, int[] selRows, ArrayList<ExtTableColumn> extTableColumns)
   {
      long begMillis = System.currentTimeMillis();
      DistinctRowsHolder distinctRowsHolder = new DistinctRowsHolder(extTableColumns);

      if (null != selRows)
      {
         for (int selRowIx : selRows)
         {
            distinctRowsHolder.addRowDistinct(sourceTable.getDataSetViewerTableModel().getRowAt(selRowIx));
         }
      }
      else
      {
         for (int viewRowIx =0; viewRowIx < sourceTable.getRowCount(); ++viewRowIx)
         {
            int modelRowIx = ((SortableTableModel) sourceTable.getModel()).transformToModelRow(viewRowIx);
            distinctRowsHolder.addRowDistinct(sourceTable.getDataSetViewerTableModel().getRowAt(modelRowIx));
         }
      }

      List<ColumnDisplayDefinition> columnDisplayDefinitions = extTableColumns.stream().map(c -> c.getColumnDisplayDefinition()).collect(Collectors.toList());
      columnDisplayDefinitions.add(DistinctValuesUtil.createFrequencyColumn());

      fillTablePanel(distinctRowsHolder.getDistinctRows(), columnDisplayDefinitions);

      writeDistinctInRowStatusBar(distinctRowsHolder);

      long distinctRowsTime = System.currentTimeMillis() - begMillis;
      s_log.info("Building distinct rows took " + distinctRowsTime + " Milli seconds");
   }

   private void writeDistinctInRowStatusBar(DistinctRowsHolder distinctRowsHolder)
   {
      String statusBarText = s_stringMgr.getString("ShowDistinctValuesCtrl.numberOfDistinctRows", distinctRowsHolder.getDistinctRowsCount());
      _dlg.lblStatus.setText(statusBarText);
      _dlg.lblStatus.setCaretPosition(0);
   }

   private void writeDistinctInColumnsStatusBar(ArrayList<ExtTableColumn> extTableColumns, DistinctValuesHolder distinctValuesHolder)
   {
      String statusBarText = null;

      for (int i = 0; i < extTableColumns.size(); i++)
      {
         ExtTableColumn extTableColumn = extTableColumns.get(i);

         String distinctValuesString = s_stringMgr.getString("ShowDistinctValuesCtrl.numberOfDistinctValuesInColumn", extTableColumn.getColumnDisplayDefinition().getColumnName(), distinctValuesHolder.getCountDistinctForColumn(i));
         if (null == statusBarText)
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


      if (distinctValuesHolder.isNullsAdded())
      {
         _dlg.btnStatusBarInfoToolTip.setInfoText(s_stringMgr.getString("ShowDistinctValuesCtrl.information.nullsAdded"));
      }
   }

   private ArrayList<ExtTableColumn> getSelectedExtTableColumns(DataSetViewerTable sourceTable)
   {
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
      return extTableColumns;
   }

   private ArrayList<ExtTableColumn> getAllExtTableColumns(DataSetViewerTable sourceTable)
   {
      ArrayList<ExtTableColumn> extTableColumns = new ArrayList<>();

      for (int colIdx = 0; colIdx < sourceTable.getColumnCount(); ++colIdx)
      {
         TableColumn col = sourceTable.getColumnModel().getColumn(colIdx);

         if (col instanceof ExtTableColumn)
         {
            extTableColumns.add((ExtTableColumn) col);
         }
      }
      return extTableColumns;
   }
}
