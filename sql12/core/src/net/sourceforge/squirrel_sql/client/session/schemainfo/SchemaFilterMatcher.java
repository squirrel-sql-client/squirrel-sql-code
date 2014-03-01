package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class SchemaFilterMatcher extends FilterMatcher
{
   public SchemaFilterMatcher(SessionProperties sessionProperties)
   {
      super(sessionProperties.getSchemaFilterInclude(), sessionProperties.getSchemaFilterExclude());
   }

}
