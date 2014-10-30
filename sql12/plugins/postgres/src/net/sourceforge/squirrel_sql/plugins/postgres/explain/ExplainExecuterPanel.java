package net.sourceforge.squirrel_sql.plugins.postgres.explain;

/*
 * Copyright (C) 2007 Daniel Regli & Yannick Winiger
 * http://sourceforge.net/projects/squirrel-sql
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLExecutionException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ExplainExecuterPanel extends JPanel implements ISQLResultExecuter
{
	static final String EXPLAIN_PREFIX = "EXPLAIN ANALYZE ";

	private static final long serialVersionUID = 9155604319585792834L;

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(ExplainExecuterPanel.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExplainExecuterPanel.class);

	static interface i18n
	{
		String TITLE = s_stringMgr.getString("ExplainExecuterPanel.title");

		String CLOSE = s_stringMgr.getString("ExplainExecuterPanel.close");

		String CLOSE_ALL_BUT_THIS = s_stringMgr.getString("ExplainExecuterPanel.closeAllButThis");

		String CLOSE_ALL = s_stringMgr.getString("ExplainExecuterPanel.closeAll");
	}

	/** Current session. */
	private final ISession _session;

	/** Current session's dialect type */
	private final DialectType _dialectType;

	/** Each tab is an <TT>ExplainTab</TT> showing the explain result of a query. */
	private JTabbedPane _tabbedExecutionsPanel;

	public ExplainExecuterPanel(ISession session)
	{
		_session = session;
		_dialectType = DialectFactory.getDialectType(_session.getMetaData());
		_session.getProperties().addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				onPropertyChange(evt.getPropertyName());
			}
		});

		createGUI();
	}

	private void createGUI()
	{
		setLayout(new BorderLayout());

		_tabbedExecutionsPanel =
			UIFactory.getInstance().createTabbedPane(_session.getProperties().getSQLExecutionTabPlacement());

		initTabPopup();

		add(_tabbedExecutionsPanel, BorderLayout.CENTER);
	}

	@Override
	public void execute(ISQLEntryPanel parent)
	{
		String sql = parent.getSQLToBeExecuted();
		if (sql == null || sql.length() == 0)
			return;

			SQLExecuterTask executer =
				new SQLExecuterTask(_session, getExplainSql(sql), new SQLExecutionHandler());
			_session.getApplication().getThreadPool().addTask(executer);
	}

	private String getExplainSql(String sql) 
	{
		StringBuilder result = new StringBuilder();
		IQueryTokenizer tokenizer = _session.getQueryTokenizer();
		tokenizer.setScriptToTokenize(sql);
		while (tokenizer.hasQuery())
		{
			String query = tokenizer.nextQuery();
				result.append("BEGIN").append(tokenizer.getSQLStatementSeparator());
				result.append(EXPLAIN_PREFIX).append(query).append(tokenizer.getSQLStatementSeparator());
				result.append("ROLLBACK").append(tokenizer.getSQLStatementSeparator());
		}
		if (s_log.isDebugEnabled())
		{
			s_log.debug("getExplainSql - Input: " + sql);
			s_log.debug("getExplainSql - Querys: " + tokenizer.getQueryCount());
			s_log.debug("getExplainSql - Result: " + result);
		}
		return result.toString();
	}

	private void addExplainTab(ResultSetDataSet rsds, SQLExecutionInfo info, IDataSetUpdateableTableModel model)
	{
		final ExplainTab tab = new ExplainTab(_session, this, rsds, info, model);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				secureAddTab(tab);
			}
		});
	}

	public void closeTab(final ExplainTab tab)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				_tabbedExecutionsPanel.removeTabAt(_tabbedExecutionsPanel.indexOfComponent(tab));
			}
		});
	}

	public void closeTabAt(final int index)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				_tabbedExecutionsPanel.removeTabAt(index);
			}
		});
	}

	private void secureAddTab(ExplainTab tab)
	{
		SessionProperties props = _session.getProperties();

		if (s_log.isDebugEnabled())
		{
			s_log.debug("secureAddTab - TabCount: " + _tabbedExecutionsPanel.getTabCount());
			s_log.debug("secureAddTab - Limited?: " + props.getLimitSQLResultTabs());
			s_log.debug("secureAddTab - TabLimit: " + props.getSqlResultTabLimit());
		}

		if (props.getLimitSQLResultTabs()
			&& props.getSqlResultTabLimit() <= _tabbedExecutionsPanel.getTabCount())
		{
			closeTabAt(0);
		}
		_tabbedExecutionsPanel.addTab(tab.getTitle(), null, tab, tab.getToolTip());
		_tabbedExecutionsPanel.setSelectedComponent(tab);
	}

	private void onPropertyChange(String propertyName)
	{
		if (propertyName.equals(SessionProperties.IPropertyNames.SQL_EXECUTION_TAB_PLACEMENT))
		{
			_tabbedExecutionsPanel.setTabPlacement(_session.getProperties().getSQLExecutionTabPlacement());
		}
	}

	private void initTabPopup()
	{
		final JPopupMenu popup = new JPopupMenu();

		JMenuItem close = new JMenuItem(i18n.CLOSE);
		close.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_tabbedExecutionsPanel.remove(_tabbedExecutionsPanel.getSelectedIndex());
			}
		});
		popup.add(close);

		JMenuItem closeAllButThis = new JMenuItem(i18n.CLOSE_ALL_BUT_THIS);
		closeAllButThis.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (Component c : _tabbedExecutionsPanel.getComponents())
				{
					if (c != _tabbedExecutionsPanel.getSelectedComponent())
						_tabbedExecutionsPanel.remove(c);
				}
			}
		});
		popup.add(closeAllButThis);

		JMenuItem closeAll = new JMenuItem(i18n.CLOSE_ALL);
		closeAll.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_tabbedExecutionsPanel.removeAll();
			}
		});
		popup.add(closeAll);

		_tabbedExecutionsPanel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				showPopup(e, popup);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				showPopup(e, popup);
			}
		});
	}

	private void showPopup(MouseEvent e, JPopupMenu popup)
	{
		if (e.isPopupTrigger())
		{
			int index =
				_tabbedExecutionsPanel.getUI().tabForCoordinate(_tabbedExecutionsPanel, e.getX(), e.getY());
			if (-1 != index)
			{
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	public void reRunTab(ExplainTab tab)
	{
			SQLExecuterTask executer =
				new SQLExecuterTask(_session, getExplainSql(tab.getQuery()), new SQLExecutionHandler(tab));
			_session.getApplication().getThreadPool().addTask(executer);
	}

	/** This class is the handler for the execution of sql against the ExplainPanel */
	private class SQLExecutionHandler implements ISQLExecuterHandler
	{
		ExplainTab _tabToReplace;

		public SQLExecutionHandler()
		{
		}

		public SQLExecutionHandler(ExplainTab tabToReplace)
		{
			_tabToReplace = tabToReplace;
		}

		@Override
		public void sqlToBeExecuted(final String sql)
		{
		}

		@Override
		public void sqlExecutionComplete(SQLExecutionInfo exInfo, int processedStatementCount,
			int statementCount)
		{
			double executionLength = ((double) exInfo.getSQLExecutionElapsedMillis()) / 1000;
			double outputLength = ((double) exInfo.getResultsProcessingElapsedMillis()) / 1000;
			double totalLength = executionLength + outputLength;

			final NumberFormat numberFormat = NumberFormat.getNumberInstance();
			Object[] args =
				new Object[]
				{ processedStatementCount, statementCount, numberFormat.format(totalLength),
						numberFormat.format(executionLength), numberFormat.format(outputLength) };

			_session.showMessage(s_stringMgr.getString("ExplainExecuterPanel.queryStatistics", args));
		}

		@Override
		public void sqlExecutionCancelled()
		{
		}

		@Override
		public void sqlDataUpdated(int updateCount)
		{
		}

		@Override
		public void sqlResultSetAvailable(ResultSetWrapper rs, SQLExecutionInfo info,
			IDataSetUpdateableTableModel model) throws DataSetException
		{

			ResultSetDataSet rsds = new ResultSetDataSet();
			rsds.setResultSet(rs.getResultSet(), _dialectType);

			if (_tabToReplace != null)
			{
				_tabToReplace.reInit(rsds, info, model);
			} else
			{
				addExplainTab(rsds, info, model);
			}
      }

		@Override
		public void sqlExecutionWarning(SQLWarning warn)
		{
			_session.showMessage(warn);
		}

		@Override
		public void sqlStatementCount(int statementCount)
		{
		}

		@Override
		public String sqlExecutionException(Throwable th, String postErrorString)
		{
			String message = _session.formatException(new SQLExecutionException(th, postErrorString));
			try {
				_session.getSQLConnection().createStatement().executeQuery("ROLLBACK;");
			} catch (SQLException rollbackException) {
				message +=  rollbackException.getMessage();
			}
			
			_session.showErrorMessage(message);

			if (_session.getProperties().getWriteSQLErrorsToLog())
			{
				s_log.info(message);
			}

         return message;
		}

      @Override
      public void sqlCloseExecutionHandler(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }
   }

	@Override
	public String getTitle()
	{
		return i18n.TITLE;
	}

	@Override
	public JComponent getComponent()
	{
		return this;
	}

	@Override
	public IResultTab getSelectedResultTab()
	{
		throw new UnsupportedOperationException("ExplainExecuter has no ResultTabs");
	}
}
