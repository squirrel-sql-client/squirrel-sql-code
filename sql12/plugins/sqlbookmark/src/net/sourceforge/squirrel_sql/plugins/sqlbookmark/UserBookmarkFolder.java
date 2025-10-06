package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

public class UserBookmarkFolder
{
   private final String _folderName;

   public UserBookmarkFolder(String folderName)
   {
      _folderName = folderName;
   }

   public String getFolderName()
   {
      return _folderName;
   }

   @Override
   public String toString()
   {
      return getFolderName();
   }
}
