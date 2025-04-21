package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.globalsearch.GlobalSearchType;

@FunctionalInterface
public interface DataSetViewerFindRemoteControl
{
   FirstSearchResult executeFindTillFirstResult(String textToSearch, GlobalSearchType globalSearchType);
}
