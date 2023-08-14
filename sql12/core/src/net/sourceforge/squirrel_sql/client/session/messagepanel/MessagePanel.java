package net.sourceforge.squirrel_sql.client.session.messagepanel;
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

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.stdtextpopup.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This is the message panel at the bottom of the session sheet.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MessagePanel extends JTextPane implements IMessageHandler
{
	private static final ILogger s_log = LoggerController.createLogger(MessagePanel.class);

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MessagePanel.class);

	private final TextPopupMenu _popupMenu = new MessagePanelPopupMenu();

	/**
	 * Attribute sets for error and last message.
	 */
	private SimpleAttributeSet _saSetMessage;
	private SimpleAttributeSet _saSetMessageHistory;

	private SimpleAttributeSet _saSetWarning;
	private SimpleAttributeSet _saSetWarningHistory;

	private SimpleAttributeSet _saSetError;
	private SimpleAttributeSet _saSetErrorHistory;

	private int _lastLength;
	private String _lastMessage;
	private SimpleAttributeSet _lastSaSet;


   private DefaultExceptionFormatter defaultExceptionFormatter = new DefaultExceptionFormatter();
   
   public MessagePanel()
   {
		///////////////////////////////////////////////////////////////////
      // Message
      _saSetMessage = new SimpleAttributeSet();
      _saSetMessageHistory = new SimpleAttributeSet();
      //
      ////////////////////////////////////////////////////////////////

      ///////////////////////////////////////////////////////////////////
      // Warning
      _saSetWarning = new SimpleAttributeSet();
		_saSetWarningHistory = new SimpleAttributeSet();
      //
      ////////////////////////////////////////////////////////////////

      /////////////////////////////////////////////////////////////////
      // Error
      _saSetError = new SimpleAttributeSet();
		_saSetErrorHistory = new SimpleAttributeSet();
      //
      //////////////////////////////////////////////////////////////////

		applyMessagePanelStyle(Main.getApplication().getSquirrelPreferences());

		initPopup();
	}

	public void applyMessagePanelStyle(SquirrelPreferences prefs)
	{
		MessagePanelStylePreferenceWrapper wrp = new MessagePanelStylePreferenceWrapper(prefs);

		// Messages
		if(wrp.isSetMessageBackground())
		{
			StyleConstants.setBackground(_saSetMessage, wrp.getMessageBackground());
		}
		if(wrp.isSetMessageForeground())
		{
			StyleConstants.setForeground(_saSetMessage, wrp.getMessageForeground());
		}

		if(wrp.isSetMessageHistoryBackground())
		{
			StyleConstants.setBackground(_saSetMessageHistory, wrp.getMessageHistoryBackground());
		}
		if(wrp.isSetMessageHistoryForeground())
		{
			StyleConstants.setForeground(_saSetMessageHistory, wrp.getMessageHistoryForeground());
		}


		// Warnings
		if(wrp.isSetWarningBackground())
		{
			StyleConstants.setBackground(_saSetWarning, wrp.getWarningBackground());
		}
		if(wrp.isSetWarningForeground())
		{
			StyleConstants.setForeground(_saSetWarning, wrp.getWarningForeground());
		}

		if(wrp.isSetWarningHistoryBackground())
		{
			StyleConstants.setBackground(_saSetWarningHistory, wrp.getWarningHistoryBackground());
		}
		if(wrp.isSetWarningHistoryForeground())
		{
			StyleConstants.setForeground(_saSetWarningHistory, wrp.getWarningHistoryForeground());
		}

		// Errors
		if(wrp.isSetErrorBackground())
		{
			StyleConstants.setBackground(_saSetError, wrp.getErrorBackground());
		}
		if(wrp.isSetErrorForeground())
		{
			StyleConstants.setForeground(_saSetError, wrp.getErrorForeground());
		}

		if(wrp.isSetErrorHistoryBackground())
		{
			StyleConstants.setBackground(_saSetErrorHistory, wrp.getErrorHistoryBackground());
		}
		if(wrp.isSetErrorHistoryForeground())
		{
			StyleConstants.setForeground(_saSetErrorHistory, wrp.getErrorHistoryForeground());
		}

//		StyleConstants.setBackground(_saSetMessage, Color.green);
//
//		StyleConstants.setBackground(_saSetWarning, Color.yellow);
//		StyleConstants.setBackground(getHistorySaSet(_saSetWarning), new Color(255,255,210)); // a light yellow
//
//
//		StyleConstants.setForeground(_saSetError, Color.red);
//		StyleConstants.setForeground(getHistorySaSet(_saSetError), new Color(255,102,102));

	}

	/**
	 * Not using a Map or equals here because SimpleAttributeSet's equals() / hashCode()
	 * depends on its contents and {@link #applyMessagePanelStyle(SquirrelPreferences)} changes the contents
	 */
	private SimpleAttributeSet getHistorySaSet(SimpleAttributeSet saSet)
	{
		if(saSet == _saSetMessage)
		{
			return _saSetMessageHistory;
		}
		else if(saSet == _saSetWarning)
		{
			return _saSetWarningHistory;
		}
		else if(saSet == _saSetError)
		{
			return _saSetErrorHistory;
		}
		else
		{
			throw new IllegalArgumentException("Don't know any history SimpleAttributeSet for " + saSet);
		}
	}

	private void initPopup()
	{
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


	public void addToMessagePanelPopup(Action action)
   {
		_popupMenu.add(action);
   }


   /**
    * Show a message describing the passed throwable object.
    *
    * @param th	The throwable object.
    * @param session the session that generated the exception.
    */
   public synchronized void showMessage(final Throwable th, final ExceptionFormatter formatter)
   {
      privateShowMessage(th, formatter, _saSetMessage);
   }
   

   /**
	 * Show an error message describing the passed exception. The controls
	 * background color will be changed to show it is an error msg.
	 *
	 * @param	th		Exception.
     * @param session the session that generated the exception. 
	 */
	public synchronized void showErrorMessage(final Throwable th, ExceptionFormatter formatter)
	{
		privateShowMessage(th, formatter, _saSetError);
	}

	@Override
	public void showErrorMessage(String msg, Throwable ex)
	{
		addLineOnEDT(msg + " | " + Utilities.getExceptionStringSave(ex), _saSetError);
	}

	/**
    * Show a message.
    *
    * @param msg	The message to be shown.
    */
   public synchronized void showMessage(final String msg)
   {
      privateShowMessage(msg, _saSetMessage);
   }

   public void showWarningMessage(String msg)
   {
      privateShowMessage(msg, _saSetWarning);
   }

	@Override
	public void showWarningMessage(Throwable th, ExceptionFormatter formatter)
	{
		privateShowMessage(th, formatter, _saSetWarning);
	}



	/**
	 * Show an error message. The controls
	 * background color will be changed to show it is an error msg.
 * @param	th		Exception.
	 */
	public synchronized void showErrorMessage(final String msg)
	{
		privateShowMessage(msg, _saSetError);
	}


   /**
    * Private method, the real implementation of the corresponding show*Message methods.
    *
    * @param th	The throwable whose details shall be displayed.
    * @param saSet The SimpleAttributeSet to be used for message output.
    */
   private void privateShowMessage(final Throwable th, final ExceptionFormatter formatter, final SimpleAttributeSet saSet)
   {
		if (th != null)
		{

			String message = "";
			if (formatter == null)
			{
				message = defaultExceptionFormatter.format(th);
			}
			else
			{
				try
				{
					message = formatter.format(th);
				}
				catch (Exception e)
				{
					s_log.error("Unable to format message: " + e.getMessage(), e);
				}
			}

			privateShowMessage(message, saSet);
         if (saSet == _saSetMessage)
         {
            s_log.info("privateShowMessage: Exception was: " + th.getMessage(), th);
         }
         else if (saSet == _saSetWarning)
         {
            s_log.warn("privateShowMessage: Exception was: " + th.getMessage(), th);
         }
         else
         {
            s_log.error("privateShowMessage: Exception was: " + th.getMessage(), th);
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
		addLineOnEDT(msg, saSet);
	}

	private void addLineOnEDT(String msg, SimpleAttributeSet saSet)
	{
		GUIUtils.processOnSwingEventThread(() -> addLine(msg, saSet));
	}

	/**
	 * Do the real appending of text to the message panel. The last message is always highlighted.
	 * @todo Highlight not only the last message, but all messages from SQL statements which were run together.
	 *
	 * @param string	The String to be appended.
	 * @param saSet		The SimpleAttributeSet to be used for the string.
	 */
	private void append(String string, SimpleAttributeSet saSet)
	{
		Document document = getStyledDocument();
		try
		{
         /////////////////////////////////////////////////////////////////////////////////
         // Checks if the former message should be highlighted in a 'history' color.
         if (document.getLength() >= _lastLength && null != _lastMessage)
			{
            SimpleAttributeSet historySaSet = getHistorySaSet(_lastSaSet);
            document.remove(_lastLength, _lastMessage.length());
            document.insertString(document.getLength(), _lastMessage, historySaSet);
			}
         //
         ///////////////////////////////////////////////////////////////////////////////////

         _lastLength = document.getLength();
			_lastMessage = string;
			_lastSaSet = saSet;

			document.insertString(document.getLength(), string, saSet);
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

	private void clearMessages()
	{
		try
		{
			Document doc = MessagePanel.this.getDocument();
			doc.remove(0, doc.getLength());
			_lastMessage = null;
		}
		catch (BadLocationException ex)
		{
			s_log.error("Error clearing document", ex);
		}
	}


	/**
	 * Popup menu for this message panel.
	 */
	private class MessagePanelPopupMenu extends TextPopupMenu
	{
		public MessagePanelPopupMenu()
		{
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
				super(s_stringMgr.getString("MessagePanel.clearLabel"));
			}

			public void actionPerformed(ActionEvent evt)
			{
				clearMessages();
			}
		}
	}

}
