package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.sql.Connection;
import java.util.Date;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This is the tab displaying connection status information.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectionStatusTab extends BaseDataSetTab
{
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ConnectionStatusTab.class);
    
	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
        // i18n[ConnectionStatusTab.title=Status]
		return s_stringMgr.getString("ConnectionStatusTab.title");
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
        // i18n[ConnectionStatusTab.hint=Connection Status]
		return s_stringMgr.getString("ConnectionStatusTab.hint");
	}

	/**
	 * Create the <TT>IDataSet</TT> to be displayed in this tab.
	 */
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISession session = getSession();
		final ISQLConnection conn = session.getSQLConnection();
		return new JavabeanDataSet(new ConnectionInfo(conn, session));
	}

	/**
	 * Java bean containing connection status information.
	 */
	public static final class ConnectionInfo
	{
		private String _catalog;
		private boolean _isReadOnly;
		private boolean _isClosed;
		private boolean _autoCommit;
		private Date _timeOpened;
		private String _transIsol;

		ConnectionInfo(ISQLConnection conn, ISession session)
		{
			super();
			Connection jdbcConn = conn.getConnection();

			try
			{
				_isClosed = jdbcConn.isClosed();
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			try
			{
				_isReadOnly = jdbcConn.isReadOnly();
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			try
			{
				_catalog = conn.getCatalog();
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			try
			{
				_autoCommit = conn.getAutoCommit();
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			try
			{
				final int isol = jdbcConn.getTransactionIsolation();
				switch(isol)
				{
					case Connection.TRANSACTION_NONE:
						_transIsol = "TRANSACTION_NONE";
						break;
					case Connection.TRANSACTION_READ_COMMITTED:
						_transIsol = "TRANSACTION_READ_COMMITTED";
						break;
					case Connection.TRANSACTION_READ_UNCOMMITTED:
						_transIsol = "TRANSACTION_READ_UNCOMMITTED";
						break;
					case Connection.TRANSACTION_REPEATABLE_READ:
						_transIsol = "TRANSACTION_REPEATABLE_READ";
						break;
					case Connection.TRANSACTION_SERIALIZABLE:
						_transIsol = "TRANSACTION_SERIALIZABLE";
						break;
					default:
						_transIsol = "Unknown: " + isol;
						break;
				}
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			_timeOpened = conn.getTimeOpened();
		}

		public String getCatalog()
		{
			return _catalog;
		}

		public boolean isReadOnly()
		{
			return _isReadOnly;
		}

		public boolean isClosed()
		{
			return _isClosed;
		}

		public boolean getAutoCommit()
		{
			return _autoCommit;
		}

		public Date getTimeOpened()
		{
			return _timeOpened;
		}

		public String getTransactionisolationLevel()
		{
			return _transIsol;
		}
	}
}
