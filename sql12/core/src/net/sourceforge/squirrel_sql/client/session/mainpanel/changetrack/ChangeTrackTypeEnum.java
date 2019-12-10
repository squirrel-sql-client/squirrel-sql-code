package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.fw.props.Props;

public enum ChangeTrackTypeEnum
{
   MANUAL,
   FILE,
   GIT;

   private static final String PREF_KEY_CHANGE_TRACK_TYPE = "Squirrel.ChangeTrackTypeEnum.type";


   public static ChangeTrackTypeEnum getPreference()
   {
      return valueOf(Props.getString(PREF_KEY_CHANGE_TRACK_TYPE, FILE.name()));
   }

   public void savePreference()
   {
      Props.putString(PREF_KEY_CHANGE_TRACK_TYPE, name());
   }
}
