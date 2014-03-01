package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;

public class BookmarksAccessor
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(BookmarksAccessor.class);


   private static BoomarksExternalService getService(IApplication application)
   {
      BoomarksExternalService si = (BoomarksExternalService) application.getPluginManager().bindExternalPluginService("sqlbookmark", BoomarksExternalService.class);
      if (null == si)
      {
         // i18n[BookmarksAccessor.bookmarksPluginNeeded=Running bookmarks is only available with the Bookmarks Plugin.\nGet the plugin from www.squirrelsql.org. It's free.]
         String msg = s_stringMgr.getString("BookmarksAccessor.bookmarksPluginNeeded");
         JOptionPane.showMessageDialog(application.getMainFrame(), msg);
         return null;
      }
      return si;
   }


   public static void selectBookmark(ISQLEntryPanel entryPanel)
   {
      getService(entryPanel.getSession().getApplication()).selectBookmark(entryPanel);
   }
}
