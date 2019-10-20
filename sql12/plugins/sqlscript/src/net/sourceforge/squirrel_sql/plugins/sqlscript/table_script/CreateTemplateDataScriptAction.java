package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;
/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateTemplateDataScriptAction extends SquirrelAction implements IObjectTreeAction
{
   private final SQLScriptPlugin _plugin;
   private IObjectTreeAPI _objectTreeAPI;

   public CreateTemplateDataScriptAction(IApplication app, IResources resources, SQLScriptPlugin plugin)
   {
      super(app, resources);
      _plugin = plugin;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_objectTreeAPI != null)
      {
         new CreateDataScriptCommand(_objectTreeAPI, true).execute();
      }
   }

   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
      setEnabled(null != _objectTreeAPI);
   }
}
