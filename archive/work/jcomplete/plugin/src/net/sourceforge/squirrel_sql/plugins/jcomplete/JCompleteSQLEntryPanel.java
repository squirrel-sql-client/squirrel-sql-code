/*
 * Copyright (C) 2002 Christian Sell
 * csell@users.sourceforge.net
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
 *
 * created by cse, 14.10.2002 11:18:23
 */
package net.sourceforge.squirrel_sql.plugins.jcomplete;

import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.sql.SQLException;

import javax.swing.JScrollPane;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.CaretListener;
import net.sourceforge.squirrel_sql.client.session.BaseSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.jcomplete.SQLCompletionHandler;
import net.sourceforge.jcomplete.CompletionHandler;
import net.sourceforge.jcomplete.ui.SQLCompletionAdapter;
import net.sourceforge.jcomplete.ui.DocumentAdapter;

/**
 * a substitute for the default entry panel which installs the completion engine.
 * The code was copied from DefaultSQLEntryPanel, because we needed access to some
 * private variables (the text component in particular)
 * @version $Id: JCompleteSQLEntryPanel.java,v 1.1 2002-10-14 19:13:56 csell Exp $
 */
public class JCompleteSQLEntryPanel extends BaseSQLEntryPanel
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(JCompleteSQLEntryPanel.class);

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

	public JCompleteSQLEntryPanel(ISession session)
	{
		super();
		if (session == null) {
			throw new IllegalArgumentException("ISession == null");
		}

		_app = session.getApplication();
		_comp = new MyTextArea(_app, this);
		_scroller = new JScrollPane(_comp);

        try {
            //create the parser interface handler
            SQLCompletionHandler handler = new SQLCompletionHandler(
                  new ErrorListener(), session.getSQLConnection().getSQLMetaData().getJDBCMetaData());

            //connect the handler to the document
            _comp.getDocument().addDocumentListener(
                  new DocumentAdapter(handler, _comp.getDocument()));

            //connect the GUI to the handler
            _comp.addKeyListener(new SQLCompletionAdapter(
                  _comp, handler, KeyEvent.CTRL_MASK, KeyEvent.VK_SPACE));
        }
        catch (SQLException e) {
            s_log.error("error creating SQL entry panel", e);
        }
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
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#setUndoActions(javax.swing.Action, javax.swing.Action)
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
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#getText()
	 */
	public String getText()
	{
		return _comp.getText();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#getSelectedText()
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
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#getCaretPosition()
	 */
	public int getCaretPosition()
	{
		return _comp.getCaretPosition();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#setTabSize(int)
	 */
	public void setTabSize(int tabSize)
	{
		_comp.setTabSize(tabSize);
	}

	public void setFont(Font font)
	{
		_comp.setFont(font);
	}

	public void addMouseListener(MouseListener lis)
	{
		_comp.addMouseListener(lis);
	}

	public void removeMouseListener(MouseListener lis)
	{
		_comp.removeMouseListener(lis);
	}

	public void setCaretPosition(int pos)
	{
		_comp.setCaretPosition(pos);
	}

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
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#getSelectionStart()
	 */
	public int getSelectionStart()
	{
		return _comp.getSelectionStart();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#setSelectionStart(int)
	 */
	public void setSelectionStart(int pos)
	{
		_comp.setSelectionStart(pos);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#getSelectionEnd()
	 */
	public int getSelectionEnd()
	{
		return _comp.getSelectionEnd();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#setSelectionEnd(int)
	 */
	public void setSelectionEnd(int pos)
	{
		_comp.setSelectionEnd(pos);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#hasFocus()
	 */
	public boolean hasFocus()
	{
		return _comp.hasFocus();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#requestFocus()
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
		private JCompleteSQLEntryPanel _pnl;

		private MyTextArea(IApplication app, JCompleteSQLEntryPanel pnl)
		{
			super();
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
    private class ErrorListener implements CompletionHandler.ErrorListener
    {
        public void errorDetected(String message, int line, int column)
        {
            s_log.info("["+line+":"+column+"] "+message+"\n");
        }
    }
}