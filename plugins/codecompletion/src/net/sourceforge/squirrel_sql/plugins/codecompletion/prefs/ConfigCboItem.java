package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

class ConfigCboItem
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(ConfigCboItem.class);


   static final ConfigCboItem[] items = new ConfigCboItem[]
   {
      new ConfigCboItem(CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS),
      new ConfigCboItem(CodeCompletionPreferences.CONFIG_SP_WITHOUT_PARARMS),
      new ConfigCboItem(CodeCompletionPreferences.CONFIG_UDF_WITH_PARARMS),
      new ConfigCboItem(CodeCompletionPreferences.CONFIG_UDF_WITHOUT_PARARMS)
   };

   static ConfigCboItem getItemForConfig(int completionConfig)
   {
      switch(completionConfig)
      {
         case CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS:
            return items[0];
         case CodeCompletionPreferences.CONFIG_SP_WITHOUT_PARARMS:
            return items[1];
         case CodeCompletionPreferences.CONFIG_UDF_WITH_PARARMS:
            return items[2];
         case CodeCompletionPreferences.CONFIG_UDF_WITHOUT_PARARMS:
            return items[3];
         default:
            throw new IllegalArgumentException("Unknown completionConfig " + completionConfig);
      }
   }



   private String _toString;

   private int _completionConfig;

   private ConfigCboItem(int completionConfig)
   {
      _completionConfig = completionConfig;
      switch(completionConfig)
      {
         case CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS:
            // i18n[codecompletion.prefs.table.spWithParams=SP with params]
            _toString = s_stringMgr.getString("codecompletion.prefs.table.spWithParams");
            break;
         case CodeCompletionPreferences.CONFIG_SP_WITHOUT_PARARMS:
            // i18n[codecompletion.prefs.table.spWithoutParams=SP without params]
            _toString = s_stringMgr.getString("codecompletion.prefs.table.spWithoutParams");
            break;
         case CodeCompletionPreferences.CONFIG_UDF_WITH_PARARMS:
            // i18n[codecompletion.prefs.table.udfWithParams=UDF with params]
            _toString = s_stringMgr.getString("codecompletion.prefs.table.udfWithParams");
            break;
         case CodeCompletionPreferences.CONFIG_UDF_WITHOUT_PARARMS:
            // i18n[codecompletion.prefs.table.udfWithoutParams=UDF without params]
            _toString = s_stringMgr.getString("codecompletion.prefs.table.udfWithoutParams");
            break;
      }
   }

   public String toString()
   {
      return _toString;
   }

   public int getCompletionConfig()
   {
      return _completionConfig;
   }
}
