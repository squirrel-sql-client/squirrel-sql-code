package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import net.sourceforge.squirrel_sql.fw.props.Props;

public enum RerunResultTabMode
{
   DEFAULT,
   TIMER_REPEATS;

   private static final String PREF_RERUN_RESULT_TAB_MODE = "RerunResultTabMode.rerun.result.tab.mode";

   static RerunResultTabMode getCurrentMode()
   {
      return RerunResultTabMode.valueOf(Props.getString(PREF_RERUN_RESULT_TAB_MODE, RerunResultTabMode.DEFAULT.name()));
   }

   static void setCurrentMode(RerunResultTabMode mode)
   {
      Props.putString(PREF_RERUN_RESULT_TAB_MODE, mode.name());
   }
}
