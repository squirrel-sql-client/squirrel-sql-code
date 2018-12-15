package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003 Gerd Wagner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.util.StringTokenizer;
class EditExtrasUtilities
{
	static String quoteText(String textToQuote, boolean sbAppend)
	{
		if (null == textToQuote)
		{
			throw new IllegalArgumentException("textToQuote can not be null");
		}

		String[] lines = textToQuote.split("\n");

		StringBuffer ret = new StringBuffer();

		if (sbAppend)
		{
			ret.append("sb.append(\"").append(
				trimRight(lines[0].replaceAll("\"", "\\\\\"")));
		}
		else
		{
			ret.append("\"").append(
				trimRight(lines[0].replaceAll("\"", "\\\\\"")));
		}

		for (int i = 1; i < lines.length; ++i)
		{
			if (sbAppend)
			{
				ret.append(" \"); \nsb.append(\"").append(
					trimRight(lines[i].replaceAll("\"", "\\\\\"")));
			}
			else
			{
				ret.append(" \" +\n\"").append(
					trimRight(lines[i].replaceAll("\"", "\\\\\"")));
			}
		}

		if (sbAppend)
		{
			ret.append(" \");");
		}
		else
		{
			ret.append(" \";");
		}

		return ret.toString();
	}

	/**
	 * textToUnquote is seen as a tokens separated by quotes. All tokens
	 * that contain a new line character are left out.
	 *
	 * @param	textToUnquote	Text to be unquoted.
	 * 
	 * @return	The unquoted text.
	 */
	static String unquoteText(String textToUnquote)
	{

		if(false == isQuoted(textToUnquote))
		{
			return textToUnquote;
		}


		// new line to the begining so that sb.append( will be removed
		// new line to the end so that a semi colon at the end will be removed.                    
		textToUnquote = "\n" + textToUnquote + "\n";

		StringTokenizer st = new StringTokenizer(textToUnquote, "\"");

		StringBuffer ret = new StringBuffer();
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			String trimmedToken = token;
			if (0 != token.trim().length() && -1 == token.indexOf('\n'))
			{
				if (trimmedToken.endsWith("\\n"))
				{
					// Some people put new line characters in their SQL to have nice debug output. 
					// Remove these new line characters too.                                       
					trimmedToken = trimmedToken.substring(0, trimmedToken.length() - 2);
				}

				if (trimmedToken.endsWith("\\"))
				{
					ret.append(trimmedToken, 0, trimmedToken.length() - 1).append("\"");
				}
				else
				{
					ret.append(trimmedToken).append("\n");
				}
			}
		}
		if (ret.toString().endsWith("\n"))
		{
			ret.setLength(ret.length() - 1);
		}
		return ret.toString();
	}

	private static boolean isQuoted(String textToUnquote)
	{
		return -1 < textToUnquote.indexOf('"');
	}

	static String trimRight(String toTrim)                   
	{                                                         
		if( 0 >= toTrim.length())                             
		{                                                     
			return toTrim;                                    
		}                                                     
                                                          
		int i;                                                
		for(i=toTrim.length(); i > 0; --i)                    
		{                                                     
			if( !Character.isWhitespace(toTrim.charAt(i-1)) ) 
			{                                                 
				break;                                        
			}                                                 
		}                                                     
                                                          
		return toTrim.substring(0, i);                        
	}                                                         

}
