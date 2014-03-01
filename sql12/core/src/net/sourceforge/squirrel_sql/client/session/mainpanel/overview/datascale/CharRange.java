package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

public class CharRange
{
   private int _minChar = ' ';
   private int _maxChar = '~';

   private boolean _isInit;
   private boolean _initializing;
   private boolean _hasNullOrEmptyString;

   public char getRange()
   {
      return (char) (_maxChar - _minChar + 1);
   }

   public void init(String s)
   {
      if(false == _initializing)
      {
         return;
      }
      
      if(null == s || 0 == s.length())
      {
         _hasNullOrEmptyString = true;
         return;
      }

      if(false == _isInit)
      {
         _minChar = s.charAt(0);
         _maxChar = s.charAt(0);
         _isInit = true;
      }

      for (int i = 0; i < s.length(); i++)
      {
         _minChar = Math.min(_minChar, s.charAt(i));
         _maxChar = Math.max(_maxChar, s.charAt(i));
      }
   }

   void beginInit()
   {
      _initializing = true;
   }

   void endInit()
   {
      _initializing = false;
   }

   public char getMinChar()
   {
      if (_hasNullOrEmptyString && 0 < _minChar)
      {
         // This makes sure that null or empty string can be separated from other strings.
         // Before this was done null or empty string could not be in their own interval.
         // null still cant be separated from empty string.
         return (char) (_minChar-1);
      }
      else
      {
         return (char) _minChar;
      }
   }

   public char getMaxChar()
   {
      return (char) _maxChar;
   }
}
