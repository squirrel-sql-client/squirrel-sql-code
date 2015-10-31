package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.action.EditExtrasExternalServiceImpl;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class EditExtrasAccessor
{
   public static void quoteHQL(ISQLEntryPanel entryPanel)
   {
      EditExtrasExternalServiceImpl si = new EditExtrasExternalServiceImpl(entryPanel.getSession().getApplication().getSquirrelPreferences().isCopyQuotedSqlsToClip());
      si.quoteSQL(entryPanel);
   }


   public static void quoteHQLSb(ISQLEntryPanel entryPanel)
   {
      EditExtrasExternalServiceImpl si = new EditExtrasExternalServiceImpl(entryPanel.getSession().getApplication().getSquirrelPreferences().isCopyQuotedSqlsToClip());
      si.quoteSQLSb(entryPanel);
   }

   public static void unquoteHQL(ISQLEntryPanel entryPanel)
   {
      EditExtrasExternalServiceImpl si = new EditExtrasExternalServiceImpl(entryPanel.getSession().getApplication().getSquirrelPreferences().isCopyQuotedSqlsToClip());
      si.unquoteSQL(entryPanel);
   }

   public static String escapeDate(ISQLEntryPanel entryPanel)
   {
      EditExtrasExternalServiceImpl si = new EditExtrasExternalServiceImpl(entryPanel.getSession().getApplication().getSquirrelPreferences().isCopyQuotedSqlsToClip());
      return si.getDateEscape(GUIUtils.getOwningFrame(entryPanel.getTextComponent()));
   }
}
