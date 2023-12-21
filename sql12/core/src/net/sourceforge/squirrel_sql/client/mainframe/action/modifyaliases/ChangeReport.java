package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

public class ChangeReport
{
   private StringBuilder _changes = new StringBuilder();


   public int length()
   {
      return _changes.length();
   }

   public ChangeReport append(String msg)
   {
      _changes.append(msg);
      return this;
   }

   public ChangeReport append(char c)
   {
      _changes.append(c);
      return this;
   }

   public String getString()
   {
      return _changes.toString();
   }
}
