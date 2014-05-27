package org.squirrelsql.table.tableedit;

import org.squirrelsql.table.ResultSetMetaDataLoaderConstants;
import org.squirrelsql.table.TableLoader;

import java.util.List;

public class SQLResultMetaDataFacade
{
   private TableLoader _resultMetaDataTableLoader;

   public SQLResultMetaDataFacade(TableLoader resultMetaDataTableLoader)
   {
      _resultMetaDataTableLoader = resultMetaDataTableLoader;
   }

   public String getColumnNameAt(int resultColIx)
   {
      return getColumnNames().get(resultColIx);
   }

   public String getColumnClassNameAt(int resultColIx)
   {
      return _getMetaDataCells(ResultSetMetaDataLoaderConstants.GET_COLUMN_CLASS_NAME).get(resultColIx);
   }


   public List<String> getColumnNames()
   {
      return _getMetaDataCells(ResultSetMetaDataLoaderConstants.GET_COLUMN_NAME);
   }

   private List<String> _getMetaDataCells(ResultSetMetaDataLoaderConstants resultSetMetaDataLoaderConstant)
   {
      return _resultMetaDataTableLoader.getCellsAsString(resultSetMetaDataLoaderConstant.getMetaDataColumnName());
   }

   public int getSqlTypeAt(int editColIx)
   {
      return Integer.parseInt(_getMetaDataCells(ResultSetMetaDataLoaderConstants.GET_COLUMN_TYPE).get(editColIx));
   }
}
