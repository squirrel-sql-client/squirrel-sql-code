package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class CatalogFilterMatcher extends FilterMatcher
{
   public CatalogFilterMatcher(SessionProperties properties)
   {
      super(properties.getCatalogFilterInclude(), properties.getCatalogFilterExclude());
   }
}
