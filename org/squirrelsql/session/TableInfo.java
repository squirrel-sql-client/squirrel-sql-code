package org.squirrelsql.session;

public class TableInfo
{
   private final String _catalog;
   private final String _schema;
   private final String _tableType;
   private final String _name;
   private String _qualifiedName;

   public TableInfo(String catalog, String schema, String tableType, String name, String qualifiedName)
   {
      _catalog = catalog;
      _schema = schema;
      _tableType = tableType;
      _name = name;
      _qualifiedName = qualifiedName;
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

   public String getQualifiedName()
   {
      return _qualifiedName;
   }
}
