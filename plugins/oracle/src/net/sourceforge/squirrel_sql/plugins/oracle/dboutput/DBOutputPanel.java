package net.sourceforge.squirrel_sql.plugins.oracle.dboutput;
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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MemoryComboBox;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.SQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.event.SQLResultExecuterTabEvent;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.ResultTabEvent;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter.ISQLResultExecuterFactory;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class DBOutputPanel extends JPanel
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(DBOutputPanel.class);

	/** Current session. */
	private ISession _session;
        
        private JTextArea _textArea;
        private Timer _refreshTimer = new Timer(true);
        
        private boolean _autoRefresh = false;
        private int _refreshPeriod = 10;
        
        public class RefreshTimerTask extends TimerTask {
          public void run() {
            populateDBOutput();
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
	public DBOutputPanel(ISession session)
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
          _refreshTimer.cancel();
          _refreshTimer = new Timer(true);
          if (_autoRefresh && (_refreshPeriod > 0)) {
            _refreshTimer.scheduleAtFixedRate(new RefreshTimerTask(), _refreshPeriod * 1000, _refreshPeriod * 1000);
          }
        }
        
        public void setAutoRefresh(boolean enable) {
          if (enable != _autoRefresh) {
            resetTimer();
          }
        }
        
        public boolean getAutoRefesh() {
          return _autoRefresh;
        }
        
        public void setAutoRefreshPeriod(int seconds) {
          _refreshPeriod = seconds;
          resetTimer();
        }
        
        public int getAutoRefreshPeriod() {
          return _refreshPeriod;
        }
        
        public synchronized void populateDBOutput() {
          try {
            final StringBuffer buf = new StringBuffer();
            //In an effort to avoid non JDBC standard out parameter types ie
            //oracle specific ones, the dbms_output.get_line is used rather than
            //the dbms_output.getlines. The disadvantage is there are more trips
            //to the server to return multiple lines.
            CallableStatement c = _session.getSQLConnection().getConnection().prepareCall("{call dbms_output.getline(?, ?)}");
            c.registerOutParameter(1, java.sql.Types.VARCHAR);
            c.registerOutParameter(2, java.sql.Types.INTEGER);
            //Get 10 lines at a time.
            int status = 0;
            while (status == 0) {
              c.execute();
              status = c.getInt(2);
              if (status == 0)
                buf.append(c.getString(1));            
            }
            if (buf.length() > 0) {
              final JTextArea store = _textArea;
              SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  store.append(buf.toString());
                }
              });
            }
          } catch (SQLException ex) {
            _session.getMessageHandler().showErrorMessage(ex);
          }
        }

	private void createGUI()
	{
            final IApplication app = _session.getApplication();
            setLayout(new BorderLayout());
            _textArea = new JTextArea();
            _textArea.setEditable(false);
            add(new JScrollPane(_textArea));
	}

}
