package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.fw.resources.IResources;

import java.awt.event.ActionEvent;

public class EditBookmarksAction extends SquirrelAction implements ISessionAction
{
   private SQLBookmarkPlugin _plugin;
   private ISession _session;

   public EditBookmarksAction(IApplication app, IResources resources, SQLBookmarkPlugin plugin)
   {
      super(app, resources);
      if (plugin == null)
      {
         throw new IllegalArgumentException("null IPlugin passed");
      }
      _plugin = plugin;
   }


   public void actionPerformed(ActionEvent evt)
   {
      _plugin.addSQLPanelAPIListeningForBookmarks(_session.getSQLPanelAPIOfActiveSessionWindow());

      _session.getSQLPanelAPIOfActiveSessionWindow().addSQLPanelListener(new ISQLPanelAdapter()
      {
         public void sqlEntryAreaClosed(SQLPanelEvent evt)
         {
            onSQLEntryAreaClosed();
         }
      });

      GlobalPreferencesSheet.showSheet(SQLBookmarkPreferencesPanel.class);
   }

   private void onSQLEntryAreaClosed()
   {
      if (null != _session)
      {
         _plugin.removeSQLPanelAPIListeningForBookmarks(_session.getSQLPanelAPIOfActiveSessionWindow());
      }
   }

   public void setSession(ISession session)
   {
      _session = session;
   }
}
