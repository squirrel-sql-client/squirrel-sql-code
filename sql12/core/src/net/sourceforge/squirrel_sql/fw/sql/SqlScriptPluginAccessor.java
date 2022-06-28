package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.ScriptPluginInterface;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SqlScriptPluginAccessor
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SqlScriptPluginAccessor.class);

   public static boolean startsWithSqlToFileMarker(QueryHolder sql)
   {
      ScriptPluginInterface si = (ScriptPluginInterface) Main.getApplication().getPluginManager().bindExternalPluginService("sqlscript", ScriptPluginInterface.class);

      if(null == si)
      {
         writeScriptPLuginMissingMessage();
         return false;
      }

      return si.startsWithSqlToFileMarker(sql.getQuery());
   }

   public static String formatTableName(ITableInfo tInfo)
   {
      ScriptPluginInterface si = (ScriptPluginInterface) Main.getApplication().getPluginManager().bindExternalPluginService("sqlscript", ScriptPluginInterface.class);

      if (null == si)
      {
         writeScriptPLuginMissingMessage();
         return tInfo.getSimpleName();
      }

      return si.formatTableName(tInfo);
   }

   private static void writeScriptPLuginMissingMessage()
   {
      String msg = s_stringMgr.getString("SqlScriptPluginAccessor.scriptPluginNeeded");
      Main.getApplication().getMessageHandler().showErrorMessage(msg);
   }

   public static boolean isQualifyTableRequired()
   {
      ScriptPluginInterface si = (ScriptPluginInterface) Main.getApplication().getPluginManager().bindExternalPluginService("sqlscript", ScriptPluginInterface.class);

      if (null == si)
      {
         writeScriptPLuginMissingMessage();
         return false;
      }
      return si.isQualifyTableRequired();
   }
}
