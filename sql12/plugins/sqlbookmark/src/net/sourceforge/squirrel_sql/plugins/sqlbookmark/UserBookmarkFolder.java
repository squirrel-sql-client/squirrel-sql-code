package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

public class UserBookmarkFolder
{
   private String _folderName;

   public UserBookmarkFolder(String folderName)
   {
      _folderName = folderName;
   }

   @Override
   public String toString()
   {
      return _folderName;
   }

   public String getFolderName()
   {
      return _folderName;
   }

   public void setFolderName(String folderName)
   {
      _folderName = folderName;
   }
}
