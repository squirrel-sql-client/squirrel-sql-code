package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class RunBookmarkTagHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RunBookmarkTagHandler.class);

   private static final String RUN_BOOKMARK_TAG = "@runbookmark";

   public static boolean requiresToRunBookmark(String sql)
   {
      return StringUtils.startsWith(StringUtils.trim(sql), RUN_BOOKMARK_TAG);
   }

   public static String toBookmarkSql(String sql, BookmarkManager bookmarkManager)
   {
      String sqlWithBookmarkPrefix = sql;

      int bookmarkBeginMarkerPos = sqlWithBookmarkPrefix.indexOf('\'');
      if(-1 == bookmarkBeginMarkerPos)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("RunBookmarkTagHandler.noBookmarkBeginMarker"));
         throw new IllegalStateException("@runbookmark tag found without bookmark to run. Bookmark name must be given after the tag in single quotes.");
      }

      int fileEndMarkerPos = sqlWithBookmarkPrefix.indexOf('\'', bookmarkBeginMarkerPos + 1);
      if(-1 == fileEndMarkerPos)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("RunBookmarkTagHandler.noBookmarkEndMarker"));
         throw new IllegalStateException("@runbookmark tag found without bookmark to run. Bookmark name must be given after the tag in single quotes.");
      }

      String bookmarkName = sqlWithBookmarkPrefix.substring(bookmarkBeginMarkerPos + 1, fileEndMarkerPos).trim();
      Bookmark bookmark = bookmarkManager.get(bookmarkName);

      if(null == bookmark)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("RunBookmarkTagHandler.bookmarkDoesNotExist", bookmarkName));
         throw new IllegalStateException("Bookmark named %s does not exist".formatted(bookmarkName));
      }

      return bookmark.getSql();
   }

   public static boolean scriptContainsRunBookmarkTag(String script)
   {
      return StringUtils.contains(script, RUN_BOOKMARK_TAG);
   }
}
