package net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab;

public class AdditionalSQLTabCounter
{
   private int _currentNumber;

   public int nextNumber()
   {
      return ++_currentNumber;
   }
}
