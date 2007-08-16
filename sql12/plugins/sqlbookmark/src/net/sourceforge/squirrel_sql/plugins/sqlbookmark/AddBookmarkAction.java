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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

/**
 * Prompt for name and add a new bookmark into the system.
 *
 * @author Joseph Mocker
 */
public class AddBookmarkAction extends SquirrelAction
   implements ISQLPanelAction
{
    private static final long serialVersionUID = 1L;
    transient private ISession session;
    transient private SQLBookmarkPlugin plugin;

   public AddBookmarkAction(IApplication app, Resources rsrc,
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
         new AddBookmarkCommand(getParentFrame(evt), session, plugin).execute();
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
