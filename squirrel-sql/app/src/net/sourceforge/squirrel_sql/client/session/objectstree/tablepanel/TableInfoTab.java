package net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel;
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.datasetviewer.BaseDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfoDataSet;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * This is the tab for table information.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TableInfoTab extends BaseTablePanelTab
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface TableInfoi18n
	{
		String TITLE = "Info";
		String HINT = "Basic information";
		String GET_ROW_COUNT = "Get Row Count";
		String REFRESH_ROW_COUNT = "Refresh Row Count";
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(TableInfoTab.class);

	/** Component to be displayed. */
	private TableInfoComponent _comp;

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
		return TableInfoi18n.TITLE;
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
		return TableInfoi18n.HINT;
	}

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	public synchronized Component getComponent()
	{
		if (_comp == null)
		{
			_comp = new TableInfoComponent();
		}
		return _comp;
	}

	/**
	 * @see BaseObjectPanelTab#clear()
	 */
	public void clear()
	{
		((TableInfoComponent) getComponent()).clear();
	}

	/**
	 * Refresh the component displaying the <TT>ITableInfo</TT> object.
	 */
	public synchronized void refreshComponent() throws IllegalStateException
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		ITableInfo ti = getTableInfo();
		if (ti == null)
		{
			throw new IllegalStateException("Null ITableInfo");
		}
		((TableInfoComponent) getComponent()).load(session, ti);
	}

	/**
	 * Component for this tab.
	 */
	private class TableInfoComponent extends JPanel
	{
		private boolean _fullyCreated = false;
		private JLabel _rowCountLbl = new JLabel("");
		private JLabel _rowCountTitleLbl = new JLabel("Row count:");
		private RowCountButton _rowCountBtn;
		private TableInfoDataSet _ds;
		private IDataSetViewer _viewer;

		TableInfoComponent()
		{
			super(new BorderLayout());
		}

		void clear()
		{
			if (_rowCountLbl != null)
			{
				_rowCountLbl.setText("");
			}
			if (_viewer != null)
			{
				_viewer.clear();
			}
			_rowCountTitleLbl.setVisible(false);
			if (_rowCountBtn != null)
			{
				_rowCountBtn.setText(TableInfoi18n.GET_ROW_COUNT);
			}
		}

		void load(final ISession session, final ITableInfo ti)
		{
			try
			{
				// Lazily create the user interface.
				if (!_fullyCreated)
				{
					createUserInterface();
					_fullyCreated = true;
				}

				_rowCountBtn.setTableInfo(ti);

				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						if (session.getProperties().getShowRowCount())
						{
							new UpdateRowCountCommand(session, ti).execute();
						}
						else
						{
							_rowCountLbl.setText("");
						}

						_ds.setTableInfo(ti);
						try
						{
							_viewer.show(_ds);
						}
						catch (DataSetException dse)
						{
							s_log.error("Error", dse);
						}
					}
				});			}
			catch (Exception ex)
			{
				_rowCountLbl.setText("<error>");
				s_log.error("Error", ex);
			}
		}

		private void setRowCountText(String nbrRows)
		{
			_rowCountLbl.setText(nbrRows);
			_rowCountBtn.setText(TableInfoTab.TableInfoi18n.REFRESH_ROW_COUNT);
			_rowCountTitleLbl.setVisible(true);
		}

		private void createUserInterface() throws DataSetException
		{
			final ISession session = getSession();
			_rowCountBtn = new RowCountButton(session);
			_rowCountTitleLbl.setVisible(false);

			// Panel displays the row count for the table.
			final JPanel pnl = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(2, 2, 2, 2);
			gbc.weightx = 0;

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_rowCountBtn, gbc);

			++gbc.gridx;
			pnl.add(_rowCountTitleLbl, gbc);

			++gbc.gridx;
			gbc.weightx = 1.0;
			pnl.add(_rowCountLbl, gbc);

			// Panel displays table info.
			String destClassName = session.getProperties().getTableOutputClassName();
			_viewer = BaseDataSetViewerDestination.getInstance(destClassName);
			_ds = new TableInfoDataSet();
			Runnable run = new Runnable()
			{
				public void run()
				{
					add(pnl, BorderLayout.NORTH);
					add(new JScrollPane(_viewer.getComponent()), BorderLayout.CENTER);
				}
			};
			SwingUtilities.invokeLater(run);
		}

		private final class RowCountButton extends JButton
												implements ActionListener
		{
			private ISession _session;
			private ITableInfo _ti;
	
			RowCountButton(ISession session)
			{
				super(TableInfoi18n.GET_ROW_COUNT);
				_session = session;
				addActionListener(this);
			}
	
			void setTableInfo(ITableInfo ti)
			{
				_ti = ti;
			}
	
			public void actionPerformed(ActionEvent evt)
			{
				new UpdateRowCountCommand(_session, _ti).execute();
			}
		}

		private final class UpdateRowCountCommand implements ICommand
		{
			private ISession _session;
			private ITableInfo _ti;
			
			UpdateRowCountCommand(ISession session, ITableInfo ti)
			{
				super();
				_session = session;
				_ti = ti;
			}
	
			public void execute()
			{
				String nbrRows = "<Unknown>";
				if (_ti != null)
				{
					try
					{
						Statement stmt = _session.getSQLConnection().createStatement();
						try
						{
							ResultSet rs = stmt.executeQuery("select count(*) from " + _ti.getQualifiedName());
							if (rs.next())
							{
								nbrRows = String.valueOf(rs.getLong(1));
							}
							else
							{
								nbrRows = "0";
							}
						}
						finally
						{
							stmt.close();
						}
					}
					catch (Exception ex)
					{
						nbrRows = "<error>";
						s_log.error("Error retrieving row count for table", ex);
					}
				}
				TableInfoComponent.this.setRowCountText(nbrRows);
			}
		}
	}
}