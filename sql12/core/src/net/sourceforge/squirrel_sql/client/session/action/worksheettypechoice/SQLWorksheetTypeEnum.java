package net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice;

import java.util.prefs.Preferences;

public enum SQLWorksheetTypeEnum
{
   SQL_WORKSHEET,
   SQL_TAB;

   private static final String PREF_KEY_SQLWORKSHEET_TYPE = "Squirrel.SQLWorksheetTypeEnum.type";


   public static SQLWorksheetTypeEnum getSelecteType()
   {
      return valueOf(Preferences.userRoot().get(PREF_KEY_SQLWORKSHEET_TYPE, SQL_WORKSHEET.name()));
   }

   public void saveSelected()
   {
      Preferences.userRoot().put(PREF_KEY_SQLWORKSHEET_TYPE, name());
   }

}
