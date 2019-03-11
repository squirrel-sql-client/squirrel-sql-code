package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseListener;

import javax.swing.JTextArea;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.client.gui.dnd.FileEditorDropTargetListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DefaultSQLEntryPanel extends BaseSQLEntryPanel
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DefaultSQLEntryPanel.class);

	/** Current session. */
	private ISession _session;

	/** Text area control. */
	private MyTextArea _myTextArea;

    @SuppressWarnings("unused")
    private DropTarget dt;
	
	public DefaultSQLEntryPanel(ISession session)
	{
		super(session.getApplication());
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_session = session;
		_myTextArea = new MyTextArea(session);
		
		dt = new DropTarget(_myTextArea, new FileEditorDropTargetListener(session));
	}

	/**
	 * Retrieve the text area component. Normally this would be a subclass
	 * of <TT>javax.swing.text.JTextComponent</TT> but a plugin may use a
	 * class otehr than a Swing text control.
	 *
	 * @return	The text area component.
	 */
	public JTextArea getTextComponent()
	{
		return _myTextArea;
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

	public void addUndoableEditListener(UndoableEditListener lis)
	{
		_myTextArea.getDocument().addUndoableEditListener(lis);
	}

	public void removeUndoableEditListener(UndoableEditListener lis)
	{
		_myTextArea.getDocument().removeUndoableEditListener(lis);
	}

	/*
	 * @see ISQLEntryPanel#hasOwnUndoableManager()
	 */
	public boolean hasOwnUndoableManager()
	{
		return false;
	}


	/**
	 * @see ISQLEntryPanel#getText()
	 */
	public String getText()
	{
		return _myTextArea.getText();
	}

	/**
	 * @see ISQLEntryPanel#getSelectedText()
	 */
	public String getSelectedText()
	{
		return _myTextArea.getSelectedText();
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script without selecting it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 */
	public void setText(String sqlScript)
	{
		setText(sqlScript, true);
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script and specify whether to select it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	public void setText(String sqlScript, boolean select)
	{
		_myTextArea.setText(sqlScript);
		if (select)
		{
			setSelectionEnd(getText().length());
			setSelectionStart(0);
		}
      _myTextArea.setCaretPosition(0);
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
	 * 						in the sql entry area.
	 */
	public void appendText(String sqlScript, boolean select)
	{
		final int start = select ? getText().length() : 0;
		_myTextArea.append(sqlScript);
		if (select)
		{
			setSelectionEnd(getText().length());
			setSelectionStart(start);
		}
	}

	/**
	 * Replace the currently selected text in the SQL entry area
	 * with the passed text.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 */
	public void replaceSelection(String sqlScript)
	{
		_myTextArea.replaceSelection(sqlScript);
	}

	/**
	 * @see ISQLEntryPanel#getCaretPosition()
	 */
	public int getCaretPosition()
	{
		return _myTextArea.getCaretPosition();
	}

	/**
	 * @see ISQLEntryPanel#setTabSize(int)
	 */
	public void setTabSize(int tabSize)
	{
		_myTextArea.setTabSize(tabSize);
	}

	public void setFont(Font font)
	{
		_myTextArea.setFont(font);
	}


	/**
	 * @see ISQLEntryPanel#addMouseListener(MouseListener)
	 */
	public void addMouseListener(MouseListener lis)
	{
		_myTextArea.addMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#removeListener(MouseListener)
	 */
	public void removeMouseListener(MouseListener lis)
	{
		_myTextArea.removeMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#setCaretPosition(int)
	 */
	public void setCaretPosition(int pos)
	{
		_myTextArea.setCaretPosition(pos);
	}

	/*
	 * @see ISQLEntryPanel#getCaretLineNumber()
	 */
	public int getCaretLineNumber()
	{
		try
		{
			return _myTextArea.getLineOfOffset(_myTextArea.getCaretPosition());
		}
		catch (BadLocationException ex)
		{
			return 0;
		}
	}

	public int getCaretLinePosition()
	{
		int caretPos = _myTextArea.getCaretPosition();
		int caretLineOffset = caretPos;
		try
		{
			caretLineOffset = _myTextArea.getLineStartOffset(getCaretLineNumber());
		}
		catch (BadLocationException ex)
		{
			s_log.error("BadLocationException in getCaretLinePosition", ex);
		}
		return caretPos - caretLineOffset;
	}

	/**
	 * @see ISQLEntryPanel#getSelectionStart()
	 */
	public int getSelectionStart()
	{
		return _myTextArea.getSelectionStart();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionStart(int)
	 */
	public void setSelectionStart(int pos)
	{
		_myTextArea.setSelectionStart(pos);
	}

	/**
	 * @see ISQLEntryPanel#getSelectionEnd()
	 */
	public int getSelectionEnd()
	{
		return _myTextArea.getSelectionEnd();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionEnd(int)
	 */
	public void setSelectionEnd(int pos)
	{
		_myTextArea.setSelectionEnd(pos);
	}

	/**
	 * @see ISQLEntryPanel#hasFocus()
	 */
	public boolean hasFocus()
	{
		return _myTextArea.hasFocus();
	}

	/**
	 * @see ISQLEntryPanel#requestFocus()
	 */
	public void requestFocus()
	{
		_myTextArea.requestFocus();
	}

	/*
	 * @see ISQLEntryPanel#addCaretListener(CaretListener)
	 */
	public void addCaretListener(CaretListener lis)
	{
		_myTextArea.removeCaretListener(lis);
		_myTextArea.addCaretListener(lis);
	}

	/*
	 * @see ISQLEntryPanel#removeCaretListener(CaretListener)
	 */
	public void removeCaretListener(CaretListener lis)
	{
		_myTextArea.removeCaretListener(lis);
	}

	public void addSQLTokenListener(SQLTokenListener tl)
	{
		// Not implemented
	}

	public void removeSQLTokenListener(SQLTokenListener tl)
	{
		// Not implemented
	}

   public ISession getSession()
   {
      return _session;
   }

	public void setMarkCurrentSQLActive(boolean b)
	{
		_myTextArea._markCurrentSqlHandler.setActive(b);
	}

	private static class MyTextArea extends JTextArea
	{

		private MarkCurrentSqlHandler _markCurrentSqlHandler;

		private MyTextArea(ISession session)
		{
			SessionProperties props = session.getProperties();
			final FontInfo fi = props.getFontInfo();
			if (fi != null)
			{
				this.setFont(props.getFontInfo().createFont());
			}

			_markCurrentSqlHandler = new MarkCurrentSqlHandler(this, session);


			/////////////////////////////////////////////////////////////////////
			// To prevent the caret from being hidden by the current SQL mark
			putClientProperty("caretWidth", 3);
			//
			////////////////////////////////////////////////////////////////////
		}

		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			_markCurrentSqlHandler.paintMark(g);
		}
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#setUndoManager(javax.swing.undo.UndoManager)
     */
    public void setUndoManager(UndoManager manager) {
        // no support for undo
    }
}
