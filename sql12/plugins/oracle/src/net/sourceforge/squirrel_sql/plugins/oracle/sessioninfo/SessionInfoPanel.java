package net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;
import net.sourceforge.squirrel_sql.plugins.oracle.common.AutoWidthResizeTable;

public class SessionInfoPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

   /**
    * Logger for this class.
    */
   private static final ILogger s_log = 
       LoggerController.createLogger(SessionInfoPanel.class);

   //JMH Remove the current sql text. Create a tabbed pane for session details (including sql text)
   private static final String SESSION_INFO_SQL = "SELECT sess.Sid, " +
      "     sess.serial#, " +
      "     NVL(sess.username, bgproc.name), " +
      "     sess.schemaname, " +
      "     sess.status, " +
      "     sess.server, " +
      "     sess.osuser, " +
      "     sess.machine, " +
      "     sess.terminal, " +
      "     sess.program, " +
      "     sess.process, " +
      "     sess.type, " +
      "     sess.module, " +
      "     sess.action, " +
      "     sess.client_Info, " +
      "     sess_io.block_gets, " +
      "     sess_io.consistent_gets, " +
      "     sess_io.physical_reads, " +
      "     sess_io.block_changes, " +
      "     sess_io.consistent_changes, " +
      "     sess_stat.value*10, " +
      "     sess.last_call_et, " +
      "     d.sql_text, " +
      "     sess.sql_address || ':' || sql_hash_value, " +
      "     sess.prev_sql_addr || ':' || prev_hash_value, " +
      "     sess.logon_time " +
      "FROM v$session sess, " +
      "     v$bgprocess bgproc," +
      "     v$sess_io sess_io, " +
      "     v$sesstat sess_stat, " +
      "     v$sql d " +
      "WHERE sess.sid = sess_io.sid ( + ) " +
      "  AND sess.sid = sess_stat.sid ( + ) " +
      "  AND sess.paddr = bgproc.paddr ( + ) " +
      "  AND ( sess_stat.statistic# = 12 OR sess_stat.statistic# IS NULL ) " +
      "  AND sess.sql_address = d.address ( + ) " +
      "  AND sess.sql_hash_value = d.hash_value ( + ) " +
      "  AND ( d.child_number = 0 OR d.child_number IS NULL ) " +
      "ORDER BY sess.sid ";
//JMH: For additional performance we could utilise the fixed_table_sequence column
//from the session, to investigate which rows need to be updated on a refresh
//See V$SESSION doco for more info.

   /**
    * Current session.
    */
   transient private ISession _session;

   private AutoWidthResizeTable _sessionInfo;
   private boolean hasResized = false;
   transient private Timer _refreshTimer = new Timer(true);

   private boolean _autoRefresh = false;
   private int _refreshPeriod = 10;

   public class RefreshTimerTask extends TimerTask
   {
      public void run()
      {
         populateSessionInfo();
      }
   }

   /**
    * Ctor.
    *
    * @param autoRefeshPeriod
    * @param   session    Current session.
    * @throws IllegalArgumentException Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
    */
   public SessionInfoPanel(ISession session, int autoRefeshPeriod)
   {
      super();
      _session = session;
      _refreshPeriod = autoRefeshPeriod;
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
         _refreshTimer.scheduleAtFixedRate(new RefreshTimerTask(),
            _refreshPeriod * 1000,
            _refreshPeriod * 1000);
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

   public boolean getAutoRefesh()
   {
      return _autoRefresh;
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

   protected DefaultTableModel createTableModel()
   {
      DefaultTableModel tm = new DefaultTableModel();
      tm.addColumn("Sid");
      tm.addColumn("Serial #");
      tm.addColumn("Session Name");
      tm.addColumn("Schema");
      tm.addColumn("Status");
      tm.addColumn("Server");
      tm.addColumn("OS user");
      tm.addColumn("Machine");
      tm.addColumn("Terminal");
      tm.addColumn("Program");
      tm.addColumn("Process");
      tm.addColumn("Type");
      tm.addColumn("Module");
      tm.addColumn("Action");
      tm.addColumn("Client Info");
      tm.addColumn("Block Gets");
      tm.addColumn("Consistent Gets");
      tm.addColumn("Physical Reads");
      tm.addColumn("Block Changes");
      tm.addColumn("Consistent Changes");
      tm.addColumn("CPU time (ms)");
      tm.addColumn("Last SQL");
      tm.addColumn("Current SQL Statement");
      tm.addColumn("SQL Address");
      tm.addColumn("Prev SQL Address");
      tm.addColumn("Logon Time");
      return tm;
   }

   public synchronized void populateSessionInfo()
   {
      if (!OraclePlugin.checkObjectAccessible(_session, SESSION_INFO_SQL))
      {
         return;
      }
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try
      {
         Connection con = _session.getSQLConnection().getConnection();
         if (s_log.isDebugEnabled()) {
             s_log.debug("populateSessionInfo: running sql - "+SESSION_INFO_SQL);
         }
         pstmt = con.prepareStatement(SESSION_INFO_SQL);
         rs = pstmt.executeQuery();
         final DefaultTableModel tm = createTableModel();
         while (rs.next())
         {
           String sid = rs.getString(1);
           String serNum = rs.getString(2);
           String sessionName = rs.getString(3);
           String schema = rs.getString(4);
           String status = rs.getString(5);
           String server = rs.getString(6);
           String OSusr = rs.getString(7);
           String machine = rs.getString(8);
           String terminal = rs.getString(9);
           String program = rs.getString(10);
           String process = rs.getString(11);
           String type = rs.getString(12);
           String module = rs.getString(13);
           String action = rs.getString(14);
           String clientInfo = rs.getString(15);
           String blockGets = rs.getString(16);
           String consistentGets = rs.getString(17);
           String physReads = rs.getString(18);
           String blockChanges = rs.getString(19);
           String consistentChanges = rs.getString(20);
           String CPUtime = rs.getString(21);
           String lastSQL = rs.getString(22);
           String currSQL = rs.getString(23);
           String SQLaddr = rs.getString(24);
           String prevSQLaddr = rs.getString(25);
           String logonTime = rs.getString(26);

           //Should probably create my own table model but i am being a bit slack.
           tm.addRow(new Object[]{sid, serNum, sessionName, schema, status, server,
              OSusr, machine, terminal, program, process, type, module,
              action, clientInfo, blockGets, consistentGets,
              physReads, blockChanges, consistentChanges, CPUtime,
              lastSQL, currSQL, SQLaddr, prevSQLaddr, logonTime});
         }
         updateTableModel(tm);
      }
      catch (SQLException ex)
      {
         _session.showErrorMessage(ex);
      } finally {
          SQLUtilities.closeResultSet(rs);
          SQLUtilities.closeStatement(pstmt);
      }
   }

   /**
    * Sets the specified updated TableModel using the EDT.
    * 
    * @param tm the TableModel to set.
    */
   private void updateTableModel(final DefaultTableModel tm) {
       GUIUtils.processOnSwingEventThread(new Runnable() {
           public void run() {
               _sessionInfo.setModel(tm);
               if (!hasResized)
               {
                 //Only resize once.
                 hasResized = true;
                 _sessionInfo.resizeColumnWidth(300);
               }
           }
       });       
   }
   
   private void createGUI()
   {
      setLayout(new BorderLayout());
      _sessionInfo = new AutoWidthResizeTable(new DefaultTableModel());
      _sessionInfo.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      add(new JScrollPane(_sessionInfo));
      populateSessionInfo();
	}
}
