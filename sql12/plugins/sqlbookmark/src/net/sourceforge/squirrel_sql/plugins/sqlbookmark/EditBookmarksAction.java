package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import java.awt.event.ActionEvent;

public class EditBookmarksAction extends SquirrelAction
   implements ISessionAction
{
   private SQLBookmarkPlugin _plugin;

   public EditBookmarksAction(IApplication app, Resources rsrc,
                              SQLBookmarkPlugin plugin)
      throws IllegalArgumentException
   {
      super(app, rsrc);
      if (plugin == null)
      {
         throw new IllegalArgumentException("null IPlugin passed");
      }
      _plugin = plugin;
   }


   public void actionPerformed(ActionEvent evt)
   {
      GlobalPreferencesSheet.showSheet(_plugin.getApplication(), SQLBookmarkPreferencesPanel.class);
   }

   public void setSession(ISession session)
   {
   }
}
