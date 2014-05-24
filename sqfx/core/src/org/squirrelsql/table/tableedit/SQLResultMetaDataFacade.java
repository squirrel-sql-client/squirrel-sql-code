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

   public List<String> getColumnNames()
   {
      return _resultMetaDataTableLoader.getCellsAsString(ResultSetMetaDataLoaderConstants.GET_COLUMN_NAME.getMetaDataColumnName());
   }
}
