package net.sourceforge.squirrel_sql.fw.completion.util;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CompletionParser
{
   private static final String DEREF_CHAR = ".";
   private static final String DOUBLE_QUOTE_CHAR = "\"";

   private ArrayList<String> _tokens;
   private String _stringToParse;
   private int _stringToParsePosition;
   private String _stringToReplace;
   private String _textTillCaret;


   public CompletionParser(String textTillCaret)
   {
      this(textTillCaret, false);
   }


   public CompletionParser(String textTillCaret, boolean completeQualified)
   {
      _textTillCaret = textTillCaret;
      _stringToParse = StringUtilities.stripDoubleQuotes(CompletionUtils.getStringToParse(textTillCaret));
      _stringToParsePosition = CompletionUtils.getStringToParsePosition ( textTillCaret );

      StringTokenizer st = new StringTokenizer(_stringToParse, DEREF_CHAR);
      _tokens = new ArrayList<>();
      while(st.hasMoreTokens())
      {
         _tokens.add(StringUtilities.stripDoubleQuotes(st.nextToken()));
      }

      if(   textTillCaret.endsWith(DEREF_CHAR) || textTillCaret.endsWith(DEREF_CHAR + DOUBLE_QUOTE_CHAR)
         || 0 == _tokens.size())
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
    * True when _stringToParse contains a {@link DEREF_CHAR}.
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
      return _textTillCaret.length() - _stringToReplace.length();
   }

   public String getTextTillCaret()
   {
      return _textTillCaret;
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
