package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DistinctValuesHolder
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DistinctValuesHolder.class);

   private HashMap<Integer, DistinctValuesData> _colIx_distinctValuesData = new HashMap<>();

   private boolean _nullsAdded;

   public void addDistinct(int colIx, Object value)
   {
      DistinctValuesData distinctValuesData = _colIx_distinctValuesData.computeIfAbsent(colIx, ix -> new DistinctValuesData());
      distinctValuesData.addUnique(value);
   }

   public List<Object[]> getDistinctRows()
   {
      ArrayList<Object[]> ret = new ArrayList<>();


      for (int rowIx = 0; rowIx < getMaxDistinctValues(); rowIx++)
      {
         Object[] row;
         if (1 == _colIx_distinctValuesData.size())
         {
            // If there's just one column we add a count-column to show the how often the values occur.
            row = new Object[2];
         }
         else
         {
            row = new Object[_colIx_distinctValuesData.size()];
         }

         for (int colIx = 0; colIx < _colIx_distinctValuesData.size(); colIx++)
         {
            if(rowIx < _colIx_distinctValuesData.get(colIx).size())
            {
               row[colIx] = _colIx_distinctValuesData.get(colIx).getValue(rowIx);
            }
            else
            {
               _nullsAdded = true;
            }
         }

         if (1 == _colIx_distinctValuesData.size())
         {
            // If there's just one column we add a count-column to show the how often the values occur.
            row[1] = _colIx_distinctValuesData.get(0).getValueCount(rowIx);
         }

         ret.add(row);
      }

      return ret;
   }

   private int getMaxDistinctValues()
   {
      return _colIx_distinctValuesData.values().stream().mapToInt( d -> d.size()).max().getAsInt();
   }

   public int getCountDistinctForColumn(int colIx)
   {
      return _colIx_distinctValuesData.get(colIx).size();
   }

   public boolean isNullsAdded()
   {
      return _nullsAdded;
   }

   /**
    * If there's just one column we add a count-column to show the how often the values occur.
    */
   public List<ColumnDisplayDefinition> maybeAppendCountColumn(List<ColumnDisplayDefinition> originalDisplayDefinitions)
   {
      if(1 != _colIx_distinctValuesData.size())
      {
         return originalDisplayDefinitions;
      }

      ArrayList<ColumnDisplayDefinition> ret = new ArrayList<>(originalDisplayDefinitions);
      final ColumnDisplayDefinition dispDef = new ColumnDisplayDefinition(200, s_stringMgr.getString("DistinctValuesHolder.count.column.name"));
      dispDef.setSqlType(Types.INTEGER);
      dispDef.setSqlTypeName("INTEGER");

      ret.add(dispDef);

      return ret;
   }
}
