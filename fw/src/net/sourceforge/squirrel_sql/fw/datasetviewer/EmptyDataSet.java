package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class EmptyDataSet implements IDataSet
{


   public int getColumnCount() throws DataSetException
   {
      return 0;
   }

   public DataSetDefinition getDataSetDefinition() throws DataSetException
   {
      return new DataSetDefinition(new ColumnDisplayDefinition[0]);
   }

   public boolean next(IMessageHandler msgHandler) throws DataSetException
   {
      return false;
   }

   public Object get(int columnIndex) throws DataSetException
   {
      throw new IndexOutOfBoundsException("An EmptyDataSet does not have colums");
   }
}
