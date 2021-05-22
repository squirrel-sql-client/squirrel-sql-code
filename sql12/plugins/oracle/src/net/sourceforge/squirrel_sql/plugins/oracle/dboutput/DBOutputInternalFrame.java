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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrame;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrameCallback;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleToolBar;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;

public class DBOutputInternalFrame extends OracleInternalFrame
{
   private static final String PREF_PART_DB_OUTPUT_FRAME = "DBOutputFrame";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DBOutputInternalFrame.class);

   private DBOutputPanel _dbOutputPanel;

   private DBOutputToolBar _toolBar;

   private Resources _resources;

   public DBOutputInternalFrame(ISession session, Resources resources)
   {
      // I18n[oracle.dbOutputTitle=Oracle DB output for: {0}]
      super(session, s_stringMgr.getString("oracle.dbOutputTitle", session.getTitle()));
      _resources = resources;
      createGUI(session);
   }

   private void createGUI(ISession session)
   {
      addWidgetListener(new WidgetAdapter()
      {
         public boolean widgetClosing(WidgetEvent e)
         {
            onWidgetClosing();
            return true;
         }
      });


      Icon icon = _resources.getIcon(getClass(), "frameIcon"); //i18n
      if (icon != null)
      {
         setFrameIcon(icon);
      }


      OracleInternalFrameCallback cb = (stayOnTop, autoRefreshPeriod) -> onCreatePanelAndToolBar(stayOnTop, autoRefreshPeriod);


      initFromPrefs(PREF_PART_DB_OUTPUT_FRAME, cb);
   }

   private void onCreatePanelAndToolBar(boolean stayOnTop, int autoRefreshPeriod)
   {
      _dbOutputPanel = new DBOutputPanel(getSession(), autoRefreshPeriod);
      _toolBar = new DBOutputToolBar(getSession(), stayOnTop, autoRefreshPeriod);
      JPanel contentPanel = new JPanel(new BorderLayout());
      contentPanel.add(_toolBar, BorderLayout.NORTH);
      contentPanel.add(_dbOutputPanel, BorderLayout.CENTER);
      setContentPane(contentPanel);

      _dbOutputPanel.setAutoRefreshPeriod(autoRefreshPeriod);
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
         super(session, DBOutputInternalFrame.this);
         createGUI(session, stayOnTop, autoRefeshPeriod);
      }

      private void createGUI(ISession session, boolean stayOnTop, int autoRefreshPeriod)
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
         _autoRefresh.addActionListener(e -> _dbOutputPanel.setAutoRefresh(_autoRefresh.isSelected()));
         add(_autoRefresh);


         //Create spinner for update period
         final SpinnerNumberModel model = new SpinnerNumberModel(autoRefreshPeriod, 1, 60, 5);
         JSpinner refreshRate = new JSpinner(model);
         refreshRate.addChangeListener(e -> _dbOutputPanel.setAutoRefreshPeriod(model.getNumber().intValue()));
         add(refreshRate);

         add(new JLabel(s_stringMgr.getString("oracle.Seconds2")));
      }

   }
}
