package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.DataTruncation;
import java.sql.SQLException;
import java.sql.SQLWarning;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * This is the message panel at the bottom of the session sheet.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class MessagePanel extends JTextArea implements IMessageHandler
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(MessagePanel.class);

	/** Application API. */
	private IApplication _app;

	/** Popup menu for this component. */
	private TextPopupMenu _popupMenu = new TextPopupMenu();

	MessagePanel(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		_popupMenu.setTextComponent(this);

		// Add mouse listener for displaying popup menu.
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					_popupMenu.show(evt);
				}
			}
			public void mouseReleased(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					_popupMenu.show(evt);
				}
			}
		});
	}

	/**
	 * Add the passed line to the end of the messages display. Position
	 * display so the the newly added line will be displayed.
	 *
	 * @param	line	The line to be added.
	 */
	public void addLine(String line)
	{
		append("\n");
		append(line);
		int len = getDocument().getLength();
		select(len, len);
	}

	public void showMessage(final Throwable th)
	{
		if (th != null)
		{
			// Thread safe support for every call to this method:
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if (th instanceof DataTruncation)
					{
						DataTruncation ex = (DataTruncation) th;
						StringBuffer buf = new StringBuffer();
						buf.append("Data Truncation error occured on")
							.append(ex.getRead() ? " a read " : " a write ")
							.append(" of column ")
							.append(ex.getIndex())
							.append("Data was ")
							.append(ex.getDataSize())
							.append(" bytes long and ")
							.append(ex.getTransferSize())
							.append(" bytes were transferred.");
						showMessage(buf.toString());
					}
					else if (th instanceof SQLWarning)
					{
						SQLWarning ex = (SQLWarning) th;
						while (ex != null)
						{
							showMessage("Warning:   " + ex.getMessage());
							showMessage("SQLState:  " + ex.getSQLState());
							showMessage("ErrorCode: " + ex.getErrorCode());
							s_log.debug("Error", th);
							ex = ex.getNextWarning();
						}
					}
					else if (th instanceof SQLException)
					{
						SQLException ex = (SQLException) th;
						while (ex != null)
						{
							showMessage("Error:     " + ex.getMessage());
							showMessage("SQLState:  " + ex.getSQLState());
							showMessage("ErrorCode: " + ex.getErrorCode());
							s_log.debug("Error", th);
							ex = ex.getNextException();
						}
					}
					else
					{
						showMessage(th.toString());
						s_log.debug("Error shown in MessagePanel", th);
					}
				}
			});
		}
	}

	public void showMessage(final String msg)
	{
		if (msg == null)
		{
			throw new IllegalArgumentException("null Message");
		}
		// Thread safe support for every call to this method:
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				addLine(msg);
			}
		});
	}
}