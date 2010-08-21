package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import java.util.List;

public class OverviewDataSet implements IDataSet
{
   private int _curIx = -1;
   private ColumnDisplayDefinition[] _columnDisplayDefinitions;
   private List<Object[]> _allRows;


   public OverviewDataSet(List<Object[]> allRows, ColumnDisplayDefinition[] columnDisplayDefinitions)
   {
      _allRows = allRows;
      _columnDisplayDefinitions = columnDisplayDefinitions;
   }

   public int getColumnCount() throws DataSetException
   {
      return _columnDisplayDefinitions.length;
   }

   public DataSetDefinition getDataSetDefinition() throws DataSetException
   {
      return new DataSetDefinition(_columnDisplayDefinitions);
   }

   public boolean next(IMessageHandler msgHandler) throws DataSetException
   {
      return ++_curIx < _allRows.size();
   }

   public Object get(int columnIndex) throws DataSetException
   {
      return _allRows.get(_curIx)[columnIndex];
   }
}