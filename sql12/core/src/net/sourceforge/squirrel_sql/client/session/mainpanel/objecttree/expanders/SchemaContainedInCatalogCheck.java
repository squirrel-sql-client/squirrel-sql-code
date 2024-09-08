package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLSchema;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.List;

public class SchemaContainedInCatalogCheck
{
   private final boolean _supportsCatalogs;
   private final SQLDatabaseMetaData _md;
   private List<SQLSchema> _sqlSchemas;

   public SchemaContainedInCatalogCheck(boolean supportsCatalogs, SQLDatabaseMetaData md)
   {
      _supportsCatalogs = supportsCatalogs;
      _md = md;
   }

   public boolean containedInCatalog(String catalogName, String schemaName)
   {
      try
      {
         if(      false == _supportsCatalogs
               || StringUtilities.isEmpty(catalogName, true)
               || StringUtilities.isEmpty(schemaName, true))
         {
            return true;
         }

         if(null == _sqlSchemas)
         {
            _sqlSchemas = _md.getSchemas();
         }

         for(SQLSchema sqlSchema : _sqlSchemas)
         {
            if( StringUtils.equalsIgnoreCase(sqlSchema.getSchema(), schemaName))
            {
               // For MSSQL Server java.sql.DatabaseMetaData.getSchemas()
               // returns null catalogs for Schemas which belong to all catalogs.
               if(StringUtilities.isEmpty(sqlSchema.getCatalog(), true))
               {
                  return true;
               }

               if(StringUtils.equalsIgnoreCase(catalogName, sqlSchema.getCatalog()))
               {
                  return true;
               }
            }
         }

         return false;
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
