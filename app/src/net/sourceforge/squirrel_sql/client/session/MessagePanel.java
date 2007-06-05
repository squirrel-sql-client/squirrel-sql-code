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
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
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

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(MessagePanel.class);

	/** Popup menu for this component. */
	private final TextPopupMenu _popupMenu = new MessagePanelPopupMenu();

	/**
	 * Attribute sets for error and last message.
	 */
	private SimpleAttributeSet _saSetMessage;
//	private SimpleAttributeSet _saSetErrorHistory;
	private SimpleAttributeSet _saSetError;
	private SimpleAttributeSet _saSetWarning;

	/**
	 * Save into these attributes the parameters of the last message being output.
	 * @todo In the near future: if more than one message shall be remembered, then these variables
	 * need to be replaced with a dynamic storage (ArrayList or similar).
	 */
	private int _lastLength;
	private String _lastMessage;
	private SimpleAttributeSet _lastSASet;

   private HashMap _saSetHistoryBySaSet =new HashMap();

   private ExceptionFormatter formatter = null;
   
   private static interface I18N {
       //i18n[MessagePanel.clearLabel=Clear]
       String CLEAR_LABEL = s_stringMgr.getString("MessagePanel.clearLabel");
   }
   
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

      ///////////////////////////////////////////////////////////////////
      // Message
      _saSetMessage = new SimpleAttributeSet();
      StyleConstants.setBackground(_saSetMessage, Color.green);

      SimpleAttributeSet saSetMessageHistory = new SimpleAttributeSet();
      StyleConstants.setBackground(saSetMessageHistory, getBackground());
      _saSetHistoryBySaSet.put(_saSetMessage, saSetMessageHistory);
      //
      ////////////////////////////////////////////////////////////////


      ///////////////////////////////////////////////////////////////////
      // Warning
      _saSetWarning = new SimpleAttributeSet();
      StyleConstants.setBackground(_saSetWarning, Color.yellow);

      SimpleAttributeSet saSetWarningHistory = new SimpleAttributeSet();
      StyleConstants.setBackground(saSetWarningHistory, new Color(255,255,210)); // a light yellow
      _saSetHistoryBySaSet.put(_saSetWarning, saSetWarningHistory);
      //
      ////////////////////////////////////////////////////////////////


      /////////////////////////////////////////////////////////////////
      // Error
      _saSetError = new SimpleAttributeSet();
      StyleConstants.setBackground(_saSetError, Color.red);

      SimpleAttributeSet saSetErrorHistory = new SimpleAttributeSet();
      StyleConstants.setBackground(saSetErrorHistory, Color.pink);
      _saSetHistoryBySaSet.put(_saSetError, saSetErrorHistory);
      //
      //////////////////////////////////////////////////////////////////


   }


   public void addToMessagePanelPopup(Action act)
   {
      _popupMenu.add(act);   
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
         privateShowMessage(th, _saSetMessage);
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
    * Show a message.
    *
    * @param msg	The message to be shown.
    */
   public synchronized void showMessage(final String msg)
   {
      if (msg != null)
      {
         privateShowMessage(msg, _saSetMessage);
      }
   }

   public void showWarningMessage(String msg)
   {
      if (msg != null)
      {
         privateShowMessage(msg, _saSetWarning);
      }
   }

   /**
    * Sets the exception formatter to use when handling messages.
    * 
    * @param formatter the ExceptionFormatter
    */
   public void setExceptionFormatter(ExceptionFormatter formatter) {
       this.formatter = formatter; 
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
         if (formatter != null && formatter.formatsException(th)) {
             String msg = formatter.format(th);
             privateShowMessage(msg, saSet);
         } 
         else if (th instanceof DataTruncation)
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
         /////////////////////////////////////////////////////////////////////////////////
         // Checks if the former message should be highlighted in a 'history' color.
         if (document.getLength() >= _lastLength && null != _lastMessage)
			{
            SimpleAttributeSet historySaSet = (SimpleAttributeSet) _saSetHistoryBySaSet.get(_lastSASet);
            document.remove(_lastLength, _lastMessage.length());
            document.insertString(document.getLength(), _lastMessage, historySaSet);
			}
         //
         ///////////////////////////////////////////////////////////////////////////////////

         _lastLength = document.getLength();
			_lastMessage = string;
			_lastSASet = saSet;

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
				super(I18N.CLEAR_LABEL);
			}

			public void actionPerformed(ActionEvent evt)
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
		}
	}
}
