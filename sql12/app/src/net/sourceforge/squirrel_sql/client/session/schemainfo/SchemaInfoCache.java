package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

import java.util.*;

public class SchemaInfoCache
{
   final List catalogs = new ArrayList();
   final List schemas = new ArrayList();
   final HashMap cachedTableTypes = new HashMap();

   final TreeMap keywords = new TreeMap();
   final TreeMap dataTypes = new TreeMap();
   final Map functions = Collections.synchronizedMap(new TreeMap());
   final Map tableNames = Collections.synchronizedMap(new TreeMap());
   final Map columnNames = Collections.synchronizedMap(new TreeMap());
   final Map procedureNames = Collections.synchronizedMap(new TreeMap());

   final Map extendedColumnInfosByTableName = Collections.synchronizedMap(new TreeMap());
   final Map iTableInfos = Collections.synchronizedMap(new TreeMap());
   final Map iProcedureInfos = Collections.synchronizedMap(new TreeMap());

   final Hashtable tableInfosBySimpleName = new Hashtable();
   final Hashtable procedureInfosBySimpleName = new Hashtable();

   public SchemaInfoCache(SQLAlias alias)
   {
      //To change body of created methods use File | Settings | File Templates.
   }
}
