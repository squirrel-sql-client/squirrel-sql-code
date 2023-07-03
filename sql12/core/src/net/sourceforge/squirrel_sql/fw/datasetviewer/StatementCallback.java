package net.sourceforge.squirrel_sql.fw.datasetviewer;


import net.sourceforge.squirrel_sql.client.session.ISession;

public interface StatementCallback
{
   boolean isContinueReadActive();

   int getFirstBlockCount();

   int getContinueBlockCount();

   void closeStatementIfContinueReadActive();

   boolean isMaxRowsWasSet();

   int getMaxRowsCount();

   ISession getSession();
}
