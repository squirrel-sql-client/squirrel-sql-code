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

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.EditableSqlCheck;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.BaseDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.PostgresPlugin;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

public class ExplainTab extends JPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExplainTab.class);

	static interface i18n
	{
		String CAN_NOT_EDIT = s_stringMgr.getString("ExplainTab.cannotedit");

	}

	/** Current session. */
	private final ISession _session;

	/** Parent Explain Executer Panel */
	private final ExplainExecutorPanel _parent;

	private SQLExecutionInfo _info;

	private ResultSetDataSet _rsds;

	private IDataSetUpdateableTableModel _model;

	/** The sql query (without the explain command) */
	private String _query;

	/** Scroll pane for the SQL results. */
	private final JScrollPane _resultSetSp = new JScrollPane();

	private boolean _allowsEditing;

	public ExplainTab(ISession session, ExplainExecutorPanel parent, ResultSetDataSet rsds,
                     SQLExecutionInfo info, IDataSetUpdateableTableModel model)
	{
		_session = session;
		_parent = parent;
		_rsds = rsds;
		_info = info;
		_model = model;

		init();
		createGUI();
	}

	private void init()
	{
		_query = _info.getSQL().substring(ExplainExecutorPanel.EXPLAIN_PREFIX.length());

		_model.addListener(new DataSetUpdateableTableModelListener()
		{
			@Override
			public void forceEditMode(boolean mode)
			{
				onForceEditMode(mode);
			}
		});

		_allowsEditing = new EditableSqlCheck(_info, _session).allowsEditing();

		if (_allowsEditing) setResultSetMode(_session.getProperties().getSQLResultsOutputClassName());
		else setResultSetMode(_session.getProperties().getReadOnlySQLResultsOutputClassName());
	}

	public void reInit(ResultSetDataSet rsds, SQLExecutionInfo info, IDataSetUpdateableTableModel model)
	{
		_rsds = rsds;
		_info = info;
		_model = model;

		final JScrollPane old_resultSetSp = _resultSetSp;

		init();

		_resultSetSp.setBorder(BorderFactory.createEmptyBorder());

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				remove(old_resultSetSp);
				add(_resultSetSp, BorderLayout.CENTER);
			}
		});
	}

	private void createGUI()
	{
		setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2, 0, 0));
		buttonPanel.add(new TabButton(new RerunAction()));
		buttonPanel.add(new TabButton(new CloseAction()));

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BorderLayout());
		headerPanel.add(buttonPanel, BorderLayout.EAST);
		headerPanel.add(new JLabel(_query), BorderLayout.CENTER);
		add(headerPanel, BorderLayout.NORTH);

		_resultSetSp.setBorder(BorderFactory.createEmptyBorder());
		add(_resultSetSp, BorderLayout.CENTER);
	}

	public String getTitle()
	{
		if (_query.length() > 20) { return _query.substring(0, 20); }
		return _query;
	}

	public String getToolTip()
	{
		return _query;
	}

	public String getQuery()
	{
		return _query;
	}

	private void onForceEditMode(boolean editable)
	{
		if (editable && !_allowsEditing)
		{
			JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), i18n.CAN_NOT_EDIT);
			return;
		}

		if(editable)
		{
			setResultSetMode(SessionProperties.IDataSetDestinations.READ_ONLY_TABLE);
		}
		else
		{
			setResultSetMode(_session.getProperties().getReadOnlySQLResultsOutputClassName());
		}
	}

	private void setResultSetMode(final String outputClassName)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final IDataSetViewer resultSetOutput =
					BaseDataSetViewerDestination.createInstance(outputClassName, _model, _session);
				_resultSetSp.setViewportView(resultSetOutput.getComponent());
				_resultSetSp.setRowHeader(null);
				_rsds.resetCursor();

				try
				{
					resultSetOutput.show(_rsds, null);
				}
				catch (DataSetException e)
				{
					throw new RuntimeException(e);
				}
			}
		});
	}

	private final class TabButton extends JButton
	{

		public TabButton(Action action)
		{
			super(action);
			setMargin(new Insets(0, 0, 0, 0));
			setBorderPainted(false);
			setText("");
		}
	}

	private class CloseAction extends SquirrelAction
	{

		CloseAction()
		{
			super(_session.getApplication(), new MyResources(PostgresPlugin.class.getName(),
				PostgresPlugin.class.getClassLoader()));
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			_parent.closeTab(ExplainTab.this);
		}
	}

	private class RerunAction extends SquirrelAction
	{

		RerunAction()
		{
			super(_session.getApplication(), new MyResources(PostgresPlugin.class.getName(),
				PostgresPlugin.class.getClassLoader()));
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			_parent.reRunTab(ExplainTab.this);
		}
	}

	private class MyResources extends Resources
	{
		protected MyResources(String rsrcBundleBaseName, ClassLoader cl)
		{
			super(rsrcBundleBaseName, cl);
		}
	}
}
