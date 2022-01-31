package net.sourceforge.squirrel_sql.client.session.objecttreesearch;

public class ObjectTreeSearchPartition
{
   private String catalog;
   private String schema;
   private String object;

   public ObjectTreeSearchPartition(String catalog, String schema, String object)
   {
      this.catalog = catalog;
      this.schema = schema;
      this.object = object;
   }

   public String getCatalog()
   {
      return catalog;
   }

   public String getSchema()
   {
      return schema;
   }

   public String getObject()
   {
      return object;
   }
}
