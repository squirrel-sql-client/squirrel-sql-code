/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action.wikiTable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * A custom transferable that will inform the system clipboard that the data
 * being transferred as "text/plain".
 * 
 * @author Stefan Willinger
 * 
 */
public class WikiTableSelection implements Transferable {

	
	private DataFlavor[] supportedFlavors = null;
	private String data = null;

	/**
	 * Default Constructor.
	 * A WikiTableSelection supports a {@link DataFlavor} <code>text/plain</code> 
	 * @throws ClassNotFoundException if the class for  {@link DataFlavor} doesn't exist. 
	 */
	public WikiTableSelection() throws ClassNotFoundException {
		super();
        DataFlavor plainFlavor = new DataFlavor("text/plain");
        supportedFlavors = new DataFlavor[] { plainFlavor };
	}

	/**
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	/**
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.
	 * datatransfer.DataFlavor)
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		boolean result = false;
		for (DataFlavor f : supportedFlavors) {
			if (f.getMimeType().equals(flavor.getMimeType())) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * @see
	 * java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer
	 * .DataFlavor)
	 */
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (isDataFlavorSupported(flavor)) {
            return new ByteArrayInputStream(data.getBytes());
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
	}

	/**
	 * @param string
	 */
	public void setData(String string) {
		this.data = string;
		
	}

}
