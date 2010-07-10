/*
 * Copyright (C) 2003 Greg Mackness
 * gmackness@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Creates and marshalls an ObjectTreeTransferrable from an ObjectTree on a data transfer.
 * @author <A HREF="mailto:gmackness@users.sourceforge.net">Greg Mackness</A> *
 */
public class ObjectTreeTransferHandler extends TransferHandler {

	private DataFlavor objectTreeDataFlavor;

	public ObjectTreeTransferHandler() {
		super();
		objectTreeDataFlavor = ObjectTreeTransferable.getObjectTreeDataFlavor();
	}
	
	public int getSourceActions(JComponent c) {
		return COPY;
	}

	public boolean importData(JComponent c, Transferable t) {
		return false;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		return false;
	}

	protected Transferable createTransferable(JComponent c) {
		ObjectTree objectTree = (ObjectTree)c;
		ObjectTreeTransferSelection selection = new ObjectTreeTransferSelection();
		ObjectTreeNode[] selectedNodes = objectTree.getSelectedNodes();
		for(int i=0;i<selectedNodes.length;i++) {
			getItems(selectedNodes[i],selection);
		}
		ObjectTreeTransferable transferable = null;
		try {
			transferable = new ObjectTreeTransferable(selection);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return transferable;
	}

	private void getItems(ObjectTreeNode node,ObjectTreeTransferSelection selection) {
		//add this node to the appropriate collection 
		selection.addDatabaseObject(node.getDatabaseObjectInfo());
		if(node.getAllowsChildren() && node.getChildCount()>0) {
			Enumeration enum = node.children();
			while(enum.hasMoreElements()) {
				ObjectTreeNode child = (ObjectTreeNode)enum.nextElement();
				getItems(child,selection);
			}
		}
	}
}
