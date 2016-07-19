package org.squirrelsql.sqlreformat;


import org.squirrelsql.settings.SQLKeyWord;

public class SectionsHandler
{
   private boolean _indentSections;
   private boolean _currentPieceIsSectionBegin;
   private int _offset;
   private boolean _sqlHasSections;

   public SectionsHandler(boolean indentSections)
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

   public static boolean isSectionBegin(String piece)
   {

      if(   isSelectSectionBegin(piece)
            || beginsWithKeyword(piece, SQLKeyWord.FROM.name())
            || beginsWithKeyword(piece, SQLKeyWord.WHERE.name())
            || beginsWithKeyword(piece, SQLKeyWord.UNION.name())
            || beginsWithKeyword(piece, SQLKeyWord.GROUP.name())
            || beginsWithKeyword(piece, SQLKeyWord.ORDER.name()))
      {
         return true;
      }
      return false;
   }

   public static boolean isSelectSectionBegin(String piece)
   {
      return beginsWithKeyword(piece, SQLKeyWord.SELECT.name());
   }

   private static boolean beginsWithKeyword(String piece, String keyword)
   {
      String trimedUcPiece = piece.trim().toUpperCase();

      if(false == trimedUcPiece.startsWith(keyword.toUpperCase()))
      {
         return false;
      }

      return trimedUcPiece.length() == keyword.length() || Character.isWhitespace(trimedUcPiece.charAt(keyword.length()));

   }

}
