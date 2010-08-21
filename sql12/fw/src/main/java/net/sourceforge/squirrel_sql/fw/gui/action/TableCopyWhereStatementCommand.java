package net.sourceforge.squirrel_sql.fw.gui.action;

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

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * This command gets the current selected text from a <TT>JTable</TT>
 * and formats it as HTML table and places it on the system clipboard.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TableCopyWhereStatementCommand extends TableCopySqlPartCommandBase implements ICommand
{
   /**
    * The table we are copying data from.
    */
   private JTable _table;

   /**
    * Ctor specifying the <TT>JTable</TT> to get the data from.
    *
    * @param	table	The <TT>JTable</TT> to get data from.
    * @throws	IllegalArgumentException Thrown if <tt>null</tt> <tt>JTable</tt> passed.
    */
   public TableCopyWhereStatementCommand(JTable table)
   {
      super();
      if (table == null)
      {
         throw new IllegalArgumentException("JTable == null");
      }
      _table = table;
   }

   /**
    * Execute this command.
    */
   public void execute()
   {
      int nbrSelRows = _table.getSelectedRowCount();
      int nbrSelCols = _table.getSelectedColumnCount();
      int[] selRows = _table.getSelectedRows();
      int[] selCols = _table.getSelectedColumns();
      if (selRows.length != 0 && selCols.length != 0)
      {
         StringBuffer buf = new StringBuffer("WHERE ");
         for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
         {

            if(1 < nbrSelCols)
            {
               if(0 < rowIdx)
               {
                  buf.append("OR (");
               }
               else
               {
                  buf.append("(");
               }
            }
            else
            {
               if(0 < rowIdx)
               {
                  buf.append("OR ");
               }
            }

            boolean firstCol = true;
            for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
            {

               TableColumn col = _table.getColumnModel().getColumn(selCols[colIdx]);

               ColumnDisplayDefinition colDef = null;
               if(col instanceof ExtTableColumn)
               {
                  colDef = ((ExtTableColumn) col).getColumnDisplayDefinition();
               }
               else
               {
                  continue;
               }

               if(firstCol)
               {
                  firstCol = false;
               }
               else
               {
                  buf.append(" AND ");
               }

               final Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);
               buf.append(colDef.getColumnName()).append(getData(colDef, cellObj, StatType.WHERE));
            }

            if(1 < nbrSelCols)
            {
               buf.append(")\n");
            }
            else
            {
               if(100 < buf.length() - buf.toString().lastIndexOf("\n"))
               {
                  buf.append("\n");
               }
               else
               {
                  buf.append(" ");
               }
            }
         }
         final StringSelection ss = new StringSelection(buf.toString());
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
      }
   }


}