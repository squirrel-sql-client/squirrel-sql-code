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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.common.AutoWidthResizeTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InvalidObjectsPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(InvalidObjectsPanel.class);


   /**
    * Logger for this class.
    */
   private static final ILogger s_log = LoggerController.createLogger(InvalidObjectsPanel.class);

   /**
    * Current session.
    */
   private ISession _session;

   private AutoWidthResizeTable _invalidObjects;
   private boolean hasResized = false;


   private static final String invalidObjectSQL = "SELECT owner, " +
      "object_name, " +
      "object_type " +
      "FROM sys.all_objects " +
      "WHERE status = 'INVALID'";

   /**
    * Ctor.
    *
    * @param   session    Current session.
    * @throws IllegalArgumentException Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
    */
   public InvalidObjectsPanel(ISession session)
   {
      super();
      _session = session;
      createGUI();
   }

   /**
    * Current session.
    */
   public ISession getSession()
   {
      return _session;
   }

   protected DefaultTableModel createTableModel()
   {
      DefaultTableModel tm = new DefaultTableModel()
      {
         public boolean isCellEditable(int row, int column)
         {
            return false;
         }
      };

      // i18n[oracle.owner=Owner]
      tm.addColumn(s_stringMgr.getString("oracle.owner"));
      // i18n[oracle.objectName=Object Name]
      tm.addColumn(s_stringMgr.getString("oracle.objectName"));
      // i18n[oracle.objectType=Object Type]
      tm.addColumn(s_stringMgr.getString("oracle.objectType"));
      return tm;
   }

   public synchronized void repopulateInvalidObjects()
   {
      try
      {
         PreparedStatement s = _session.getSQLConnection().getConnection().prepareStatement(invalidObjectSQL);
         if (s.execute())
         {
            ResultSet rs = s.getResultSet();
            DefaultTableModel tm = createTableModel();
            while (rs.next())
            {
               String owner = rs.getString(1);
               String object_name = rs.getString(2);
               String object_type = rs.getString(3);
               //Should probably create my own table model but i am being a bit slack.
               tm.addRow(new Object[]{owner, object_name, object_type});
            }
            _invalidObjects.setModel(tm);
            if (!hasResized)
            {
               //Only resize once.
               hasResized = true;
               _invalidObjects.resizeColumnWidth(300);
            }
         }
      }
      catch (SQLException ex)
      {
         _session.showErrorMessage(ex);
      }
   }

   private void createGUI()
   {
      setLayout(new BorderLayout());
      _invalidObjects = new AutoWidthResizeTable();
      _invalidObjects.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      add(new JScrollPane(_invalidObjects));

      repopulateInvalidObjects();
	}

}
