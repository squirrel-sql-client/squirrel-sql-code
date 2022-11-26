package net.sourceforge.squirrel_sql.client.preferences.themes;

import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum ThemesEnum
{
   LIGH("ThemesEnum.light"),
   DARK("ThemesEnum.dark");

   public static final String PREF_KEY_THEMESCONTROLLER_LAST_APPLIED_THEMS = "GlobalPreferences.ThemesController.last.applied.theme";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ThemesEnum.class);
   private final String _title;

   ThemesEnum(String titleI18nKey)
   {
      _title = titleI18nKey;
   }

   public static ThemesEnum getCurrentTheme()
   {
      return ThemesEnum.valueOf(Props.getString(PREF_KEY_THEMESCONTROLLER_LAST_APPLIED_THEMS, ThemesEnum.LIGH.name()));
   }

   public static void saveCurrentTheme(ThemesEnum themesEnum)
   {
      Props.putString(PREF_KEY_THEMESCONTROLLER_LAST_APPLIED_THEMS, themesEnum.name());
   }


   @Override
   public String toString()
   {
      return s_stringMgr.getString(_title);
   }
}
