package net.sourceforge.squirrel_sql.plugins.oracle;

public class OracleGlobalPrefs
{
   private boolean _loadSysSchema = false;

   public boolean isLoadSysSchema()
   {
      return _loadSysSchema;
   }

   public void setLoadSysSchema(boolean loadSysSchema)
   {
      this._loadSysSchema = loadSysSchema;
   }
}
