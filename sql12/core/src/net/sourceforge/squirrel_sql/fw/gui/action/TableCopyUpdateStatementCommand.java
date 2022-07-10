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

import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.sql.SqlScriptPluginAccessor;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 * This command gets the current selected text from a <TT>JTable</TT>
 * and formats it as HTML table and places it on the system clipboard.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TableCopyUpdateStatementCommand extends TableCopySqlPartCommandBase implements ICommand
{
   /**
    * The table we are copying data from.
    */
   private final JTable _table;
   private DataModelImplementationDetails _dataModelImplementationDetails;

   /**
    * Ctor specifying the <TT>JTable</TT> to get the data from.
    *
    * @param   table   The <TT>JTable</TT> to get data from.
    * @param dataModelImplementationDetails
    * @throws	IllegalArgumentException Thrown if <tt>null</tt> <tt>JTable</tt> passed.
    */
   public TableCopyUpdateStatementCommand(JTable table, DataModelImplementationDetails dataModelImplementationDetails)
   {
      super(dataModelImplementationDetails.getSession());
      _dataModelImplementationDetails = dataModelImplementationDetails;
      if (table == null)
      {
         throw new IllegalArgumentException("JTable == null");
      }
      _table = table;
   }

   /**
    * Execute this command.
    */
   @Override
   public void execute()
   {
      int nbrSelRows = _table.getSelectedRowCount();
      int nbrSelCols = _table.getSelectedColumnCount();
      int[] selRows = _table.getSelectedRows();
      int[] selCols = _table.getSelectedColumns();


      if (selRows.length == 0 || selCols.length == 0)
      {
         return;
      }

      TableNameProvider tableNameProvider = new TableNameProvider(_dataModelImplementationDetails);

      StringBuffer buf = new StringBuffer();
      for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
      {
         StringBuffer setListBuf = new StringBuffer();

         boolean firstCol = true;
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {

            TableColumn col = _table.getColumnModel().getColumn(selCols[colIdx]);

            ColumnDisplayDefinition colDef;
            if(col instanceof ExtTableColumn)
            {
               colDef = ((ExtTableColumn) col).getColumnDisplayDefinition();
            }
            else
            {
               continue;
            }

            tableNameProvider.addColDef(colDef);


            if (firstCol)
            {
               firstCol = false;
            }
            else
            {
               setListBuf.append(",");
            }

            final Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);
            setListBuf.append(SqlScriptPluginAccessor.formatColumnName(colDef.getColumnName())).append(getData(colDef, cellObj, StatType.UPDATE));
         }

         tableNameProvider.colDefsFinished();

         buf.append("UPDATE "+ tableNameProvider.getTableName() +" SET ").append(setListBuf).append(" WHERE\n");
      }

      ClipboardUtil.copyToClip(buf);
   }
}
