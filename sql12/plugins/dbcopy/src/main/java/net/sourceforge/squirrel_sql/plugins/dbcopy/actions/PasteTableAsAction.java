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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbcopy.commands.PasteTableCommand;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class PasteTableAsAction extends SquirrelAction
                                     implements ISessionAction {

	/** Current plugin. */
	private final SessionInfoProvider sessionInfoProv;

    /** The IApplication that we can use to display error dialogs */
    private IApplication app = null;

    /** Logger for this class. */
    private final static ILogger log =
                         LoggerController.createLogger(PasteTableAsAction.class);

    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(PasteTableAsAction.class);

    /**
     * Creates a new SQuirreL action that gets fired whenever the user chooses
     * the paste operation.
     *
     * @param app
     * @param rsrc
     * @param plugin
     */
    public PasteTableAsAction(IApplication app, Resources rsrc,
                              DBCopyPlugin plugin) {
        super(app, rsrc);
        this.app = app;
        sessionInfoProv = plugin;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {

       if(null == sessionInfoProv.getSourceDatabaseObjects())
       {
          return;
       }

       if(1 != sessionInfoProv.getSourceDatabaseObjects().size())
       {
          JOptionPane.showMessageDialog(app.getMainFrame(), s_stringMgr.getString("EditPasteTableNameDlg.onlyOneTableMsg"));
          return;
       }

       EditPasteTableNameDlg dlg = new EditPasteTableNameDlg(app.getMainFrame());
       GUIUtils.centerWithinParent(dlg);
       dlg.setVisible(true);

       if(null == dlg.getTableName())
       {
          return;
       }

       PasteTableUtil.excePasteTable(sessionInfoProv, app, dlg.getTableName());


       System.out.println("dlg.getTableName() = " + dlg.getTableName());

    }

	/**
	 * Set the current session.
	 * 
	 * @param	session		The current session.
	 */
    public void setSession(ISession session) {
        sessionInfoProv.setDestSession(session);        
    }
}
