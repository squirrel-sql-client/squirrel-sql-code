package net.sourceforge.squirrel_sql.client.preferences.themes;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum ThemesEnum
{
   LIGH("ThemesEnum.light"),
   DARK("ThemesEnum.dark");

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ThemesEnum.class);
   private final String _title;

   ThemesEnum(String titleI18nKey)
   {
      _title = titleI18nKey;
   }


   @Override
   public String toString()
   {
      return s_stringMgr.getString(_title);
   }
}
