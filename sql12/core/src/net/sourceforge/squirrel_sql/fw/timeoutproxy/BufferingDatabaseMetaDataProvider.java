package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class BufferingDatabaseMetaDataProvider implements DatabaseMetaDataProvider
{
   private DatabaseMetaDataProvider _metaDataProvider;
   private DatabaseMetaData _metaData;

   public BufferingDatabaseMetaDataProvider(DatabaseMetaDataProvider metaDataProvider)
   {
      _metaDataProvider = metaDataProvider;
   }

   @Override
   public DatabaseMetaData getDataBaseMetaData()
   {
      try
      {
         if(null == _metaData)
         {
            _metaData = _metaDataProvider.getDataBaseMetaData();
         }
         return _metaData;
      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
