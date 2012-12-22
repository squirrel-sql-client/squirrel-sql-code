package net.sourceforge.squirrel_sql.fw.datasetviewer;


public class SimpleStatementCallback implements StatementCallback
{
   @Override
   public boolean isContinueReadActive()
   {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public int getFirstBlockCount()
   {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public int getContinueBlockCount()
   {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void closeStatementIfContinueReadActive()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public boolean isMaxRowsWasSet()
   {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public int getMaxRowsCount()
   {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
   }
}
