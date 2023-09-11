package net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;

import java.util.TreeMap;

public class RawDataTable
{
   private String[] _colNames;

   private TreeMap<Integer, Object[]> _rows = new TreeMap<>();

   public void setColumnNames(String[] colNames)
   {
      _colNames = colNames;
   }

   public String[] getColNames()
   {
      return _colNames;
   }

   public void setCell(int rowIdx, int colIdx, Object cellObj)
   {
      if (null == cellObj)
      {
         cellObj = BaseDataTypeComponent.NULL_VALUE_PATTERN;
      }
      else
      {
         cellObj = "" + cellObj;
      }

      _rows.computeIfAbsent(rowIdx, idx -> new Object[_colNames.length])[colIdx] = cellObj;
   }

   public String getRawColumnString(String colName)
   {
      int colIx = -1;
      for (int i = 0; i < _colNames.length; i++)
      {
         if(_colNames[i].equalsIgnoreCase(colName))
         {
            colIx = i;
            break;
         }
      }

      if(-1 == colIx)
      {
         throw new IllegalArgumentException("Unknown column name: " + colName);
      }

      StringBuilder ret = new StringBuilder();

      for (Object[] value : _rows.values())
      {
         if (0 < ret.length())
         {
            ret.append("\n");
         }
         ret.append(value[colIx]);
      }

      return ret.toString();
   }

}
