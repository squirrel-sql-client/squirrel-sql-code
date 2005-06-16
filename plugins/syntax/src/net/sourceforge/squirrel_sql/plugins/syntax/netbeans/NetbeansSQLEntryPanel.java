package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;
/*
 * Copyright (C) 2004 Gerd Wagner
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
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.BaseSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.SessionTextEditPopupMenu;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.netbeans.editor.ext.ExtKit;

public class NetbeansSQLEntryPanel extends BaseSQLEntryPanel
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(NetbeansSQLEntryPanel.class);

	/** Application API. */
	private IApplication _app;

	/** Text component. */
	private NetbeansSQLEditorPane _textArea;

	/** Popup menu for this component. */
	private SessionTextEditPopupMenu _textPopupMenu;

	/** Listener for displaying the popup menu. */
	private MouseListener _sqlEntryMouseListener = new MyMouseListener();

   private SyntaxFactory _syntaxFactory;
   private ISession _session;
   private SyntaxPugin _plugin;
   private boolean _isFirstAutocorrectInSession = true;

   NetbeansSQLEntryPanel(ISession session, SyntaxPreferences prefs, SyntaxFactory syntaxFactory, SyntaxPugin plugin)
	{
      _plugin = plugin;

      if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

      _syntaxFactory = syntaxFactory;
      _session = session;
      _plugin = plugin;

		_app = session.getApplication();

		_textArea = new NetbeansSQLEditorPane(session, prefs, syntaxFactory, _plugin, getIdentifier());

		_textPopupMenu = new SessionTextEditPopupMenu();
		_textArea.addMouseListener(_sqlEntryMouseListener);


      _textArea.getDocument().addDocumentListener(new DocumentListener()
      {
         public void changedUpdate(DocumentEvent e)
         {
            //To change body of implemented methods use File | Settings | File Templates.
         }

         public void insertUpdate(DocumentEvent e)
         {
            onInsertUpdate(e);
         }

         public void removeUpdate(DocumentEvent e)
         {
            //To change body of implemented methods use File | Settings | File Templates.
         }
      });
	}

   private void onInsertUpdate(DocumentEvent e)
   {
      try
      {
         if(1 != e.getLength())
         {
            return;
         }

         final String insertChar = e.getDocument().getText(e.getOffset(), 1);

         if (Character.isWhitespace(insertChar.charAt(0)))
         {
            String autoCorrCandidate = getStringBeforeWhiteSpace(e.getOffset()).toUpperCase();
            final String corr = (String) _plugin.getAutoCorrectProviderImpl().getAutoCorrects().get(autoCorrCandidate);
            if(null != corr)
            {
               setSelectionStart(e.getOffset() - autoCorrCandidate.length());
               setSelectionEnd(e.getOffset());

               if(_isFirstAutocorrectInSession)
               {
                  _session.getMessageHandler().showMessage(autoCorrCandidate + " has been auto corrected / extended to " + corr + ". To configure auto correct / abreviations see Menu Session --> Syntax --> Configure auto correct / abreviation");
                  _isFirstAutocorrectInSession = false;
               }

               SwingUtilities.invokeLater(new Runnable()
               {
                  public void run()
                  {
                     replaceSelection(corr + insertChar);
                  }
               });
            }
         }
      }
      catch (BadLocationException ex)
      {
         throw new RuntimeException(ex);

      }
   }

   private String getStringBeforeWhiteSpace(int offset)
   {
      try
      {
         String text = _textArea.getDocument().getText(0, offset);


         String ret = null;
         int begPos = text.length();
         for(int i=text.length()-1; 0 <= i; --i)
         {
            if(Character.isWhitespace(text.charAt(i)))
            {
               break;
            }
            --begPos;
         }

         ret = text.substring(begPos, text.length());

         return ret;

      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
   }


   public int getCaretLineNumber()
   {
      final int pos = getCaretPosition();
      final Document doc = _textArea.getDocument();
      final Element docElem = doc.getDefaultRootElement();
      return docElem.getElementIndex(pos);
   }


	/**
	 * @see ISQLEntryPanel#gettextComponent()
	 */
	public JComponent getTextComponent()
	{
		return _textArea;
	}

	/**
	 * If the component returned by <TT>getTextComponent</TT> contains
	 * its own scroll bars return <TT>true</TT> other wise this component
	 * will be wrapped in the scroll pane when added to the SQL panel.
	 *
	 * @return	<TT>true</TT> if text component already handles scrolling.
	 */
	public boolean getDoesTextComponentHaveScroller()
	{
		return false;
	}

	/**
	 * @see ISQLEntryPanel#getText()
	 */
	public String getText()
	{
		return _textArea.getText();
	}

	public void setFont(Font font)
	{
      // See SQLSettingsInitializer to find out how fonts are
      // handled in the Netbeans editor.
		// _textArea.setFont(font);
	}

	/**
	 * @see ISQLEntryPanel#getSelectedText()
	 */
	public String getSelectedText()
	{
		return _textArea.getSelectedText();
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script without selecting it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 */
	public void setText(String text)
	{
		setText(text, false);
      _session.getParserEventsProcessor(getIdentifier()).triggerParser();
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script and specify whether to select it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 * @param 	select		If <TT>true</TT> then select the passed script
	 *						in the sql entry area.
	 */
	public void setText(String text, boolean select)
	{
		_textArea.setText(text);
		setSelectionEnd(_textArea.getDocument().getLength());
		setSelectionStart(0);
      _session.getParserEventsProcessor(getIdentifier()).triggerParser();
	}

	/**
	 * Append the passed SQL script to the SQL entry area but don't select
	 * it.
	 *
	 * @param	sqlScript	The script to be appended.
	 */
	public void appendText(String sqlScript)
	{
		appendText(sqlScript, false);
	}

	/**
	 * Append the passed SQL script to the SQL entry area and specify
	 * whether it should be selected.
	 *
	 * @param	sqlScript	The script to be appended.
	 * @param	select		If <TT>true</TT> then select the passed script
	 *						in the sql entry area.
	 */
	public void appendText(String sqlScript, boolean select)
	{
		Document doc = _textArea.getDocument();

		try
		{
			int start = 0;
			if (select)
			{
				start = doc.getLength();
			}

			doc.insertString(doc.getLength(), sqlScript, null);

			if (select)
			{
				setSelectionEnd(doc.getLength());
				setSelectionStart(start);
			}

         _session.getParserEventsProcessor(getIdentifier()).triggerParser();

		}
		catch (Exception ex)
		{
			s_log.error("Error appending text to text area", ex);
		}
	}

	/**
	 * @see ISQLEntryPanel#getCaretPosition()
	 */
	public int getCaretPosition()
	{
		return _textArea.getCaretPosition();
	}

	public void setCaretPosition(int value)
	{
		_textArea.setCaretPosition(value);
	}

	/**
	 * @see ISQLEntryPanel#setTabSize(int)
	 */
	public void setTabSize(int tabSize)
	{
		_textArea.getDocument().putProperty(PlainDocument.tabSizeAttribute,
												new Integer(tabSize));
	}

	/**
	 * @see ISQLEntryPanel#getSelectionStart()
	 */
	public int getSelectionStart()
	{
		return _textArea.getSelectionStart();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionStart(int)
	 */
	public void setSelectionStart(int pos)
	{
		_textArea.setSelectionStart(pos);
	}

	/**
	 * @see ISQLEntryPanel#getSelectionEnd()
	 */
	public int getSelectionEnd()
	{
		return _textArea.getSelectionEnd();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionEnd(int)
	 */
	public void setSelectionEnd(int pos)
	{
		_textArea.setSelectionEnd(pos);
	}

	/**
	 * Replace the currently selected text in the SQL entry area
	 * with the passed text.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 */
	public void replaceSelection(String sqlScript)
	{
		_textArea.replaceSelection(sqlScript);
      _session.getParserEventsProcessor(getIdentifier()).triggerParser();

	}

	/**
	 * @see ISQLEntryPanel#hasFocus()
	 */
	public boolean hasFocus()
	{
		return _textArea.hasFocus();
	}

	/**
	 * @see ISQLEntryPanel#requestFocus()
	 */
	public void requestFocus()
	{
		_textArea.requestFocus();
	}

	/**
	 * Add a hierarchical menu to the SQL Entry Area popup menu.
	 *
	 * @param	menu	The menu that will be added.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>Menu</TT> passed.
	 */
	public void addToSQLEntryAreaMenu(JMenu menu)
	{
		if (menu == null)
		{
			throw new IllegalArgumentException("Menu == null");
		}

		_textPopupMenu.add(menu);
	}

	/**
	 * Add an <TT>Action</TT> to the SQL Entry Area popup menu.
	 *
	 * @param	action	The action to be added.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>Action</TT> passed.
	 */
	public JMenuItem addToSQLEntryAreaMenu(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}

		return _textPopupMenu.add(action);
	}

	/**
	 * @see ISQLEntryPanel#addMouseListener(java.awt.event.MouseListener)
	 */
	public void addMouseListener(MouseListener lis)
	{
		_textArea.addMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#removeMouseListener(java.awt.event.MouseListener)
	 */
	public void removeMouseListener(MouseListener lis)
	{
		_textArea.removeMouseListener(lis);
	}

	public void updateFromPreferences()
	{
		_textArea.updateFromPreferences();
	}

	/**
	 * @see ISQLEntryPanel#hasOwnUndoableManager()
	 */
	public boolean hasOwnUndoableManager()
	{
		return false;
	}

	/**
	 * @see ISQLEntryPanel#addUndoableEditListener(javax.swing.event.UndoableEditListener)
	 */
	public void addUndoableEditListener(UndoableEditListener listener)
	{
		_textArea.setUndoManager(listener);
	}

	/**
	 * @see ISQLEntryPanel#removeUndoableEditListener(javax.swing.event.UndoableEditListener)
	 */
	public void removeUndoableEditListener(UndoableEditListener listener)
	{
		_textArea.getDocument().removeUndoableEditListener(listener);
	}

	/**
	 * @see ISQLEntryPanel#setUndoActions(javax.swing.Action, javax.swing.Action)
	 */
	public void setUndoActions(Action undo, Action redo)
	{
		_textPopupMenu.addSeparator();
		_app.getResources().addToPopupMenu(undo, _textPopupMenu);
		_app.getResources().addToPopupMenu(redo, _textPopupMenu);
		_textPopupMenu.addSeparator();
	}


	/**
	 * @see ISQLEntryPanel#getCaretLinePosition()
	 */
	public int getCaretLinePosition()
	{
      String textTillCarret = getText().substring(0, getCaretPosition());

      int lineFeedIndex = textTillCarret.lastIndexOf('\n');
      if(- 1 == lineFeedIndex)
      {
         return getCaretPosition();
      }
      else
      {
         return getCaretPosition() - lineFeedIndex - 1;
      }

// this didn't work      
//		final int pos = getCaretPosition();
//		final Document doc = _textArea.getStyledDocument();
//		final Element docElem = doc.getDefaultRootElement();
//		final Element lineElem = docElem.getElement(getCaretLineNumber());
//		return lineElem.getElementIndex(pos);
	}

	/**
	 * @see ISQLEntryPanel#addCaretListener(javax.swing.event.CaretListener)
	 */
	public void addCaretListener(CaretListener lis)
	{
		_textArea.addCaretListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#removeCaretListener(javax.swing.event.CaretListener)
	 */
	public void removeCaretListener(CaretListener lis)
	{
		_textArea.removeCaretListener(lis);
	}

	public void addSQLTokenListener(SQLTokenListener tl)
	{
      _syntaxFactory.addSQLTokenListeners(_session, tl);
	}

	public void removeSQLTokenListener(SQLTokenListener tl)
	{
      _syntaxFactory.addSQLTokenListeners(_session, tl);
	}

   public void showFindDialog(ActionEvent evt)
   {
      SQLKit kit = (SQLKit) _textArea.getEditorKit();
      kit.getActionByName(ExtKit.findAction).actionPerformed(evt);
   }

   public void showReplaceDialog(ActionEvent evt)
   {
      SQLKit kit = (SQLKit) _textArea.getEditorKit();
      kit.getActionByName(ExtKit.replaceAction).actionPerformed(evt);
   }

   private final class MyMouseListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				displayPopupMenu(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				displayPopupMenu(evt);
			}
		}

		private void displayPopupMenu(MouseEvent evt)
		{
			_textPopupMenu.setTextComponent(_textArea);
			_textPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}
}
