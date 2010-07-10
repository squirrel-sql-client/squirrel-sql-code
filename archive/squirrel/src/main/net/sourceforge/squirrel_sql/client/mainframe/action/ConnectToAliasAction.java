package net.sourceforge.squirrel_sql.client.mainframe.action;
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

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.AliasesList;

/**
 * This <CODE>Action</CODE> allows the user to connect to an alias.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectToAliasAction extends SquirrelAction {
    /**
     * List of all the users aliases.
     */
    private AliasesList _aliases;

    /**
     * Ctor specifying the list of aliases.
     *
     * @param   app     Application API.
     * @param   list    List of <TT>ISQLAlias</TT> objects.
     */
    public ConnectToAliasAction(IApplication app, AliasesList list) {
        super(app);
        _aliases = list;
    }

    /**
     * Perform this action. Retrieve the current alias from this list and
     * connect to it.
     *
     * @param   evt     The current event.
     */
    public void actionPerformed(ActionEvent evt) {
        final ISQLAlias alias = _aliases.getSelectedAlias();
        if (alias != null) {
            new ConnectToAliasCommand(getApplication(), getParentFrame(evt), alias).execute();
        }
    }
}
