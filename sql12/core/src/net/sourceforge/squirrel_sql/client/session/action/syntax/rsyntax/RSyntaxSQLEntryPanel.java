package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax;

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

import net.sourceforge.squirrel_sql.client.gui.dnd.FileEditorDropTargetListener;
import net.sourceforge.squirrel_sql.client.session.BaseSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.PrioritizedCaretMouseListener;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.action.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintHandler;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IUndoHandler;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.undo.UndoManager;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseListener;
import java.util.HashMap;


public class RSyntaxSQLEntryPanel extends BaseSQLEntryPanel
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(RSyntaxSQLEntryPanel.class);

   /** Text component. */
	private SquirrelRSyntaxTextArea _textArea;


	private ISession _session;

   private RSyntaxPropertiesWrapper _propertiesWrapper;


	@SuppressWarnings("unused")
	private DropTarget dt;
   private RTextScrollPane _textScrollPane;

   RSyntaxSQLEntryPanel(ISession session, SyntaxPreferences prefs, HashMap<String, Object> props)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;

		_propertiesWrapper = new RSyntaxPropertiesWrapper(props);


		_textArea = new SquirrelRSyntaxTextArea(session, prefs, _propertiesWrapper, getIdentifier());


		_textScrollPane = new RTextScrollPane(_textArea);

		_textScrollPane.setLineNumbersEnabled(prefs.isLineNumbersEnabled());


		dt = new DropTarget(_textArea, new FileEditorDropTargetListener(session));


		//////////////////////////////////////////////////////////////////////
		// Dragging inside the text area itself conflicts with file dnd
		// so we disable it. See bug #3006515
		_textArea.setDragEnabled(false);
		//
		////////////////////////////////////////////////////////////////////
	}

   public int getCaretLineNumber()
	{
		final int pos = getCaretPosition();
		return getLineOfPosition(pos);
	}

	public int getLineOfPosition(int pos)
	{
		final Document doc = _textArea.getDocument();
		final Element docElem = doc.getDefaultRootElement();
		return docElem.getElementIndex(pos);
	}

	/**
	 * @see ISQLEntryPanel#gettextComponent()
	 */
	public JTextArea getTextComponent()
	{
		return _textArea;
	}

	/**
	 * If the component returned by <TT>getTextComponent</TT> contains its own
	 * scroll bars return <TT>true</TT> other wise this component will be
	 * wrapped in the scroll pane when added to the SQL panel.
	 *
	 * @return <TT>true</TT> if text component already handles scrolling.
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
	 * Replace the contents of the SQL entry area with the passed SQL script
	 * without selecting it.
	 *
	 * @param sqlScript
	 *           The script to be placed in the SQL entry area..
	 */
	public void setText(String text)
	{
		setText(StringUtilities.removeCarriageReturn(text), true);
		triggerParser();
	}

   /**
	 * Replace the contents of the SQL entry area with the passed SQL script and
	 * specify whether to select it.
	 *
	 * @param sqlScript
	 *           The script to be placed in the SQL entry area..
	 * @param select
	 *           If <TT>true</TT> then select the passed script in the sql
	 *           entry area.
	 */
	public void setText(String text, boolean select)
	{
      text = StringUtilities.removeCarriageReturn(text);
		_textArea.setText(text);
		if (select)
		{
			setSelectionEnd(_textArea.getDocument().getLength());
			setSelectionStart(0);
		}
		triggerParser();
		setCaretPosition(0);
	}

	/**
	 * Append the passed SQL script to the SQL entry area but don't select it.
	 *
	 * @param sqlScript
	 *           The script to be appended.
	 */
	public void appendText(String sqlScript)
	{
      sqlScript = StringUtilities.removeCarriageReturn(sqlScript);
		appendText(sqlScript, false);
	}

	/**
	 * Append the passed SQL script to the SQL entry area and specify whether it
	 * should be selected.
	 *
	 * @param sqlScript
	 *           The script to be appended.
	 * @param select
	 *           If <TT>true</TT> then select the passed script in the sql
	 *           entry area.
	 */
	public void appendText(String sqlScript, boolean select)
	{
      sqlScript = StringUtilities.removeCarriageReturn(sqlScript);
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

			triggerParser();

		} catch (Exception ex)
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
														Integer.valueOf(tabSize));
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
	 * Replace the currently selected text in the SQL entry area with the passed
	 * text.
	 *
	 * @param sqlScript
	 *           The script to be placed in the SQL entry area.
	 */
	public void replaceSelection(String sqlScript)
	{
      sqlScript = StringUtilities.removeCarriageReturn(sqlScript);
		_textArea.replaceSelection(sqlScript);

		triggerParser();

	}


	@Override
	public void triggerParser()
	{
		IParserEventsProcessor parserEventsProcessor = _propertiesWrapper.getParserEventsProcessor(getIdentifier(),	_session);

		if (null != parserEventsProcessor)
		{
			parserEventsProcessor.triggerParser();
		}
	}

	@Override
	public void addTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
	{
		_textArea.addTextAreaPaintListener(textAreaPaintListener);
	}

	@Override
	public void removeTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
	{
		_textArea.removeTextAreaPaintListener(textAreaPaintListener);
	}

	@Override
   public TextAreaPaintHandler getTextAreaPaintHandler()
   {
      return _textArea.getTextAreaPaintHandler();
   }

   @Override
   public void setPrioritizedCaretMouseListener(PrioritizedCaretMouseListener prioritizedCaretMouseListener)
   {
   	_textArea.setPrioritizedCaretMouseListener(prioritizedCaretMouseListener);
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
		//System.out.println("RSyntaxSQLEntryPanel.requestFocus " + new Date());
		SwingUtilities.invokeLater(() -> _textArea.requestFocus());
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
	 * @see ISQLEntryPanel#addUndoableEditListener(javax.swing.event.UndoableEditListener)
	 */
	public void addUndoableEditListener(UndoableEditListener listener)
	{
		_textArea.addUndoableEditListener(listener);
	}

	/**
	 * @see ISQLEntryPanel#removeUndoableEditListener(javax.swing.event.UndoableEditListener)
	 */
	public void removeUndoableEditListener(UndoableEditListener listener)
	{
		_textArea.getDocument().removeUndoableEditListener(listener);
	}

	/**
	 * @see ISQLEntryPanel#getCaretLinePosition()
	 */
	public int getCaretLinePosition()
	{
		String textTillCarret = getText().substring(0, getCaretPosition());

		int lineFeedIndex = textTillCarret.lastIndexOf('\n');
		if (-1 == lineFeedIndex)
		{
			return getCaretPosition();
		} else
		{
			return getCaretPosition() - lineFeedIndex - 1;
		}

		// this didn't work
		// final int pos = getCaretPosition();
		// final Document doc = _textArea.getStyledDocument();
		// final Element docElem = doc.getDefaultRootElement();
		// final Element lineElem = docElem.getElement(getCaretLineNumber());
		// return lineElem.getElementIndex(pos);
	}

	/**
	 * @see ISQLEntryPanel#addCaretListener(javax.swing.event.CaretListener)
	 */
	public void addCaretListener(CaretListener lis)
	{
		_textArea.removeCaretListener(lis);
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
		_textArea.addSQLTokenListeners(tl);
	}

	public void removeSQLTokenListener(SQLTokenListener tl)
	{
		_textArea.removeSQLTokenListeners(tl);
	}

	public ISession getSession()
	{
		return _session;
	}

//	public void showFindDialog(ActionEvent evt)
//	{
//		SQLKit kit = (SQLKit) _textArea.getEditorKit();
//		kit.getActionByName(ExtKit.findAction).actionPerformed(evt);
//	}
//
//	public void showReplaceDialog(ActionEvent evt)
//	{
//		SQLKit kit = (SQLKit) _textArea.getEditorKit();
//		kit.getActionByName(ExtKit.replaceAction).actionPerformed(evt);
//	}


   /**
    * @see ISQLEntryPanel#hasOwnUndoableManager()
    */
   public boolean hasOwnUndoableManager()
   {
      return true;
   }

   @Override
   public IUndoHandler createUndoHandler()
   {
      return _textArea.createUndoHandler();    //To change body of overridden methods use File | Settings | File Templates.
   }

   public void setUndoManager(UndoManager manager)
	{
		//_textArea.setUndoManager(manager);
	}

	/**
	 * Sets the session referenced by this class to null so that it can be
	 * garbage-collected.
	 */
	public void sessionEnding()
	{
      _textArea.sessionEnding();
		_session = null;
	}

   @Override
   public JScrollPane getTextAreaEmbeddedInScrollPane()
   {
      return _textScrollPane;
   }
}
