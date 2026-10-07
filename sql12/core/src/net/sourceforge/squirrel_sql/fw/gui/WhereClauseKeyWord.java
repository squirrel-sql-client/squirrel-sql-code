package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.fw.gui.switchablemenu.SwitchableMenuOption;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum WhereClauseKeyWord implements SwitchableMenuOption
{
   WHERE,
   AND,
   OR;

   private static final String PREF_KEY_LEADING_KEYWORD = "Squirrel.copywherestatementmenu.leading.keyword";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(WhereClauseKeyWord.class);




   public static WhereClauseKeyWord getSelected()
   {
      String keyword = Props.getString(PREF_KEY_LEADING_KEYWORD, WhereClauseKeyWord.WHERE.name());
      return WhereClauseKeyWord.valueOf(keyword);
   }

   public static void setSelected(WhereClauseKeyWord keyWord)
   {
      Props.putString(PREF_KEY_LEADING_KEYWORD, keyWord.name());
   }

   @Override
   public String getChildMenuText()
   {
      return switch(this)
      {
         case WHERE -> s_stringMgr.getString("WhereClauseKeyWords.switch.parent.menu.default.to.and.copy.as",WHERE.name());
         case AND -> s_stringMgr.getString("WhereClauseKeyWords.switch.parent.menu.default.to.and.copy.as",AND.name());
         case OR -> s_stringMgr.getString("WhereClauseKeyWords.switch.parent.menu.default.to.and.copy.as",OR.name());
      };
   }

   @Override
   public String getParentMenuText()
   {
      return switch(this)
      {
         case WHERE -> s_stringMgr.getString("WhereClauseKeyWords.copy.as", WHERE.name());
         case AND -> s_stringMgr.getString("WhereClauseKeyWords.copy.as", AND.name());
         case OR -> s_stringMgr.getString("WhereClauseKeyWords.copy.as", OR.name());
      };
   }
}
