package net.sourceforge.squirrel_sql.client.session.parser;

import net.sourceforge.squirrel_sql.client.session.SchemaInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;

import java.util.List;
import java.util.Vector;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class SQLSchemaImpl implements SQLSchema
{
   private ISession _session;

   SQLSchemaImpl(ISession session)
	{
      _session = session;
	}

	public SQLSchema.Table getTable(String catalog, String schema, String name)
	{
      try
      {
         String[] tableNames = _session.getSchemaInfo().getTables();

         for (int i = 0; i < tableNames.length; i++)
         {
            if(tableNames[i].equalsIgnoreCase(name))
            {
               return new SQLSchema.Table(null, null, tableNames[i], _session.getSQLConnection().getConnection().getMetaData());
            }
         }
         return null;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

	public List getTables(String catalog, String schema, String name)
	{
      try
      {
         Vector ret = new Vector();
         String[] tableNames = _session.getSchemaInfo().getTables();

         for (int i = 0; i < tableNames.length; i++)
         {
            ret.add(new SQLSchema.Table(null, null, tableNames[i], _session.getSQLConnection().getConnection().getMetaData()));
         }
         return ret;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

	public SQLSchema.Table getTableForAlias(String alias)
	{
		return null;
	}
}
