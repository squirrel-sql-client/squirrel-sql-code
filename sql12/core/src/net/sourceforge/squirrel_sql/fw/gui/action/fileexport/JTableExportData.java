/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;

import javax.swing.JTable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The implementation of {@link IExportData} for exporting a {@link JTable}.
 * This class encapsulate the access to the model of a JTable.
 * Its possible, to handle the full table, or only the selected part of it.
 * <p>
 * <b>Note:</b> This class is the result of a refactoring task. The code was taken from TableExportCsvCommand.
 *
 * @author Stefan Willinger
 */
public class JTableExportData implements IExportData
{

   private JTable _table;

   private int _nbrSelRows;
   private int _nbrSelCols;
   private int[] _selRows;
   private int[] _selcols;

   /**
    * Constructor using a JTable.
    *
    * @param table    the JTable to use.
    * @param complete flag, if the complete table or only the selection should be used.
    */
   public JTableExportData(JTable table, boolean complete)
   {
      _table = table;

      _nbrSelRows = table.getSelectedRowCount();
      if (0 == _nbrSelRows || complete)
      {
         _nbrSelRows = table.getRowCount();
      }

      _nbrSelCols = table.getSelectedColumnCount();
      if (0 == _nbrSelCols || complete)
      {
         _nbrSelCols = table.getColumnCount();
      }

      _selRows = table.getSelectedRows();
      if (0 == _selRows.length || complete)
      {
         _selRows = new int[_nbrSelRows];
         for (int i = 0; i < _selRows.length; i++)
         {
            _selRows[i] = i;
         }
      }

      _selcols = table.getSelectedColumns();
      if (0 == _selcols.length || complete)
      {
         _selcols = new int[_nbrSelCols];
         for (int i = 0; i < _selcols.length; i++)
         {
            _selcols[i] = i;
         }
      }

   }

   /**
    * Reads the header of the table.
    *
    * @see IExportData#getHeaders()
    */
   @Override
   public Iterator<String> getHeaders()
   {
      List<String> headers = new ArrayList<String>();
      for (int colIdx = 0; colIdx < _nbrSelCols; ++colIdx)
      {
         String columnName = _table.getColumnName(_selcols[colIdx]);
         headers.add(columnName);
      }
      return headers.iterator();
   }

   /**
    * @see IExportData#getRows(boolean)
    */
   @Override
   public Iterator<ExportDataRow> getRows()
   {
      List<ExportDataRow> rows = new ArrayList<>(_nbrSelRows);
      for (int rowIdx = 0; rowIdx < _nbrSelRows; ++rowIdx)
      {
         List<ExportCellData> cells = new ArrayList<>(_nbrSelCols);

         for (int colIdx = 0; colIdx < _nbrSelCols; ++colIdx)
         {
            ExportCellData cellObj;

            final Object obj = _table.getValueAt(_selRows[rowIdx], _selcols[colIdx]);

            if (_table.getColumnModel().getColumn(colIdx) instanceof ExtTableColumn)
            {
               ExtTableColumn col = (ExtTableColumn) _table.getColumnModel().getColumn(_selcols[colIdx]);
               cellObj = new ExportCellData(col.getColumnDisplayDefinition(), obj, rowIdx, colIdx);
            }
            else
            {
               cellObj = new ExportCellData(null, obj, rowIdx, colIdx);
            }

            if(_table instanceof DataSetViewerTable)
            {
               final Color excelExportColor = ((DataSetViewerTable) _table).getColoringService().getExcelExportRelevantColor(rowIdx, colIdx, obj);
               cellObj.setExcelExportColor(excelExportColor);
            }

            cells.add(cellObj);
         }
         rows.add(new ExportDataRow(cells, rowIdx));
      }
      return rows.iterator();
   }

   @Override
   public void close()
   {

   }

}
