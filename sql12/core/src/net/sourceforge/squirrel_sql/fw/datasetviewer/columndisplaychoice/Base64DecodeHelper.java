package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import java.util.Base64;

public class Base64DecodeHelper
{
   public static byte[] decodeBase64OmittingInvalidBase64Chars(String toDecode)
   {
      // Filter out invalid Base64 characters
      StringBuilder filteredInput = new StringBuilder();
      for(char c : toDecode.toCharArray())
      {
         if(isBase64Character(c))
         {
            filteredInput.append(c);
         }
      }
      // Decode the filtered Base64 string
      return Base64.getDecoder().decode(filteredInput.toString());
   }

   private static boolean isBase64Character(char c)
   {
      return (c >= 'A' && c <= 'Z') ||
            (c >= 'a' && c <= 'z') ||
            (c >= '0' && c <= '9') ||
            c == '+' ||
            c == '/' ||
            c == '=';
   }
}
