package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.sql.Types;

/**
 * Command for exporting a {@link JTable} into a file.
 * <p><b>Note:</b> The structure of this class has be really changed.
 * The content of this class was split up into the package <pre>net.sourceforge.squirrel_sql.fw.gui.action.export</pre>
 *
 * @author Stefan Willinger
 * @see net.sourceforge.squirrel_sql.fw.gui.action.export
 */
public class TableExport
{
   private JTable _table;
   private Exporter _exporter;

   public TableExport(JTable table)
   {
      _table = table;

      final ExporterCallback exporterCallback = new ExporterCallback()
      {
         @Override
         public TableExportPreferences getExportPreferences()
         {
            return TableExportPreferencesDAO.loadPreferences();
         }

         @Override
         public ProgressAbortCallback createProgressController()
         {
            return null;
         }

         @Override
         public boolean checkMissingData(String separatorChar)
         {
            return onCheckMissingData(separatorChar);
         }

         @Override
         public IExportData createExportData(ExportController ctrl)
         {
            return new JTableExportData(_table, ctrl.isExportComplete());
         }
      };

      _exporter = new Exporter(exporterCallback, ExportControllerFactory.createExportControllerForTable(SwingUtilities.windowForComponent(_table)));
   }

   private boolean onCheckMissingData(String sepChar)
   {
      // TODO: if the use checks "export entire table" and doesn't select all,
      // then the selected indices are not set, and this check doesn't properly
      // detect missing data.  If export entire table is selected, we need to
      // set the selected indexes below correctly.
      int firstSelectedColIdx = _table.getSelectedColumn();
      int lastSelectedColIdx = firstSelectedColIdx + _table.getSelectedColumnCount();
      int firstSelectedRowIdx = _table.getSelectedRow();
      int lastSelectedRowIdx = firstSelectedRowIdx + _table.getSelectedRowCount();
      for (int colIdx = _table.getSelectedColumn(); colIdx < lastSelectedColIdx; colIdx++)
      {
         if(false == _table.getColumnModel().getColumn(colIdx) instanceof ExtTableColumn)
         {
            continue;
         }

         ExtTableColumn col = (ExtTableColumn) _table.getColumnModel().getColumn(colIdx);
         int sqlType = col.getColumnDisplayDefinition().getSqlType();
         if (sqlType == Types.CLOB)
         {
            for (int rowIdx = firstSelectedRowIdx; rowIdx < lastSelectedRowIdx; rowIdx++)
            {
               Object cellObj = _table.getValueAt(rowIdx, colIdx);
               // TODO stefan why did we need the csv data?
               String data = DataExportCSVWriter.getDataCSV(sepChar, "" + cellObj);
               if (ClobDescriptor.i18n.CLOB_LABEL.equals(data))
               {
                  return true;
               }
            }
         }
      }
      return false;
   }


   public void export()
   {
      _exporter.export();
   }
}
