package org.squirrelsql.sqlreformat;

import org.squirrelsql.settings.SQLKeyWord;

public class IndentSectionsHandler
{
   private boolean _indentSections;
   private boolean _currentPieceIsSectionBegin;
   private int _offset;
   private boolean _sqlHasSections;

   public IndentSectionsHandler(boolean indentSections)
   {
      _indentSections = indentSections;
   }

   public void before(String piece)
   {
      _currentPieceIsSectionBegin = isSectionBegin(piece);

      if(_currentPieceIsSectionBegin)
      {
         _sqlHasSections = true;
      }

      if (")".equals(piece) && _offset > 0)
      {
         --_offset;
      }
   }

   public void after(String piece)
   {
      if ("(".equals(piece))
      {
         ++_offset;
      }
   }


   public int getExtraIndentCount()
   {
      if (false == _indentSections || false == _sqlHasSections)
      {
         return 0;
      }

      if(_currentPieceIsSectionBegin)
      {
         return _offset;
      }
      else
      {
         return _offset + 1;
      }
   }

   private boolean isSectionBegin(String piece)
   {

      if(   piece.trim().toUpperCase().startsWith(SQLKeyWord.SELECT.name())
            || piece.trim().toUpperCase().startsWith(SQLKeyWord.FROM.name())
            || piece.trim().toUpperCase().startsWith(SQLKeyWord.WHERE.name())
            || piece.trim().toUpperCase().startsWith(SQLKeyWord.UNION.name())
            || piece.trim().toUpperCase().startsWith(SQLKeyWord.GROUP.name())
            || piece.trim().toUpperCase().startsWith(SQLKeyWord.ORDER.name()))
      {
         return true;
      }
      return false;
   }

}
