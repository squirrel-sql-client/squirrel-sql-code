package net.sourceforge.squirrel_sql.client.session.action.dataimport.gui;
/*
 * Copyright (C) 2007 Thorsten Mürell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 * This is a table model for the column mapping table at the bottom
 * of the file import dialog.
 *
 * @author Thorsten Mürell
 */
public class ColumnMappingTableModel extends AbstractTableModel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColumnMappingTableModel.class);

   private ExtendedColumnInfo[] _columns;
   private Vector<String> _mapping = new Vector<String>();
   private Vector<String> _defaults = new Vector<String>();

   /**
    * The default constructor.
    *
    * @param columns These are the columns of the destination table
    */
   public ColumnMappingTableModel(ExtendedColumnInfo[] columns)
   {
      this._columns = columns;
      for (int i = 0; i < columns.length; i++)
      {
         _mapping.add(SpecialColumnMapping.SKIP.getVisibleString());
         _defaults.add("");
      }
   }

   /*
    * (non-Javadoc)
    * @see javax.swing.table.TableModel#getColumnCount()
    */
   public int getColumnCount()
   {
      return 3;
   }

   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getRowCount()
    */
   public int getRowCount()
   {
      return _columns.length;
   }

   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getValueAt(int, int)
    */
   public Object getValueAt(int rowIndex, int columnIndex)
   {
      if (columnIndex == ColumnMappingConstants.INDEX_TABLE_COLUMN)
      {
         return _columns[rowIndex].getColumnName();
      }
      else if (columnIndex == ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN)
      {
         return _mapping.get(rowIndex);
      }
      else if (columnIndex == ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN)
      {
         return _defaults.get(rowIndex);
      }
      return null;
   }

   /* (non-Javadoc)
    * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
    */
   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      if (columnIndex == ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN ||
            columnIndex == ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN)
      {
         return true;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
    */
   @Override
   public void setValueAt(Object value, int row, int col)
   {
      if (col == ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN)
      {
         _mapping.set(row, value.toString());
      }
      else if (col == ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN)
      {
         _defaults.set(row, value.toString());
      }
      fireTableCellUpdated(row, col);
   }

   /* (non-Javadoc)
    * @see javax.swing.table.AbstractTableModel#getColumnName(int)
    */
   @Override
   public String getColumnName(int column)
   {
      if (column == ColumnMappingConstants.INDEX_TABLE_COLUMN)
      {
         // i18n[ImportFileDialogCtrl.tableColumn=Table column]
         return s_stringMgr.getString("ImportFileDialog.tableColumn");
      }
      else if (column == ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN)
      {
         // i18n[ImportFileDialogCtrl.importFileColumn=Import file column]
         return s_stringMgr.getString("ImportFileDialog.importFileColumn");
      }
      else if (column == ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN)
      {
         // i18n[ImportFileDialogCtrl.fixedValue=Fixed value]
         return s_stringMgr.getString("ImportFileDialog.fixedValue");
      }
      return null;
   }

   /**
    * Returns the index of the column with the given name.
    *
    * @param columnName The column name
    * @return The row of the given column
    */
   public int findTableColumn(String columnName)
   {
      int i = 0;
      for (i = 0; i < _columns.length; i++)
      {
         if (columnName.equals(_columns[i].getColumnName()))
         {
            return i;
         }
      }
      return -1;
   }

   /**
    * Resets the column mappings to "Skip"
    */
   public void resetMappings()
   {
      _mapping.clear();
      _defaults.clear();
      for (int i = 0; i < _columns.length; i++)
      {
         _mapping.add(SpecialColumnMapping.SKIP.getVisibleString());
         _defaults.add("");
      }
      fireTableDataChanged();
   }



   public String getMapping(ExtendedColumnInfo column)
   {
      int pos = findTableColumn(column.getColumnName());
      return getValueAt(pos, 1).toString();
   }

   public String getFixedValue(ExtendedColumnInfo column)
   {
      int pos = findTableColumn(column.getColumnName());
      return getValueAt(pos, 2).toString();
   }

   public int getColumnCountExcludingSkipped(ExtendedColumnInfo[] columns)
   {
      int count = 0;
      for (ExtendedColumnInfo column : columns)
      {
         int pos = findTableColumn(column.getColumnName());
         String mapping = getValueAt(pos, 1).toString();
         if (!SpecialColumnMapping.SKIP.getVisibleString().equals(mapping))
         {
            count++;
         }
      }
      return count;
   }


}