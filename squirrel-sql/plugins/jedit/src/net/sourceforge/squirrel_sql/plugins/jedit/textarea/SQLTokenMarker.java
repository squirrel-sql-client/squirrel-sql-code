/*
 * SQLTokenMarker.java - Generic SQL token marker
 * Copyright (C) 1999 mike dillon
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package net.sourceforge.squirrel_sql.plugins.jedit.textarea;
import javax.swing.text.Segment;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * SQL token marker.
 *
 * @author mike dillon
 * @version $Id: SQLTokenMarker.java,v 1.4 2002-08-07 09:18:33 colbell Exp $
 */
public class SQLTokenMarker extends TokenMarker
{
	protected DatabaseMetaData _dmd;
	
	private HashMap _hmTableColumns = new HashMap();
	
	private int offset, lastOffset, lastKeyword, length;

	// public members
	public SQLTokenMarker(KeywordMap k)
	{
		this(k, false);
	}

	public SQLTokenMarker(KeywordMap k, boolean tsql)
	{
		keywords = k;
		isTSQL = tsql;
	}

	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		offset = lastOffset = lastKeyword = line.offset;
		length = line.count + offset;

loop:
		for(int i = offset; i < length; i++)
		{
			switch(line.array[i])
			{
			case '*':
				if(token == Token.COMMENT1 && length - i >= 1 && line.array[i+1] == '/')
				{
					token = Token.NULL;
					i++;
					addToken((i + 1) - lastOffset,Token.COMMENT1);
					lastOffset = i + 1;
				}
				else if (token == Token.NULL)
				{
					searchBack(line, i);
					addToken(1,Token.OPERATOR);
					lastOffset = i + 1;
				}
				break;
			case '[':
				if(token == Token.NULL)
				{
					searchBack(line, i);
					token = Token.LITERAL1;
					literalChar = '[';
					lastOffset = i;
				}
				break;
			case ']':
				if(token == Token.LITERAL1 && literalChar == '[')
				{
					token = Token.NULL;
					literalChar = 0;
					addToken((i + 1) - lastOffset,Token.LITERAL1);
					lastOffset = i + 1;
				}
				break;
			// colbell - Add ';' so that syntax highlighting works
			// when line terminated by a ';'.
			case '.': case ',': case '(': case ')': case ';':
				if (token == Token.NULL) {
					searchBack(line, i);
					addToken(1, Token.NULL);
					lastOffset = i + 1;
				}
				break;
			case '+': case '%': case '&': case '|': case '^':
			case '~': case '<': case '>': case '=':
				if (token == Token.NULL) {
					searchBack(line, i);
					addToken(1,Token.OPERATOR);
					lastOffset = i + 1;
				}
				break;
			case ' ': case '\t':
				if (token == Token.NULL) {
					searchBack(line, i, false);
				}
				break;
			case ':':
				if(token == Token.NULL)
				{
					addToken((i+1) - lastOffset,Token.LABEL);
					lastOffset = i + 1;
				}
				break;
			case '/':
				if(token == Token.NULL)
				{
					if (length - i >= 2 && line.array[i + 1] == '*')
					{
						searchBack(line, i);
						token = Token.COMMENT1;
						lastOffset = i;
						i++;
					}
					else
					{
						searchBack(line, i);
						addToken(1,Token.OPERATOR);
						lastOffset = i + 1;
					}
				}
				break;
			case '-':
				if(token == Token.NULL)
				{
					if (length - i >= 2 && line.array[i+1] == '-')
					{
						searchBack(line, i);
						addToken(length - i,Token.COMMENT1);
						lastOffset = length;
						break loop;
					}
					else
					{
						searchBack(line, i);
						addToken(1,Token.OPERATOR);
						lastOffset = i + 1;
					}
				}
				break;
			case '!':
				if(isTSQL && token == Token.NULL && length - i >= 2 &&
				(line.array[i+1] == '=' || line.array[i+1] == '<' || line.array[i+1] == '>'))
				{
					searchBack(line, i);
					addToken(1,Token.OPERATOR);
					lastOffset = i + 1;
				}
				break;
			case '"': case '\'':
				if(token == Token.NULL)
				{
					token = Token.LITERAL1;
					literalChar = line.array[i];
					addToken(i - lastOffset,Token.NULL);
					lastOffset = i;
				}
				else if(token == Token.LITERAL1 && literalChar == line.array[i])
				{
					token = Token.NULL;
					literalChar = 0;
					addToken((i + 1) - lastOffset,Token.LITERAL1);
					lastOffset = i + 1;
				}
				break;
			default:
				break;
			}
		}
		if(token == Token.NULL)
			searchBack(line, length, false);
		if(lastOffset != length)
			addToken(length - lastOffset,token);
		return token;
	}

	public KeywordMap SQUIRREL_getKeywordMap() {
		return keywords;
	}

	// protected members
	protected boolean isTSQL = false;

	// private members
	private KeywordMap keywords;
	private char literalChar = 0;

	private void searchBack(Segment line, int pos)
	{
		searchBack(line, pos, true);
	}

	private void searchBack(Segment line, int pos, boolean padNull)
	{
		int len = pos - lastKeyword;
		KeywordMap.Keyword keyword = keywords.lookup(line,lastKeyword,len);
		if(keyword != null)
		{
			if(lastKeyword != lastOffset)
				addToken(lastKeyword - lastOffset,Token.NULL);
			addToken(len,keyword.id);
			lastOffset = pos;
			if(_dmd != null && keyword.id == Token.TABLE)
			{
				if(!_hmTableColumns.containsKey(keyword.keyword))
				{
					ResultSet rs = null;
					try
					{
						rs = _dmd.getColumns(null,null,new String(keyword.keyword),null);
						while(rs.next())
						{
							keywords.add(rs.getString(4),Token.COLOMN);
						}
					} catch(Exception e){} 
					finally
					{
						_hmTableColumns.put(keyword.keyword,"");
						try
						{
							rs.close();
						} catch(Exception e){}
					}
				}
			}
		}
		lastKeyword = pos + 1;
		if (padNull && lastOffset < pos)
			addToken(pos - lastOffset, Token.NULL);
	}
}
