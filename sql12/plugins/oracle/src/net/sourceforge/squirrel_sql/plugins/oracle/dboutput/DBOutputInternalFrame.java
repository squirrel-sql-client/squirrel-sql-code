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
import java.util.prefs.Preferences;
import java.beans.PropertyVetoException;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;

public class DBOutputInternalFrame extends BaseSessionInternalFrame
{

   private static final String PREF_KEY_ORACLE_DB_OUTPUTFRAME_X = "Squirrel.oracle.DBOutputFrame_X";
   private static final String PREF_KEY_ORACLE_DB_OUTPUTFRAME_Y = "Squirrel.oracle.DBOutputFrame_Y";
   private static final String PREF_KEY_ORACLE_DB_OUTPUTFRAME_WIDTH = "Squirrel.oracle.DBOutputFrame_WIDTH";
   private static final String PREF_KEY_ORACLE_DB_OUTPUTFRAME_HEIGHT = "Squirrel.oracle.DBOutputFrame_HEIGHT";
   private static final String PREF_KEY_ORACLE_DB_OUTPUTFRAME_STAY_ON_TOP = "Squirrel.oracle.DBOutputFrame_STAY_ON_TOP";
   private static final String PREF_KEY_ORACLE_DB_OUTPUTFRAME_AUTO_REFRESH_SEC = "Squirrel.oracle.DBOutputFrame__AUTO_REFRESH_SEC";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DBOutputInternalFrame.class);

   private static final ILogger s_log =
      LoggerController.createLogger(DBOutputInternalFrame.class);



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
      super(session, session.getTitle(), true, true, true, true);
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
      addInternalFrameListener(new InternalFrameAdapter()
      {
         public void internalFrameClosing(InternalFrameEvent e)
         {
            onInternalFrameClosing();
         }
      });


      Icon icon = _resources.getIcon(getClass(), "frameIcon"); //i18n
      if (icon != null)
      {
         setFrameIcon(icon);
      }

      final int x = Preferences.userRoot().getInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_X, 0);
      final int y = Preferences.userRoot().getInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_Y, 0);
      final int width = Preferences.userRoot().getInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_WIDTH, 400);
      final int height = Preferences.userRoot().getInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_HEIGHT, 200);
      final boolean stayOnTop = Preferences.userRoot().getBoolean(PREF_KEY_ORACLE_DB_OUTPUTFRAME_STAY_ON_TOP, false);
      int autoRefeshPeriod = Preferences.userRoot().getInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_AUTO_REFRESH_SEC, 10);
      autoRefeshPeriod = Math.max(1,autoRefeshPeriod);

      _dbOutputPanel = new DBOutputPanel(getSession(), autoRefeshPeriod);
      _toolBar = new DBOutputToolBar(getSession(), stayOnTop, autoRefeshPeriod);
      JPanel contentPanel = new JPanel(new BorderLayout());
      contentPanel.add(_toolBar, BorderLayout.NORTH);
      contentPanel.add(_dbOutputPanel, BorderLayout.CENTER);
      setContentPane(contentPanel);





      _dbOutputPanel.setAutoRefreshPeriod(autoRefeshPeriod);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            Rectangle rectMain = _app.getMainFrame().getDesktopPane().getBounds();
            Rectangle rect = new Rectangle();
            rect.x = x;
            if(rectMain.width - x < 50) rect.x = 0;

            rect.y = y;
            if(rectMain.height - y < 50) rect.y = 0;


            rect.width = Math.max(100, width);
            rect.height = Math.max(100, height);
            if(rect.x + rect.width > rectMain.width || rect.y + rect.height > rectMain.height)
            {
               rect.x = 0; rect.width = 400;
               rect.y = 0; rect.height = 200;
            }

            try
            {
               setMaximum(false);
            }
            catch (PropertyVetoException e)
            {
               s_log.error(e);
            }
            setBounds(rect);

            setVisible(true);
         }
      });
   }

   private void onInternalFrameClosing()
   {
      Rectangle rect = getBounds();

      Preferences.userRoot().putInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_X, rect.x);
      Preferences.userRoot().putInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_Y, rect.y);
      Preferences.userRoot().putInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_WIDTH, rect.width);
      Preferences.userRoot().putInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_HEIGHT, rect.height);
      Preferences.userRoot().putBoolean(PREF_KEY_ORACLE_DB_OUTPUTFRAME_STAY_ON_TOP, _toolBar.isStayOnTop());
      Preferences.userRoot().putInt(PREF_KEY_ORACLE_DB_OUTPUTFRAME_AUTO_REFRESH_SEC, _dbOutputPanel.getAutoRefreshPeriod());

      //Turn off auto refresh when we are shutting down.
      _dbOutputPanel.setAutoRefresh(false);
   }

   /**
    * The class representing the toolbar at the top of a dboutput internal frame
    */
   private class DBOutputToolBar extends ToolBar
   {
      private JCheckBox _stayOnTop;
      private JCheckBox _autoRefresh;
      private JSpinner _refreshRate;

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


         // i18n[oracle.dboutputStayOnTop=Stay on top]
         _stayOnTop = new JCheckBox(s_stringMgr.getString("oracle.dboutputStayOnTop"), false);
         _stayOnTop.setSelected(stayOnTop);

         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               onStayOnTopChanged(_stayOnTop.isSelected());
               _stayOnTop.addActionListener(new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {
                     onStayOnTopChanged(_stayOnTop.isSelected());
                  }
               });
            }
         });


         add(_stayOnTop);


         //Create spinner for update period
         final SpinnerNumberModel model = new SpinnerNumberModel(autoRefeshPeriod, 1, 60, 5);
         _refreshRate = new JSpinner(model);
         _refreshRate.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               _dbOutputPanel.setAutoRefreshPeriod(model.getNumber().intValue());
            }
         });
         add(_refreshRate);
         // i18n[oracle.Seconds2=(seconds)]
         add(new JLabel(s_stringMgr.getString("oracle.Seconds2")));
      }

      private void onStayOnTopChanged(boolean selected)
      {
         if(selected)
         {
            getDesktopPane().setLayer(DBOutputInternalFrame.this, JLayeredPane.PALETTE_LAYER.intValue());
         }
         else
         {
            getDesktopPane().setLayer(DBOutputInternalFrame.this, JLayeredPane.DEFAULT_LAYER.intValue());
         }

         // Needs to be done in both cases because if the window goes back to
         // the default layer it goes back behind all other windows too.
         toFront();
      }

      public boolean isStayOnTop()
      {
         return _stayOnTop.isSelected();
      }
   }
}
