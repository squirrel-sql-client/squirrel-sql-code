package net.sourceforge.squirrel_sql.client.util.codereformat;

import net.sourceforge.squirrel_sql.client.preferences.codereformat.FormatSqlPref;

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
            || beginsWithKeyword(piece, FormatSqlPref.FROM)
            || beginsWithKeyword(piece, FormatSqlPref.WHERE)
            || beginsWithKeyword(piece, FormatSqlPref.UNION)
            || beginsWithKeyword(piece, FormatSqlPref.GROUP)
            || beginsWithKeyword(piece, FormatSqlPref.ORDER))
      {
         return true;
      }
      return false;
   }

   public static boolean isSelectSectionBegin(String piece)
   {
      return beginsWithKeyword(piece, FormatSqlPref.SELECT);
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
