package net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice;

import net.sourceforge.squirrel_sql.fw.props.Props;

public enum SQLWorksheetTypeEnum
{
   SQL_WORKSHEET,
   SQL_TAB;

   private static final String PREF_KEY_SQLWORKSHEET_TYPE = "Squirrel.SQLWorksheetTypeEnum.type";


   public static SQLWorksheetTypeEnum getSelectedType()
   {
      return valueOf(Props.getString(PREF_KEY_SQLWORKSHEET_TYPE, SQL_WORKSHEET.name()));
   }

   public void saveSelected()
   {
      Props.putString(PREF_KEY_SQLWORKSHEET_TYPE, name());
   }

}
