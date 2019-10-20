package net.sourceforge.squirrel_sql.plugins.dataimport.action;
/*
 * Copyright (C) 2007 Thorsten Mürell
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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.resources.IResources;

/**
 * An action to import table data from a file.
 *
 * @author Thorsten Mürell
 */
public class ImportTableDataAction extends SquirrelAction implements IObjectTreeAction
{
   private IObjectTreeAPI _objectTreeAPI;

   public ImportTableDataAction(IApplication app, IResources resources)
   {
      super(app, resources);
   }

   @Override
   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
      setEnabled(null != _objectTreeAPI);
   }

   @Override
   public void actionPerformed(ActionEvent ev)
   {
      if (_objectTreeAPI != null)
      {
         IDatabaseObjectInfo[] tables = _objectTreeAPI.getSelectedDatabaseObjects();
         if (tables.length != 1 || false == tables[0] instanceof ITableInfo)
         {

         }

         new ImportTableDataCommand(_objectTreeAPI.getSession(), (ITableInfo) tables[0]).execute();
      }
   }

}
