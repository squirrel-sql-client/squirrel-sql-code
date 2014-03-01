package net.sourceforge.squirrel_sql.fw.completion.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CompletionParser
{
   private static final String DEREF_CHAR = ".";

   private ArrayList<String> _tokens = new ArrayList<String>();
   private String _stringToParse;
   private int _stringToParsePosition;
   private String _stringToReplace;
   private String _textTillCarret;


   public CompletionParser(String textTillCarret)
   {
      this(textTillCarret, false);
   }


   public CompletionParser(String textTillCarret, boolean completeQualified)
   {
      _textTillCarret = textTillCarret;
      _stringToParse = CompletionUtils.getStringToParse(textTillCarret);
      _stringToParsePosition = CompletionUtils.getStringToParsePosition ( textTillCarret );

      StringTokenizer st = new StringTokenizer(_stringToParse, DEREF_CHAR);
      _tokens = new ArrayList<String>();
      while(st.hasMoreTokens())
      {
         _tokens.add(st.nextToken());
      }

      if(textTillCarret.endsWith(DEREF_CHAR) || 0 == _tokens.size())
      {
         _tokens.add("");
      }

      if(completeQualified)
      {
         _stringToReplace = _stringToParse;
      }
      else
      {
         _stringToReplace = _tokens.get(_tokens.size() - 1);
      }

   }


   /**
    * True when _stringToParse contains a DEREF_CHAR.
    */
   public boolean isQualified()
   {
      return 1 < _tokens.size();
   }


   public String getStringToParse()
   {
      return _stringToParse;
   }


   public String getToken(int index)
   {
      return _tokens.get(index);
   }

   public int size()
   {
      return _tokens.size();
   }


   public int getStringToParsePosition()
   {
      return _stringToParsePosition;
   }


   public String getStringToReplace()
   {
      return _stringToReplace;
   }


   public int getReplacementStart()
   {
      return _textTillCarret.length() - _stringToReplace.length();
   }

   public String getTextTillCarret()
   {
      return _textTillCarret;
   }

   public String getLastToken()
   {
      return _tokens.get(_tokens.size()-1);
   }

   public String getAllButFirst()
   {
      String ret = _tokens.get(1);
      for (int i = 2; i < _tokens.size(); i++)
      {
         ret += DEREF_CHAR + _tokens.get(i);
      }
      return ret;
   }
}
