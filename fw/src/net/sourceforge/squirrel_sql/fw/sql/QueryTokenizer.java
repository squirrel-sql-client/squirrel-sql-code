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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class QueryTokenizer
{
	private ArrayList _queries = new ArrayList();
	private Iterator _queryIterator;

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(QueryTokenizer.class);
    
    private static final String pattern = 
        "^\\s*CREATE\\s+PROCEDURE.*|^\\s*CREATE\\s+OR\\s+REPLACE\\s+PROCEDURE\\s+.*";
    
    private Pattern procPattern = Pattern.compile(pattern, Pattern.DOTALL);
    
	public QueryTokenizer(String sql, 
                          String querySep, 
                          String lineCommentBegin, 
                          boolean removeMultiLineComment,
                          boolean isOracle)
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

        if (isOracle) {
            // Oracle allows statement separators in PL/SQL blocks.  The process
            // of tokenizing above renders these procedure blocks as separate 
            // statements, which is invalid for Oracle.  Since "/" is the way 
            // in SQL-Plus to denote the end of a procedure, re-assemble any 
            // create procedure statements that we find.
            joinProcedureFragments();
        }
        
        expandFileIncludes(querySep, 
                           lineCommentBegin, 
                           removeMultiLineComment, 
                           isOracle);

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
    
    /** 
     * This uses statements that begin with "@" to indicate that the following
     * text is a file containing SQL statements that should be loaded.  This 
     * should eventually be made to work with other dbs like MySQL which uses
     * "source" or "\." to indicate an include file. 
     * 
     * @param querySep
     * @param lineCommentBegin
     * @param removeMultiLineComment
     */
    private void expandFileIncludes(String querySep, 
                                    String lineCommentBegin,     
                                    boolean removeMultiLineComment,
                                    boolean isOracle) {
        ArrayList tmp = new ArrayList();
        for (Iterator iter = _queries.iterator(); iter.hasNext();) {
            String sql = (String) iter.next();
            // TODO: make this configurable
            if (sql.startsWith("@")) {
                try {
                    List fileSQL = 
                        getStatementsFromIncludeFile(sql.substring(1),
                                                     querySep,
                                                     lineCommentBegin,
                                                     removeMultiLineComment,
                                                     isOracle);
                    tmp.addAll(fileSQL);
                } catch (Exception e) {
                    s_log.error(
                       "Unexpected error while attempting to include file " +
                       "from "+sql, e);
                }
                
            } else {
                tmp.add(sql);
            }
        }
        _queries = tmp;
    }
    
    private List getStatementsFromIncludeFile(String filename,
                                              String querySep, 
                                              String lineCommentBegin,     
                                              boolean removeMultiLineComment,
                                              boolean isOracle) 
        throws Exception 
    {
        ArrayList result = new ArrayList();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Attemping to open file '"+filename+"'");
        }
        File f = new File(filename);
        /*
        if (f.canRead()) {
        */
            StringBuffer fileLines = new StringBuffer();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String next = reader.readLine();
                while (next != null) {
                    fileLines.append(next);
                    fileLines.append("\n");
                    next = reader.readLine();
                }
            } catch (Exception e) {
                s_log.error(
                    "Unexpected exception while reading lines from file " +
                    "("+filename+")", e);
            }
            if (fileLines.toString().length() > 0) {
                QueryTokenizer qt = new QueryTokenizer(fileLines.toString(),
                                                       querySep,
                                                       lineCommentBegin,
                                                       removeMultiLineComment,
                                                       isOracle);
                for (Iterator iter = qt._queryIterator; iter.hasNext();) {
                    String sql = (String) iter.next();
                    result.add(sql);
                }
            }
            /*
        } else {
            s_log.error("Unable to open file: "+filename+" for reading");
        }
        */
        return result;
    }    
    
    /**
     * This will scan the _queries list looking for CREATE PROCEDURE fragments
     * and will combine successive queries until the "/" is indicating the end
     * of the procedure.  This is Oracle-specific.  Eventually this code should
     * be relocated to the Oracle plugin.
     */
    private void joinProcedureFragments() {
        
        boolean inProcedure = false;
        StringBuffer collector = null;
        ArrayList tmp = new ArrayList();
        for (Iterator iter = _queries.iterator(); iter.hasNext();) {
            String next = (String) iter.next();
            if (procPattern.matcher(next.toUpperCase()).matches()) {
                inProcedure = true;
                collector = new StringBuffer(next);
                collector.append(";");
                continue;
            } 
            if (next.startsWith("/")) {
                inProcedure = false;
                if (collector != null) {
                    tmp.add(collector.toString());
                    // Since "/" isn't the statement separator, check to see if this 
                    // statement is more than just "/" and add the rest.
                    if (next.length() > 1) {
                        tmp.add(next.substring(1));
                    }
                    collector = null;
                } else {
                    // Stray "/" - or we failed to find "CREATE PROCEDURE ..."
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Detected stray slash(/) char. Skipping");
                    }
                }
                continue;
            }
            if (inProcedure) {
                collector.append(next);
                collector.append(";");
                continue;
            } 
            tmp.add(next);
        }
        _queries = tmp;
    }
    
	public boolean hasQuery()
	{
		return _queryIterator.hasNext();
	}

	public String nextQuery()
	{
		return (String) _queryIterator.next();
	}


	public static void main(String[] args)
	{
		//String sql = "A'''' sss ;  GO ;; GO'";
		//String sql = "A\n--x\n--y\n/*\nB";
		//String sql = "GO GO";
	    String sql = "@c:\\tools\\sql\\file.sql";
        
        
		QueryTokenizer qt = new QueryTokenizer(sql, "GO", "--", true, true);

		while(qt.hasQuery())
		{
			System.out.println(">" + qt.nextQuery() + "<");
		}
	}
}
