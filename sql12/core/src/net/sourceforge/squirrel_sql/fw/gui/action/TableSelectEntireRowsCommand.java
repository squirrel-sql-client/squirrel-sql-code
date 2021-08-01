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
package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import javax.swing.JTable;

/**
 * Command to expand the current selection of a table to span the complete affected rows.
 *
 * @author Stefan Willinger
 */

/**
 * @author Stefan Willinger
 *
 */
public class TableSelectEntireRowsCommand implements ICommand
{

   private JTable table;

   public TableSelectEntireRowsCommand(JTable table)
   {
      setTable(table);
   }

   @Override
   public void execute()
   {
      getTable().setColumnSelectionInterval(0, getTable().getColumnCount() - 1);
   }

   /**
    * @return the _table
    */
   public JTable getTable()
   {
      return this.table;
   }

   /**
    * @param _table the _table to set
    */
   public void setTable(JTable table)
   {
      if (table == null)
      {
         throw new IllegalArgumentException("JTable == null");
      }
      this.table = table;
   }
}
