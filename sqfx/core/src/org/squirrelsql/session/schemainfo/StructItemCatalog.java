package org.squirrelsql.session.schemainfo;

public class StructItemCatalog extends StructItem
{
   private String _catalog;

   public StructItemCatalog(String catalog)
   {
      _catalog = catalog;
   }

   public String getCatalog()
   {
      return _catalog;
   }


   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      StructItemCatalog that = (StructItemCatalog) o;

      if (_catalog != null ? !_catalog.equals(that._catalog) : that._catalog != null) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return _catalog != null ? _catalog.hashCode() : 0;
   }

   public String getItemName()
   {
      return "Catalog " + _catalog;
   }


}
