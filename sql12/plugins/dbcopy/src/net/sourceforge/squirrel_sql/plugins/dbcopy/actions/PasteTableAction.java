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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;


public class PasteTableAction extends SquirrelAction
                                     implements ISessionAction {

	private static final long serialVersionUID = 1L;

	/** Current plugin. */
	private final SessionInfoProvider sessionInfoProv;

    /** The IApplication that we can use to display error dialogs */
    private IApplication app = null;
    
    /**
     * Creates a new SQuirreL action that gets fired whenever the user chooses
     * the paste operation.
     * 
     * @param app
     * @param rsrc
     * @param plugin
     */
    public PasteTableAction(IApplication app, Resources rsrc,
    									DBCopyPlugin plugin) {
        super(app, rsrc);
        this.app = app;
        sessionInfoProv = plugin;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
       PasteTableUtil.excePasteTable(sessionInfoProv, app, null);
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
