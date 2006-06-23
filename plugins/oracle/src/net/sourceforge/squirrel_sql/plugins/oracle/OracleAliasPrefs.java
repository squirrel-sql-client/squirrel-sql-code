package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.io.Serializable;

public class OracleAliasPrefs implements Serializable
{
   private boolean _loadAccessibleSchemasExceptSYS = true;
   private boolean _loadAccessibleSchemasAndSYS = false;
   private boolean _loadAllSchemas = false;
   private IIdentifier _aliasIdentifier;

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

   public IIdentifier getAliasIdentifier()
   {
      return _aliasIdentifier;
   }

   public void setAliasIdentifier(IIdentifier iIdentifier)
   {
      _aliasIdentifier = iIdentifier;
   }

}
