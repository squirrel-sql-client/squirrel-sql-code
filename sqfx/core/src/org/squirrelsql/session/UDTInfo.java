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


   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      UDTInfo udtInfo = (UDTInfo) o;

      if (_catalog != null ? !_catalog.equals(udtInfo._catalog) : udtInfo._catalog != null) return false;
      if (_schema != null ? !_schema.equals(udtInfo._schema) : udtInfo._schema != null) return false;
      return !(_name != null ? !_name.equals(udtInfo._name) : udtInfo._name != null);

   }

   @Override
   public int hashCode()
   {
      int result = _catalog != null ? _catalog.hashCode() : 0;
      result = 31 * result + (_schema != null ? _schema.hashCode() : 0);
      result = 31 * result + (_name != null ? _name.hashCode() : 0);
      return result;
   }
}
