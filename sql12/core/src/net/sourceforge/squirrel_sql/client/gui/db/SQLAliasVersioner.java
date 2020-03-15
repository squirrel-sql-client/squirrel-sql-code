package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.Serializable;

public class SQLAliasVersioner implements Serializable
{
   private SQLAlias _sqlAlias;

   public SQLAliasVersioner(SQLAlias sqlAlias)
   {
      _sqlAlias = sqlAlias;
   }

   /**
    * This creates a dummy versioner to allow callers the convenience not to check nulls.
    */
   public SQLAliasVersioner()
   {
   }

   public void trigger(Object oldVal, Object newVal)
   {
      if(null == _sqlAlias || false == Main.getApplication().getGlobalSQLAliasVersioner().isEnabled())
      {
         return;
      }

      if(false == Utilities.equalsRespectNull(oldVal, newVal))
      {
         _sqlAlias.setAliasVersionTimeMills(System.currentTimeMillis());
      }
   }

   public void updateVersion()
   {
      if(null == _sqlAlias || false == Main.getApplication().getGlobalSQLAliasVersioner().isEnabled())
      {
         return;
      }

      _sqlAlias.setAliasVersionTimeMills(System.currentTimeMillis());
   }


   public Java8CloseableFix switchOff()
   {
      return Main.getApplication().getGlobalSQLAliasVersioner().switchOff();
   }

}
