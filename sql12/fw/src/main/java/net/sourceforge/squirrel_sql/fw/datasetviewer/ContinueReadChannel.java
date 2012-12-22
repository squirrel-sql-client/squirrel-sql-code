package net.sourceforge.squirrel_sql.fw.datasetviewer;

public interface ContinueReadChannel
{
   public void readMoreResults();

   void closeStatementAndResultSet();
}
