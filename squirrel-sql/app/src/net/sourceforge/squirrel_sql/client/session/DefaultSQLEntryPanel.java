package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import sun.security.action.GetLongAction;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class DefaultSQLEntryPanel extends BaseSQLEntryPanel
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(DefaultSQLEntryPanel.class);

	/** Application API. */
	private IApplication _app;

	/** Text area control. */
	private MyTextArea _comp;

	/** Scroll pane for text control. */
	private JScrollPane _scroller;

	/** Popup menu for this component. */
	private TextPopupMenu _textPopupMenu = new TextPopupMenu();

	/** Listener for displaying the popup menu. */
	private MouseListener _sqlEntryMouseListener = new MyMouseListener();

	public DefaultSQLEntryPanel(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		_comp = new MyTextArea(app, this);
		_scroller = new JScrollPane(_comp);
	}

	public void addUndoableEditListener(UndoableEditListener lis)
	{
		_comp.getDocument().addUndoableEditListener(lis);

	}

	public void removeUndoableEditListener(UndoableEditListener lis)
	{
		_comp.getDocument().removeUndoableEditListener(lis);
	}

	/*
	 * @see ISQLEntryPanel#hasOwnUndoableManager()
	 */
	public boolean hasOwnUndoableManager()
	{
		return false;
	}

	/**
	 * @see ISQLEntryPanel#setUndoActions(Action, Action)
	 */
	public void setUndoActions(Action undo, Action redo)
	{
		_textPopupMenu.addSeparator();
		_app.getResources().addToPopupMenu(undo, _textPopupMenu);
		_app.getResources().addToPopupMenu(redo, _textPopupMenu);
	}

	/**
	 * Return the text area control. In this case a <TT>JScrollPane</TT> wrapped
	 * around an instance of <TT>JTextArea</TT>.
	 *
	 * @return	an instance of <TT>JScrollPane</TT>.
	 */
	public JComponent getJComponent()
	{
		return _scroller;
	}

	/**
	 * @see ISQLEntryPanel#getText()
	 */
	public String getText()
	{
		return _comp.getText();
	}

	/**
	 * @see ISQLEntryPanel#getSelectedText()
	 */
	public String getSelectedText()
	{
		return _comp.getSelectedText();
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script without selecting it.
	 * 
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 */
	public void setText(String sqlScript)
	{
		setText(sqlScript, false);
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
		_comp.setText(sqlScript);
		if (select)
		{
			setSelectionEnd(getText().length());
			setSelectionStart(0);
			_comp.setCaretPosition(0);
		}
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
		if (!getText().endsWith("\n") && !sqlScript.startsWith("\n"))
		{
			_comp.append("\n");
		}
		int start = 0;
		if (select)
		{
			start = getText().length();
		}
		_comp.append(sqlScript);
		if (select)
		{
			setSelectionEnd(getText().length());
			setSelectionStart(start);
			_comp.setCaretPosition(start);
		}
	}

	/**
	 * @see ISQLEntryPanel#getCaretPosition()
	 */
	public int getCaretPosition()
	{
		return _comp.getCaretPosition();
	}

	/**
	 * @see ISQLEntryPanel#setTabSize(int)
	 */
	public void setTabSize(int tabSize)
	{
		_comp.setTabSize(tabSize);
	}

	public void setFont(Font font)
	{
		_comp.setFont(font);
	}

	/**
	 * @see ISQLEntryPanel#addMouseListener(MouseListener)
	 */
	public void addMouseListener(MouseListener lis)
	{
		_comp.addMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#removeListener(MouseListener)
	 */
	public void removeMouseListener(MouseListener lis)
	{
		_comp.removeMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#setCaretPosition(int)
	 */
	public void setCaretPosition(int pos)
	{
		_comp.setCaretPosition(pos);
	}

	/*
	 * @see ISQLEntryPanel#getCaretLineNumber()
	 */
	public int getCaretLineNumber()
	{
		try
		{
			return _comp.getLineOfOffset(_comp.getCaretPosition());
		}
		catch (BadLocationException ex)
		{
			return 0;
		}
	}

	public int getCaretLinePosition()
	{
		int caretPos = _comp.getCaretPosition();
		int caretLineOffset = caretPos;
		try
		{
			caretLineOffset = _comp.getLineStartOffset(getCaretLineNumber());
		}
		catch (BadLocationException ignore)
		{
		}
		return caretPos - caretLineOffset;
	}

	/**
	 * @see ISQLEntryPanel#getSelectionStart()
	 */
	public int getSelectionStart()
	{
		return _comp.getSelectionStart();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionStart(int)
	 */
	public void setSelectionStart(int pos)
	{
		_comp.setSelectionStart(pos);
	}

	/**
	 * @see ISQLEntryPanel#getSelectionEnd()
	 */
	public int getSelectionEnd()
	{
		return _comp.getSelectionEnd();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionEnd(int)
	 */
	public void setSelectionEnd(int pos)
	{
		_comp.setSelectionEnd(pos);
	}

	/**
	 * @see ISQLEntryPanel#hasFocus()
	 */
	public boolean hasFocus()
	{
		return _comp.hasFocus();
	}

	/**
	 * @see ISQLEntryPanel#requestFocus()
	 */
	public void requestFocus()
	{
		_comp.requestFocus();
	}

	/*
	 * @see ISQLEntryPanel#addCaretListener(CaretListener)
	 */
	public void addCaretListener(CaretListener lis)
	{
		_comp.addCaretListener(lis);
	}

	/*
	 * @see ISQLEntryPanel#removeCaretListener(CaretListener)
	 */
	public void removeCaretListener(CaretListener lis)
	{
		_comp.removeCaretListener(lis);
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
			_textPopupMenu.setTextComponent(_comp);
			_textPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	private static class MyTextArea extends JTextArea
	{
		private IApplication _app;
		private DefaultSQLEntryPanel _pnl;

		private MyTextArea(IApplication app, DefaultSQLEntryPanel pnl)
		{
			super();
			_app = app;
			_pnl = pnl;
			SessionProperties props = app.getSquirrelPreferences().getSessionProperties();
			FontInfo fi = props.getFontInfo();
			if (fi != null)
			{
				this.setFont(props.getFontInfo().createFont());
			}
		}

		public void addNotify()
		{
			super.addNotify();
			_pnl.addMouseListener(_pnl._sqlEntryMouseListener);
		}

		public void removeNotify()
		{
			_pnl.removeMouseListener(_pnl._sqlEntryMouseListener);
			super.removeNotify();
		}
	}

}