package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class RefreshTreeAction extends SquirrelAction implements ISessionAction {

    private ISession _session;

    public RefreshTreeAction(IApplication app) {
        super(app);
    }

    public void setSession(ISession session) {
        _session = session;
    }

    public void actionPerformed(ActionEvent evt) {
        if (_session != null) {
            CursorChanger cursorChg = new CursorChanger(MainFrame.getInstance());
            cursorChg.show();
            try {
                _session.getSessionSheet().refreshTree();
            } catch (BaseSQLException ex) {
                getApplication().getLogger().showMessage(Logger.ILogTypes.ERROR,
                                    "Error occured refreshing the objects tree");
            } finally {
                cursorChg.restore();
            }
        }
    }
}

