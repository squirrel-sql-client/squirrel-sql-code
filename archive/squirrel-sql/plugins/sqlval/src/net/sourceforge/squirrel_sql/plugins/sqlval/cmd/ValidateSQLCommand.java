package net.sourceforge.squirrel_sql.plugins.sqlval.cmd;

/*
 * Copyright (C) 2002 Colin Bell
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
import com.mimer.ws.validateSQL.ValidatorResult;

import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSession;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceValidator;

/**
 * This <CODE>ICommand</CODE> will validate the passed SQL.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ValidateSQLCommand implements ICommand
{
	private final WebServicePreferences _prefs;
	private final WebServiceSessionProperties _sessionProps;
	private final String _sql;
	private final char _stmtSepChar;
	private final String _solComment;
	private String _results;

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ValidateSQLCommand.class);

	public ValidateSQLCommand(WebServicePreferences prefs,
				WebServiceSessionProperties sessionProps, String sql,
				char stmtSepChar, String solComment)
	{
		super();
		_prefs = prefs;
		_sessionProps = sessionProps;
		_sql = sql;
		_stmtSepChar = stmtSepChar;
		_solComment = solComment;
	}

	public void openSession(WebServiceSession info)
	{
		if (info == null)
		{
			throw new IllegalArgumentException("ValidationInfo == null");
		}
	}

	public String getResults()
	{
		return _results;
	}

	public void execute() throws BaseException
	{
		try
		{
			// Open connection to the webservice.
			WebServiceSession wss = new WebServiceSession(_prefs,_sessionProps);
			wss.open();

			final WebServiceValidator val = new WebServiceValidator(wss, _sessionProps);
			final QueryTokenizer qt = new QueryTokenizer(_sql, _stmtSepChar, _solComment);

			final StringBuffer results = new StringBuffer(1024);
			while (qt.hasQuery())
			{
				// TODO: When message are can have some text in red (error)
				// and some normal then put out errors in red.
				ValidatorResult rc = val.validate(qt.nextQuery());
				results.append(rc.getData());
			}
			_results = results.toString().trim();

		}
		catch (Throwable th)
		{
			throw new BaseException(th);
		}
	}

}