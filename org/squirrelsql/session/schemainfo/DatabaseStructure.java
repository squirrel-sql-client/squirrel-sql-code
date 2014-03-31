package org.squirrelsql.session.schemainfo;

import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.services.Utils;

import java.util.ArrayList;

public class DatabaseStructure extends StructItem
{
   private String _aliasName;

   public DatabaseStructure(String aliasName)
   {
      _aliasName = aliasName;
   }

   public ArrayList<StructItem> getLeaves()
   {
      ArrayList<StructItem> ret = new ArrayList<>();

      fillLeaves(ret);

      return ret;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DatabaseStructure that = (DatabaseStructure) o;

      if (_aliasName != null ? !_aliasName.equals(that._aliasName) : that._aliasName != null) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return _aliasName != null ? _aliasName.hashCode() : 0;
   }

   public <T> T visitTopToBottom(DatabaseStructureVisitor<T> databaseStructureVisitor, SchemaCacheConfig schemaCacheConfig)
   {
      T parent =  databaseStructureVisitor.visit(null, this);

      visit(databaseStructureVisitor, parent, schemaCacheConfig);

      return parent;
   }


   private <T extends StructItem> ArrayList<T> getStructItemsByType(final Class<T> structItemClass)
   {
      ArrayList<T> ret = new ArrayList<>();

      DatabaseStructureVisitor<Object> databaseStructureVisitor = new DatabaseStructureVisitor<Object>()
      {
         @Override
         public Object visit(Object resultOfParenVisit, StructItem structItem)
         {
            if(structItemClass.equals(structItem.getClass()))
            {
               ret.add((T) structItem);
            }
            return null;
         }
      };

      visitTopToBottom(databaseStructureVisitor, SchemaCacheConfig.LOAD_ALL);
      return ret;
   }

   public ArrayList<StructItemCatalog> getCatalogs()
   {
      return getStructItemsByType(StructItemCatalog.class);
   }

   public ArrayList<StructItemSchema> getSchemas()
   {
      return getStructItemsByType(StructItemSchema.class);
   }

   public StructItemCatalog getCatalogByName(String catalogName)
   {
      ArrayList<StructItemCatalog> catalogs = CollectionUtil.filter(getCatalogs(), (t) -> Utils.compareRespectEmpty(t.getCatalog(), catalogName));

      if(0 == catalogs.size())
      {
         return null;
      }

      return catalogs.get(0);
   }


   public ArrayList<StructItemSchema> getSchemasByName(String schemaName)
   {
      return CollectionUtil.filter(getSchemas(), (t) -> Utils.compareRespectEmpty(schemaName, t.getSchema()));
   }

   public ArrayList<StructItemSchema> getSchemaByNameAsArray(String catalogName, String schemaName)
   {
      return CollectionUtil.filter(getSchemas(), (t) -> Utils.compareRespectEmpty(catalogName, t.getCatalog()) && Utils.compareRespectEmpty(schemaName, t.getSchema()));
   }
}
