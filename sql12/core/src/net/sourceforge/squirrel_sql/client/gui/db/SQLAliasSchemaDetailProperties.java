package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.Serializable;

public class SQLAliasSchemaDetailProperties implements Comparable<SQLAliasSchemaDetailProperties>, Serializable
{
   public static final int SCHEMA_LOADING_ID_LOAD_DONT_CACHE = 0;
   public static final int SCHEMA_LOADING_ID_LOAD_AND_CACHE = 1;
   public static final int SCHEMA_LOADING_ID_DONT_LOAD = 2;

   private String _schemaName;
   private int _table ;
   private int _view;
   private int _procedure;
   private int _UDT;
   private SQLAliasVersioner _versioner = new SQLAliasVersioner();

   public String getSchemaName()
   {
      return _schemaName;
   }

   public void setSchemaName(String schemaName)
   {
      _versioner.trigger(_schemaName, schemaName);
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
      // Not really exact because in Schema Properties UI changes aren't saved yet but should be good enough.
      _versioner.trigger(_table, id);
      _table = id;
   }

   public void setView(int id)
   {
      // Not really exact because in Schema Properties UI changes aren't saved yet but should be good enough.
      _versioner.trigger(_view, id);
      _view = id;
   }

   public void setProcedure(int id)
   {
      // Not really exact because in Schema Properties UI changes aren't saved yet but should be good enough.
      _versioner.trigger(_procedure, id);
      _procedure = id;
   }

   public int getUDT()
   {
      return _UDT;
   }

   public void setUDT(int UDT)
   {
      _UDT = UDT;
   }


   public int compareTo(SQLAliasSchemaDetailProperties other)
   {
      return _schemaName.compareTo(other._schemaName);
   }

   public void acceptAliasVersioner(SQLAliasVersioner versioner)
   {
      _versioner = versioner;
   }
}
