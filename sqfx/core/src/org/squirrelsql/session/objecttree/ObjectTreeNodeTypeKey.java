package org.squirrelsql.session.objecttree;

import org.squirrelsql.services.DatabaseObjectType;

public class ObjectTreeNodeTypeKey
{
   public static final ObjectTreeNodeTypeKey ALIAS_TYPE_KEY = new ObjectTreeNodeTypeKey("ALIAS_TYPE_KEY", null);
   public static final ObjectTreeNodeTypeKey CATALOG_TYPE_KEY = new ObjectTreeNodeTypeKey("CATALOG_TYPE_KEY", null);
   public static final ObjectTreeNodeTypeKey SCHEMA_TYPE_KEY = new ObjectTreeNodeTypeKey("SCHEMA_TYPE_KEY", null);
   public static final ObjectTreeNodeTypeKey TABLE_TYPE_TYPE_KEY = new ObjectTreeNodeTypeKey("TABLE_TYPE_TYPE_KEY", DatabaseObjectType.TABLE);
   public static final ObjectTreeNodeTypeKey PROCEDURE_TYPE_KEY = new ObjectTreeNodeTypeKey("PROCEDURE_TYPE_KEY", DatabaseObjectType.PROCEDURE);
   public static final ObjectTreeNodeTypeKey UDT_TYPE_KEY = new ObjectTreeNodeTypeKey("UDT_TYPE_KEY", DatabaseObjectType.USER_DEFINED_TYPE);
   public static final ObjectTreeNodeTypeKey TABLE_TYPE_KEY = new ObjectTreeNodeTypeKey("TABLE_TYPE_KEY", null);


   private String _key;
   private DatabaseObjectType _databaseObjectType;

   public ObjectTreeNodeTypeKey(String key, DatabaseObjectType databaseObjectType)
   {
      _key = key;
      _databaseObjectType = databaseObjectType;
   }

   public String getKey()
   {
      return _key;
   }

   public DatabaseObjectType getDatabaseObjectType()
   {
      return _databaseObjectType;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ObjectTreeNodeTypeKey that = (ObjectTreeNodeTypeKey) o;

      if (!_key.equals(that._key)) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return _key.hashCode();
   }
}
