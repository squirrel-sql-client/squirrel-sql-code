package net.sourceforge.squirrel_sql.plugins.netezza.tokenizer;

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

import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ITokenizerFactory;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.TokenizerSessPropsInteractions;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class is loaded by the Netezza Plugin and registered with all Netezza Sessions as the query tokenizer
 * if the plugin is loaded. It handles some of the syntax allowed in Netezza scripts that would be hard to
 * parse in a generic way for any database. It handles create statements for stored procedures.
 */
public class NetezzaQueryTokenizer extends QueryTokenizer implements IQueryTokenizer
{
	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(NetezzaQueryTokenizer.class);

	private static final String PROCEDURE_PATTERN =
		"^\\s*CREATE\\s+PROCEDURE.*|^\\s*CREATE\\s+OR\\s+REPLACE\\s+PROCEDURE\\s+.*";

	private Pattern procPattern = Pattern.compile(PROCEDURE_PATTERN, Pattern.DOTALL);

	private IQueryTokenizerPreferenceBean _prefs = null;

	public NetezzaQueryTokenizer(IQueryTokenizerPreferenceBean prefs)
	{
		super(prefs.getStatementSeparator(), prefs.getLineComment(), prefs.isRemoveMultiLineComments());
		_prefs = prefs;
	}

	public void setScriptToTokenize(String script)
	{
		super.setScriptToTokenize(script);

		// Since it is likely to have the procedure separator on it's own line,
		// and it is key to letting us know that proceeding statements form a
		// multi-statement procedure or function, it deserves it's own place in
		// the _queries arraylist. If it is followed by other procedure or
		// function creation blocks, we may fail to detect that, so this just
		// goes through the list and breaks apart statements on newline so that
		// this cannot happen.
		breakApartNewLines();

		// Netezza allows statement separators in procedure blocks. The process
		// of tokenizing above renders these procedure blocks as separate
		// statements, which are not valid to be executed separately. Here, we
		// re-assemble any create procedure statements that we
		// find using the beginning procedure block pattern and the procedure
		// separator.
		joinFragments(procPattern, false);

		_queryIterator = _queries.iterator();
	}

	/**
	 * Sets the ITokenizerFactory which is used to create additional instances of the IQueryTokenizer - this is
	 * used for handling file includes recursively.
	 */
	protected void setFactory()
	{
		_tokenizerFactory = new ITokenizerFactory()
		{
			public IQueryTokenizer getTokenizer()
			{
				return new NetezzaQueryTokenizer(_prefs);
			}
		};
	}

	/**
	 * This will loop through _queries and break apart lines that look like <code> 
	 * 
	 * list: 
	 *   element: <sep>\n\ncreate proc...
	 *    
	 * into 
	 * 
	 * list: 
	 *   element: <sep> 
	 *   element: create proc...
	 * 
	 * </code>
	 */
	private void breakApartNewLines()
	{
		ArrayList<String> tmp = new ArrayList<String>();
		String procSep = _prefs.getProcedureSeparator();
		for (Iterator<String> iter = _queries.iterator(); iter.hasNext();)
		{
			String next = iter.next();
			if (next.startsWith(procSep))
			{
				tmp.add(procSep);
				String[] parts = next.split(procSep + "\\n+");
				for (int i = 0; i < parts.length; i++)
				{
					if (!"".equals(parts[i]) && !procSep.equals(parts[i]))
					{
						tmp.add(parts[i]);
					}
				}
			}
			else if (next.endsWith(procSep))
			{
				String chopped = StringUtilities.chop(next);
				tmp.add(chopped);
				tmp.add(procSep);
			}
			else
			{
				tmp.add(next);
			}
		}
		_queries = tmp;
	}

	/**
	 * This will scan the _queries list looking for fragments matching the specified pattern and will combine
	 * successive fragments until the procedure separator is encountered which indicated the end of the code
	 * block.
	 * 
	 * @param skipStraySep
	 *           if we find a slash before matching a pattern and this is true, we will exclude it from our
	 *           list of sql queries.
	 */
	private void joinFragments(Pattern pattern, boolean skipStraySep)
	{

		boolean inMultiSQLStatement = false;
		StringBuffer collector = null;
		ArrayList<String> tmp = new ArrayList<String>();
		String procSep = _prefs.getProcedureSeparator();
		String stmtSep = _prefs.getStatementSeparator();
		for (Iterator<String> iter = _queries.iterator(); iter.hasNext();)
		{
			String next = iter.next();
			if (pattern.matcher(next.toUpperCase()).matches())
			{
				inMultiSQLStatement = true;
				collector = new StringBuffer(next);
				collector.append(stmtSep);
				continue;
			}
			if (next.startsWith(procSep))
			{
				inMultiSQLStatement = false;
				if (collector != null)
				{
					// For Netezza, the procedure and statement separator must be appended - they are part of the 
					// statement.
					collector.append(procSep);
					collector.append(stmtSep);
					tmp.add(collector.toString());
					collector = null;
				}
				else
				{
					if (skipStraySep)
					{
						// Stray sep - or we failed to find pattern
						if (s_log.isDebugEnabled())
						{
							s_log.debug("Detected stray procedure separator(" + procSep + "). Skipping");
						}
					}
					else
					{
						tmp.add(next);
					}
				}
				continue;
			}
			if (inMultiSQLStatement)
			{
				collector.append(next);
				collector.append(stmtSep);
				continue;
			}
			tmp.add(next);
		}
		_queries = tmp;
	}

	@Override
	public TokenizerSessPropsInteractions getTokenizerSessPropsInteractions()
	{
		if (_prefs.isInstallCustomQueryTokenizer())
		{
			TokenizerSessPropsInteractions ret = new TokenizerSessPropsInteractions();
			ret.setTokenizerDefinesRemoveMultiLineComment(true);
			ret.setTokenizerDefinesStartOfLineComment(true);
			ret.setTokenizerDefinesStatementSeparator(true);

			return ret;
		}
		else
		{
			return super.getTokenizerSessPropsInteractions();
		}
	}

}
