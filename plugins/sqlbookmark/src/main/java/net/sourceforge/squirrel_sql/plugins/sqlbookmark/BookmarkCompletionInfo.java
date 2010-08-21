package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;


public class BookmarkCompletionInfo extends CompletionInfo
{
   private Bookmark _bookmark;
   private int maxCandidateNameLen;

   public BookmarkCompletionInfo(Bookmark bookmark)
   {
      _bookmark = bookmark;
   }

   public String getCompareString()
   {
      return _bookmark.getName();
   }

   public String getCompletionString()
   {
      return _bookmark.getSql();
   }

   public String toString()
   {

      return _bookmark.getName() + getDist() + (null == _bookmark.getDescription() ? "" :_bookmark.getDescription());
   }

   private String getDist()
   {
      int len = maxCandidateNameLen - _bookmark.getName().length() + 4;

      StringBuffer ret = new StringBuffer();

      for(int i=0; i < len; ++i)
      {
         ret.append(' ');
      }

      return ret.toString();
   }

   public void setMaxCandidateNameLen(int maxNameLen)
   {
      this.maxCandidateNameLen = maxNameLen;
   }

   public Bookmark getBookmark()
   {
      return _bookmark;
   }
}
