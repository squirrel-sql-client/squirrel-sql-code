package net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects;
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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class InvalidObjectsPanel extends JPanel
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(InvalidObjectsPanel.class);

	/** Current session. */
	private ISession _session;

        private JTable _invalidObjects;


        private static final String invalidObjectSQL = "SELECT owner, "+
                                                       "object_name, "+
                                                       "object_type "+
                                                       "FROM sys.all_objects "+
                                                       "WHERE status = 'INVALID'";
	/**
	 * Ctor.
	 *
	 * @param	session	 Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public InvalidObjectsPanel(ISession session)
	{
		super();
		_session = session;
		createGUI();
	}

        /** Current session. */
        public ISession getSession() {
          return _session;
        }

        protected DefaultTableModel createTableModel() {
          DefaultTableModel tm = new DefaultTableModel();
          tm.addColumn("Owner");
          tm.addColumn("Object Name");
          tm.addColumn("Object Type");
          return tm;
        }

        public synchronized void repopulateInvalidObjects() {
          try {
            PreparedStatement s = _session.getSQLConnection().getConnection().prepareStatement(invalidObjectSQL);
            if (s.execute()) {
              ResultSet rs = s.getResultSet();
              DefaultTableModel tm = createTableModel();
              while (rs.next()) {
                String owner = rs.getString(1);
                String object_name = rs.getString(2);
                String object_type = rs.getString(3);
                //Should probably create my own table model but i am being a bit slack.
                tm.addRow(new Object[] {owner, object_name, object_type});
              }
              _invalidObjects.setModel(tm);
            }
          } catch (SQLException ex) {
            _session.getMessageHandler().showErrorMessage(ex);
          }
        }

	private void createGUI()
	{
            final IApplication app = _session.getApplication();
            setLayout(new BorderLayout());
            _invalidObjects = new JTable();
            _invalidObjects.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            add(new JScrollPane(_invalidObjects));

            repopulateInvalidObjects();
	}

}
