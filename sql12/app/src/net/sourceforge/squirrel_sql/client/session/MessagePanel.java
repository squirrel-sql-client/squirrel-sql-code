package net.sourceforge.squirrel_sql.client.session;
/*
 * TODO: i18n
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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.DataTruncation;
import java.sql.SQLException;
import java.sql.SQLWarning;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This is the message panel at the bottom of the session sheet.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MessagePanel extends JTextPane implements IMessageHandler
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(MessagePanel.class);

	/** Popup menu for this component. */
	private final TextPopupMenu _popupMenu = new MessagePanelPopupMenu();

	/**
	 * Attribute sets for error and last message.
	 */
	private SimpleAttributeSet _saSetLastMessage;
	private SimpleAttributeSet _saSetLastMessageError;
	private SimpleAttributeSet _saSetError;

	/**
	 * Save into these attributes the parameters of the last message being output.
	 * @todo In the near future: if more than one message shall be remembered, then these variables
	 * need to be replaced with a dynamic storage (ArrayList or similar).
	 */
	private int _lastLength;
	private String _lastMessage;
	private SimpleAttributeSet _lastSASet;

	/**
	 * Default ctor.
	 */
	public MessagePanel()
	{
		super();

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

		// Initialize sttribute sets.
		// Last message, no error.
		_saSetLastMessage = new SimpleAttributeSet();
		StyleConstants.setBackground(_saSetLastMessage, Color.green);
		// Last message, error.
		_saSetLastMessageError = new SimpleAttributeSet();
		StyleConstants.setBackground(_saSetLastMessageError, Color.red);
		// Message, with error.
		_saSetError = new SimpleAttributeSet();
		StyleConstants.setBackground(_saSetError, Color.pink);
	}

	/**
	 * Show a message describing the passed throwable object.
	 *
	 * @param th	The throwable object.
	 */
	public synchronized void showMessage(final Throwable th)
	{
		if (th != null)
		{
			privateShowMessage(th, null);
		}
	}

	/**
	 * Show a message.
	 *
	 * @param msg	The message to be shown.
	 */
	public synchronized void showMessage(final String msg)
	{
		if (msg != null)
		{
			privateShowMessage(msg, null);
		}
	}

	/**
	 * Show an error message describing the passed exception. The controls
	 * background color will be changed to show it is an error msg.
	 *
	 * @param	th		Exception.
	 */
	public synchronized void showErrorMessage(final Throwable th)
	{
		if (th != null)
		{
			privateShowMessage(th, _saSetError);
		}
	}

	/**
	 * Show an error message. The controls
	 * background color will be changed to show it is an error msg.
	 *
	 * @param	th		Exception.
	 */
	public synchronized void showErrorMessage(final String msg)
	{
		if (msg != null)
		{
			privateShowMessage(msg, _saSetError);
		}
	}

	/**
	 * Private method, the real implementation of the corresponding show*Message methods.
	 *
	 * @param th	The throwable whose details shall be displayed.
	 * @param saSet The SimpleAttributeSet to be used for message output.
	 */
	private void privateShowMessage(final Throwable th, SimpleAttributeSet saSet)
	{
		if (th != null)
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
				privateShowMessage(buf.toString(), saSet);
			}
			else if (th instanceof SQLWarning)
			{
				SQLWarning ex = (SQLWarning) th;
				while (ex != null)
				{
					StringBuffer buf = new StringBuffer();
					buf.append("Warning:   ")
						.append(ex.getMessage())
						.append("\nSQLState:  ")
						.append(ex.getSQLState())
						.append("\nErrorCode: ")
						.append(ex.getErrorCode());
					s_log.debug("Warning shown in MessagePanel", th);
					ex = ex.getNextWarning();
					privateShowMessage(buf.toString(), saSet);
				}
			}
			else if (th instanceof SQLException)
			{
				SQLException ex = (SQLException) th;
				while (ex != null)
				{
					StringBuffer buf = new StringBuffer();
					buf.append("Error:	 ")
						.append(ex.getMessage())
						.append("\nSQLState:  ")
						.append(ex.getSQLState())
						.append("\nErrorCode: ")
						.append(ex.getErrorCode());
					s_log.debug("Error", th);
					ex = ex.getNextException();
					privateShowMessage(buf.toString(), saSet);
				}
			}
			else
			{
				privateShowMessage(th.toString(), saSet);
				s_log.debug("Exception shown in MessagePanel", th);
			}
		}
	}

	/**
	 * Private method, the real implementation of the corresponding show*Message methods.
	 *
	 * @param msg	The message to be displayed.
	 * @param saSet	The SimpleAttributeSet to be used for message output.
	 */
	private void privateShowMessage(final String msg, final SimpleAttributeSet saSet)
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
				addLine(msg, saSet);
			}
		});
	}

	/**
	 * Do the real appending of text to the message panel. The last message is always highlighted.
	 * @todo Highlight not only the last message, but all messages from SQL statements which were run together.
	 *
	 * @param string	The String to be appended.
	 * @param saSet		The SimpleAttributeSet to be used for for the string.
	 */
	private void append(String string, SimpleAttributeSet saSet)
	{
		Document document = getStyledDocument();
		try
		{
			// Check if document was cleared or if this is the first message being output.
			if (document.getLength() >= _lastLength && null != _lastMessage)
			{
				document.remove(_lastLength, _lastMessage.length());
				document.insertString(document.getLength(), _lastMessage, _lastSASet);
			}
			_lastLength = document.getLength();
			_lastMessage = string;
			_lastSASet = saSet;
			document.insertString(document.getLength(), string,
				saSet == _saSetError ? _saSetLastMessageError : _saSetLastMessage);
		}
		catch (BadLocationException ble)
		{
			s_log.error("Error appending text to MessagePanel document.", ble);
		}
	}

	/**
	 * Add the passed line to the end of the messages display. Position
	 * display so the the newly added line will be displayed.
	 *
	 * @param line		The line to be added.
	 * @param saSet		The SimpleAttributeSet to be used for for the string.
	 */
	private void addLine(String line, SimpleAttributeSet saSet)
	{
		if (getDocument().getLength() > 0)
		{
			append("\n", saSet);
		}
		append(line, saSet);
		final int len = getDocument().getLength();
		select(len, len);
	}

	/**
	 * Popup menu for this message panel.
	 */
	private class MessagePanelPopupMenu extends TextPopupMenu
	{
		public MessagePanelPopupMenu()
		{
			super();
			add(new ClearAction());
		}

		/**
		 * Class handles clearing the message area. Resets the background
		 * colour (to get rid of error msg colour) as well as clearing
		 * the text.
		 */
		private class ClearAction extends BaseAction
		{
			protected ClearAction()
			{
				super("Clear");
			}

			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					Document doc = MessagePanel.this.getDocument();
					doc.remove(0, doc.getLength());
				}
				catch (BadLocationException ex)
				{
					s_log.error("Error clearing document", ex);
				}
			}
		}
	}
}
