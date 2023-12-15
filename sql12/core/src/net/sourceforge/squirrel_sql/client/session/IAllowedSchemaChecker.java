package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

@FunctionalInterface
public interface IAllowedSchemaChecker
{
   /**
    * There is no need to cache allowed Schemas in a Plugin. 
    * Session Manager already does this.
    */
   String[] getAllowedSchemas(ISQLConnection con, SQLAlias alias);
}
