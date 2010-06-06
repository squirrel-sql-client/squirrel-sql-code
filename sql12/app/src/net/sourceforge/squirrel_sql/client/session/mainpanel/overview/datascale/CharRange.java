package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

public class CharRange
{
   private int _minChar = ' ';
   private int _maxChar = '~';

   private boolean _isInit;
   private boolean _initializing;

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
      return (char) _minChar;
   }

   public char getMaxChar()
   {
      return (char) _maxChar;
   }
}
