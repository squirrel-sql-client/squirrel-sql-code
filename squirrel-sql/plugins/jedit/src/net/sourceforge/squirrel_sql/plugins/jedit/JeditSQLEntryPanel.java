package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.PlainDocument;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.InputHandler;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.JEditTextArea;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxDocument;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxStyle;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.TextAreaPainter;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.Token;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.BaseSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;

class JeditSQLEntryPanel extends BaseSQLEntryPanel
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(JeditSQLEntryPanel.class);

	/** Application API. */
	private IApplication _app;

	/** Text component. */
	private JEditTextArea _jeditTextArea;

	/** Rightclick menu for <TT>_jeditTextArea</TT>. */
	private JeditPopupMenu _jeditPopup;

	/** Jedit preferences for the current session. */
	private JeditPreferences _prefs;

	JeditSQLEntryPanel(ISession session, JeditPlugin plugin, JeditPreferences prefs)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null JeditPlugin passed");
		}
		if (prefs == null)
		{
			throw new IllegalArgumentException("Null JeditPreferences passed");
		}

		_app = session.getApplication();
		_prefs = (JeditPreferences)session.getPluginObject(plugin, JeditConstants.ISessionKeys.PREFS);
		_jeditTextArea = new JEditTextArea(new JeditTextAreaDefaults(_prefs));
		_jeditTextArea.setTokenMarker(
			new JeditSQLTokenMarker(session.getSQLConnection()));
		_jeditTextArea.setRightClickPopup(_jeditPopup = new JeditPopupMenu(session, plugin, _jeditTextArea));

		ActionCollection coll = session.getApplication().getActionCollection();
		Action action = coll.get(ExecuteSqlAction.class);
		if (action != null)
		{
			InputHandler ih = _jeditTextArea.getInputHandler();
			PluginResources rsrc = plugin.getResources();
			String rsrcKey = "jeditshortcut." + rsrc.getClassName(action.getClass());
			String binding = rsrc.getString(rsrcKey);
			if (binding != null && binding.length() > 0)
			{
				ih.addKeyBinding(binding, action);
				s_log.debug("Adding binding: " + binding);
			}
		}
	}

	/**
	 * @see ISQLEntryPanel#getJComponent()
	 */
	public JComponent getJComponent()
	{
		return _jeditTextArea;
	}

	/**
	 * @see ISQLEntryPanel#getText()
	 */
	public String getText()
	{
		return _jeditTextArea.getText();
	}

	/**
	 * @see ISQLEntryPanel#getSelectedText()
	 */
	public String getSelectedText()
	{
		return _jeditTextArea.getSelectedText();
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
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script and specify whether to select it.
	 * 
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	public void setText(String text, boolean select)
	{
		_jeditTextArea.setText(text);
		setSelectionEnd(_jeditTextArea.getDocument().getLength());
		setSelectionStart(0);
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
		SyntaxDocument doc = _jeditTextArea.getDocument();
		try
		{
			if (!getText().endsWith("\n") && !sqlScript.startsWith("\n"))
			{
				doc.insertString(doc.getLength(), "\n", null);
			}
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
				_jeditTextArea.setCaretPosition(start);
			}
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
		return _jeditTextArea.getCaretPosition();
	}

	public void setCaretPosition(int value)
	{
		_jeditTextArea.setCaretPosition(value);
	}

	/**
	 * @see ISQLEntryPanel#setTabSize(int)
	 */
	public void setTabSize(int tabSize)
	{
		_jeditTextArea.getDocument().putProperty(PlainDocument.tabSizeAttribute,
													new Integer(tabSize));
	}

	public void setFont(Font font)
	{
		_jeditTextArea.setFont(font);
	}

	/**
	 * @see ISQLEntryPanel#getSelectionStart()
	 */
	public int getSelectionStart()
	{
		return _jeditTextArea.getSelectionStart();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionStart(int)
	 */
	public void setSelectionStart(int pos)
	{
		_jeditTextArea.setSelectionStart(pos);
	}

	/**
	 * @see ISQLEntryPanel#getSelectionEnd()
	 */
	public int getSelectionEnd()
	{
		return _jeditTextArea.getSelectionEnd();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionEnd(int)
	 */
	public void setSelectionEnd(int pos)
	{
		_jeditTextArea.setSelectionEnd(pos);
	}

	/**
	 * @see ISQLEntryPanel#hasFocus()
	 */
	public boolean hasFocus()
	{
		return _jeditTextArea.hasFocus();
	}

	/**
	 * @see ISQLEntryPanel#requestFocus()
	 */
	public void requestFocus()
	{
		_jeditTextArea.requestFocus();
	}

	/**
	 * @see ISQLEntryPanel#addMouseListener(MouseListener)
	 */
	public void addMouseListener(MouseListener lis)
	{
		_jeditTextArea.addMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#removeMouseListener(MouseListener)
	 */
	public void removeMouseListener(MouseListener lis)
	{
		_jeditTextArea.removeMouseListener(lis);
	}

	JEditTextArea getTypedComponent()
	{
		return _jeditTextArea;
	}

	void updateFromPreferences(JeditPreferences prefs)
	{
		if (prefs == null)
		{
			throw new IllegalArgumentException("Null JEditPreferences passed");
		}
		JEditTextArea comp = getTypedComponent();
		TextAreaPainter painter = comp.getPainter();
		SyntaxStyle[] styles = painter.getStyles();
		styles[Token.KEYWORD1] =
			new SyntaxStyle(new Color(prefs.getKeyword1RGB()), false, true);
		styles[Token.KEYWORD2] =
			new SyntaxStyle(new Color(prefs.getKeyword2RGB()), false, true);
		styles[Token.KEYWORD3] =
			new SyntaxStyle(new Color(prefs.getKeyword3RGB()), false, true);
		styles[Token.COLOMN] =
			new SyntaxStyle(new Color(prefs.getColumnRGB()), false, true);
		styles[Token.TABLE] =
			new SyntaxStyle(new Color(prefs.getTableRGB()), false, true);
		painter.setStyles(styles);
		painter.setEOLMarkersPainted(prefs.getEOLMarkers());
		painter.setBlockCaretEnabled(prefs.isBlockCaretEnabled());
		painter.setBracketHighlightEnabled(prefs.getBracketHighlighting());
		painter.setLineHighlightEnabled(prefs.getCurrentLineHighlighting());
		comp.setCaretBlinkEnabled(prefs.getBlinkCaret());
		painter.setCaretColor(new Color(prefs.getCaretRGB()));
		painter.setSelectionColor(new Color(prefs.getSelectionRGB()));
		painter.setLineHighlightColor(new Color(prefs.getLineHighlightRGB()));
		painter.setEOLMarkerColor(new Color(prefs.getEOLMarkerRGB()));
		painter.setBracketHighlightColor(new Color(prefs.getBracketHighlightRGB()));
	}

	/*
	 * @see ISQLEntryPanel#hasOwnUndoableManager()
	 */
	public boolean hasOwnUndoableManager()
	{
		return false;
	}

	/*
	 * @see ISQLEntryPanel#addUndoableEditListener(UndoableEditListener)
	 */
	public void addUndoableEditListener(UndoableEditListener listener)
	{
		_jeditTextArea.getDocument().addUndoableEditListener(listener);
	}

	/*
	 * @see ISQLEntryPanel#removeUndoableEditListener(UndoableEditListener)
	 */
	public void removeUndoableEditListener(UndoableEditListener listener)
	{
		_jeditTextArea.getDocument().removeUndoableEditListener(listener);
	}

	/**
	 * @see ISQLEntryPanel#setUndoActions(Action, Action)
	 */
	public void setUndoActions(Action undo, Action redo)
	{
		_jeditPopup.addSeparator();
		_app.getResources().addToPopupMenu(undo, _jeditPopup);
		_app.getResources().addToPopupMenu(redo, _jeditPopup);
	}

	/*
	 * @see ISQLEntryPanel#getCaretLineNumber()
	 */
	public int getCaretLineNumber()
	{
		return _jeditTextArea.getCaretLine();
	}

	/*
	 * @see ISQLEntryPanel#getCaretLinePosition()
	 */
	public int getCaretLinePosition()
	{
		int caretPos = _jeditTextArea.getCaretPosition();
		int caretLineOffset = caretPos;
		//		try {
		caretLineOffset = _jeditTextArea.getLineStartOffset(getCaretLineNumber());
		//		} catch (BadLocationException ignore) {
		//		}			
		return caretPos - caretLineOffset;
	}

	/*
	 * @see ISQLEntryPanel#addCaretListener(CaretListener)
	 */
	public void addCaretListener(CaretListener lis)
	{
		_jeditTextArea.addCaretListener(lis);
	}

	/*
	 * @see ISQLEntryPanel#removeCaretListener(CaretListener)
	 */
	public void removeCaretListener(CaretListener lis)
	{
		_jeditTextArea.removeCaretListener(lis);
	}

}