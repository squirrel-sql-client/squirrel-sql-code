package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class TableTypeFilterMatcher extends FilterMatcher
{
   public TableTypeFilterMatcher(SessionProperties properties)
   {
      super(properties.getTableTypeFilterInclude(), properties.getTableTypeFilterExclude());
   }

   /**
    * Means simpleObjectName in match() must exactly match this simpleObjectName
    */
   public TableTypeFilterMatcher(String simpleObjectName)
   {
      super(simpleObjectName, null);
   }

   /**
    * Means every object name matches.
    */
   public TableTypeFilterMatcher()
   {
      super(null, null);
   }
}
