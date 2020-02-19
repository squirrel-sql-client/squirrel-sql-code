package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import java.util.List;

public class SimpleDataSet implements IDataSet
{
   private int _curIx = -1;
   private ColumnDisplayDefinition[] _columnDisplayDefinitions;
   private List<Object[]> _allRows;


   public SimpleDataSet(List<Object[]> allRows, ColumnDisplayDefinition[] columnDisplayDefinitions)
   {
      _allRows = allRows;
      _columnDisplayDefinitions = columnDisplayDefinitions;
   }

   public int getColumnCount()
   {
      return _columnDisplayDefinitions.length;
   }

   public DataSetDefinition getDataSetDefinition()
   {
      return new DataSetDefinition(_columnDisplayDefinitions);
   }

   public boolean next(IMessageHandler msgHandler)
   {
      return ++_curIx < _allRows.size();
   }

   public Object get(int columnIndex)
   {
      return _allRows.get(_curIx)[columnIndex];
   }
}