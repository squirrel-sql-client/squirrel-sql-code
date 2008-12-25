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
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SGATraceInternalFrame extends OracleInternalFrame
{
    private static final long serialVersionUID = 1L;


    private static final String PREF_PART_SGA_FRAME = "SGAFrame";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SGATraceInternalFrame.class);

   private SGATracePanel _sgaTracePanel;
   /**
    * Toolbar for window.
    */
   private SGATraceToolBar _toolBar;

   transient private Resources _resources;

   public SGATraceInternalFrame(ISession session, Resources resources)
   {
      // I18n[oracle.sgaTitle=Oracle SGA trace for: {0}]
      super(session, s_stringMgr.getString("oracle.sgaTitle", session.getTitle()));
      _resources = resources;
      createGUI();
   }

   public SGATracePanel getSGATracePanel()
   {
      return _sgaTracePanel;
   }

   private void createGUI()
   {
      addWidgetListener(new WidgetAdapter()
      {
         public void widgetClosing(WidgetEvent e)
         {
            SGATraceInternalFrame.super.internalFrameClosing(_toolBar.isStayOnTop(), _sgaTracePanel.getAutoRefreshPeriod());
            _sgaTracePanel.setAutoRefresh(false);
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
            _sgaTracePanel = new SGATracePanel(getSession(), autoRefeshPeriod);
            _toolBar = new SGATraceToolBar(getSession(), stayOnTop, autoRefeshPeriod);
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(_toolBar, BorderLayout.NORTH);
            contentPanel.add(_sgaTracePanel, BorderLayout.CENTER);
            setContentPane(contentPanel);
         }
      };

      initFromPrefs(PREF_PART_SGA_FRAME, cb);


   }

   /**
    * The class representing the toolbar at the top of a SGA Trace internal frame
    */
   private class SGATraceToolBar extends OracleToolBar
   {
    private static final long serialVersionUID = 1L;

    SGATraceToolBar(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         super();
         createGUI(session, stayOnTop, autoRefeshPeriod);
      }

      private void createGUI(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         IApplication app = session.getApplication();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(new GetSGATraceAction(app, _resources, _sgaTracePanel));

         addStayOnTop(stayOnTop);


         //Create checkbox for enabling auto refresh
         // i18n[oracle.enableAutoRefresh=Enable auto refresh]
         final JCheckBox autoRefresh = new JCheckBox(s_stringMgr.getString("oracle.enableAutoRefresh"), false);
         autoRefresh.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               _sgaTracePanel.setAutoRefresh(autoRefresh.isSelected());
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
               _sgaTracePanel.setAutoRefreshPeriod(model.getNumber().intValue());
            }
         });
         add(refreshRate);
         // i18n[oracle.refreshSecons=(seconds)]
         add(new JLabel(s_stringMgr.getString("oracle.refreshSecons")));
      }
   }
}
