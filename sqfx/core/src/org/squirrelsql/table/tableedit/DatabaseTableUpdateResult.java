package org.squirrelsql.table.tableedit;

public class DatabaseTableUpdateResult
{
   private Object _interpretedNewValue = null;
   private int _updateCount = -1;
   private Throwable _updateError;

   public DatabaseTableUpdateResult(Object interpretedNewValue, int updateCount)
   {
      _interpretedNewValue = interpretedNewValue;
      _updateCount = updateCount;
   }

   public DatabaseTableUpdateResult(Exception e)
   {
      _updateError = e;
   }

   public Object getInterpretedNewValue()
   {
      return _interpretedNewValue;
   }

   public int getUpdateCount()
   {
      return _updateCount;
   }

   public Throwable getUpdateError()
   {
      return _updateError;
   }
}
