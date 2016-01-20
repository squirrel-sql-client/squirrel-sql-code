package net.sourceforge.squirrel_sql.client.util.codereformat;

import net.sourceforge.squirrel_sql.client.preferences.codereformat.FormatSqlPref;

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

      if(   piece.trim().toUpperCase().startsWith(FormatSqlPref.SELECT)
            || piece.trim().toUpperCase().startsWith(FormatSqlPref.FROM)
            || piece.trim().toUpperCase().startsWith(FormatSqlPref.WHERE)
            || piece.trim().toUpperCase().startsWith(FormatSqlPref.UNION)
            || piece.trim().toUpperCase().startsWith(FormatSqlPref.GROUP)
            || piece.trim().toUpperCase().startsWith(FormatSqlPref.ORDER))
      {
         return true;
      }
      return false;
   }

}
