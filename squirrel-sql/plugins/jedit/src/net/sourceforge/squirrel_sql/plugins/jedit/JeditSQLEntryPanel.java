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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import net.sourceforge.squirrel_sql.plugins.jedit.textarea.JEditTextArea;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxDocument;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxStyle;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.TextAreaPainter;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.Token;

class JeditSQLEntryPanel implements ISQLEntryPanel {
	/** Text component. */
	private JEditTextArea _jeditTextArea;

	JeditSQLEntryPanel(ISession session, JeditPreferences prefs) {
		super();
		if (prefs == null) {
			throw new IllegalArgumentException("Null JeditPreferences passed");
		}

		_jeditTextArea = new JEditTextArea(new JeditTextAreaDefaults(prefs));
		_jeditTextArea.setTokenMarker(new JeditSQLTokenMarker(session.getSQLConnection()));
		Font font = (Font)UIManager.get("TextArea.font");
		if (font != null) {
			_jeditTextArea.getPainter().setFont(font);
		}
	}

	/**
	 * @see ISQLEntryPanel#getComponent()
	 */
	public Component getComponent() {
		return _jeditTextArea;
	}

	/**
	 * @see ISQLEntryPanel#getText()
	 */
	public String getText() {
		return _jeditTextArea.getText();
	}

	/**
	 * @see ISQLEntryPanel#getSelectedText()
	 */
	public String getSelectedText() {
		return _jeditTextArea.getSelectedText();
	}

	/**
	 * @see ISQLEntryPanel#setText(String)
	 */
	public void setText(String text) {
		_jeditTextArea.setText(text);
	}

	/**
	 * @see ISQLEntryPanel#appendText(String)
	 */
	public void appendText(String text) {
		SyntaxDocument doc = _jeditTextArea.getDocument();
		try {
			doc.insertString(doc.getLength(), text, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see ISQLEntryPanel#getCaretPosition()
	 */
	public int getCaretPosition() {
		return _jeditTextArea.getCaretPosition();
	}

	public void setCaretPosition(int value) {
		_jeditTextArea.setCaretPosition(value);
    }

	/**
	 * @see ISQLEntryPanel#setRows(int)
	 */
	public void setRows(int rowCount) {
		//??
	}

	/**
	 * @see ISQLEntryPanel#setTabSize(int)
	 */
	public void setTabSize(int tabSize) {
		//??
	}

	/**
	 * @see ISQLEntryPanel#addMouseListener(MouseListener)
	 */
	public void addMouseListener(MouseListener lis) {
		_jeditTextArea.addMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#removeMouseListener(MouseListener)
	 */
	public void removeMouseListener(MouseListener lis) {
		_jeditTextArea.removeMouseListener(lis);
	}

	JEditTextArea getTypedComponent() {
		return _jeditTextArea;
	}

	void updateFromPreferences(JeditPreferences prefs)
			throws IllegalArgumentException {
		if (prefs == null) {
			throw new IllegalArgumentException("Null JEditPreferences passed");
		}
		JEditTextArea comp = getTypedComponent();
		TextAreaPainter painter = comp.getPainter();
		SyntaxStyle[] styles = painter.getStyles();
		styles[Token.KEYWORD1] = new SyntaxStyle(new Color(prefs.getKeyword1RGB()), false, true);
		styles[Token.KEYWORD2] = new SyntaxStyle(new Color(prefs.getKeyword2RGB()), false, true);
		styles[Token.KEYWORD3] = new SyntaxStyle(new Color(prefs.getKeyword3RGB()), false, true);
		painter.setStyles(styles);
	}

}

