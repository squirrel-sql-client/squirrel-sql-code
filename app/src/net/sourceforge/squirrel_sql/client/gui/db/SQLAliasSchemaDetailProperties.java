package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.Serializable;

public class SQLAliasSchemaDetailProperties implements Comparable, Serializable
{
   public static final int SCHEMA_LOADING_ID_LOAD_DONT_CACHE = 0;
   public static final int SCHEMA_LOADING_ID_LOAD_AND_CACHE = 1;
   public static final int SCHEMA_LOADING_ID_DONT_LOAD = 2;

   private String _schemaName;
   private int _table ;
   private int _view;
   private int _procedure;

   public String getSchemaName()
   {
      return _schemaName;
   }

   public void setSchemaName(String schemaName)
   {
      _schemaName = schemaName;
   }


   public int getTable()
   {
      return _table;
   }

   public int getView()
   {
      return _view;
   }

   public int getProcedure()
   {
      return _procedure;
   }

   public void setTable(int id)
   {
      _table = id;
   }

   public void setView(int id)
   {
      _view = id;
   }

   public void setProcedure(int id)
   {
      _procedure = id;
   }

   public int compareTo(Object other)
   {
      SQLAliasSchemaDetailProperties buf = (SQLAliasSchemaDetailProperties) other;
      return _schemaName.compareTo(buf._schemaName);
   }
}
