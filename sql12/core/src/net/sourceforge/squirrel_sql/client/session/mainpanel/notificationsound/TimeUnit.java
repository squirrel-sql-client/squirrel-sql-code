package net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum TimeUnit
{
   SECONDS(I18nProvider.s_stringMgr.getString("TimeUnit.seconds")),
   MINUTES(I18nProvider.s_stringMgr.getString("TimeUnit.minutes"));

   private String _unitDesc;

   TimeUnit(String unitDesc)
   {
      _unitDesc = unitDesc;
   }

   private interface I18nProvider
   {
      StringManager s_stringMgr = StringManagerFactory.getStringManager(TimeUnit.class);
   }


   @Override
   public String toString()
   {
      return _unitDesc;
   }
}
