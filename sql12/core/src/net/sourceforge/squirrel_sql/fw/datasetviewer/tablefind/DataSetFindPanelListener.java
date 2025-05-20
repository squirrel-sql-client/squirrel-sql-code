package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

public interface DataSetFindPanelListener
{
   void hideFindPanel();

   void matchFound(String currentSearchString, DataSetSearchMatchType selectedMatchType, boolean caseSensitive);
}
