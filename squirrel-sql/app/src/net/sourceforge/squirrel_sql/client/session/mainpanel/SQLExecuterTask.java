package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001 Johan Companger
 * jcompagner@j-com.nl
 *
 * Modification copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class SQLExecuterTask implements Runnable {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SQLExecuterTask.class);

	private SQLPanel _sqlPanel;
	private ISession _session;
	private String _sql;
	private JPanel _cancelPanel = new CancelPanel();
	private Statement _stmt;
	private boolean _bStopExecution = false;

	public SQLExecuterTask(SQLPanel sqlPanel, ISession session, String sql) {
		super();
		_sqlPanel = sqlPanel;
		_session = session;
		_sql = sql;
	}

	public void run() {
		_sqlPanel.setCancelPanel(_cancelPanel);
		boolean bCancelPanelRemoved = false;
		try {
			final long start = System.currentTimeMillis();
			SessionProperties props = _session.getProperties();
			_stmt = _session.getSQLConnection().createStatement();
			try {
				if (props.getSqlLimitRows()) {
					_stmt.setMaxRows(props.getSqlNbrRowsToShow());
				}
				QueryTokenizer qt = new QueryTokenizer(_sql, props.getSqlStatementSeparatorChar());
				while (qt.hasQuery() && !_bStopExecution) {
					if (bCancelPanelRemoved) {
						_sqlPanel.setCancelPanel(_cancelPanel);
						bCancelPanelRemoved = false;
					}
					final String sToken = qt.nextQuery();
					if (!sToken.startsWith("--")) {
						if (_stmt.execute(sToken)) {
							if (_bStopExecution) {
								break;
							}
							ResultSet rs = _stmt.getResultSet();
							if (rs != null) {
								try {
									_sqlPanel.addResultsTab(sToken, new ResultSetDataSet(rs),
															new ResultSetMetaDataDataSet(rs),
															_cancelPanel);
									bCancelPanelRemoved = true;
								} finally {
									rs.close();
								}
							}
						} else {
							_session.getMessageHandler().showMessage(_stmt.getUpdateCount() + " Rows Updated");
						}
					}
				}
				//
				if (_bStopExecution || !bCancelPanelRemoved)
					//				{
					//
					_sqlPanel.removeCancelPanel(_cancelPanel);
				//				}
				final long finish = System.currentTimeMillis();
				_session.getMessageHandler().showMessage("Elapsed time for query(milliseconds) : " + (finish - start));
				//  i18n
//			} catch (Throwable ex) {
//				showMessage(_session, ex);
			} finally {
				//if (_bStopExecution || !bCancelPanelRemoved) {
		//			_sqlPanel.removeCancelPanel(_cancelPanel);
				//}
				try {
					_stmt.close();
				} finally {
					_stmt = null;
				}
			}
		} catch (Throwable ex) {
			_session.getMessageHandler().showMessage(ex);
		} finally {
			if (_bStopExecution || !bCancelPanelRemoved) {
				_sqlPanel.removeCancelPanel(_cancelPanel);
			}
		}
	}

	private final class CancelPanel extends JPanel implements ActionListener {
		private CancelPanel() {
			setLayout(new FlowLayout());
			JButton button = new JButton("Cancel");
			button.addActionListener(this);
			add(button);
		}

		public void actionPerformed(ActionEvent event) {
			_bStopExecution = true;
			try {
				if (_stmt != null) {
					_stmt.cancel();
				}
			} catch (Throwable th) {
				s_log.error("Error occured cancelling SQL", th);
			}
		}
	}

}