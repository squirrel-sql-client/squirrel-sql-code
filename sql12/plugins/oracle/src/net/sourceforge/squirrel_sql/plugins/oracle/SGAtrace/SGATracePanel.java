package net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace;
/*
 * Copyright (C) 2004 Jason Height
 * jmheight@users.sourceforge.net
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
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.oracle.common.AutoWidthResizeTable;

public class SGATracePanel extends JPanel
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(SGATracePanel.class);

       private static final String sgaTraceSQL =
          "  SELECT a.SQL_Text, "+
          "         a.First_Load_Time, "+
          "         b.username, "+
          "         a.Parse_Calls, "+
          "         a.Executions, "+
          "         a.Sorts, "+
          "         a.Disk_Reads, "+
          "         a.Buffer_Gets, "+
          "         a.Rows_Processed, "+
          "         DECODE ( a.Executions, "+
          "                  0, "+
          "                  'N/A', "+
          "                  ROUND ( a.Sorts / a.Executions, "+
          "                          3 ) ), "+
          "         DECODE ( a.Executions, "+
          "                  0, "+
          "                  'N/A', "+
          "                  ROUND ( a.Disk_Reads / a.Executions, "+
          "                          3 ) ), "+
          "         DECODE ( a.Executions, "+
          "                  0, "+
          "                  'N/A', "+
          "                  ROUND ( a.Buffer_Gets / a.Executions, "+
          "                          3 ) ), "+
          "         DECODE ( a.Executions, "+
          "                  0, "+
          "                  'N/A', "+
          "                  ROUND ( a.Rows_Processed / a.Executions, "+
          "                          3 ) ), "+
          "         DECODE ( a.Rows_Processed, "+
          "                  0, "+
          "                  'N/A', "+
          "                  ROUND ( a.Sorts / a.Rows_Processed, "+
          "                          3 ) ), "+
          "         DECODE ( a.Rows_Processed, "+
          "                  0, "+
          "                  'N/A', "+
          "                  ROUND ( a.Disk_Reads / a.Rows_Processed, "+
          "                          3 ) ) , "+
          "         DECODE ( a.Rows_Processed, "+
          "                  0, "+
          "                  'N/A', "+
          "                  ROUND ( a.Buffer_Gets / a.Rows_Processed, "+
          "                          3 ) ), "+
          "         a.Address || ':' || a.Hash_Value "+
          "    FROM v$sqlarea a, "+
          "         sys.all_users b "+
          "   WHERE a.parsing_user_id = b.user_id ";

	/** Current session. */
	private ISession _session;

        private AutoWidthResizeTable _sgaTrace;
        private boolean hasResized = false;
        private Timer _refreshTimer = new Timer(true);

        private boolean _autoRefresh = false;
        private int _refreshPeriod = 10;

        public class RefreshTimerTask extends TimerTask {
          public void run() {
            populateSGATrace();
          }
        }

	/**
	 * Ctor.
	 *
	 * @param	session	 Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public SGATracePanel(ISession session)
	{
		super();
		_session = session;
		createGUI();
	}

        /** Current session. */
        public ISession getSession() {
          return _session;
        }

        private void resetTimer() {
          if (_refreshTimer != null) {
            _refreshTimer.cancel();
            //Nil out the timer so that it can be gc'd
            _refreshTimer = null;
          }
          if (_autoRefresh && (_refreshPeriod > 0)) {
            _refreshTimer = new Timer(true);
            _refreshTimer.scheduleAtFixedRate(new RefreshTimerTask(),
                                              _refreshPeriod * 1000,
                                              _refreshPeriod * 1000);
          }
        }

        public void setAutoRefresh(boolean enable) {
          if (enable != _autoRefresh) {
            _autoRefresh = enable;
            resetTimer();
          }
        }

        public boolean getAutoRefesh() {
          return _autoRefresh;
        }

        public void setAutoRefreshPeriod(int seconds) {
          if (_refreshPeriod != seconds) {
            _refreshPeriod = seconds;
            resetTimer();
          }
        }

        public int getAutoRefreshPeriod() {
          return _refreshPeriod;
        }

        protected DefaultTableModel createTableModel() {
          DefaultTableModel tm = new DefaultTableModel();
          tm.addColumn("SQL Text");
          tm.addColumn("First Load Time");
          tm.addColumn("Parse Schema");
          tm.addColumn("Parse Calls");
          tm.addColumn("Executions");
          tm.addColumn("Sorts");
          tm.addColumn("Disk Reads");
          tm.addColumn("Buffer Gets");
          tm.addColumn("Rows");
          tm.addColumn("Sorts per Exec");
          tm.addColumn("Disk Reads per Exec");
          tm.addColumn("Buffer Gets per Exec");
          tm.addColumn("Rows per Exec");
          tm.addColumn("Sorts per Row");
          tm.addColumn("Disk Reads per Row");
          tm.addColumn("Buffer Gets per Row");
          return tm;
        }

        public synchronized void populateSGATrace() {
          try {
            PreparedStatement s = _session.getSQLConnection().getConnection().prepareStatement(sgaTraceSQL);
            if (s.execute()) {
              ResultSet rs = s.getResultSet();
              DefaultTableModel tm = createTableModel();
              while (rs.next()) {
                String sqlText = rs.getString(1);
                String flt = rs.getString(2);
                String schema = rs.getString(3);
                String calls = rs.getString(4);
                String executions = rs.getString(5);
                String sorts = rs.getString(6);
                String diskReads = rs.getString(7);
                String bufGets = rs.getString(8);
                String rows = rs.getString(9);
                String sortsExec = rs.getString(10);
                String diskReadsExec = rs.getString(11);
                String rowsExec = rs.getString(12);
                String sortsRows = rs.getString(13);
                String diskReadsRow = rs.getString(14);
                String bufGetsRow = rs.getString(15);

                //Should probably create my own table model but i am being a bit slack.
                tm.addRow(new Object[] {sqlText, flt, schema, calls, executions,
                                        sorts, diskReads, bufGets, rows, sortsExec,
                                        diskReadsExec, rowsExec, sortsRows, diskReadsRow,
                                        bufGetsRow});
              }
              _sgaTrace.setModel(tm);
              if (!hasResized) {
                hasResized = true;
                _sgaTrace.resizeColumnWidth(300);
              }
            }
          } catch (SQLException ex) {
            _session.getMessageHandler().showErrorMessage(ex);
          }
        }

	private void createGUI()
	{
            final IApplication app = _session.getApplication();
            setLayout(new BorderLayout());
            _sgaTrace = new AutoWidthResizeTable(new DefaultTableModel());
            _sgaTrace.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            add(new JScrollPane(_sgaTrace));

            populateSGATrace();
	}

}
