package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.ResultAsText;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.ResultAsTextLineCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

public class TableCopyAlignedCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableCopyAlignedCommand.class);

   private final static String NULL_CELL = "<null>";

   private JTable _table;
   private ISession _session;

   public TableCopyAlignedCommand(JTable table, ISession session)
   {
      _session = session;
      if (table == null)
      {
         throw new IllegalArgumentException("JTable == null");
      }
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
            _session.showErrorMessage(s_stringMgr.getString("TableCopyAlignedCommand.failed.to.copy"));
            return;
         }
      }


      ColumnDisplayDefinition[] colDefs = columnDisplayDefinitions.toArray(new ColumnDisplayDefinition[columnDisplayDefinitions.size()]);

      final StringBuffer text = new StringBuffer();
      ResultAsTextLineCallback resultAsTextLineCallback = new ResultAsTextLineCallback()
      {
         @Override
         public void addLine(String line)
         {
            text.append(line);
         }
      };

      ResultAsText resultAsText = new ResultAsText(colDefs, true, resultAsTextLineCallback);

      for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
      {
         Object[] row = new Object[colDefs.length];

         int curIx = 0;
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);
            row[curIx] = cellObj;
            ++curIx;
         }

         resultAsText.addRow(row);
      }

      StringSelection ss = new StringSelection(text.toString());
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
   }
}
