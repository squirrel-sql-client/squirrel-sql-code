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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;
import net.sourceforge.squirrel_sql.plugins.oracle.common.AutoWidthResizeTable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class SGATracePanel extends JPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SGATracePanel.class);

	/** The SQL used to get SQL statistics from the SGA */
   private static final String sgaTraceSQL =
      "  SELECT a.SQL_Text, " +
         "         a.First_Load_Time, " +
         "         b.username, " +
         "         a.Parse_Calls, " +
         "         a.Executions, " +
         "         a.Sorts, " +
         "         a.Disk_Reads, " +
         "         a.Buffer_Gets, " +
         "         a.Rows_Processed, " +
         "         DECODE ( a.Executions, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Sorts / a.Executions, " +
         "                          3 ) ), " +
         "         DECODE ( a.Executions, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Disk_Reads / a.Executions, " +
         "                          3 ) ), " +
         "         DECODE ( a.Executions, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Buffer_Gets / a.Executions, " +
         "                          3 ) ), " +
         "         DECODE ( a.Executions, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Rows_Processed / a.Executions, " +
         "                          3 ) ), " +
         "         DECODE ( a.Rows_Processed, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Sorts / a.Rows_Processed, " +
         "                          3 ) ), " +
         "         DECODE ( a.Rows_Processed, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Disk_Reads / a.Rows_Processed, " +
         "                          3 ) ) , " +
         "         DECODE ( a.Rows_Processed, " +
         "                  0, " +
         "                  'N/A', " +
         "                  ROUND ( a.Buffer_Gets / a.Rows_Processed, " +
         "                          3 ) ), " +
         "         a.Address || ':' || a.Hash_Value " +
         "    FROM v$sqlarea a, " +
         "         sys.all_users b " +
         "   WHERE a.parsing_user_id = b.user_id ";

   private ISession _session;

   private AutoWidthResizeTable _sgaTrace;
   private boolean hasResized = false;
   private Timer _refreshTimer = new Timer(true);

   private boolean _autoRefresh = false;
   private int _refreshPeriod = 10;

   public SGATracePanel(ISession session, int autoRefreshPeriod)
   {
      _session = session;
      _refreshPeriod = autoRefreshPeriod;
      createGUI();
   }

   /**
    * Current session.
    */
   public ISession getSession()
   {
      return _session;
   }

   private void resetTimer()
   {
      if (_refreshTimer != null)
      {
         _refreshTimer.cancel();
         //Nil out the timer so that it can be gc'd
         _refreshTimer = null;
      }
      if (_autoRefresh && (_refreshPeriod > 0))
      {
         _refreshTimer = new Timer(true);

         final TimerTask timerTask = new TimerTask()
         {
            @Override
            public void run()
            {
               GUIUtils.processOnSwingEventThread(() -> populateSGATrace());
            }
         };

         _refreshTimer.scheduleAtFixedRate(timerTask, _refreshPeriod * 1000, _refreshPeriod * 1000);
      }
   }

   public void setAutoRefresh(boolean enable)
   {
      if (enable != _autoRefresh)
      {
         _autoRefresh = enable;
         resetTimer();
      }
   }

   public void setAutoRefreshPeriod(int seconds)
   {
      if (_refreshPeriod != seconds)
      {
         _refreshPeriod = seconds;
         resetTimer();
      }
   }

   public int getAutoRefreshPeriod()
   {
      return _refreshPeriod;
   }

   private DefaultTableModel createTableModel()
   {
      DefaultTableModel tm = new DefaultTableModel();
      // i18n[oracle.sqlText=SQL Text]
      tm.addColumn(s_stringMgr.getString("oracle.sqlText"));
      // i18n[oracle.firstLoadTime=First Load Time]
      tm.addColumn(s_stringMgr.getString("oracle.firstLoadTime"));
      // i18n[oracle.parseSchema=Parse Schema]
      tm.addColumn(s_stringMgr.getString("oracle.parseSchema"));
      // i18n[oracle.parseCalla=Parse Calls]
      tm.addColumn(s_stringMgr.getString("oracle.parseCalla"));
      // i18n[oracle.execution=Executions]
      tm.addColumn(s_stringMgr.getString("oracle.execution"));
      // i18n[oracle.sorts=Sorts]
      tm.addColumn(s_stringMgr.getString("oracle.sorts"));
      // i18n[oracle.diskReads=Disk Reads]
      tm.addColumn(s_stringMgr.getString("oracle.diskReads"));
      // i18n[oracle.bufferGets=Buffer Gets]
      tm.addColumn(s_stringMgr.getString("oracle.bufferGets"));
      // i18n[oracle.rows=Rows]
      tm.addColumn(s_stringMgr.getString("oracle.rows"));
      // i18n[oracle.sortsPerExec=Sorts per Exec]
      tm.addColumn(s_stringMgr.getString("oracle.sortsPerExec"));
      // i18n[oracle.diskReadsPerExec=Disk Reads per Exec]
      tm.addColumn(s_stringMgr.getString("oracle.diskReadsPerExec"));
      // i18n[oracle.bufferPerExec=Buffer Gets per Exec]
      tm.addColumn(s_stringMgr.getString("oracle.bufferPerExec"));
      // i18n[oracle.rowsPerExec=Rows per Exec]
      tm.addColumn(s_stringMgr.getString("oracle.rowsPerExec"));
      // i18n[oracle.sortsPerExec=Sorts per Row]
      tm.addColumn(s_stringMgr.getString("oracle.sortsPerExec"));
      // i18n[oracle.diskReadsPerRow=Disk Reads per Row]
      tm.addColumn(s_stringMgr.getString("oracle.diskReadsPerRow"));
      // i18n[oracle.buffer.getsPerRow=Buffer Gets per Row]
      tm.addColumn(s_stringMgr.getString("oracle.buffer.getsPerRow"));
      return tm;
   }

   public void populateSGATrace()
   {
      if (!OraclePlugin.checkObjectAccessible(_session, sgaTraceSQL))
      {
         return;
      }
      PreparedStatement s = null;
      ResultSet rs = null;
      try
      {
         s = _session.getSQLConnection().getConnection().prepareStatement(sgaTraceSQL);
         if (s.execute())
         {
            rs = s.getResultSet();

            DefaultTableModel tm;
            if ((_sgaTrace.getModel().getColumnCount() > 0) && (_sgaTrace.getModel() instanceof DefaultTableModel))
            {
               // Done on refresh. Assures table layout doesn't change.
               tm = (DefaultTableModel)_sgaTrace.getModel();
               tm.setRowCount(0);
            }
            else
            {
               // Done on startup
               tm = createTableModel();
            }

            while (rs.next())
            {
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
               tm.addRow(new Object[]{sqlText, flt, schema, calls, executions,
                  sorts, diskReads, bufGets, rows, sortsExec,
                  diskReadsExec, rowsExec, sortsRows, diskReadsRow,
                  bufGetsRow});
            }
            _sgaTrace.setModel(tm);
            if (!hasResized)
            {
               hasResized = true;
               _sgaTrace.resizeColumnWidth(300);
            }
         }
      }
      catch (SQLException ex)
      {
         _session.showErrorMessage(ex);
      }
      finally
      {
         SQLUtilities.closeResultSet(rs);
         SQLUtilities.closeStatement(s);
      }
   }

   private void createGUI()
   {
      setLayout(new BorderLayout());
      _sgaTrace = new AutoWidthResizeTable(new DefaultTableModel());
      _sgaTrace.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      _sgaTrace.setAutoCreateRowSorter(true);
      add(new JScrollPane(_sgaTrace));

      populateSGATrace();
	}

}
