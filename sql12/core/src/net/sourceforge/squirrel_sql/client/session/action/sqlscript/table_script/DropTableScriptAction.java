package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;
/*
 * Copyright (C) 2006 Rob Manning
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;

import java.awt.event.ActionEvent;

public class DropTableScriptAction extends SquirrelAction implements IObjectTreeAction
{
   private IObjectTreeAPI _objectTreeAPI;

   public DropTableScriptAction()
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_objectTreeAPI != null)
      {
         new DropTableScriptCommand(_objectTreeAPI).execute();
      }
   }

   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
      setEnabled(null != _objectTreeAPI);
   }
}