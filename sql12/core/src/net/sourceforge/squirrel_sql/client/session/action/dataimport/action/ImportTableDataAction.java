package net.sourceforge.squirrel_sql.client.session.action.dataimport.action;
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import java.awt.event.ActionEvent;

/**
 * An action to import table data from a file.
 *
 * @author Thorsten Mürell
 */
public class ImportTableDataAction extends SquirrelAction implements ISessionAction, IObjectTreeAction
{
   private IObjectTreeAPI _objectTreeAPI;
   private ISession _session;

   public ImportTableDataAction()
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   @Override
   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;
      setEnabled(null != _session);
   }

   @Override
   public void actionPerformed(ActionEvent ev)
   {
      if (_objectTreeAPI == null)
      {
         new ImportTableDataCommand(_session, Main.getApplication().getResources()).execute();
      }
      else
      {
         IDatabaseObjectInfo[] tables = _objectTreeAPI.getSelectedDatabaseObjects();
         if (0 < tables.length && tables[0] instanceof ITableInfo)
         {
            new ImportTableDataCommand(_objectTreeAPI.getSession(), Main.getApplication().getResources(), (ITableInfo) tables[0]).execute();
         }
         else
         {
            new ImportTableDataCommand(_session, Main.getApplication().getResources()).execute();
         }
      }
   }

}
