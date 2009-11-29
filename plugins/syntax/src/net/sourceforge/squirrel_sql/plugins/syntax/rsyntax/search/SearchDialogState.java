package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search;

public class SearchDialogState
{
   private boolean _searchUp;
   private boolean _matchCase;
   private boolean _wholeWord;
   private boolean _regExp;

   public SearchDialogState(ISquirrelSearchDialog dialog)
   {
      _searchUp = dialog.isSearchUp();
      _matchCase = dialog.isMatchCase();
      _wholeWord = dialog.isWholeWord();
      _regExp = dialog.isRegExp();
   }

   private SearchDialogState(boolean searchUp, boolean matchCase, boolean wholeWord, boolean regExp)
   {
      _searchUp = searchUp;
      _matchCase = matchCase;
      _wholeWord = wholeWord;
      _regExp = regExp;
   }

   public boolean isSearchUp()
   {
      return _searchUp;
   }

   public boolean isMatchCase()
   {
      return _matchCase;
   }

   public boolean isWholeWord()
   {
      return _wholeWord;
   }

   public boolean isRegExp()
   {
      return _regExp;
   }

   public static SearchDialogState createForLastFind()
   {
      return new SearchDialogState(false, false, false, false);
   }
}
