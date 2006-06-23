package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

public interface IAllowedSchemaChecker
{
   /**
    * There is no need to cache allowed Schemas in a Plugin. 
    * Session Manager already does this.
    */
   String[] getAllowedSchemas(SQLConnection con, SQLAlias alias);
}
