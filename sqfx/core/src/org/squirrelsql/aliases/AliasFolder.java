package org.squirrelsql.aliases;

import java.util.UUID;

public class AliasFolder implements AliasTreeNode
{
   private String _id = UUID.randomUUID().toString();

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

   public String getId()
   {
      return _id;
   }

   @Override
   public String toString()
   {
      return getName();
   }

   public void setName(String changedFolderName)
   {
      _folderName = changedFolderName;
   }
}
