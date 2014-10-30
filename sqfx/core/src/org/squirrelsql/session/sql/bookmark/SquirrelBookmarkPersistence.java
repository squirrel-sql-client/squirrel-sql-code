package org.squirrelsql.session.sql.bookmark;

public class SquirrelBookmarkPersistence
{
   private String _selShortcut;
   private boolean _useAsBookmark;
   private boolean _useAsAbbreviation;

   public void setSelShortcut(String selShortcut)
   {
      _selShortcut = selShortcut;
   }

   public String getSelShortcut()
   {
      return _selShortcut;
   }

   public void setUseAsBookmark(boolean useAsBookmark)
   {
      _useAsBookmark = useAsBookmark;
   }

   public boolean isUseAsBookmark()
   {
      return _useAsBookmark;
   }

   public void setUseAsAbbreviation(boolean useAsAbbreviation)
   {
      _useAsAbbreviation = useAsAbbreviation;
   }

   public boolean isUseAsAbbreviation()
   {
      return _useAsAbbreviation;
   }
}
