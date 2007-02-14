package net.sourceforge.squirrel_sql.plugins.oracle.tokenizer;
/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 * 
 * Based on initial work from Johan Compagner.
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
import java.util.regex.Pattern;

import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ITokenizerFactory;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;

public class OracleQueryTokenizer extends QueryTokenizer implements IQueryTokenizer
{
    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(OracleQueryTokenizer.class);
    
    private static final String PROCEDURE_PATTERN = 
        "^\\s*CREATE\\s+PROCEDURE.*|^\\s*CREATE\\s+OR\\s+REPLACE\\s+PROCEDURE\\s+.*";

    private static final String FUNCTION_PATTERN = 
        "^\\s*CREATE\\s+FUNCTION.*|^\\s*CREATE\\s+OR\\s+REPLACE\\s+FUNCTION\\s+.*";    
    
    private Pattern procPattern = Pattern.compile(PROCEDURE_PATTERN, Pattern.DOTALL);
    
    private Pattern funcPattern = Pattern.compile(FUNCTION_PATTERN, Pattern.DOTALL);
    
    private static final String ORACLE_SCRIPT_INCLUDE_PREFIX = "@";
    
    private OraclePreferenceBean _prefs = null;
    
	public OracleQueryTokenizer(OraclePreferenceBean prefs)
	{
        super(prefs.getStatementSeparator(),
              prefs.getLineComment(), 
              prefs.isRemoveMultiLineComments());
        _prefs = prefs;
	}

    public void setScriptToTokenize(String script) {
        super.setScriptToTokenize(script);
        
        // Since it is likely to have "/" on it's own line, and it is key to 
        // letting us know that proceeding statements form a multi-statement
        // procedure or function, it deserves it's own place in the _queries
        // arraylist.  If it is followed by other procedure or function creation
        // blocks, we may fail to detect that, so this just goes through the 
        // list and breaks apart statements on newline so that this cannot 
        // happen.
        breakApartNewLines();
        
        // Oracle allows statement separators in PL/SQL blocks.  The process
        // of tokenizing above renders these procedure blocks as separate 
        // statements, which is invalid for Oracle.  Since "/" is the way 
        // in SQL-Plus to denote the end of a procedure or function, we 
        // re-assemble any create procedure statements that we find.
        // This should be done before expanding file includes.  Otherwise, any
        // create sql found in files will already be joined, causing this to 
        // find create SQL without matching "/".  The process of 
        // expanding file includes already joins the sql it finds.
        joinFragments(procPattern, false);
        joinFragments(funcPattern, true);
        
        expandFileIncludes(ORACLE_SCRIPT_INCLUDE_PREFIX);
        
        _queryIterator = _queries.iterator();
    }
    
    /**
     * Sets the ITokenizerFactory which is used to create additional instances
     * of the IQueryTokenizer - this is used for handling file includes
     * recursively.  
     */    
	protected void setFactory() {
	    _tokenizerFactory = new ITokenizerFactory() {
	        public IQueryTokenizer getTokenizer() {
	            return new OracleQueryTokenizer(_prefs);
            }
        };
    }
        
    /** 
     * This will loop through _queries and break apart lines that look like
     * 
     *   /\n\ncreate proc...
     * into
     * 
     *   /
     *   create proc...
     */
    private void breakApartNewLines() {
        ArrayList tmp = new ArrayList();
        String sep = _prefs.getProcedureSeparator();
        for (Iterator iter = _queries.iterator(); iter.hasNext();) {
            String next = (String) iter.next();
            if (next.startsWith(sep)) {
                tmp.add(sep);
                String[] parts = next.split(sep+"\\n+");
                for (int i = 0; i < parts.length; i++) {
                    if (!"".equals(parts[i]) && !sep.equals(parts[i])) {
                        tmp.add(parts[i]);
                    }
                }
            } else {
                tmp.add(next);
            }
        }
        _queries = tmp;
    }
    
    /**
     * This will scan the _queries list looking for fragments matching the 
     * specified pattern and will combine successive fragments until the "/" is 
     * indicating the end of the code block.  This is Oracle-specific.  
     * 
     * @param skipStraySlash if we find a slash before matching a pattern and 
     *                       this is true, we will exclude it from our list of
     *                       sql queries.
     */
    private void joinFragments(Pattern pattern, boolean skipStraySlash) {
        
        boolean inMultiSQLStatement = false;
        StringBuffer collector = null;
        ArrayList tmp = new ArrayList();
        String sep = _prefs.getProcedureSeparator();
        for (Iterator iter = _queries.iterator(); iter.hasNext();) {
            String next = (String) iter.next();
            if (pattern.matcher(next.toUpperCase()).matches()) {
                inMultiSQLStatement = true;
                collector = new StringBuffer(next);
                collector.append(";");
                continue;
            } 
            if (next.startsWith(sep)) {
                inMultiSQLStatement = false;
                if (collector != null) {
                    tmp.add(collector.toString());
                    collector = null;
                } else {
                    if (skipStraySlash) {
                        // Stray sep - or we failed to find pattern
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Detected stray proc separator("+sep+"). Skipping");
                        }
                    } else {
                        tmp.add(next);
                    }
                }
                continue;
            }
            if (inMultiSQLStatement) {
                collector.append(next);
                collector.append(";");
                continue;
            } 
            tmp.add(next);
        }
        _queries = tmp;
    }    
}
