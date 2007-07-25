package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

public interface MappingInfoProvider
{
   // Will be called asynchronously to the event dispatch thread
   MappedClassInfo getMappedClassInfoFor(String token);

   boolean mayBeClassOrAliasName(String token);
}
