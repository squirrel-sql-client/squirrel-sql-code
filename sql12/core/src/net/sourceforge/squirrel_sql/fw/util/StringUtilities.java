package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * String handling utilities.
 * 
 * This class contains additional methods, that not exists in commons-lang from apache.
 * Methods, that are identical to commons-lang are removed. e.g. {@link StringUtils#isEmpty(String)}. 
 * Some other methods (e.g. {@link #join(String[], String)} are still available, because they have a slightly different behavior as the methods in commons-lang.
 * 
 * @see StringUtils
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class StringUtilities
{

   public static final String NULL_AS_STRING = "<null>";

   public static final char[] ILLEGAL_FILE_NAME_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };


   /**
	 * Clean the passed string. Replace whitespace characters with a single
	 * space. If a <TT>null</TT> string passed return an empty string. E.G.
	 * replace
	 *
	 * [pre]
	 * \t\tselect\t* from\t\ttab01
	 * [/pre]
	 *
	 * with
	 *
	 * [pre]
	 * select * from tab01
	 * [/pre]
	 *
	 * @param	str	String to be cleaned.
	 *
	 * @return	Cleaned string.
	 */
	public static String cleanString(String str)
	{
		final StringBuffer buf = new StringBuffer(str.length());
		char prevCh = ' ';

		for (int i = 0, limit = str.length(); i < limit; ++i)
		{
			char ch = str.charAt(i);
			if (Character.isWhitespace(ch))
			{
				if (!Character.isWhitespace(prevCh))
				{
					buf.append(' ');
				}
			}
			else
			{
				buf.append(ch);
			}
			prevCh = ch;
		}

		return buf.toString();
	}

   /**
	 * Split a string based on the given delimiter, but don't remove
	 * empty elements.
	 *
	 * @param	str			The string to be split.
	 * @param	delimiter	Split string based on this delimiter.
	 * <p />
     * <b>Not compatible to {@link StringUtils#split(String)}<b>
	 * @return	Array of split strings. Guaranteeded to be not null.
	 */
	public static String[] split(String str, char delimiter)
	{
		return split(str, delimiter, false);
	}

	/**
	 * Split a string based on the given delimiter, optionally removing
	 * empty elements.
	 *
	 * @param	str			The string to be split.
	 * @param	delimiter	Split string based on this delimiter.
	 * @param	removeEmpty	If <tt>true</tt> then remove empty elements.
	 * <p />
     * <b>Not compatible to {@link StringUtils#split(String)}<b>
	 * @return	Array of split strings. Guaranteeded to be not null.
	 */
	public static String[] split(String str, char delimiter,
										boolean removeEmpty)
	{
		// Return empty list if source string is empty.
		final int len = (str == null) ? 0 : str.length();
		if (len == 0)
		{
			return new String[0];
		}

		final List<String> result = new ArrayList<String>();
		String elem = null;
		int i = 0, j = 0;
		while (j != -1 && j < len)
		{
			j = str.indexOf(delimiter,i);
			elem = (j != -1) ? str.substring(i, j) : str.substring(i);
			i = j + 1;
			if (!removeEmpty || !(elem == null || elem.length() == 0))
			{
				result.add(elem);
			}
		}
		return result.toArray(new String[result.size()]);
	}
    
    /**
     * Joins the specified parts separating each from one another with the 
     * specified delimiter.  If delim is null, then this merely returns the 
     * concatenation of all the parts.
     * <p />
     * <b>Could not be replaced by {@link StringUtils#join(Object[], char)} because the handling of <code>null</code> as value is different<b>
     * 
     * @param parts the strings to be joined
     * @param delim the char(s) that should separate the parts in the result
     * @return a string representing the joined parts.
     */
    public static String join(String[] parts, String delim) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            result.append(part);
            if (delim != null && i < parts.length-1) {
                result.append(delim);
            }        
        }
        return result.toString();
    }
    
    public static String[] segment(String source, int maxSegmentSize) {
        ArrayList<String> tmp = new ArrayList<String>();
        if (source.length() <= maxSegmentSize) {
            return new String[] { source };
        }
        boolean done = false;
        int currBeginIdx = 0;
        int currEndIdx = maxSegmentSize;
        while (!done) {
            String segment = source.substring(currBeginIdx, currEndIdx);
            tmp.add(segment);
            if (currEndIdx >= source.length()) {
                done = true;
                continue;
            }
            currBeginIdx = currEndIdx;
            currEndIdx += maxSegmentSize;
            if (currEndIdx > source.length()) {
                currEndIdx = source.length();
            }
        }
        return tmp.toArray(new String[tmp.size()]);
    }
    
    public static int getTokenBeginIndex(String selectSQL, String token)
    {
       String lowerSel = selectSQL.toLowerCase();
       String lowerToken = token.toLowerCase().trim();

       int curPos = 0;
       int count = 0;
       while(-1 != curPos)
       {
          curPos = lowerSel.indexOf(lowerToken, curPos + lowerToken.length());

          if(-1 < curPos
                  && (0 == curPos || Character.isWhitespace(lowerSel.charAt(curPos-1)))
                  && (lowerSel.length() == curPos + lowerToken.length() || Character.isWhitespace(lowerSel.charAt(curPos + lowerToken.length())))
            )
          {
             return curPos;
          }
          // If we've loop through one time for each character in the string, 
          // then something must be wrong.  Get out!
          if (count++ > selectSQL.length()) {
              break;
          }
       }

       return curPos;
    }
    
    public static Byte[] getByteArray(byte[] bytes) {
        if (bytes == null || bytes.length == 0 ) {
            return new Byte[0];
        }
        Byte[] result = new Byte[bytes.length]; 
        for (int i = 0; i < bytes.length; i++) {
            result[i] = Byte.valueOf(bytes[i]);
        }

        return result;
    }
    
    /**
     * Chops off the very last character of the given string.
     * <p />
     * <b>Could not be replaced by {@link StringUtils#chop(String)} because the handling of <code>\r\n</code> are different<b>
     * @param aString a string to chop
     * @return the specified string minus it's last character, or null for null
     *         or empty string for a string with length == 0|1.
     */
    public static String chop(String aString) {
        if (aString == null) {
            return null;
        }
        if (aString.length() == 0) {
            return "";
        }
        if (aString.length() == 1) {
            return "";
        }
        return aString.substring(0, aString.length()-1);
    }
    
    /**
     * Returns the platform-specific line separator, or "\n" if it is not defined for some reason.
     * 
     * @return the platform-specific line separator.
     */
    public static String getEolStr() {
   	 return System.getProperty("line.separator", "\n");
    }

   public static String escapeHtmlChars(String sql)
   {
      String buf = sql.replaceAll("&", "&amp;");
      buf = buf.replaceAll("<", "&lt;");
      buf = buf.replaceAll(">", "&gt;");
      buf = buf.replaceAll("\"", "&quot;");
      return buf;
   }

   public static String javaNormalize(String text)
   {
      return javaNormalize(text, true );
   }

   public static String javaNormalize(String text, boolean ensureJavaStart)
   {
      StringBuilder buf = new StringBuilder(text.length());

      if(ensureJavaStart && Character.isJavaIdentifierStart(text.charAt(0)) )
      {
         buf.append(text.charAt(0));
      }
      else if(false == ensureJavaStart && Character.isLetterOrDigit(text.charAt(0)) )
      {
         buf.append(text.charAt(0));
      }
      else
      {
         buf.append('_');
      }


      for(int i=1; i < text.length(); ++i)
      {
         if ( Character.isLetterOrDigit(text.charAt(i)) )
         {
            buf.append(text.charAt(i));
         }
         else
         {
            buf.append('_');
         }
      }

      String ret = buf.toString();

      return ret;
   }

   public static String fileNameNormalize(String text)
   {
      StringBuilder buf = new StringBuilder(text.length());

      for(int i=0; i < text.length(); ++i)
      {

         boolean illegal = false;
         for (char illegalFileNameChar : ILLEGAL_FILE_NAME_CHARACTERS)
         {
            if(text.charAt(i) == illegalFileNameChar)
            {
               illegal = true;
               break;
            }
         }

         if ( illegal )
         {
            buf.append('_');
         }
         else
         {
            buf.append(text.charAt(i));
         }
      }

      return buf.toString().trim();
   }



   public static boolean isEmpty(String s)
   {
      return isEmpty(s, false);
   }

   public static boolean isEmpty(String s, boolean checkTrimmed)
   {
      if(null == s)
      {
         return true;
      }

      if (checkTrimmed)
      {
         return 0 == s.trim().length();
      }
      else
      {
         return 0 == s.length();
      }
   }


   public static String singleQuote(String value) {
		if (!value.trim().startsWith("'")) {
			return "'" + value + "'";
		}
		return value;
	}

   public static String emptyToNull(String s)
   {
      if(isEmpty(s, true))
      {
         return null;
      }

      return s;
   }

   public static String nullToEmpty(String str)
   {
      if(null == str)
      {
         return "";
      }

      return str;
   }


   public static String shortenBegin(String in, int maxLen, String incompleteIndikator)
   {
      if(in.length() <= maxLen)
      {
         return in;
      }

      if (null == incompleteIndikator)
      {
         return in.substring(in.length() - maxLen);
      }
      else
      {
         return incompleteIndikator +  in.substring(in.length() - maxLen);
      }
   }

   public static String shortenEnd(String in, int maxLen, String incompleteIndikator)
   {
      if(in.length() <= maxLen)
      {
         return in;
      }

      if (null == incompleteIndikator)
      {
         return in.substring(0, maxLen);
      }
      else
      {
         return in.substring(0, maxLen) + incompleteIndikator;
      }
   }

   public static boolean equalsRespectNullModuloEmptyAndWhiteSpace(String s1, String s2)
   {
      return equalsRespectNullModuloEmptyAndWhiteSpace(s1, s2, false);
   }
   public static boolean equalsRespectNullModuloEmptyAndWhiteSpace(String s1, String s2, boolean ignoreCase)
   {
      if(isEmpty(s1, true) && isEmpty(s2, true))
      {
         return true;
      }
      else if(isEmpty(s1, true) || isEmpty(s2, true))
      {
         return false;
      }
      else
      {
         if(ignoreCase)
         {
            return s1.trim().equalsIgnoreCase(s2.trim());
         }
         else
         {
            return s1.trim().equals(s2.trim());
         }
      }

   }

   /**
    * RSyntax does not like CRs, even on Windows.
    * If you insert ones you find that arrow keys do not work correctly:
    * When steping over a CR it looks like the arrow key does nothing.
    *
    * E.G.: Code reformating comes with CRs in line ends on Windows
    */
   public static String removeCarriageReturn(String text)
   {
      if(null == text)
      {
         return null;
      }

      return text.replaceAll("\r","");
   }

   public static String removeNewLine(String text)
   {
      if(null == text)
      {
         return null;
      }

      return removeCarriageReturn(text).replaceAll("\n","");
   }

   public static String prefixNulls(int toPrefix, int digitCount)
   {
      String ret = "" + toPrefix;

      while (ret.length() < digitCount)
      {
         ret = 0 + ret;
      }

      return ret;
   }

   public static String stripDoubleQuotes(String possiblyQuotedString)
   {
      if(isEmpty(possiblyQuotedString, true))
      {
         return possiblyQuotedString;
      }

      return StringUtils.strip(possiblyQuotedString, "\"");
   }

   public static String pad(int width, char padChar)
   {
      return new String(new char[width]).replace('\0', padChar);
   }

   public static String removeEmptyLines(String text)
   {
      return removeEmptyLines(text, s -> false);
   }
   public static String removeEmptyLines(String text, Predicate<String> previousLineVeto)
   {
      String[] lines = text.split("\n");

      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < lines.length; i++)
      {

         boolean append = false;
         if (false == lines[i].trim().isEmpty())
         {
            append = true;
         }
         else if (previousLineVeto.test(lines[i - 1]))
         {
            append = true;
         }

         if (append)
         {
            if (0 < builder.length())
            {
               builder.append("\n");
            }
            builder.append(lines[i]);
         }

      }
      return builder.toString();
   }

   public static String replaceNonBreakingSpacesBySpaces(String text)
   {
      return StringUtils.replaceChars(text, "\u00A0\u1680\u180e\u2000\u200a\u202f\u205f\u3000", " ");
   }
}
