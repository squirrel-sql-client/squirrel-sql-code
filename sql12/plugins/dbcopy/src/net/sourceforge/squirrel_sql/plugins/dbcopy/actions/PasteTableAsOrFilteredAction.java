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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class PasteTableAsOrFilteredAction extends SquirrelAction implements IObjectTreeAction
{
	private final SessionInfoProvider _sessionInfoProv;

    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(PasteTableAsOrFilteredAction.class);

    public PasteTableAsOrFilteredAction(Resources rsrc, DBCopyPlugin plugin)
    {
       super(Main.getApplication(), rsrc);
       _sessionInfoProv = plugin.getSessionInfoProvider();
    }

    public void actionPerformed(ActionEvent evt) {

       if(null == _sessionInfoProv.getSourceDatabaseObjects())
       {
          return;
       }

       Frame owningFrame = SessionUtils.getOwningFrame(_sessionInfoProv.getDestSession());

       if(1 != _sessionInfoProv.getSourceDatabaseObjects().size())
       {

          JOptionPane.showMessageDialog(owningFrame, s_stringMgr.getString("EditPasteTableNameDlg.onlyOneTableMsg"));
          return;
       }

       List<ITableInfo> selectedTables = _sessionInfoProv.getDestObjectTreeAPI().getSelectedTables();

       String destTableName = _sessionInfoProv.getSourceDatabaseObjects().get(0).getSimpleName();

       if(1 == selectedTables.size())
       {
          destTableName = selectedTables.get(0).getSimpleName();
       }

       EditPasteTableNameCtrl ctrl = new EditPasteTableNameCtrl(owningFrame, destTableName);

       if(null == ctrl.getTableName())
       {
          return;
       }

       _sessionInfoProv.setWhereClause(ctrl.getWhereClause());
       _sessionInfoProv.setPasteToTableName(ctrl.getTableName());

       PasteTableUtil.execPasteTable(_sessionInfoProv, Main.getApplication());
    }


   @Override
   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _sessionInfoProv.setDestObjectTree(objectTreeAPI);

      setEnabled(null != objectTreeAPI);
   }
}
