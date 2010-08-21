/*
 * Copyright (C) 2008 Michael Romankiewicz
 * microm at users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Michael Romankiewicz
 */
public class FirebirdManagerGrantTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 3032583770856669057L;
	private int treenodeType = 0;

    public FirebirdManagerGrantTreeNode() {
        super();
    }

    public FirebirdManagerGrantTreeNode(Object userObject, int piTreeNodeType) {
        super(userObject);
        treenodeType = piTreeNodeType;
    }

    /**
     * @param userObject
     */
    public FirebirdManagerGrantTreeNode(Object userObject) {
        super(userObject);

    }

    /**
     * @param userObject
     * @param allowsChildren
     */
    public FirebirdManagerGrantTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);

    }

    
    
    /**
     * @return Returns the treenodeType.
     */
    public int getTreenodeType() {
        return treenodeType;
    }
}
