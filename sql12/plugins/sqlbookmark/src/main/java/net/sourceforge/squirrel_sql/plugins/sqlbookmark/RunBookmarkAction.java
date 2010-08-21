/*
 * Copyright (C) 2003 Joseph Mocker
 * mock-sf@misfit.dhs.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

/**
 * Initiates execution of a bookmark when user clicks on a bookmark
 * menu item.
 *
 * @author Joseph Mocker
 */
public class RunBookmarkAction extends SquirrelAction
   implements ISQLPanelAction
{
    private static final long serialVersionUID = 1L;

    /**
	 * Current session to load the bookmark into
	 */
	transient private ISession session;

   /**
    * Handle to the main plugin object
    */
   transient private SQLBookmarkPlugin plugin;

   public RunBookmarkAction(IApplication app, Resources rsrc,
                            SQLBookmarkPlugin plugin)
      throws IllegalArgumentException
   {
      super(app, rsrc);
      if (plugin == null)
      {
         throw new IllegalArgumentException("null IPlugin passed");
      }
      this.plugin = plugin;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (session != null)
      {
         Object source = evt.getSource();
         if (source instanceof JMenuItem)
         {
            JMenuItem item = (JMenuItem) source;

            Bookmark bookmark =
               plugin.getBookmarkManager().get(item.getText());

            ISQLEntryPanel sqlEntryPanel;

            if (session.getActiveSessionWindow() instanceof SessionInternalFrame)
            {
               sqlEntryPanel = ((SessionInternalFrame) session.getActiveSessionWindow()).getSQLPanelAPI().getSQLEntryPanel();
            }
            else if (session.getActiveSessionWindow() instanceof SQLInternalFrame)
            {
               sqlEntryPanel = ((SQLInternalFrame) session.getActiveSessionWindow()).getSQLPanelAPI().getSQLEntryPanel();
            }
            else
            {
               return;
            }


            if (bookmark != null)
               new RunBookmarkCommand(getParentFrame(evt), session, bookmark, plugin, sqlEntryPanel).execute();
         }
      }
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      if(null != panel)
      {
         session = panel.getSession();
      }
      else
      {
         session = null;
      }
      setEnabled(null != session);
   }
   
}
