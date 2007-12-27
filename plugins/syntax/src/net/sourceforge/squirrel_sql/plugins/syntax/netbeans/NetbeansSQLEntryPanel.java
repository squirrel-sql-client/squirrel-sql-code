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
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.dnd.FileEditorDropTargetListener;
import net.sourceforge.squirrel_sql.client.session.BaseSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;

import org.netbeans.editor.ext.ExtKit;

public class NetbeansSQLEntryPanel extends BaseSQLEntryPanel
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(NetbeansSQLEntryPanel.class);

	/** Application API. */
	@SuppressWarnings("unused")
	private IApplication _app;

	/** Text component. */
	private NetbeansSQLEditorPane _textArea;

	private SyntaxFactory _syntaxFactory;

	private ISession _session;

	private SyntaxPugin _plugin;

	private NetbeansPropertiesWrapper _propertiesWrapper;

	@SuppressWarnings("unused")
	private DropTarget dt;

	NetbeansSQLEntryPanel(ISession session, SyntaxPreferences prefs,
			SyntaxFactory syntaxFactory, SyntaxPugin plugin,
			HashMap<String, Object> props)
	{
		super(session.getApplication());
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		_propertiesWrapper = new NetbeansPropertiesWrapper(props);

		_plugin = plugin;

		_syntaxFactory = syntaxFactory;
		_session = session;
		_plugin = plugin;

		_app = session.getApplication();

		_textArea = new NetbeansSQLEditorPane(	session,
															prefs,
															syntaxFactory,
															_plugin,
															getIdentifier(),
															_propertiesWrapper);

		dt = new DropTarget(_textArea, new FileEditorDropTargetListener(session));
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
	public JTextComponent getTextComponent()
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
	 * Replace the contents of the SQL entry area with the passed SQL script
	 * without selecting it.
	 * 
	 * @param sqlScript
	 *           The script to be placed in the SQL entry area..
	 */
	public void setText(String text)
	{
		setText(text, true);
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
		_textArea.setText(text);
		if (select)
		{
			setSelectionEnd(_textArea.getDocument().getLength());
			setSelectionStart(0);
		}
		triggerParser();
	}

	/**
	 * Append the passed SQL script to the SQL entry area but don't select it.
	 * 
	 * @param sqlScript
	 *           The script to be appended.
	 */
	public void appendText(String sqlScript)
	{
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
		_textArea.replaceSelection(sqlScript);

		triggerParser();

	}

	private void triggerParser()
	{
		IParserEventsProcessor parserEventsProcessor = _propertiesWrapper.getParserEventsProcessor(	getIdentifier(),
																																	_session);

		if (null != parserEventsProcessor)
		{
			parserEventsProcessor.triggerParser();
		}
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
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_textArea.requestFocus();
			}
		});
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

	public ISession getSession()
	{
		return _session;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#setUndoManager(javax.swing.undo.UndoManager)
	 */
	public void setUndoManager(UndoManager manager)
	{
		_textArea.setUndoManager(manager);
	}

	/**
	 * Sets the session referenced by this class to null so that it can be
	 * garbage-collected.
	 */
	public void sessionEnding()
	{
		_session = null;
	}
}
