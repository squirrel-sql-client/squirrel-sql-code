package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum TimeScale
{
   MILLISECONDS("TimeScale.milliSeconds", 1),
   SECONDS("TimeScale.seconds", 1000),
   MINUTES("TimeScale.minutes", 1000 * 60),
   HOURS("TimeScale.hours", 1000 * 60 *60),
   DAYS("TimeScale.days", 1000 * 60 * 60 * 24),
   WEEKS("TimeScale.weeks", 1000 * 60 * 60 * 24 * 7),
   YEARS("TimeScale.years", 1000 * 60 * 60 * 24 * 365);


   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TimeScale.class);

   private String _i18nString;
   private double _scale;

   TimeScale(String i18nString, double scale)
   {
      _i18nString = i18nString;
      _scale = scale;
   }

   @Override
   public String toString()
   {
      return s_stringMgr.getString(_i18nString);
   }

   public double scale(double value)
   {
      return value / _scale;
   }
}
