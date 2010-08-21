package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import java.util.ArrayList;

public interface AliasFinderListener
{
   void aliasesFound(ArrayList<AliasInfo> aliasInfos);
}
