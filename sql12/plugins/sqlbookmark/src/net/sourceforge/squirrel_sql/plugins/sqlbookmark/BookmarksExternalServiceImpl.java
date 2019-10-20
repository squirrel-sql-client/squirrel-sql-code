package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import java.awt.event.ActionEvent;

public class BookmarksExternalServiceImpl implements BookmarksExternalService
{
   private SQLBookmarkPlugin _plugin;

   public BookmarksExternalServiceImpl(SQLBookmarkPlugin plugin)
   {
      _plugin = plugin;
   }

   public void selectBookmark(ISQLEntryPanel entryPanel)
   {
      ActionEvent event = new ActionEvent(entryPanel, -230366, "bookmarkselect");
      new CompleteBookmarkAction(entryPanel.getSession().getApplication(), _plugin.getResources(), entryPanel, _plugin).actionPerformed(event);

   }
}
