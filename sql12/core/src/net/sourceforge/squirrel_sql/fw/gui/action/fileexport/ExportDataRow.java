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

import java.util.Iterator;
import java.util.List;

/**
 * @author Stefan Willinger
 */
public class ExportDataRow implements IExportDataRow
{
   private List<ExportCellData> cells;

   private int rowIndex;

   public ExportDataRow(List<ExportCellData> cells, int rowIndex)
   {
      setCells(cells);
      setRowIndex(rowIndex);
   }

   /**
    * @see IExportDataRow#getCells()
    */
   @Override
   public Iterator<ExportCellData> getCells()
   {
      return cells.iterator();
   }

   /**
    * @param cells the cells to set
    */
   public void setCells(List<ExportCellData> cells)
   {
      if (cells == null)
      {
         throw new IllegalArgumentException("cells = null");
      }
      this.cells = cells;
   }

   /**
    * @see IExportDataRow#getRowIndex()
    */
   public int getRowIndex()
   {
      return rowIndex;
   }

   /**
    * @param rowIndex the rowIndex to set
    */
   public void setRowIndex(int rowIndex)
   {
      if (rowIndex < 0)
      {
         throw new IllegalArgumentException("rowIndex < 0");
      }
      this.rowIndex = rowIndex;
   }


}
