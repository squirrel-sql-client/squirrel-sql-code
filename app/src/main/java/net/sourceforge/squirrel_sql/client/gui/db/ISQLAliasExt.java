package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

public interface ISQLAliasExt extends ISQLAlias
{
   SQLAliasSchemaProperties getSchemaProperties();
   void setSchemaProperties(SQLAliasSchemaProperties schemaProperties);
   
   SQLAliasColorProperties getColorProperties();
   void setColorProperties(SQLAliasColorProperties colorProperties);
   
   SQLAliasConnectionProperties getConnectionProperties();
   void setConnectionProperties(SQLAliasConnectionProperties connectionProperties);
   
}
