package net.sourceforge.squirrel_sql.fw.gui.textfind;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFinder
{

   public static MatchBounds findNthOccurrence(String text, String searchString, int n, TextFindMode textFindMode)
   {
      switch (textFindMode)
      {
         case CONTAINS:
            return findNthOccurrence(text, searchString, n);
         case CONTAINS_IGNORE_CASE:
            return findNthOccurrenceIgnoreCase(text, searchString, n);
         case CONTAINS_REG_EXP:
            return findNthOccurrenceRegex(text, searchString, n);
         default:
            throw new IllegalArgumentException("Unknown TextFindMode " + textFindMode);
      }
   }


   private static MatchBounds findNthOccurrenceIgnoreCase(String text, String searchString, int n)
   {
      String lowercaseText = text.toLowerCase();
      String lowercaseSearchString = searchString.toLowerCase();

      return findNthOccurrence(lowercaseText, lowercaseSearchString, n);
   }

   private static MatchBounds findNthOccurrence(String text, String searchString, int n)
   {
      int count = 0;
      int index = -1;

      while (count < n)
      {
         //index = text.indexOf(searchString, index + 1);
         //
         //if (index != -1)
         //{
         //   count++;
         //}

         index = text.indexOf(searchString, index + 1);
         if (index == -1)
         {
            break;
         }
         count++;
      }

      if (-1 == index)
      {
         return null;
      }

      return new MatchBounds(index, index + searchString.length());
   }



   private static MatchBounds findNthOccurrenceRegex(String text, String regex, int n)
   {
      int count = 0;
      int index = -1;

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(text);

      MatchBounds ret = new MatchBounds();
      //while (count < n && matcher.find(index + 1))
      //{
      //   index = matcher.start();
      //   ret.setBeginIx(index);
      //   ret.setEndIx(matcher.end());
      //   count++;
      //}

      while (count < n && matcher.find(index + 1))
      {
         index = matcher.start();
         ret.setBeginIx(index);
         ret.setEndIx(matcher.end());
         count++;
      }

      return (count == n) ? ret : null;
   }
}
