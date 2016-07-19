package net.sourceforge.squirrel_sql.client.util.codereformat;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum ColumnListSpiltMode
{
   ALLOW_SPLIT("ColumnListSpiltMode.i18nkey.allow.spilt"),
   REQUIRE_SPLIT("ColumnListSpiltMode.i18nkey.require.spilt"),
   DISALLOW_SPLIT("ColumnListSpiltMode.i18nkey.disallow.spilt");

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColumnListSpiltMode.class);
   private String _i18nKey;


   ColumnListSpiltMode(String i18nKey)
   {
      _i18nKey = i18nKey;
   }


   @Override
   public String toString()
   {
      return s_stringMgr.getString(_i18nKey);
   }
}
