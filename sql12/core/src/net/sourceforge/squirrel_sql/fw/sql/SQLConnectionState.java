package net.sourceforge.squirrel_sql.fw.sql;
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

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.ISessionProperties;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


/**
 * This class will save the state of an <TT>SQLConnection</TT> and
 * can apply the saved state to another <TT>SQLConnection</TT>. 
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLConnectionState
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLConnectionState.class);
	
	private final static ILogger s_log = LoggerController.createLogger(SQLConnectionState.class);

	private Integer _transIsolation;
	private String _catalog;
	private boolean _autoCommit;
	private SQLDriverPropertyCollection _connProps;

	public SQLConnectionState()
	{
	}

   public void saveState(ISQLConnection con, ISessionProperties sessionProperties, IMessageHandler msgHandler, String selectedCatalogFromCatalogsComboBox)
	{

		try
		{
			_transIsolation = null;

			if (null != con)
			{
				// The user may be reconnecting because the current connection is dead or unstable.
				// By callWithTimeOut() we make sure we don't get stuck in the calling getTransactionIsolation().
				_transIsolation = Utilities.callWithTimeout(() -> con.getTransactionIsolation());
			}
		}
		catch (Exception ex)
		{
			/*
			 * i18n [SQLConnectionState.errorSavingIsolationState]
			 *  Error saving transaction isolation.\n" +
			 *  "This might happen when reconnecting a Session to restore a broken connection.\n" +
			 *  "The new connection will use the default transaction isolation.";
			 */
			String msg = s_stringMgr.getString("SQLConnectionState.errorSavingIsolationState");

			s_log.error(msg, ex);
			msgHandler.showErrorMessage(msg, ex);
		}

		try
		{
			_catalog = selectedCatalogFromCatalogsComboBox;

			if(null != con)
			{
				_catalog = Utilities.callWithTimeout(() -> con.getCatalog());
			}
		}
		catch (Exception ex)
		{
			/*
			 * i18n [SQLConnectionState.errorSavingCatalog]
				"Error saving current catalog.\n" +
				"This might happen when reconnecting a Session to restore a broken connection.\n" +
				"The new connection will restore the catalog from catalogs combo box.";
			 */
			String msg = s_stringMgr.getString("SQLConnectionState.errorSavingCatalog");

			s_log.error(msg, ex);
			msgHandler.showErrorMessage(msg, ex);
		}

		try
		{
         // In case the connection won't be able to tell its Auto Commit state,
         // this is the best default we have.
         _autoCommit = sessionProperties.getAutoCommit();

         if(null != con)
			{
				_autoCommit = Utilities.callWithTimeout(() -> con.getAutoCommit());
			}
		}
		catch (Exception ex)
		{
			String msg = s_stringMgr.getString("SQLConnectionState.errorSavingAutoCommit");

			s_log.error(msg, ex);
			msgHandler.showErrorMessage(msg, ex);
		}

		try
		{
			_connProps = null;

			if(null != con)
			{
				_connProps = Utilities.callWithTimeout(() -> con.getConnectionProperties());
			}
		}
		catch (Exception e)
		{
			s_log.error(e);
			msgHandler.showErrorMessage(e);
		}

	}

	public void restoreState(ISQLConnection conn, IMessageHandler msgHandler)
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		if (_transIsolation != null)
		{
			try
			{
				conn.setTransactionIsolation(_transIsolation.intValue());
			}
			catch (Exception ex)
			{
				s_log.error("Error restoring transaction isolation", ex);
				msgHandler.showErrorMessage(ex, null);
			}
		}

		if (_catalog != null)
		{
			try
			{
				conn.setCatalog(_catalog);
			}
			catch (Exception ex)
			{
				s_log.error("Error restoring current catalog", ex);
				msgHandler.showErrorMessage(ex, null);
			}
		}

		try
		{
			conn.setAutoCommit(_autoCommit);
		}
		catch (Exception ex)
		{
			s_log.error("Error restoring autocommit", ex);
			msgHandler.showErrorMessage(ex, null);
		}
	}

	public SQLDriverPropertyCollection getConnectionProperties()
	{
		return _connProps;
	}

	public boolean getAutoCommit()
	{
		return _autoCommit;
	}
}

