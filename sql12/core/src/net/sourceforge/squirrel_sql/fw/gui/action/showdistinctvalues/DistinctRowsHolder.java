package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;

public class DistinctRowsHolder
{
   private ArrayList<ExtTableColumn> _columnsToRespect;

   private ArrayList<Object[]> _distinctRowsInSourceTableOrder = new ArrayList<>();

   private HashMap<String, Object[]> _distinctCheckString_distincRow = new HashMap<>();

   public DistinctRowsHolder(ArrayList<ExtTableColumn> columnsToRespect)
   {
      _columnsToRespect = columnsToRespect;
   }

   public void addRowDistinct(Object[] row)
   {
      Object[] rowToRespect = new Object[_columnsToRespect.size() + 1]; // + 1 column for frequency

      String distinctCheckString = "";

      for (int i = 0; i < _columnsToRespect.size(); i++)
      {
         ExtTableColumn extTableColumn = _columnsToRespect.get(i);

         distinctCheckString += row[extTableColumn.getModelIndex()];

         rowToRespect[i] = row[extTableColumn.getModelIndex()];
      }

      Object[] representantRow = _distinctCheckString_distincRow.get(distinctCheckString);
      if( null == representantRow )
      {
         _distinctCheckString_distincRow.put(distinctCheckString, rowToRespect);
         _distinctRowsInSourceTableOrder.add(rowToRespect);
         rowToRespect[rowToRespect.length - 1] = 1;
      }
      else
      {
         representantRow[rowToRespect.length - 1] = ((Integer)representantRow[rowToRespect.length - 1]) + 1;
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
