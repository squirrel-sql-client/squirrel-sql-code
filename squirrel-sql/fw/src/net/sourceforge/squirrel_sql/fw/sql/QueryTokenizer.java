package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2002 Johan Compagner
 * jcompagner@j-com.nl
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
import java.util.StringTokenizer;

public class QueryTokenizer
{
	private final char _querySepChar;

	private String _sQuerys;

	private String _sNextQuery;

	/**
	 * These characters at the beginning of an SQL statement indicate that it
     * is a comment.
	 */
	private String _solComment;

	/**
	 * QueryTokenizer constructor comment.
	 */
	public QueryTokenizer(String sql, char querySepChar, String solComment)
	{
		super();
		_querySepChar = querySepChar;

		if (solComment != null && solComment.trim().length() > 0)
		{ 
			_solComment = solComment;
		}
		else
		{
			_solComment = null;
		}

		if(sql != null)
		{
			_sQuerys = prepareSQL(sql);
			_sNextQuery = parse();
		}
		else
		{
			_sQuerys = "";
		}
	}

	public boolean hasQuery()
	{
		return _sNextQuery != null;
	}

	public String nextQuery()
	{
		String sReturnQuery = _sNextQuery;
		_sNextQuery = parse();
		return sReturnQuery;
	}

	public String parse()
	{
		if(_sQuerys.length() == 0)
		{
			return null;
		}
		int iQuoteCount = 1;
		int iIndex1 = -1;
		while(iQuoteCount%2 != 0)
		{
			iQuoteCount = 0;
			iIndex1 = _sQuerys.indexOf(_querySepChar,iIndex1+1);

			if(iIndex1 != -1)
			{
				int iIndex2 = _sQuerys.lastIndexOf('\'',iIndex1);
				while(iIndex2 != -1)
				{
					if(_sQuerys.charAt(iIndex2-1) != '\\')
					{
						iQuoteCount++;
					}
					iIndex2 = _sQuerys.lastIndexOf('\'',iIndex2-1);
				}
			}
			else
			{
				String sNextQuery = _sQuerys;
				_sQuerys = "";
				if (_solComment != null && sNextQuery.startsWith(_solComment))
				{
					return parse();
				}
				return replaceLineFeeds(sNextQuery);
			}
		}
		String sNextQuery = _sQuerys.substring(0,iIndex1);
		_sQuerys = _sQuerys.substring(iIndex1+1).trim();
		if (_solComment != null && sNextQuery.startsWith(_solComment))
		{
			return parse();
		}
		return replaceLineFeeds(sNextQuery);
	}

	private String prepareSQL(String sql)
	{
		StringBuffer results = new StringBuffer(1024);

		for (StringTokenizer tok = new StringTokenizer(sql.trim(), "\n", false);
				tok.hasMoreTokens();)
		{
			String line = tok.nextToken();
			if (!line.startsWith(_solComment))
			{
				results.append(line).append('\n');
			}
		}

		return results.toString();
	}

	private String replaceLineFeeds(String sql)
	{
		StringBuffer sbReturn = new StringBuffer();
		int iPrev = 0;
		int linefeed = sql.indexOf('\n');
		int iQuote = -1;
		while(linefeed != -1)
		{
			iQuote = sql.indexOf('\'',iQuote+1);
			if(iQuote != -1 && iQuote < linefeed)
			{
				int iNextQute = sql.indexOf('\'',iQuote+1);
				if(iNextQute > linefeed)
				{
					sbReturn.append(sql.substring(iPrev,linefeed));
					sbReturn.append('\n');
					iPrev = linefeed + 1;
					linefeed = sql.indexOf('\n',iPrev);
				}
			}
			else
			{
				linefeed = sql.indexOf('\n', linefeed+1);
			}
		}
		sbReturn.append(sql.substring(iPrev));
		return sbReturn.toString();
	}

	/**
	 * Creation date: (6/13/2001 3:32:13 PM)
	 * @param args java.lang.String[]
	 */
/*
	public static void main(String[] args)
	{
		String s = new String("insert into test (ikey,var) values(1,'\\\'test;');  ");
		QueryTokenizer qt = new QueryTokenizer(s);
		System.out.println(qt.nextQuery());
	}
*/
}
