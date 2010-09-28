package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import java.util.ArrayList;

public interface AliasFinderListener
{
   void aliasesFound(ArrayList<AliasInfo> aliasInfos);
}
