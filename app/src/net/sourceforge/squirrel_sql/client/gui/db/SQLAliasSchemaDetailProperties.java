package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.Serializable;

public class SQLAliasSchemaDetailProperties 
        implements Comparable<SQLAliasSchemaDetailProperties>, Serializable
{
   private static final long serialVersionUID = 1L;
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

   public int compareTo(SQLAliasSchemaDetailProperties other)
   {
      return _schemaName.compareTo(other._schemaName);
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
       final int prime = 31;
       int result = 1;
       result = prime * result
       + ((_schemaName == null) ? 0 : _schemaName.hashCode());
       return result;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
       if (this == obj)
           return true;
       if (obj == null)
           return false;
       if (getClass() != obj.getClass())
           return false;
       final SQLAliasSchemaDetailProperties other = (SQLAliasSchemaDetailProperties) obj;
       if (_schemaName == null) {
           if (other._schemaName != null)
               return false;
       } else if (!_schemaName.equals(other._schemaName))
           return false;
       return true;
   }
   
   
}
