package org.squirrelsql.aliases;

public class AliasFolder implements AliasTreeNode
{
   private String _folderName;

   public AliasFolder(String folderName)
   {
      _folderName = folderName;
   }

   @Override
   public String getName()
   {
      return _folderName;
   }

   @Override
   public String toString()
   {
      return getName();
   }
}
