package net.sourceforge.squirrel_sql.client.session.parser;

import net.sourceforge.squirrel_sql.client.session.SchemaInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;

import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.util.BaseRuntimeException;

public class SQLSchemaImpl implements SQLSchema
{
   private ISession _session;
   private Hashtable _tableCache = new Hashtable();
   private DatabaseMetaData _dmd;

   SQLSchemaImpl(ISession session)
	{
      try
      {
         _session = session;
         _dmd = _session.getSQLConnection().getConnection().getMetaData();
      }
      catch (SQLException e)
      {
         throw new BaseRuntimeException(e);
      }
   }

	public SQLSchema.Table getTable(String catalog, String schema, String name)
	{
      if(_session.getSchemaInfo().isTable(name))
      {
         String key = getKey(catalog, schema, name);
         SQLSchema.Table ret = (SQLSchema.Table) _tableCache.get(key);
         if(null == ret)
         {
            ret = new SQLSchema.Table(catalog, schema, name, _dmd);
            _tableCache.put(key, ret);
         }
         return ret;
      }
      return null;
   }

   private String getKey(String catalog, String schema, String name)
   {
      if(null == catalog)
      {
         catalog = "null";
      }
      if(null == schema)
      {
         schema = "null";
      }

      StringBuffer ret = new StringBuffer();
      ret.append(catalog).append(",").append(schema).append(",").append(name);

      return ret.toString();
   }

   public List getTables(String catalog, String schema, String name)
	{
      try
      {
         Vector ret = new Vector();
         String[] tableNames = _session.getSchemaInfo().getTables();

         for (int i = 0; i < tableNames.length; i++)
         {
            String key = getKey(catalog, schema, name);
            SQLSchema.Table buf = (SQLSchema.Table) _tableCache.get(key);
            if(null == buf)
            {
               buf = new SQLSchema.Table(catalog, schema, tableNames[i], _session.getSQLConnection().getConnection().getMetaData());
               _tableCache.put(key, buf);
            }
            ret.add(buf);
         }
         return ret;
      }
      catch (SQLException e)
      {
         throw new BaseRuntimeException(e);
      }
   }

	public SQLSchema.Table getTableForAlias(String alias)
	{
		return null;
	}
}
