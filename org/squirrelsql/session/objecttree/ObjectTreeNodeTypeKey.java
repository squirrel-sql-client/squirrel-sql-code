package org.squirrelsql.session.objecttree;

public class ObjectTreeNodeTypeKey
{
   public static final ObjectTreeNodeTypeKey ALIAS_TYPE_KEY = new ObjectTreeNodeTypeKey("ALIAS_TYPE_KEY");
   public static final ObjectTreeNodeTypeKey CATALOG_TYPE_KEY = new ObjectTreeNodeTypeKey("CATALOG_TYPE_KEY");
   public static final ObjectTreeNodeTypeKey SCHEMA_TYPE_KEY = new ObjectTreeNodeTypeKey("SCHEMA_TYPE_KEY");
   public static final ObjectTreeNodeTypeKey TABLE_TYPE_KEY = new ObjectTreeNodeTypeKey("TABLE_TYPE_KEY");
   public static final ObjectTreeNodeTypeKey PROCEDURE_TYPE_KEY = new ObjectTreeNodeTypeKey("PROCEDURE_TYPE_KEY");
   public static final ObjectTreeNodeTypeKey UDT_TYPE_KEY = new ObjectTreeNodeTypeKey("UDT_TYPE_KEY");


   private String _key;

   public ObjectTreeNodeTypeKey(String key)
   {
      _key = key;
   }

   public String getKey()
   {
      return _key;
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
