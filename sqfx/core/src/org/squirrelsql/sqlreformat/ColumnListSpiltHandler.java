package org.squirrelsql.sqlreformat;

public class ColumnListSpiltHandler
{
   private ColumnListSpiltMode _columnListSpiltMode;

   public ColumnListSpiltHandler(ColumnListSpiltMode columnListSpiltMode)
   {
      _columnListSpiltMode = columnListSpiltMode;
   }

   public boolean allowsSplit()
   {
      if (ColumnListSpiltMode.REQUIRE_SPLIT == _columnListSpiltMode || ColumnListSpiltMode.ALLOW_SPLIT == _columnListSpiltMode)
      {
         return true;
      }
      else
      {
         return false;
      }
   }


   public boolean requiresSplit()
   {
      if (ColumnListSpiltMode.REQUIRE_SPLIT == _columnListSpiltMode)
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   private boolean pieceIsSelectList(int currentIx, String[] pieces)
   {
      return SectionsHandler.isSelectSectionBegin(pieces[currentIx]) || (0 < currentIx && SectionsHandler.isSelectSectionBegin(pieces[currentIx - 1]));
   }

}
