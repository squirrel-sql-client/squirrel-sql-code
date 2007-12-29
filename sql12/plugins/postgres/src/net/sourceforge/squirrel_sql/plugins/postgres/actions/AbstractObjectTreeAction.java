package net.sourceforge.squirrel_sql.plugins.postgres.actions;
/*
* Copyright (C) 2007 Daniel Regli & Yannick Winiger
* http://sourceforge.net/projects/squirrel-sql
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

import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import java.awt.event.ActionEvent;

public abstract class AbstractObjectTreeAction extends SquirrelAction implements IObjectTreeAction {
    /** Current object tree */
    protected IObjectTreeAPI _tree;


    public AbstractObjectTreeAction(IApplication app, Resources rsrc) {
        super(app, rsrc);
    }


    public void actionPerformed(ActionEvent evt) {
        if (_tree != null) {
            IDatabaseObjectInfo[] infos = _tree.getSelectedDatabaseObjects();
            if (infos.length > 1 && !isMultipleObjectAction()) {
                _tree.getSession().showMessage(getErrorMessage());
            } else {
                try {
                    getCommand(infos).execute();
                } catch (Exception e) {
                    _tree.getSession().showMessage(e);
                }
            }
        }
    }


    protected abstract ICommand getCommand(IDatabaseObjectInfo[] info);


    protected abstract boolean isMultipleObjectAction();


    protected abstract String getErrorMessage();


    /**
     * Set the current object tree.
     *
     * @param tree The current object tree.
     */
    public void setObjectTree(IObjectTreeAPI tree) {
        _tree = tree;
        setEnabled(_tree != null);
    }
}
