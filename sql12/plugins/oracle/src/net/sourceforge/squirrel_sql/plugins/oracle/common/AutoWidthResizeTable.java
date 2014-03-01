package net.sourceforge.squirrel_sql.plugins.oracle.common;
/*
 * Copyright (C) 2004 Jason Height
 * jmheight@users.sourceforge.net
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
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class AutoWidthResizeTable extends JTable
{
  
  public AutoWidthResizeTable() {
    super();
  }
  
  public AutoWidthResizeTable(TableModel model) {
    super(model);
  }

  /** This method resizes the width of each column based on the data content.
   *  For large number of rows this is probably quite slow.
   */
  public void resizeColumnWidth() {
    resizeColumnWidth(-1);
  }

  public void resizeColumnWidth(int maxCellWidth) {
    TableModel tm = getModel();
    TableColumnModel colModel = getColumnModel();

    for (int col = tm.getColumnCount()-1; col >=0; col--) {
      TableColumn column = colModel.getColumn(col);
      TableCellRenderer renderer = column.getHeaderRenderer();
      if (renderer == null) {
        renderer = getTableHeader().getDefaultRenderer();
      }
      //Set the cell Width to the width of the header
      int cellWidth = renderer.getTableCellRendererComponent(this,
          column.getHeaderValue(), false, false, -1, col).getPreferredSize().
          width;

      renderer = getDefaultRenderer(tm.getColumnClass(col));

      for (int row = tm.getRowCount()-1; row>=0; row--) {
        int tmpWidth = renderer.getTableCellRendererComponent(this,
            tm.getValueAt(row, col), false, false, row, col).getPreferredSize().
            width;
        if (tmpWidth > cellWidth)
          cellWidth = tmpWidth;
      }
      //For reasons that i cannot exmplain, this is out by two, probably due to the
      //border or insets around a cell??
      cellWidth+=2;
      if ((maxCellWidth != -1) && (cellWidth >maxCellWidth))
        cellWidth = maxCellWidth;
      column.setPreferredWidth(cellWidth);
    }
  }

}
