package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import java.awt.event.ActionEvent;

public class BoomarksExternalServiceImpl implements BoomarksExternalService
{
   private SQLBookmarkPlugin _plugin;

   public BoomarksExternalServiceImpl(SQLBookmarkPlugin plugin)
   {
      _plugin = plugin;
   }

   public void selectBookmark(ISQLEntryPanel entryPanel)
   {
      ActionEvent event = new ActionEvent(entryPanel, -230366, "bookmarkselect");
      new CompleteBookmarkAction(entryPanel.getSession().getApplication(), _plugin.getResources(), entryPanel, _plugin).actionPerformed(event);

   }
}
