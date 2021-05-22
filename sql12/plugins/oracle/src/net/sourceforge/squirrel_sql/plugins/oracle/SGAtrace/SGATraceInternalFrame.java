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

public class SGATraceInternalFrame extends OracleInternalFrame
{
   private static final String PREF_PART_SGA_FRAME = "SGAFrame";


   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SGATraceInternalFrame.class);

   private SGATracePanel _sgaTracePanel;

   private SGATraceToolBar _toolBar;

   transient private Resources _resources;

   public SGATraceInternalFrame(ISession session, Resources resources)
   {
      super(session, s_stringMgr.getString("oracle.sgaTitle", session.getTitle()));
      _resources = resources;
      createGUI();
   }

   private void createGUI()
   {
      addWidgetListener(new WidgetAdapter()
      {
         public boolean widgetClosing(WidgetEvent e)
         {
            SGATraceInternalFrame.super.internalFrameClosing(_toolBar.isStayOnTop(), _sgaTracePanel.getAutoRefreshPeriod());
            _sgaTracePanel.setAutoRefresh(false);
            return true;
         }
      });


      Icon icon = _resources.getIcon(getClass(), "frameIcon");
      if (icon != null)
      {
         setFrameIcon(icon);
      }

      OracleInternalFrameCallback cb = (stayOnTop, autoRefreshPeriod) -> onCreatePanelAndToolBar(stayOnTop, autoRefreshPeriod);

      initFromPrefs(PREF_PART_SGA_FRAME, cb);
   }

   private void onCreatePanelAndToolBar(boolean stayOnTop, int autoRefreshPeriod)
   {
      _sgaTracePanel = new SGATracePanel(getSession(), autoRefreshPeriod);
      _toolBar = new SGATraceToolBar(getSession(), stayOnTop, autoRefreshPeriod);
      JPanel contentPanel = new JPanel(new BorderLayout());
      contentPanel.add(_toolBar, BorderLayout.NORTH);
      contentPanel.add(_sgaTracePanel, BorderLayout.CENTER);
      setContentPane(contentPanel);
   }

   /**
    * The class representing the toolbar at the top of a SGA Trace internal frame
    */
   private class SGATraceToolBar extends OracleToolBar
   {
      SGATraceToolBar(ISession session, boolean stayOnTop, int autoRefeshPeriod)
      {
         super(session, SGATraceInternalFrame.this);
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
         autoRefresh.addActionListener(e -> _sgaTracePanel.setAutoRefresh(autoRefresh.isSelected()));
         add(autoRefresh);

         //Create spinner for update period
         final SpinnerNumberModel model = new SpinnerNumberModel(autoRefeshPeriod, 1, 60, 5);
         final JSpinner refreshRate = new JSpinner(model);
         refreshRate.addChangeListener(e -> _sgaTracePanel.setAutoRefreshPeriod(model.getNumber().intValue()));
         add(refreshRate);
         // i18n[oracle.refreshSecons=(seconds)]
         add(new JLabel(s_stringMgr.getString("oracle.refreshSecons")));
      }
   }
}
