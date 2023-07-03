package net.sourceforge.squirrel_sql.fw.datasetviewer;

public class ResultSetReadState
{
   private int _countRowsRead = 0;
   private boolean _resNextHasReturnedFalse;
   private boolean _isAtLastResultRow;

   public int getCountRowsRead()
   {
      return _countRowsRead;
   }

   public void setCountRowsRead(int countRowsRead)
   {
      _countRowsRead = countRowsRead;
   }

   public boolean isResNextHasReturnedFalse()
   {
      return _resNextHasReturnedFalse;
   }

   public void setResNextHasReturnedFalse(boolean resNextHasReturnedFalse)
   {
      _resNextHasReturnedFalse = resNextHasReturnedFalse;
   }

   public void incCountRowsRead()
   {
      ++_countRowsRead;
   }

   public void setWasLastResultRowRead(boolean isAtLastResultRow)
   {
      _isAtLastResultRow = isAtLastResultRow;
   }

   public boolean wasLastResultRowRead()
   {
      return _isAtLastResultRow;
   }
}
