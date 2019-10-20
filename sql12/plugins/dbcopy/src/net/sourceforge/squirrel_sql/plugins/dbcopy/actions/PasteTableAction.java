package net.sourceforge.squirrel_sql.plugins.dbcopy.actions;
/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;


public class PasteTableAction extends SquirrelAction implements IObjectTreeAction
{
   /**
    * Current plugin.
    */
   private final SessionInfoProvider sessionInfoProv;

   /**
    * The IApplication that we can use to display error dialogs
    */
   private IApplication app = null;
   private IObjectTreeAPI _objectTreeAPI;

   /**
    * Creates a new SQuirreL action that gets fired whenever the user chooses
    * the paste operation.
    *
    * @param app
    * @param rsrc
    * @param plugin
    */
   public PasteTableAction(IApplication app, Resources rsrc, DBCopyPlugin plugin)
   {
      super(app, rsrc);
      this.app = app;
      sessionInfoProv = plugin.getSessionInfoProvider();
   }

   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent evt)
   {
      PasteTableUtil.execPasteTable(sessionInfoProv, app);
   }


   @Override
   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;

      sessionInfoProv.setDestObjectTree(_objectTreeAPI);

      setEnabled(null != _objectTreeAPI);
   }
}
