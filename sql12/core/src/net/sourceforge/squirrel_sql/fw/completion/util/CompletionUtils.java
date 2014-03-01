package net.sourceforge.squirrel_sql.fw.completion.util;

class CompletionUtils
{
   private static final char[] SEPARATORS = {' ', '\t', '\n' ,  ',', '(', '\'','"', '=', '>', '<'};

   static String getStringToParse(String textTillCaret)
   {

      int lastIndexOfLineFeed = textTillCaret.lastIndexOf('\n');
      String lineTillCaret;

      if(-1 == lastIndexOfLineFeed)
      {
         lineTillCaret = textTillCaret;
      }
      else
      {
         lineTillCaret = textTillCaret.substring(lastIndexOfLineFeed);
      }

      String beginning = "";
      if (0 != lineTillCaret.trim().length() && !Character.isWhitespace(lineTillCaret.charAt(lineTillCaret.length() - 1)))
      {
         String trimmedLineTillCaret = lineTillCaret.trim();

         int lastSeparatorIndex = getLastSeparatorIndex(trimmedLineTillCaret);
         if (-1 == lastSeparatorIndex)
         {
            beginning = trimmedLineTillCaret;
         }
         else
         {
            beginning = trimmedLineTillCaret.substring(lastSeparatorIndex + 1, trimmedLineTillCaret.length());
         }
      }

      return beginning;
   }

   static int getLastSeparatorIndex(String str)
   {
      int lastSeparatorIndex = -1;
      for (char separator : SEPARATORS)
      {
         int buf = str.lastIndexOf(separator);
         if (buf > lastSeparatorIndex)
         {
            lastSeparatorIndex = buf;
         }
      }
      return lastSeparatorIndex;
   }

   static int getStringToParsePosition(String textTillCaret)
	{

		int lastIndexOfLineFeed = textTillCaret.lastIndexOf('\n');
		String lineTillCaret;

		if (-1 == lastIndexOfLineFeed)
		{
			lineTillCaret = textTillCaret;
		}
		else
		{
			lineTillCaret = textTillCaret.substring(lastIndexOfLineFeed);
		}

		int pos = lastIndexOfLineFeed + 1;
		if (0 != lineTillCaret.length() && !Character.isWhitespace(lineTillCaret.charAt(lineTillCaret.length() - 1)))
		{
			int lastSeparatorIndex = getLastSeparatorIndex(lineTillCaret);
			if (-1 != lastSeparatorIndex)
			{
				pos += lastSeparatorIndex;
			}
		}

		return pos;
	}
}
