package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.globalsearch.GlobalSearchType;

@FunctionalInterface
public interface GlobalFindRemoteControl
{
   default FirstSearchResult executeFindTillFirstResult(String textToSearch, GlobalSearchType globalSearchType)
   {
      return executeFindTillFirstResult(textToSearch, globalSearchType, false);
   }

   FirstSearchResult executeFindTillFirstResult(String textToSearch, GlobalSearchType globalSearchType, boolean highlightAll);
}
