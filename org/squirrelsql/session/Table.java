package org.squirrelsql.session;

public class Table
{
   private final String _catalog;
   private final String _schema;
   private final String _tableType;
   private final String _name;

   public Table(String catalog, String schema, String tableType, String name)
   {
      _catalog = catalog;
      _schema = schema;
      _tableType = tableType;
      _name = name;
   }

   public String getCatalog()
   {
      return _catalog;
   }

   public String getSchema()
   {
      return _schema;
   }

   public String getTableType()
   {
      return _tableType;
   }

   public String getName()
   {
      return _name;
   }
}
