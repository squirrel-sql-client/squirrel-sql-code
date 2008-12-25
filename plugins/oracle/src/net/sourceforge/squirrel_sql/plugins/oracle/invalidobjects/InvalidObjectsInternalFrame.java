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
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrame;
import net.sourceforge.squirrel_sql.plugins.oracle.OracleInternalFrameCallback;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class InvalidObjectsInternalFrame extends OracleInternalFrame
{

   private static final String PREF_PART_INVALID_FRAME = "InvalidFrame";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(InvalidObjectsInternalFrame.class);

   private InvalidObjectsPanel _invalidObjectsPanel;
   /**
    * Toolbar for window.
    */
   private InvalidObjectsToolBar _toolBar;

   private Resources _resources;

   public InvalidObjectsInternalFrame(ISession session, Resources resources)
   {

      // I18n[oracle.invalidTitle=Oracle invalid objects for: {0}]
      super(session, s_stringMgr.getString("oracle.invalidTitle", session.getTitle()));
      _resources = resources;
      createGUI(session);
   }

   public InvalidObjectsPanel getDBOutputPanel()
   {
      return _invalidObjectsPanel;
   }

   private void createGUI(ISession session)
   {

      addWidgetListener(new WidgetAdapter()
      {
         public void widgetClosing(WidgetEvent e)
         {
            InvalidObjectsInternalFrame.super.internalFrameClosing(_toolBar.isStayOnTop(), 0);
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

            _invalidObjectsPanel = new InvalidObjectsPanel(getSession());
            _toolBar = new InvalidObjectsToolBar(getSession(), stayOnTop);
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(_toolBar, BorderLayout.NORTH);
            contentPanel.add(_invalidObjectsPanel, BorderLayout.CENTER);
            setContentPane(contentPanel);

         }
      };


      initFromPrefs(PREF_PART_INVALID_FRAME, cb);
   }


   

   /**
    * The class representing the toolbar at the top of a invalid objects internal frame
    */
   private class InvalidObjectsToolBar extends OracleToolBar
   {
      InvalidObjectsToolBar(ISession session, boolean stayOnTop)
      {
         super();
         createGUI(session, stayOnTop);
      }

      private void createGUI(ISession session, boolean stayOnTop)
      {
         IApplication app = session.getApplication();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(new GetInvalidObjectsAction(app, _resources, _invalidObjectsPanel));

         addStayOnTop(stayOnTop);
      }
   }
}
