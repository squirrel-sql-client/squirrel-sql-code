package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
public class QueryTokenizer
{
    private char _querySepChar;

    protected String _sQuerys;

    protected String _sNextQuery;

    /**
     * QueryTokenizer constructor comment.
     */
    public QueryTokenizer(String sql, char querySepChar)
    {
        super();
        _querySepChar = querySepChar;
        if(sql != null)
        {
            _sQuerys = sql.trim();
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
                return replaceLineFeeds(sNextQuery);
            }
        }
        String sNextQuery = _sQuerys.substring(0,iIndex1);
        _sQuerys = _sQuerys.substring(iIndex1+1).trim();
        return replaceLineFeeds(sNextQuery);
    }

    public String replaceLineFeeds(String sql)
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
                    sbReturn.append("\\n");
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