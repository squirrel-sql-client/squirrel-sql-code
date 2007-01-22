package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2003 Johan Compagner
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
import java.util.ArrayList;
import java.util.Iterator;

public class QueryTokenizer
{
	private ArrayList _queries = new ArrayList();
	private Iterator _queryIterator;


	public QueryTokenizer(String sql, String querySep, String lineCommentBegin, boolean removeMultiLineComment)
	{
		String MULTI_LINE_COMMENT_END = "*/";
		String MULTI_LINE_COMMENT_BEGIN = "/*";

		sql = sql.replace('\r', ' ');

		StringBuffer curQuery = new StringBuffer();

		boolean isInLiteral = false;
		boolean isInMultiLineComment = false;
		boolean isInLineComment = false;
		int literalSepCount = 0;


		for (int i = 0; i < sql.length(); ++i)
		{
			char c = sql.charAt(i);

			if(false == isInLiteral)
			{
				///////////////////////////////////////////////////////////
				// Handling of comments

				// We look backwards
				if(isInLineComment && sql.startsWith("\n", i - "\n".length()))
				{
					isInLineComment = false;
				}

				// We look backwards
				if(isInMultiLineComment && sql.startsWith(MULTI_LINE_COMMENT_END, i - MULTI_LINE_COMMENT_END.length()))
				{
					isInMultiLineComment = false;
				}


				if(false == isInLineComment && false == isInMultiLineComment)
				{
					// We look forward
					isInMultiLineComment = sql.startsWith(MULTI_LINE_COMMENT_BEGIN, i);
					isInLineComment = sql.startsWith(lineCommentBegin, i);

					if(isInMultiLineComment)
					{
						// skip ahead so the cursor is now immediately after the begin comment string
						i+=MULTI_LINE_COMMENT_BEGIN.length()+1;
					}
				}

				if((isInMultiLineComment && removeMultiLineComment) || isInLineComment)
				{
					// This is responsible that comments are not in curQuery
					continue;
				}
				//
				////////////////////////////////////////////////////////////
			}

			curQuery.append(c);

			if ('\'' == c)
			{
				if(false == isInLiteral)
				{
					isInLiteral = true;
				}
				else
				{
					++literalSepCount;
				}
			}
			else
			{
				if(0 != literalSepCount % 2)
				{
					isInLiteral = false;
				}
				literalSepCount = 0;
			}


			int querySepLen = getLenOfQuerySepIfAtLastCharOfQuerySep(sql, i, querySep,isInLiteral);

			if(-1 < querySepLen)
			{
				int newLength = curQuery.length() - querySepLen;
				if(-1 < newLength && curQuery.length() > newLength)
				{
					curQuery.setLength(newLength);

					String newQuery = curQuery.toString().trim();
					if(0 < newQuery.length())
					{
						_queries.add(curQuery.toString().trim());
					}
				}
				curQuery.setLength(0);
			}
		}

		String lastQuery = curQuery.toString().trim();
		if(0 < lastQuery.length())
		{
			_queries.add(lastQuery.toString().trim());
		}


		_queryIterator = _queries.iterator();
	}


	private int getLenOfQuerySepIfAtLastCharOfQuerySep(String sql, int i, String querySep, boolean inLiteral)
	{
		if(inLiteral)
		{
			return -1;
		}

		char c = sql.charAt(i);

		if(1 == querySep.length() && c == querySep.charAt(0))
		{
			return 1;
		}
		else
		{
			int fromIndex = i - querySep.length();
			if(0 > fromIndex)
			{
				return -1;
			}

			int querySepIndex = sql.indexOf(querySep, fromIndex);

			if(0 > querySepIndex)
			{
				return -1;
			}

			if(Character.isWhitespace(c))
			{
				if(querySepIndex + querySep.length() == i)
				{
					if(0 == querySepIndex)
					{
						return querySep.length() + 1;
					}
					else if(Character.isWhitespace(sql.charAt(querySepIndex - 1)))
					{
						return querySep.length() + 2;
					}
				}
			}
			else if(sql.length() -1 == i)
			{
				if(querySepIndex + querySep.length() - 1 == i)
				{
					if(0 == querySepIndex)
					{
						return querySep.length();
					}
					else if(Character.isWhitespace(sql.charAt(querySepIndex - 1)))
					{
						return querySep.length() + 1;
					}
				}
			}

			return -1;
		}
	}


	public boolean hasQuery()
	{
		return _queryIterator.hasNext();
	}

	public String nextQuery()
	{
		return (String) _queryIterator.next();
	}


//	public static void main(String[] args)
//	{
//		//String sql = "A'''' sss ;  GO ;; GO'";
//		String sql = "A\n--x\n--y\n/*\n*/B";
//		//String sql = "GO GO";
//
//		QueryTokenizer qt = new QueryTokenizer(sql, "GO", "--");
//
//		while(qt.hasQuery())
//		{
//			System.out.println(">" + qt.nextQuery() + "<");
//		}
//	}
}
