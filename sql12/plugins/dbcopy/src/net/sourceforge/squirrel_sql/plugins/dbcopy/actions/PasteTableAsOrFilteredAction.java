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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class PasteTableAsOrFilteredAction extends SquirrelAction implements ISessionAction
{

	/** Current plugin. */
	private final SessionInfoProvider _sessionInfoProv;


   /** Internationalized strings for this class */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(PasteTableAsOrFilteredAction.class);

    /**
     * Creates a new SQuirreL action that gets fired whenever the user chooses
     * the paste operation.
     *
     * @param rsrc
     * @param plugin
     */
    public PasteTableAsOrFilteredAction(Resources rsrc, DBCopyPlugin plugin)
    {
       super(Main.getApplication(), rsrc);
       _sessionInfoProv = plugin.getSessionInfoProvider();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
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

       List<ITableInfo> selectedTables = _sessionInfoProv.getDestSession().getObjectTreeAPIOfActiveSessionWindow().getSelectedTables();

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

       PasteTableUtil.excePasteTable(_sessionInfoProv, Main.getApplication());
    }

   /**
	 * Set the current session.
	 * 
	 * @param	session		The current session.
	 */
    public void setSession(ISession session) {
        _sessionInfoProv.setDestSession(session);
    }
}
