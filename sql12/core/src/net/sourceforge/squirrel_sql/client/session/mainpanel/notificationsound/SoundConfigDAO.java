package net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound;

import net.sourceforge.squirrel_sql.fw.props.Props;

import java.io.File;

public class SoundConfigDAO
{
   private static final String PREF_LAST_SOUND_FILE_PATH = "SoundFileDAO.sound.file.path";
   private static final String PREF_TIME = "SoundFileDAO.time";
   private static final String PREF_TIME_UNIT = "SoundFileDAO.time.unit";
   private static final String PREF_PLAY_NOTIFICATION = "SoundFileDAO.play.notification";

   public static void writeSoundFile(String soundFilePath)
   {
      Props.putString(PREF_LAST_SOUND_FILE_PATH, soundFilePath);
   }


   public static File getSoundFile()
   {
      String path = getSoundFileAsString();

      if(null == path)
      {
         return null;
      }

      return new File(path);
   }

   public static String getSoundFileAsString()
   {
      return Props.getString(PREF_LAST_SOUND_FILE_PATH, null);
   }

   public static Long playNotificationWhenMillisElapsed()
   {
      if(false == isPlayNotification())
      {
         return null;
      }

      switch (getTimeUnit())
      {
         case MINUTES:
            return (long)(getPlayNotificationAfter() * 60 * 1000);
         case SECONDS:
            return (long)(getPlayNotificationAfter() * 1000);
         default:
            throw new IllegalStateException("Unknown TimeUnit: " + getTimeUnit());
      }
   }

   static int getPlayNotificationAfter()
   {
      return Props.getInt(PREF_TIME, 1);
   }

   static TimeUnit getTimeUnit()
   {
      return TimeUnit.valueOf(Props.getString(PREF_TIME_UNIT, TimeUnit.MINUTES.name()));
   }

   static void setPlayNotificationAfter(int time)
   {
      if(time <= 0)
      {
         time = 1;
      }

      Props.putInt(PREF_TIME, time);
   }

   static void setTimeUnit(TimeUnit timeUnit)
   {
      Props.putString(PREF_TIME_UNIT, timeUnit.name());
   }

   static boolean isPlayNotification()
   {
      return Props.getBoolean(PREF_PLAY_NOTIFICATION, false);
   }

   static void setPlayNotification(boolean b)
   {
      Props.putBoolean(PREF_PLAY_NOTIFICATION, b);
   }
}
