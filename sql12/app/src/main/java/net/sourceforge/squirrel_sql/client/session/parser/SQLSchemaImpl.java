package net.sourceforge.squirrel_sql.client.session.parser;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

public class SQLSchemaImpl implements SQLSchema
{
   private ISession _session;
   private Hashtable<String, SQLSchema.Table> _tableCache = new Hashtable<String, Table>();
   private SQLDatabaseMetaData _dmd;

   SQLSchemaImpl(ISession session)
	{
         _session = session;
         if (_session != null) {
             _session.getApplication().getThreadPool().addTask(new Runnable() {
                 public void run() {
                     ISQLConnection con = _session.getSQLConnection();
                     if (con != null) {
                         _dmd = con.getSQLMetaData();
                     }
                 }
             });             
         }
   }

	public SQLSchema.Table getTable(String catalog, String schema, String name)
	{
      if(_session.getSchemaInfo().isTable(name))
      {
         String key = getKey(catalog, schema, name);
         SQLSchema.Table ret = _tableCache.get(key);
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

    public List<Table> getTables(String catalog, String schema, String name)
	{
        Vector<Table> ret = new Vector<Table>();
        String[] tableNames = _session.getSchemaInfo().getTables();
        
        for (int i = 0; i < tableNames.length; i++)
        {
            String key = getKey(catalog, schema, name);
            SQLSchema.Table buf = _tableCache.get(key);
            if(null == buf)
            {
                buf = new SQLSchema.Table(catalog, schema, tableNames[i], _dmd); 
                _tableCache.put(key, buf);
            }
            ret.add(buf);
        }
        return ret;
    }

	public SQLSchema.Table getTableForAlias(String alias)
	{
		return null;
	}
}
