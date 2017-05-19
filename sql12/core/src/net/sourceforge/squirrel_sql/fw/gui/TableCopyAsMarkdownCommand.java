package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.ResultAsText;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.ResultAsTextLineCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.steppschuh.markdowngenerator.table.Table;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;

public class TableCopyAsMarkdownCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableCopyAsMarkdownCommand.class);

   private final ISession _session;
   private final JTable _table;

   public TableCopyAsMarkdownCommand(JTable table, ISession session)
   {
      _session = session;
      _table = table;
   }

   public void execute()
   {
      int nbrSelRows = _table.getSelectedRowCount();
      int nbrSelCols = _table.getSelectedColumnCount();
      int[] selRows = _table.getSelectedRows();
      int[] selCols = _table.getSelectedColumns();

      ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions = new ArrayList<ColumnDisplayDefinition>();
      for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
      {
         TableColumn col = _table.getColumnModel().getColumn(selCols[colIdx]);

         if (col instanceof ExtTableColumn)
         {
            columnDisplayDefinitions.add(((ExtTableColumn) col).getColumnDisplayDefinition());
         }
         else
         {
            _session.showErrorMessage(s_stringMgr.getString("TableCopyAsMarkdownCommand.failed.to.copy"));
            return;
         }
      }


      ColumnDisplayDefinition[] colDefs = columnDisplayDefinitions.toArray(new ColumnDisplayDefinition[columnDisplayDefinitions.size()]);

      String[] colNames = new String[colDefs.length];

      for (int i = 0; i < colDefs.length; i++)
      {
         colNames[i] = colDefs[i].getColumnName();
      }



      Table.Builder tableBuilder = new Table.Builder();
      tableBuilder.addRow(colNames);

      for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
      {
         Object[] row = new Object[colDefs.length];

         int curIx = 0;
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);

            if(cellObj instanceof String && -1 < ((String)cellObj).indexOf('\n'))
            {
               int lineBreakPos = ((String)cellObj).indexOf('\n');
               row[curIx] = ((String)cellObj).substring(0, lineBreakPos);
            }
            else if(null == cellObj)
            {
               row[curIx] = BaseDataTypeComponent.NULL_VALUE_PATTERN;
            }
            else
            {
               row[curIx] = cellObj;
            }
            ++curIx;
         }

         tableBuilder.addRow(row);
      }

      String markdownTable = tableBuilder.build().toString();

      int width = markdownTable.indexOf('\n');

      String line = new String(new char[width]).replace('\0', '-') + "\n";

      StringSelection ss = new StringSelection(line + markdownTable + "\n" + line);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
   }
}
