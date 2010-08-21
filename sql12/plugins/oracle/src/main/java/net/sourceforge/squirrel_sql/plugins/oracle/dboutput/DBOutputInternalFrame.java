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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrame;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrameCallback;

public class DBOutputInternalFrame extends OracleInternalFrame
{
   private static final String PREF_PART_DB_OUTPUT_FRAME = "DBOutputFrame";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DBOutputInternalFrame.class);

   /**
    * Application API.
    */
   private final IApplication _app;

   /**
    * ID of the session for this window.
    */
   private IIdentifier _sessionId;

   private DBOutputPanel _dbOutputPanel;
   /**
    * Toolbar for window.
    */
   private DBOutputToolBar _toolBar;

   private Resources _resources;

   public DBOutputInternalFrame(ISession session, Resources resources)
   {
      // I18n[oracle.dbOutputTitle=Oracle DB output for: {0}]
      super(session, s_stringMgr.getString("oracle.dbOutputTitle", session.getTitle()));
      _app = session.getApplication();
      _resources = resources;
      _sessionId = session.getIdentifier();
      createGUI(session);
   }

   public DBOutputPanel getDBOutputPanel()
   {
      return _dbOutputPanel;
   }

   private void createGUI(ISession session)
   {
      addWidgetListener(new WidgetAdapter()
      {
         public void widgetClosing(WidgetEvent e)
         {
            onWidgetClosing();
         }
      });


      Icon icon = _resources.getIcon(getClass(), "frameIcon"); //i18n
      if (icon != null)
      {
         setFrameIcon(icon);
      }


      OracleInternalFrameCallback cb = new OracleInternalFrameCallback()
      {

         public void createPanelAndToolBar(boolean stayOnTop, int autoRefeshPeriod)
         {
            _dbOutputPanel = new DBOutputPanel(getSession(), autoRefeshPeriod);
            _toolBar = new DBOutputToolBar(getSession(), stayOnTop, autoRefeshPeriod);
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(_toolBar, BorderLayout.NORTH);
            contentPanel.add(_dbOutputPanel, BorderLayout.CENTER);
            setContentPane(contentPanel);

            _dbOutputPanel.setAutoRefreshPeriod(autoRefeshPeriod);
         }
      };


      initFromPrefs(PREF_PART_DB_OUTPUT_FRAME, cb);
   }


   private void onWidgetClosing()
   {

      internalFrameClosing(_toolBar.isStayOnTop(), _dbOutputPanel.getAutoRefreshPeriod());

      //Turn off auto refresh when we are shutting down.
      _dbOutputPanel.setAutoRefresh(false);
   }

   /**
    * The class representing the toolbar at the top of a dboutput internal frame
    */
   private class DBOutputToolBar extends OracleToolBar
   {
      private JCheckBox _autoRefresh;

      DBOutputToolBar(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         super();
         createGUI(session, stayOnTop, autoRefeshPeriod);
      }

      private void createGUI(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         IApplication app = session.getApplication();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(new GetDBOutputAction(app, _resources, _dbOutputPanel));
         add(new ClearDBOutputAction(app, _resources, _dbOutputPanel));

         addStayOnTop(stayOnTop);
         
         //Create checkbox for enabling auto refresh
         // i18n[oracle.dboutputEnableAutoRefer=Enable auto refresh]
         _autoRefresh = new JCheckBox(s_stringMgr.getString("oracle.dboutputEnableAutoRefer"), false);
         _autoRefresh.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               _dbOutputPanel.setAutoRefresh(_autoRefresh.isSelected());
            }
         });
         add(_autoRefresh);


         //Create spinner for update period
         final SpinnerNumberModel model = new SpinnerNumberModel(autoRefeshPeriod, 1, 60, 5);
         JSpinner refreshRate = new JSpinner(model);
         refreshRate.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               _dbOutputPanel.setAutoRefreshPeriod(model.getNumber().intValue());
            }
         });
         add(refreshRate);
         // i18n[oracle.Seconds2=(seconds)]
         add(new JLabel(s_stringMgr.getString("oracle.Seconds2")));
      }

   }
}
