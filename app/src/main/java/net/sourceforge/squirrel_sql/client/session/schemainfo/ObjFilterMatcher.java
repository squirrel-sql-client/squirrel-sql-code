package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class ObjFilterMatcher extends FilterMatcher
{
   public ObjFilterMatcher(SessionProperties properties)
   {
      super(properties.getObjectFilterInclude(), properties.getObjectFilterExclude());
   }

   /**
    * Means simpleObjectName in match() must exactly match this simpleObjectName  
    */
   public ObjFilterMatcher(String simpleObjectName)
   {
      super(simpleObjectName, null);
   }

   /**
    * Means every object name matches.
    */
   public ObjFilterMatcher()
   {
      super(null, null);
   }
}
