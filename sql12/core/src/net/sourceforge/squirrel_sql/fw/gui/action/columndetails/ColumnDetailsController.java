package net.sourceforge.squirrel_sql.fw.gui.action.columndetails;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableClickPosition;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.InStatColumnInfo;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopyInStatementCommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumnModel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnDetailsController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColumnDetailsController.class);
   private ColumnDetailsDialog _dlg;
   private DataSetViewerTablePanel _columnsDetailsPanel;

   public ColumnDetailsController(DataSetViewerTable table, ISession session, TableClickPosition tableClickPosition)
   {
      try
      {
         SimpleDataSet columnDetailsDataSet = createColumnDetailsDataSet(table, session, tableClickPosition);

         if(null == columnDetailsDataSet)
         {
            return;
         }

         _dlg = new ColumnDetailsDialog(GUIUtils.getOwningFrame(table), s_stringMgr.getString("ColumnDetailsController.title"));

         _dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

         _dlg.getContentPane().setLayout(new GridLayout(1,1));

         _columnsDetailsPanel = new DataSetViewerTablePanel();
         _columnsDetailsPanel.init(null, null);
         _columnsDetailsPanel.show(columnDetailsDataSet);

         _dlg.getContentPane().add(new JScrollPane(_columnsDetailsPanel.getComponent()));


         GUIUtils.enableCloseByEscape(_dlg);

         GUIUtils.initLocation(_dlg, 600, 180);

         _dlg.setVisible(true);
      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private SimpleDataSet createColumnDetailsDataSet(DataSetViewerTable table, ISession session, TableClickPosition tableClickPosition)
   {
      List<ColumnDisplayDefinition> selectedColDefs = getSelectedColDefs(table, session, tableClickPosition);

      if(selectedColDefs.isEmpty())
      {
         return null;
      }


      List<ColumnDisplayDefinition> columnDisplayDefinitions = new ArrayList<>();
      columnDisplayDefinitions.add(new ColumnDisplayDefinition(30, s_stringMgr.getString("ColumnDetailsController.colName")));

      boolean hasTableNames = hasTableNames(selectedColDefs);

      List<String> tableNameRow = new ArrayList<>();
      List<String> sqlTypeNameRow = new ArrayList<>();
      List<String> nullableRow = new ArrayList<>();
      List<String> columnSizeRow = new ArrayList<>();
      List<String> scaleRow = new ArrayList<>();
      List<String> precisionRow = new ArrayList<>();

      for (ColumnDisplayDefinition selColDef : selectedColDefs)
      {
         columnDisplayDefinitions.add(new ColumnDisplayDefinition(30, selColDef.getColumnHeading()));

         if(hasTableNames)
         {
            addIfEmpty(tableNameRow, s_stringMgr.getString("ColumnDetailsController.tableName"));
            String tableName = "";
            if(null != selColDef.getResultMetaDataTable())
            {
               tableName = selColDef.getResultMetaDataTable().getTableName();
            }
            tableNameRow.add(StringUtilities.nullToEmpty(tableName));
         }

         addIfEmpty(sqlTypeNameRow, s_stringMgr.getString("ColumnDetailsController.sqlTypeName"));
         sqlTypeNameRow.add(selColDef.getSqlTypeName());

         addIfEmpty(nullableRow, s_stringMgr.getString("ColumnDetailsController.nullable"));
         nullableRow.add("" + selColDef.isNullable());

         addIfEmpty(columnSizeRow, s_stringMgr.getString("ColumnDetailsController.size"));
         columnSizeRow.add("" + selColDef.getColumnSize());

         addIfEmpty(scaleRow, s_stringMgr.getString("ColumnDetailsController.scale"));
         scaleRow.add("" + selColDef.getScale());

         addIfEmpty(precisionRow, s_stringMgr.getString("ColumnDetailsController.precision"));
         precisionRow.add("" + selColDef.getPrecision());
      }


      ArrayList<Object[]> rows = new ArrayList<>();

      if(false == tableNameRow.isEmpty())
      {
         rows.add(tableNameRow.toArray());
      }

      rows.add(sqlTypeNameRow.toArray());
      rows.add(nullableRow.toArray());
      rows.add(columnSizeRow.toArray());
      rows.add(scaleRow.toArray());
      rows.add(precisionRow.toArray());

      SimpleDataSet colsDataSet = new SimpleDataSet(rows, columnDisplayDefinitions.toArray(new ColumnDisplayDefinition[0]));

      return colsDataSet;
   }

   private List<ColumnDisplayDefinition> getSelectedColDefs(DataSetViewerTable table, ISession session, TableClickPosition tableClickPosition)
   {
      ArrayList<InStatColumnInfo> inStatColumnInfos = new TableCopyInStatementCommand(table, session).getInStatColumnInfos();

      List<ColumnDisplayDefinition> colDefs = new ArrayList<>();
      if(false == inStatColumnInfos.isEmpty())
      {
         colDefs = inStatColumnInfos.stream().map(ici -> ici.getColDef()).collect(Collectors.toList());
      }
      else if(null != tableClickPosition && tableClickPosition.isClickedOnTableHeader())
      {
         TableColumnModel cm = table.getColumnModel();
         int columnIndexAtX = cm.getColumnIndexAtX(tableClickPosition.getX());
         if(cm.getColumn(columnIndexAtX) instanceof ExtTableColumn)
         {
            colDefs.add(((ExtTableColumn)cm.getColumn(columnIndexAtX)).getColumnDisplayDefinition());
         }
      }
      return colDefs;
   }

   private static void addIfEmpty(List<String> row, String rowName)
   {
      if(row.isEmpty())
      {
         row.add(rowName);
      }
   }

   private static boolean hasTableNames(List<ColumnDisplayDefinition> colDefs)
   {
      for (ColumnDisplayDefinition colDef : colDefs)
      {
         if(null != colDef.getResultMetaDataTable() )
         {
            String tableName = colDef.getResultMetaDataTable().getTableName();
            if(false == StringUtilities.isEmpty(tableName, true))
            {
               return true;
            }
         }
      }
      return false;
   }
}
