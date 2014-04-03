package org.squirrelsql.session.completion;

import org.squirrelsql.session.schemainfo.StructItemCatalog;

public class CatalogCompletionCandidate extends CompletionCandidate
{
   private StructItemCatalog _catalog;

   public CatalogCompletionCandidate(StructItemCatalog catalog)
   {
      _catalog = catalog;
   }

   @Override
   public String getReplacement()
   {
      return _catalog.getCatalog();
   }

   @Override
   public String getObjectTypeName()
   {
      return "CATALOG";
   }
}
