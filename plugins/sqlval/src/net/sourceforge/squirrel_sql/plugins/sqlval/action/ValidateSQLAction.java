package net.sourceforge.squirrel_sql.plugins.sqlval.action;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlval.LogonDialog;
import net.sourceforge.squirrel_sql.plugins.sqlval.SQLValidatorPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;
import net.sourceforge.squirrel_sql.plugins.sqlval.cmd.ValidateSQLCommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
/**
 * This action will validate the current SQL.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ValidateSQLAction extends SquirrelAction implements ISessionAction
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ValidateSQLAction.class);


	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ValidateSQLAction.class);

	/** Preferences. */
	private final WebServicePreferences _prefs;

	/** Current plugin. */
	private final SQLValidatorPlugin _plugin;

	/** Current session. */
	private ISession _session;

	/**
	 * Ctor.
	 * 
	 * @param	app		Application API.
	 * @param	rsrc	Resources to build this action from.
	 * @param	prefs	Plugin preferences.
	 * @param	plugin	Plugin
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT>WebServicePreferences</TT> passed.
	 */
	public ValidateSQLAction(IApplication app, Resources rsrc,
									WebServicePreferences prefs,
									SQLValidatorPlugin plugin)
	{
		super(app, rsrc);
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		_prefs = prefs;
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			final WebServiceSessionProperties sessionProps = _plugin.getWebServiceSessionProperties(_session);
			if (!sessionProps.getWebServiceSession().isOpen())
			{
				JDialog dlog = new LogonDialog(_session, _prefs, sessionProps);
				dlog.setVisible(true);
			}

			if (sessionProps.getWebServiceSession().isOpen())
			{
				validateSQL();
			}
		}
	}

	public void setSession(ISession session)
	{
		_session = session;
	}

	private void validateSQL()
	{
		final ISQLPanelAPI api = _session.getSessionInternalFrame().getSQLPanelAPI();
		final String sql = api.getSQLScriptToBeExecuted();
		if (sql != null && sql.trim().length() > 0)
		{
			final WebServiceSessionProperties wssProps = _plugin.getWebServiceSessionProperties(_session);
			final String stmtSep= _session.getQueryTokenizer().getSQLStatementSeparator();
			final String solComment = _session.getQueryTokenizer().getLineCommentBegin();
			final ValidationProps valProps = new ValidationProps(_prefs, wssProps,
													sql, stmtSep, solComment, _session);
			new Executor(_session.getApplication(), valProps, _session.getProperties()).execute();
		}
		else
		{
			// i18n[sqlval.noSql=No SQL specified]
			_session.showErrorMessage(s_stringMgr.getString("sqlval.noSql"));
		}
	}

	static final class ValidationProps
	{
		final WebServicePreferences _prefs;
		final WebServiceSessionProperties _sessionProps;
		final String _sql;
		final String _stmtSep;
		final String _solComment;
        final ISession _session;

		ValidationProps(WebServicePreferences prefs,
						WebServiceSessionProperties sessionProps,
                        String sql, String stmtSep,
						String solComment, ISession session)
		{
			super();
			_prefs = prefs;
			_sessionProps = sessionProps;
			_sql = sql;
			_stmtSep= stmtSep;
			_solComment = solComment;
            _session = session;
		}
	}

	static class Executor implements ICommand
	{
		private final IApplication _app;
		private final ValidationProps _valProps;
      private SessionProperties _sessionProperties;

      Executor(IApplication app, ValidationProps valProps, SessionProperties sessionProperties)
      {
         super();
         _app = app;
         _valProps = valProps;
         _sessionProperties = sessionProperties;
      }

		public void execute()
		{
			ValidateSQLCommand cmd = new ValidateSQLCommand(_valProps._prefs,
											_valProps._sessionProps,
											_valProps._sql, _valProps._stmtSep,
											_valProps._solComment,
											_sessionProperties,
                                            _valProps._session);
			try
			{
				cmd.execute();
                _valProps._session.showMessage(cmd.getResults());
			}
			catch (Throwable th)
			{
				final String msg = "Error occured when talking to the web service";
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}
}

