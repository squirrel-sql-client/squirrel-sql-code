package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DistinctRowsHolder
{
   private ArrayList<ExtTableColumn> _columnsToRespect;

   private ArrayList<Object[]> _distinctRowsInSourceTableOrder = new ArrayList<>();

   private HashSet<String> _distinctCheck = new HashSet<>();

   public DistinctRowsHolder(ArrayList<ExtTableColumn> columnsToRespect)
   {
      _columnsToRespect = columnsToRespect;
   }

   public void addRowDistinct(Object[] row)
   {
      Object[] rowToRespect = new Object[_columnsToRespect.size()];

      String distinctCheckString = "";

      for (int i = 0; i < _columnsToRespect.size(); i++)
      {
         ExtTableColumn extTableColumn = _columnsToRespect.get(i);

         distinctCheckString += row[extTableColumn.getModelIndex()];

         rowToRespect[i] = row[extTableColumn.getModelIndex()];
      }

      if(false == _distinctCheck.contains(distinctCheckString))
      {
         _distinctCheck.add(distinctCheckString);
         _distinctRowsInSourceTableOrder.add(rowToRespect);
      }

   }

   public List<Object[]> getDistinctRows()
   {
      return _distinctRowsInSourceTableOrder;
   }

   public int getDistinctRowsCount()
   {
      return _distinctRowsInSourceTableOrder.size();
   }
}
