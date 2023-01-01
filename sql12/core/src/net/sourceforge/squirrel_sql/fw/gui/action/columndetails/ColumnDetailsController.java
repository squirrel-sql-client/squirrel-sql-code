package net.sourceforge.squirrel_sql.fw.gui.action.columndetails;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.InStatColumnInfo;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopyInStatementCommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class ColumnDetailsController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColumnDetailsController.class);
   private ColumnDetailsDialog _dlg;
   private DataSetViewerTablePanel _columnsDetailsPanel;

   public ColumnDetailsController(DataSetViewerTable table, ISession session)
   {
      try
      {
         SimpleDataSet columnDetailsDataSet = createColumnDetailsDataSet(table, session);

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

   private SimpleDataSet createColumnDetailsDataSet(DataSetViewerTable table, ISession session)
   {
      ArrayList<InStatColumnInfo> inStatColumnInfos = new TableCopyInStatementCommand(table, session).getInStatColumnInfos();

      if(inStatColumnInfos.isEmpty())
      {
         return null;
      }


      boolean hasTableNames = hasTableNames(inStatColumnInfos);

      List<String> tableNameRow = new ArrayList<>();
      List<String> sqlTypeNameRow = new ArrayList<>();
      List<String> nullableRow = new ArrayList<>();
      List<String> columnSizeRow = new ArrayList<>();
      List<String> scaleRow = new ArrayList<>();
      List<String> precisionRow = new ArrayList<>();


      List<ColumnDisplayDefinition> columnDisplayDefinitions = new ArrayList<>();

      columnDisplayDefinitions.add(new ColumnDisplayDefinition(30, s_stringMgr.getString("ColumnDetailsController.colName")));

      for (InStatColumnInfo inStatColumnInfo : inStatColumnInfos)
      {
         columnDisplayDefinitions.add(new ColumnDisplayDefinition(30, inStatColumnInfo.getColDef().getColumnHeading()));

         if(hasTableNames)
         {
            addIfEmpty(tableNameRow, s_stringMgr.getString("ColumnDetailsController.tableName"));
            String tableName = "";
            if(null != inStatColumnInfo.getColDef().getResultMetaDataTable())
            {
               tableName = inStatColumnInfo.getColDef().getResultMetaDataTable().getTableName();
            }
            tableNameRow.add(StringUtilities.nullToEmpty(tableName));
         }

         addIfEmpty(sqlTypeNameRow, s_stringMgr.getString("ColumnDetailsController.sqlTypeName"));
         sqlTypeNameRow.add(inStatColumnInfo.getColDef().getSqlTypeName());

         addIfEmpty(nullableRow, s_stringMgr.getString("ColumnDetailsController.nullable"));
         nullableRow.add("" + inStatColumnInfo.getColDef().isNullable());

         addIfEmpty(columnSizeRow, s_stringMgr.getString("ColumnDetailsController.size"));
         columnSizeRow.add("" + inStatColumnInfo.getColDef().getColumnSize());

         addIfEmpty(scaleRow, s_stringMgr.getString("ColumnDetailsController.scale"));
         scaleRow.add("" + inStatColumnInfo.getColDef().getScale());

         addIfEmpty(precisionRow, s_stringMgr.getString("ColumnDetailsController.precision"));
         precisionRow.add("" + inStatColumnInfo.getColDef().getPrecision());
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

   private static void addIfEmpty(List<String> row, String rowName)
   {
      if(row.isEmpty())
      {
         row.add(rowName);
      }
   }

   private static boolean hasTableNames(ArrayList<InStatColumnInfo> inStatColumnInfos)
   {
      for (InStatColumnInfo inStatColumnInfo : inStatColumnInfos)
      {
         if(null != inStatColumnInfo.getColDef().getResultMetaDataTable() )
         {
            String tableName = inStatColumnInfo.getColDef().getResultMetaDataTable().getTableName();
            if(false == StringUtilities.isEmpty(tableName, true))
            {
               return true;
            }
         }
      }
      return false;
   }
}
