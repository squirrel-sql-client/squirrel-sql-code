package net.sourceforge.squirrel_sql.client.gui.db;

public class GlobalSQLAliasVersioner
{
   private boolean _enabled;

   public Java8CloseableFix switchOff()
   {
      _enabled = false;

      return () -> _enabled = true;
   }

   public boolean isEnabled()
   {
      return _enabled;
   }
}
