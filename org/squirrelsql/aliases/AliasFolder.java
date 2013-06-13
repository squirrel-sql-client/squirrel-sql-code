package org.squirrelsql.aliases;

public class AliasFolder extends AliasTreeNode
{
   private String _folderName;

   public AliasFolder(String folderName)
   {
      _folderName = folderName;
   }

   @Override
   public String toString()
   {
      return _folderName;
   }
}
