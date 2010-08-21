/*
 * Copyright (C) 2005 Gerd Wagner
 * gerdwagner@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.fw.datasetviewer;

import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;

public class ExtTableColumn extends TableColumn
{
   private ColumnDisplayDefinition _colDef;

   public ExtTableColumn(int modelIndex, int width, TableCellRenderer cellRenderer, TableCellEditor cellEditor)
   {
      super(modelIndex, width, cellRenderer, cellEditor);
   }

   public void setColumnDisplayDefinition(ColumnDisplayDefinition colDef)
   {
      _colDef = colDef;
   }

   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return _colDef;
   }
}
