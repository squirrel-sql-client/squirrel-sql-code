package org.squirrelsql.session.parser;

import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.parser.kernel.SQLSchema;

import java.util.ArrayList;
import java.util.List;

public class SQLSchemaImpl implements SQLSchema
{
   private Session _session;

   SQLSchemaImpl(Session session)
	{
      _session = session;
   }

   public Table getTable(String catalog, String schema, String name)
   {
      List<TableInfo> tablesBySimpleName = _session.getSchemaCache().getTablesBySimpleName(name);
      if (0 < tablesBySimpleName.size())
      {
         return new Table(catalog, schema, name, _session);
      }
      return null;
   }

   public List<Table> getTables(String catalog, String schema, String name)
   {
      List<TableInfo> tableNames = _session.getSchemaCache().getTablesByFullyQualifiedName(catalog, schema, name);

      List<Table> ret = new ArrayList<>();

      for (TableInfo tableInfo : tableNames)
      {
         Table buf = new Table(catalog, schema, tableInfo.getName(), _session);
         ret.add(buf);
      }
      return ret;
   }

	public Table getTableForAlias(String alias)
	{
		return null;
	}
}
