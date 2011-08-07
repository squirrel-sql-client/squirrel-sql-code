package net.sourceforge.squirrel_sql.fw.gui.action;

import java.sql.Types;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.DataExportCSVWriter;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportData;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.JTableExportData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Command for exporting a {@link JTable} into a file.
 * <p><b>Note:</b> The structure of this class has be really changed.
 *  The content of this class was split up into the package <pre>net.sourceforge.squirrel_sql.fw.gui.action.export</pre> 
 * @author Stefan Willinger
 * @see net.sourceforge.squirrel_sql.fw.gui.action.export
 */
public class TableExportCsvCommand extends AbstractExportCommand
{

   static ILogger s_log = LoggerController.createLogger(TableExportCsvCommand.class);
   
   JTable _table;

   public TableExportCsvCommand(JTable table)
   {
	   super();
      _table = table;
   }

   /**
    *
 * @see net.sourceforge.squirrel_sql.fw.gui.action.AbstractExportCommand#checkMissingData(java.lang.String)
 */
   @Override
protected boolean checkMissingData(String sepChar) {
       // TODO: if the use checks "export entire table" and doesn't select all, 
       // then the selected indices are not set, and this check doesn't properly
       // detect missing data.  If export entire table is selected, we need to 
       // set the selected indexes below correctly.
       int firstSelectedColIdx = _table.getSelectedColumn();
       int lastSelectedColIdx = firstSelectedColIdx + _table.getSelectedColumnCount();
       int firstSelectedRowIdx = _table.getSelectedRow();
       int lastSelectedRowIdx = firstSelectedRowIdx + _table.getSelectedRowCount();
       for (int colIdx = _table.getSelectedColumn(); colIdx < lastSelectedColIdx; colIdx++) {
           ExtTableColumn col = (ExtTableColumn) _table.getColumnModel().getColumn(colIdx);
           int sqlType = col.getColumnDisplayDefinition().getSqlType();
           if (sqlType == Types.CLOB) {
               for (int rowIdx = firstSelectedRowIdx; rowIdx < lastSelectedRowIdx; rowIdx++) {
                   Object cellObj = _table.getValueAt(rowIdx, colIdx);
                   // TODO stefan why did we need the csv data?
                   String data = DataExportCSVWriter.getDataCSV(sepChar, cellObj.toString());
                   if (data != null && ClobDescriptor.i18n.CLOB_LABEL.equals(data)) {
                       return true;
                   }
               }
           }
       }
       return false;
   }


   /**
    * @see net.sourceforge.squirrel_sql.fw.gui.action.AbstractExportCommand#createExportData()
    */
   @Override
   protected IExportData createExportData(TableExportCsvController ctrl) {
	   return  new JTableExportData(_table, ctrl.exportComplete());
   }




}
