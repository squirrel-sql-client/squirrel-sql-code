package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2004 Gerd Wagner
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
public class XmlRefomatter
{
	public static String reformatXml(String xml)
	{
		try
		{
			StringBuffer ret = new StringBuffer();
			int depth = 0;
			ParseRes parseRes = getParseRes(xml, 0);

			xml = xml.trim();

			while(null != parseRes)
			{

				if(ParseRes.BEGIN_TAG == parseRes.type)
				{
					ret.append(getIndent(depth)).append(parseRes.item);
					ParseRes nextRes = getParseRes(xml, parseRes.pos);
					if(ParseRes.TEXT != nextRes.type)
					{
						ret.append("\n");
					}

					++depth;
				}
				else if(ParseRes.END_TAG == parseRes.type)
				{
					--depth;
					if(ret.toString().endsWith("\n"))
					{
						ret.append(getIndent(depth));
					}
					ret.append(parseRes.item).append("\n");
				}
				else if(ParseRes.CLOSED_TAG == parseRes.type)
				{
					ret.append(getIndent(depth)).append(parseRes.item).append("\n");
				}
				else if(ParseRes.TEXT == parseRes.type)
				{
					ret.append(parseRes.item);
				}
				parseRes = getParseRes(xml, parseRes.pos);
			}

			return ret.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return xml;

	}

	private static ParseRes getParseRes(String xml, int pos)
	{
		if(pos >= xml.length())
		{
			return null;
		}

		pos = moveOverWhiteSpaces(xml, pos);

		int ltIndex = xml.indexOf("<", pos);
		int gtIndex = xml.indexOf(">", pos);

		ParseRes ret = new ParseRes();

		if(pos == ltIndex)
		{
			ret.item = xml.substring(ltIndex, gtIndex+1);

			if(xml.length() > ltIndex+1 && xml.charAt(ltIndex+1) == '/')
			{
				ret.type = ParseRes.END_TAG;
			}
			else if(pos < gtIndex-1 && xml.charAt(gtIndex-1) == '/')
			{
				ret.type = ParseRes.CLOSED_TAG;
			}
			else
			{
				ret.type = ParseRes.BEGIN_TAG;
			}
			ret.pos = gtIndex+1;
		}
		else
		{
			ret.type = ParseRes.TEXT;
			ret.item = xml.substring(pos, ltIndex);
			ret.pos = ltIndex;
		}

		return ret;
	}

	private static int moveOverWhiteSpaces(String xml, int pos)
	{
		int ret = pos;

		while(Character.isWhitespace(xml.charAt(ret)))
		{
			++ret;
		}
		return ret;

	}

	private static String getIndent(int depth)
	{
		StringBuffer ret = new StringBuffer("");
		for(int i=0; i < depth; ++i)
		{
			ret.append("   ");
		}
		return ret.toString();
	}

	static class ParseRes
	{
		public static final int BEGIN_TAG = 0;
		public static final int END_TAG = 1;
		public static final int CLOSED_TAG = 2;
		public static final int TEXT = 3;

		String item;
		int type;
		int pos;
	}
}
