package org.squirrelsql.session.sql.bookmark;

public class Bookmark
{
   private final String _sql;
   private final String _selShortcut;
   private final String _description;
   private String _distString;

   public Bookmark(String sql, String selShortcut, String description)
   {
      _sql = sql;
      _selShortcut = selShortcut;
      _description = description;
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

   public void setDisplaySpace(int distLen)
   {
      StringBuffer buf = new StringBuffer();

      for (int j = 0; j < distLen; j++)
      {
          buf.append(" ");
      }

      _distString = buf.toString();

   }

   @Override
   public String toString()
   {
      return _selShortcut + _distString + _description;
   }
}
