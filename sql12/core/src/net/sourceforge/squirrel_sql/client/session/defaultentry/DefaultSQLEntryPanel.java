package net.sourceforge.squirrel_sql.client.session.defaultentry;
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

import net.sourceforge.squirrel_sql.client.gui.dnd.FileEditorDropTargetListener;
import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintHandler;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintListener;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseListener;

public class DefaultSQLEntryPanel extends BaseSQLEntryPanel
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DefaultSQLEntryPanel.class);

	/** Current session. */
	private ISession _session;

	/** Text area control. */
	private SquirrelDefaultTextArea _defaultTextArea;

	private final JScrollPane _defaultTextScrollPane;

    @SuppressWarnings("unused")
    private DropTarget dt;
	
	public DefaultSQLEntryPanel(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_session = session;
		_defaultTextArea = new SquirrelDefaultTextArea(session);

		_defaultTextScrollPane = new JScrollPane(_defaultTextArea);

		dt = new DropTarget(_defaultTextArea, new FileEditorDropTargetListener(session));
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
		return _defaultTextArea;
	}

	@Override
	public JScrollPane getTextAreaEmbeddedInScrollPane()
	{
		return _defaultTextScrollPane;
	}

	@Override
	public void setTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
	{
		_defaultTextArea.setTextAreaPaintListener(textAreaPaintListener);
	}

   @Override
   public TextAreaPaintHandler getTextAreaPaintHandler()
   {
      return _defaultTextArea.getTextAreaPaintHandler();
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
		_defaultTextArea.getDocument().addUndoableEditListener(lis);
	}

	public void removeUndoableEditListener(UndoableEditListener lis)
	{
		_defaultTextArea.getDocument().removeUndoableEditListener(lis);
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
		return _defaultTextArea.getText();
	}

	/**
	 * @see ISQLEntryPanel#getSelectedText()
	 */
	public String getSelectedText()
	{
		return _defaultTextArea.getSelectedText();
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script without selecting it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 */
	public void setText(String sqlScript)
	{
		setText(StringUtilities.removeCarriageReturn(sqlScript), true);
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
		_defaultTextArea.setText(sqlScript);
		if (select)
		{
			setSelectionEnd(getText().length());
			setSelectionStart(0);
		}
      _defaultTextArea.setCaretPosition(0);
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
		_defaultTextArea.append(sqlScript);
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
		_defaultTextArea.replaceSelection(sqlScript);
	}

	/**
	 * @see ISQLEntryPanel#getCaretPosition()
	 */
	public int getCaretPosition()
	{
		return _defaultTextArea.getCaretPosition();
	}

	/**
	 * @see ISQLEntryPanel#setTabSize(int)
	 */
	public void setTabSize(int tabSize)
	{
		_defaultTextArea.setTabSize(tabSize);
	}

	public void setFont(Font font)
	{
		_defaultTextArea.setFont(font);
	}


	/**
	 * @see ISQLEntryPanel#addMouseListener(MouseListener)
	 */
	public void addMouseListener(MouseListener lis)
	{
		_defaultTextArea.addMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#removeListener(MouseListener)
	 */
	public void removeMouseListener(MouseListener lis)
	{
		_defaultTextArea.removeMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#setCaretPosition(int)
	 */
	public void setCaretPosition(int pos)
	{
		_defaultTextArea.setCaretPosition(pos);
	}

	/*
	 * @see ISQLEntryPanel#getCaretLineNumber()
	 */
	public int getCaretLineNumber()
	{
		int caretPosition = _defaultTextArea.getCaretPosition();

		return getLineOfPosition(caretPosition);
	}

	public int getLineOfPosition(int pos)
	{
		try
		{
			return _defaultTextArea.getLineOfOffset(pos);
		}
		catch (BadLocationException ex)
		{
			return 0;
		}
	}

	public int getCaretLinePosition()
	{
		int caretPos = _defaultTextArea.getCaretPosition();
		int caretLineOffset = caretPos;
		try
		{
			caretLineOffset = _defaultTextArea.getLineStartOffset(getCaretLineNumber());
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
		return _defaultTextArea.getSelectionStart();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionStart(int)
	 */
	public void setSelectionStart(int pos)
	{
		_defaultTextArea.setSelectionStart(pos);
	}

	/**
	 * @see ISQLEntryPanel#getSelectionEnd()
	 */
	public int getSelectionEnd()
	{
		return _defaultTextArea.getSelectionEnd();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionEnd(int)
	 */
	public void setSelectionEnd(int pos)
	{
		_defaultTextArea.setSelectionEnd(pos);
	}

	/**
	 * @see ISQLEntryPanel#hasFocus()
	 */
	public boolean hasFocus()
	{
		return _defaultTextArea.hasFocus();
	}

	/**
	 * @see ISQLEntryPanel#requestFocus()
	 */
	public void requestFocus()
	{
		_defaultTextArea.requestFocus();
	}

	/*
	 * @see ISQLEntryPanel#addCaretListener(CaretListener)
	 */
	public void addCaretListener(CaretListener lis)
	{
		_defaultTextArea.removeCaretListener(lis);
		_defaultTextArea.addCaretListener(lis);
	}

	/*
	 * @see ISQLEntryPanel#removeCaretListener(CaretListener)
	 */
	public void removeCaretListener(CaretListener lis)
	{
		_defaultTextArea.removeCaretListener(lis);
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
		_defaultTextArea.setMarkCurrentSQLActive(b);
	}

	public void setUndoManager(UndoManager manager)
	{
		_defaultTextArea.getDocument().addUndoableEditListener(manager);
	}

	public void setPrioritizedCaretMouseListener(PrioritizedCaretMouseListener prioritizedCaretMouseListener)
	{
		_defaultTextArea.setPrioritizedCaretMouseListener(prioritizedCaretMouseListener);
	}
}
