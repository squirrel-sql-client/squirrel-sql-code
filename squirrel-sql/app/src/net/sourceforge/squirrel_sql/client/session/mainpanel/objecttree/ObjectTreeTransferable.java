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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * This class acts as a data provider for a Transfer operation.
 * 
 * @author  <A HREF="mailto:gmackness@users.sourceforge.net">Greg Mackness</A>
 */
public class ObjectTreeTransferable implements Transferable {

	public static final String MIME_TYPE_OBJECT_TRANSFER_SELECTION = DataFlavor.javaJVMLocalObjectMimeType +
						";class=net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeTransferSelection";
	private DataFlavor objectTreeFlavor;
	private ObjectTreeTransferSelection selection;
	/**
	 * @param selection The ObjectTreeTransferSelection to pass to a target TransferHandler.
	 * @throws ClassNotFoundException Only thrown if <code>ObjectTreeTransferSelection.class</code> cannot be found.
	 */
	public ObjectTreeTransferable(ObjectTreeTransferSelection selection) throws ClassNotFoundException {
		this.selection = selection;
		objectTreeFlavor = new DataFlavor(MIME_TYPE_OBJECT_TRANSFER_SELECTION);
	}
	/**
	 * @inheritDoc
	 */
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors = new DataFlavor[1];
		flavors[0] = objectTreeFlavor;
		return flavors;
	}
	/** 
	 * @inheritDoc
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if(flavor.isMimeTypeEqual(objectTreeFlavor) || flavor.getMimeType().startsWith(MIME_TYPE_OBJECT_TRANSFER_SELECTION)) {
			return true;
		}
		return false;
	}

	/**
	 * @inheritdoc
	 * @param a DataFlavor with a mime-type of <code>MIME_TYPE_OBJECT_TRANSFER_SELECTION</code>
	 * @return an ObjectTreeTransferSelection
	 */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if(!flavor.isMimeTypeEqual(objectTreeFlavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return selection;
	}
	
	/**
	 * Returns a DataFlavor instance for mime-type MIME_TYPE_OBJECT_TRANSFER_SELECTION
	 * @return the new DataFlavor
	 */
	public static DataFlavor getObjectTreeDataFlavor() {
		try {
			return new DataFlavor(MIME_TYPE_OBJECT_TRANSFER_SELECTION);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
