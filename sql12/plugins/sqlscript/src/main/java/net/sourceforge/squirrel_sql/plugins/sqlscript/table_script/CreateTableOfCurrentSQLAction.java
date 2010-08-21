package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

/*
 * Copyright (C) 2005 Gerd Wagner
 * gerdwagner@users.sourceforge.net
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

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;

public class CreateTableOfCurrentSQLAction extends SquirrelAction implements ISQLPanelAction
{

   /**
    * Current session.
    */
   private ISession _session;

   /**
    * Current plugin.
    */
   private final SQLScriptPlugin _plugin;

   public CreateTableOfCurrentSQLAction(IApplication app, Resources rsrc,
                                        SQLScriptPlugin plugin)
   {
      super(app, rsrc);
      _plugin = plugin;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_session != null)
      {
         new CreateTableOfCurrentSQLCommand(_session, _plugin).execute();
      }
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      if(null != panel)
      {
         _session = panel.getSession();
      }
      else
      {
         _session = null;
      }
      setEnabled(null != _session);
   }
}
