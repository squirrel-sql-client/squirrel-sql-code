package org.squirrelsql.aliases;

import org.squirrelsql.services.dndpositionmarker.DropIntoInfo;

public interface AliasTreeNode extends DropIntoInfo
{
   String getId();
   String getName();

   @Override
   default boolean allowsDropInto()
   {
      return this instanceof AliasFolder;
   }
}
