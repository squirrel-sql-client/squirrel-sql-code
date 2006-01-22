package net.sourceforge.squirrel_sql.fw.gui.action;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.SquirrelTableCellRenderer;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This command gets the current selected text from a <TT>JTable</TT>
 * and formats it as HTML table and places it on the system clipboard.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TableCopyHtmlCommand implements ICommand
{
	/** The table we are copying data from. */
	private JTable _table;

    private static HTMLSelection htmlSelection = null;
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(TableCopyHtmlCommand.class);
    
    /** Logger for this class. */
    private static ILogger s_log =
        LoggerController.createLogger(TableCopyHtmlCommand.class);
    
	/**
	 * Ctor specifying the <TT>JTable</TT> to get the data from.
	 *
	 * @param	table	The <TT>JTable</TT> to get data from.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if <tt>null</tt> <tt>JTable</tt> passed.
	 */
	public TableCopyHtmlCommand(JTable table)
	{
		super();
		if (table == null)
		{
			throw new IllegalArgumentException("JTable == null");
		}
		_table = table;
        if (htmlSelection == null) {
            try {
                htmlSelection = new HTMLSelection();
            } catch (Exception e) {
                String msg = 
                    s_stringMgr.getString("TableCopyHtmlCommand.error.flavors");
                s_log.error(msg, e);
            }
        }
	}

	/**
	 * Execute this command.
	 */
	public void execute()
	{
		int nbrSelRows = _table.getSelectedRowCount();
		int nbrSelCols = _table.getSelectedColumnCount();
		int[] selRows = _table.getSelectedRows();
		int[] selCols = _table.getSelectedColumns();
        
		if (selRows.length != 0 && selCols.length != 0)
		{
            StringBuffer buf = new StringBuffer(1024);
			buf.append("<table border=1><tr BGCOLOR=\"#CCCCFF\">");
			for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
			{
				buf.append("<th>");
				buf.append(_table.getColumnName(selCols[colIdx]));
				buf.append("</th>");
			}
			buf.append("</tr>\n");
			for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
			{
				buf.append("<tr>");
				for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
				{
					TableCellRenderer cellRenderer = _table.getCellRenderer(selRows[rowIdx], selCols[colIdx]);
					Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);

					if(cellRenderer instanceof SquirrelTableCellRenderer)
					{
						cellObj = ((SquirrelTableCellRenderer)cellRenderer).renderValue(cellObj);
					}


					buf.append("<td>");
					if (cellObj == null)
					{
						buf.append("&nbsp;");
					}
					else
					{
						final String tmp = cellObj. toString();
						if (tmp.trim().equals(""))
						{
							buf.append("&nbsp;");
						}
						else
						{
							buf.append(htmlizeString(tmp));
						}
					}
//					else if (cellObj instanceof String)
//					{
//						final String tmp = (String)cellObj;
//						if (tmp.trim().equals(""))
//							buf.append("&nbsp;");
//						else
//						{
//							buf.append(htmlizeString(tmp));
//						}
//					}
//					else
//					{
//						buf.append(cellObj);
//					}
					buf.append("</td>");
				}
				buf.append("</tr>\n");
			}
			buf.append("</table>");

            htmlSelection.setData(buf.toString());
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		    cb.setContents(htmlSelection, null);
		}
	}

	private static final String htmlizeString(String str)
	{
		final StringBuffer buf = new StringBuffer(1024);
		for (int i = 0, limit = str.length(); i < limit; ++i)
		{
			switch (str.charAt(i))
			{
				case '<':
					buf.append("&lt;");
					break;
				case '>':
					buf.append("&gt;");
					break;
				case '&':
					buf.append("&amp;");
					break;
				case '"':
					buf.append("&quot;");
					break;
				case '\n':
					buf.append("<BR>");
					break;
				default:
					buf.append(str.charAt(i));
			}
		}
		return buf.toString();
	}
    
    /**
     * A custom transferable that will inform the system clipboard that the 
     * data being transferred "prefers" to be represented in HTML format / 
     * MIME type is "text/html".  Will fallback to "text/plain" for applications
     * that don't support "text/html". 
     */
    private static class HTMLSelection implements Transferable {

        DataFlavor[] supportedFlavors = null;
        String _data = null;
        
        /**
         * Constructor.
         * 
         * @throws Exception if the "html" or "plain" flavors cannot be resolved. 
         */
        public HTMLSelection() throws Exception {
            DataFlavor htmlFlavor = new DataFlavor("text/html");
            DataFlavor plainFlavor = new DataFlavor("text/plain");
            supportedFlavors = new DataFlavor[] { htmlFlavor, plainFlavor };
        }
        
        /**
         * Sets the data that will be transferred to the clipboard.
         * 
         * @param data the data to transfer.
         */
        public void setData(String data) {
            _data = data;
        }
        
        /* (non-Javadoc)
         * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
         */
        public DataFlavor[] getTransferDataFlavors() {
            return supportedFlavors;
        }

        /* (non-Javadoc)
         * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
         */
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            boolean result = false;
            for (int i = 0; i < supportedFlavors.length; i++) {
                DataFlavor f = supportedFlavors[i];
                if (f.getMimeType().equals(flavor.getMimeType())) {
                    result = true;
                }
            }
            return result;
        }

        /* (non-Javadoc)
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         */
        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException
        {
            if (isDataFlavorSupported(flavor)) {
                return new ByteArrayInputStream(_data.getBytes());
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

    }
}
