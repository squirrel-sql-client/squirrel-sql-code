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
import java.awt.Component;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.UndoableEditListener;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;

import net.sourceforge.squirrel_sql.plugins.jedit.textarea.JEditTextArea;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxDocument;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxStyle;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.TextAreaPainter;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.Token;

class JeditSQLEntryPanel implements ISQLEntryPanel {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(JeditSQLEntryPanel.class);

	/** Text component. */
	private JEditTextArea _jeditTextArea;

	/** Jedit preferences for the current session. */
	private JeditPreferences _prefs;

	/** Listener for the session preferences. */
	private SessionPreferencesListener _sessionPrefsListener;

	JeditSQLEntryPanel(ISession session, JeditPlugin plugin, JeditPreferences prefs) {
		super();
		if (session == null) {
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (plugin == null) {
			throw new IllegalArgumentException("Null JeditPlugin passed");
		}
		if (prefs == null) {
			throw new IllegalArgumentException("Null JeditPreferences passed");
		}

		_prefs = (JeditPreferences)session.getPluginObject(plugin, JeditConstants.ISessionKeys.PREFS);
		_jeditTextArea = new JEditTextArea(new JeditTextAreaDefaults(_prefs));
		_jeditTextArea.setTokenMarker(new JeditSQLTokenMarker(session.getSQLConnection()));

		_sessionPrefsListener = new SessionPreferencesListener(plugin, session, _prefs);
		_prefs.addPropertyChangeListener(_sessionPrefsListener);
	}

	// Need to call this at the appropriate time??
	public void cleanup() {
		if (_sessionPrefsListener != null) {
			_prefs.removePropertyChangeListener(_sessionPrefsListener);
			_sessionPrefsListener = null;
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
			s_log.error("Error appending text to text area", ex);
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
	 * @see ISQLEntryPanel#setTabSize(int)
	 */
	public void setTabSize(int tabSize) {
		//??
	}

	/**
	 * @see ISQLEntryPanel#getSelectionStart()
	 */
	public int getSelectionStart() {
		return _jeditTextArea.getSelectionStart();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionStart(int)
	 */
	public void setSelectionStart(int pos) {
		_jeditTextArea.setSelectionStart(pos);
	}

	/**
	 * @see ISQLEntryPanel#getSelectionEnd()
	 */
	public int getSelectionEnd() {
		return _jeditTextArea.getSelectionEnd();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionEnd(int)
	 */
	public void setSelectionEnd(int pos) {
		_jeditTextArea.setSelectionEnd(pos);
	}

	/**
	 * @see ISQLEntryPanel#hasFocus()
	 */
	public boolean hasFocus() {
		return _jeditTextArea.hasFocus();
	}

	/**
	 * @see ISQLEntryPanel#requestFocus()
	 */
	public void requestFocus() {
		_jeditTextArea.requestFocus();
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

	private void updateFromPreferences(JeditPreferences prefs)
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
		painter.setEOLMarkersPainted(prefs.getEolMarkers());
		painter.setBlockCaretEnabled(prefs.isBlockCaretEnabled());
		painter.setBracketHighlightEnabled(prefs.getBracketHighlighting());
		painter.setLineHighlightEnabled(prefs.getCurrentLineHighlighting());
		comp.setCaretBlinkEnabled(prefs.getBlinkCaret());
		comp.setFont(prefs.getFontInfo().createFont());
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

	private static final class SessionPreferencesListener implements PropertyChangeListener {
		private JeditPlugin _plugin;
		private ISession _session;
		private JeditPreferences _prefs;
		private boolean _usingJeditControl;		
		SessionPreferencesListener(JeditPlugin plugin, ISession session, JeditPreferences prefs) {
			super();
			_plugin = plugin;
			_session = session;
			_prefs = prefs;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			final String propName = evt.getPropertyName();

			if (propName == null || propName.equals(
					JeditPreferences.IPropertyNames.USE_JEDIT_CONTROL)) {
				synchronized (_session) {
					SessionSheet sheet = _session.getSessionSheet();
					if (sheet != null) {
						sheet.replaceSQLEntryPanel(_plugin.getJeditFactory().createSQLEntryPanel(_session));
					}
				}
			}

			if (propName == null ||
					!propName.equals(JeditPreferences.IPropertyNames.USE_JEDIT_CONTROL)) {
				if (_prefs.getUseJeditTextControl()) {
					JeditSQLEntryPanel pnl = (JeditSQLEntryPanel)_session.getPluginObject(_plugin, JeditConstants.ISessionKeys.JEDIT_SQL_ENTRY_CONTROL);
					if (pnl != null) {
						pnl.updateFromPreferences(_prefs);
					}
				}
			}
		}
	}
}

