package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum SavedSessionsGroupDlgDefaultButton
{
   SAVE(I18n.s_stringMgr.getString("SavedSessionsGroupDlgDefaultButton.save.group")),
   GIT_COMMIT(I18n.s_stringMgr.getString("SavedSessionsGroupDlgDefaultButton.git.commit.group"));

   private final String _defaultButtonName;

   SavedSessionsGroupDlgDefaultButton(String defaultButtonName)
   {
      _defaultButtonName = defaultButtonName;
   }

   @Override
   public String toString()
   {
      return _defaultButtonName;
   }

   private interface I18n
   {
      StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionsGroupDlgDefaultButton.class);
   }
}
