package net.sourceforge.squirrel_sql.plugins.oracle;

public class OracleGlobalPrefs
{
   private boolean _loadAccessibleSchemasExceptSYS = true;
   private boolean _loadAccessibleSchemasAndSYS = false;
   private boolean _loadAllSchemas = false;

   public boolean isLoadAccessibleSchemasExceptSYS()
   {
      return _loadAccessibleSchemasExceptSYS;
   }

   public void setLoadAccessibleSchemasExceptSYS(boolean loadAccessibleSchemasExceptSYS)
   {
      this._loadAccessibleSchemasExceptSYS = loadAccessibleSchemasExceptSYS;
   }

   public boolean isLoadAccessibleSchemasAndSYS()
   {
      return _loadAccessibleSchemasAndSYS;
   }

   public void setLoadAccessibleSchemasAndSYS(boolean loadAccessibleSchemasAndSYS)
   {
      _loadAccessibleSchemasAndSYS = loadAccessibleSchemasAndSYS;
   }

   public boolean isLoadAllSchemas()
   {
      return _loadAllSchemas;
   }

   public void setLoadAllSchemas(boolean loadAllSchemas)
   {
      _loadAllSchemas = loadAllSchemas;
   }
}
