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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrame;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrameCallback;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SessionInfoInternalFrame extends OracleInternalFrame
{

   private static final String PREF_PART_INFO_FRAME = "InfoFrame";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SessionInfoInternalFrame.class);



   private SessionInfoPanel _sessionInfoPanel;
   /**
    * Toolbar for window.
    */
   private SessionInfoToolBar _toolBar;

   private Resources _resources;

   public SessionInfoInternalFrame(ISession session, Resources resources)
   {
      // I18n[oracle.infoTitle=Oracle Session info for: {0}]
      super(session, s_stringMgr.getString("oracle.infoTitle", session.getTitle()));
      _resources = resources;
      createGUI(session);
   }

   public SessionInfoPanel getDBOutputPanel()
   {
      return _sessionInfoPanel;
   }

   private void createGUI(ISession session)
   {

      addWidgetListener(new WidgetAdapter()
      {
         public void widgetClosing(WidgetEvent e)
         {
            SessionInfoInternalFrame.super.internalFrameClosing(_toolBar.isStayOnTop(), _sessionInfoPanel.getAutoRefreshPeriod());
            _sessionInfoPanel.setAutoRefresh(false);
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
            _sessionInfoPanel = new SessionInfoPanel(getSession(), autoRefeshPeriod);
            _toolBar = new SessionInfoToolBar(getSession(), stayOnTop, autoRefeshPeriod);
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(_toolBar, BorderLayout.NORTH);
            contentPanel.add(_sessionInfoPanel, BorderLayout.CENTER);
            setContentPane(contentPanel);

            _sessionInfoPanel.setAutoRefreshPeriod(autoRefeshPeriod);
         }
      };

      initFromPrefs(PREF_PART_INFO_FRAME, cb);
   }

   /**
    * The class representing the toolbar at the top of a session info internal frame
    */
   private class SessionInfoToolBar extends OracleToolBar
   {
      SessionInfoToolBar(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         super();
         createGUI(session, stayOnTop, autoRefeshPeriod);
      }

      private void createGUI(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         IApplication app = session.getApplication();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(new GetSessionInfoAction(app, _resources, _sessionInfoPanel));

         addStayOnTop(stayOnTop);

         //Create checkbox for enabling auto refresh
         // i18n[oracle.auotRefresh2=Enable auto refresh]
         final JCheckBox autoRefresh = new JCheckBox(s_stringMgr.getString("oracle.auotRefresh2"), false);
         autoRefresh.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               _sessionInfoPanel.setAutoRefresh(autoRefresh.isSelected());
            }
         });
         add(autoRefresh);

         //Create spinner for update period
         final SpinnerNumberModel model = new SpinnerNumberModel(autoRefeshPeriod, 1, 60, 5);
         final JSpinner refreshRate = new JSpinner(model);
         refreshRate.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               _sessionInfoPanel.setAutoRefreshPeriod(model.getNumber().intValue());
            }
         });
         add(refreshRate);
         // i18n[oracle.secons3=(seconds)]
         add(new JLabel(s_stringMgr.getString("oracle.secons3")));
      }
   }
}
