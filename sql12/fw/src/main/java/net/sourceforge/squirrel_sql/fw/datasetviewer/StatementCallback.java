package net.sourceforge.squirrel_sql.fw.datasetviewer;


public interface StatementCallback
{
   boolean isContinueReadActive();

   int getFirstBlockCount();

   int getContinueBlockCount();

   void closeStatementIfContinueReadActive();

   boolean isMaxRowsWasSet();

   int getMaxRowsCount();
}
