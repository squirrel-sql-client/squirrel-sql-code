package org.squirrelsql.session.completion;

import org.squirrelsql.session.schemainfo.StructItemCatalog;

/**
 * Created by gerd on 16.02.14.
 */
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
}
