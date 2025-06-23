package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;

import javax.swing.JTable;
import java.util.List;

public class UITableExportData
{
   private JTable _tableExportDialogWasOpenedFor;
   private List<ExportDataInfo> _sqlResultDataSetViewersExportDataList;
   private boolean _exportUITableSelection;
   private SQLExecutionInfo _sqlExecutionInfo;

   public UITableExportData(JTable table, SQLExecutionInfo sqlExecutionInfo)
   {
      _tableExportDialogWasOpenedFor = table;
      _sqlExecutionInfo = sqlExecutionInfo;
   }

   public JTable getTableExportDialogWasOpenedFor()
   {
      return _tableExportDialogWasOpenedFor;
   }

   public List<ExportDataInfo> getSqlResultDataSetViewersExportDataList()
   {
      return _sqlResultDataSetViewersExportDataList;
   }

   public void setSqlResultDataSetViewersExportDataList(List<ExportDataInfo> sqlResultDataSetViewersExportDataList)
   {
      _sqlResultDataSetViewersExportDataList = sqlResultDataSetViewersExportDataList;
   }

   public boolean isExportUITableSelection()
   {
      return _exportUITableSelection;
   }

   public void setExportUITableSelection(boolean exportUITableSelection)
   {
      _exportUITableSelection = exportUITableSelection;
   }

   public SQLExecutionInfo getSqlExecutionInfo()
   {
      return _sqlExecutionInfo;
   }

}
