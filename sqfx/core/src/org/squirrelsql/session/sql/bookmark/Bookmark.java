package org.squirrelsql.session.sql.bookmark;

public class Bookmark
{
   private String _sql;
   private String _selShortcut;
   private String _description;

   private boolean _useAsBookmark = true;
   private boolean _useAsAbbreviation = false;

   private String _distString;


   public Bookmark()
   {
   }

   public Bookmark(String selShortcut, String description, String sql)
   {
      this(selShortcut, description, sql, false, false);
   }

   Bookmark(String selShortcut, String description, String sql, boolean useAsBookmark, boolean useAsAbbreviation)
   {
      _sql = sql;
      _selShortcut = selShortcut;
      _description = description;
      _useAsBookmark = useAsBookmark;
      _useAsAbbreviation = useAsAbbreviation;
   }

   public String getSql()
   {
      return _sql;
   }

   public String getSelShortcut()
   {
      return _selShortcut;
   }

   public String getDescription()
   {
      return _description;
   }


   public void setSql(String sql)
   {
      _sql = sql;
   }

   public void setSelShortcut(String selShortcut)
   {
      _selShortcut = selShortcut;
   }

   public void setDescription(String description)
   {
      _description = description;
   }

   public boolean isUseAsBookmark()
   {
      return _useAsBookmark;
   }

   public void setUseAsBookmark(boolean useAsBookmark)
   {
      _useAsBookmark = useAsBookmark;
   }

   public boolean isUseAsAbbreviation()
   {
      return _useAsAbbreviation;
   }

   public void setUseAsAbbreviation(boolean useAsAbbreviation)
   {
      _useAsAbbreviation = useAsAbbreviation;
   }


   /**
    * Needed for displaying bookmarks of StandardActionConfiguration.EXEC_BOOKMARK.
    */
   public void setDisplaySpace(int distLen)
   {
      StringBuffer buf = new StringBuffer();

      for (int j = 0; j < distLen; j++)
      {
          buf.append(" ");
      }

      _distString = buf.toString();

   }

   /**
    * Needed for displaying bookmarks of StandardActionConfiguration.EXEC_BOOKMARK.
    */
   @Override
   public String toString()
   {
      return _selShortcut + _distString + _description;
   }
}
