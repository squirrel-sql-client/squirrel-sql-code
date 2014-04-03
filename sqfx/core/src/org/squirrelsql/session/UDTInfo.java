package org.squirrelsql.session;

public class UDTInfo
{
   private final String _catalog;
   private final String _schema;
   private final String _name;
   private final int _dataType;

   public UDTInfo(String catalog, String schema, String name, int dataType)
   {
      _catalog = catalog;
      _schema = schema;
      _name = name;
      _dataType = dataType;
   }

   public String getCatalog()
   {
      return _catalog;
   }

   public String getSchema()
   {
      return _schema;
   }

   public String getName()
   {
      return _name;
   }

   public int getDataType()
   {
      return _dataType;
   }
}
