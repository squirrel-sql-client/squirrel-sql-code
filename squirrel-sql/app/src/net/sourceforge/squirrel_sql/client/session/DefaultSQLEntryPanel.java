package net.sourceforge.squirrel_sql.client.session;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.UndoableEditListener;
//import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
//import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
//import net.sourceforge.squirrel_sql.client.session.action.UndoAction;

public class DefaultSQLEntryPanel implements ISQLEntryPanel {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SQLPanel.class);

	/** Text area control. */
	private MyTextArea _comp;

	/** Scroll pane for text control. */
	private JScrollPane _scroller;

	/** Popup menu for this component. */
	private TextPopupMenu _textPopupMenu = new TextPopupMenu();

	/** Listener for displaying the popup menu. */
	private MouseListener _sqlEntryMouseListener = new MyMouseListener();

	/** Undo manager for this component. */
//	private UndoManager _undoManager = new UndoManager();

	public DefaultSQLEntryPanel(IApplication app) {
		super();

//		UndoAction undo = new UndoAction(app, _undoManager);
//		RedoAction redo = new RedoAction(app, _undoManager);

//		_textPopupMenu.add(undo);
//		_textPopupMenu.add(redo);

		_comp = new MyTextArea(app);//, undo, redo);
		_scroller = new JScrollPane(_comp);
	}

	public void addUndoableEditListener(UndoableEditListener lis) {
		_comp.getDocument().addUndoableEditListener(lis);
		
	}

	public void removeUndoableEditListener(UndoableEditListener lis) {
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
	public void setUndoActions(Action undo, Action redo) {
		_textPopupMenu.addSeparator();
		_textPopupMenu.add(undo);
		_textPopupMenu.add(redo);
	}

	/**
	 * Return the text area control. In this case a <TT>JScrollPane</TT> wrapped
	 * around an instance of <TT>JTextArea</TT>.
	 * 
	 * @return	an instance of <TT>JScrollPane</TT>.
	 */
	public JComponent getJComponent() {
		return _scroller;
	}

	/**
	 * @see ISQLEntryPanel#getText()
	 */
	public String getText() {
		return _comp.getText();
	}

	/**
	 * @see ISQLEntryPanel#getSelectedText()
	 */
	public String getSelectedText() {
		return _comp.getSelectedText();
	}

	/**
	 * @see ISQLEntryPanel#setText(String)
	 */
	public void setText(String text) {
		_comp.setText(text);
	}

	/**
	 * @see ISQLEntryPanel#appendText(String)
	 */
	public void appendText(String text) {
		_comp.append(text);
	}
	/**
	 * @see ISQLEntryPanel#getCaretPosition()
	 */
	public int getCaretPosition() {
		return _comp.getCaretPosition();
	}

	/**
	 * @see ISQLEntryPanel#setTabSize(int)
	 */
	public void setTabSize(int tabSize) {
		_comp.setTabSize(tabSize);
	}

	/**
	 * @see ISQLEntryPanel#addMouseListener(MouseListener)
	 */
	public void addMouseListener(MouseListener lis) {
		_comp.addMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#removeListener(MouseListener)
	 */
	public void removeMouseListener(MouseListener lis) {
		_comp.removeMouseListener(lis);
	}

	/**
	 * @see ISQLEntryPanel#setCaretPosition(int)
	 */
	public void setCaretPosition(int pos) {
		_comp.setCaretPosition(pos);
	}

	/**
	 * @see ISQLEntryPanel#getSelectionStart()
	 */
	public int getSelectionStart() {
		return _comp.getSelectionStart();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionStart(int)
	 */
	public void setSelectionStart(int pos) {
		_comp.setSelectionStart(pos);
	}

	/**
	 * @see ISQLEntryPanel#getSelectionEnd()
	 */
	public int getSelectionEnd() {
		return _comp.getSelectionEnd();
	}

	/**
	 * @see ISQLEntryPanel#setSelectionEnd(int)
	 */
	public void setSelectionEnd(int pos) {
		_comp.setSelectionEnd(pos);
	}

	/**
	 * @see ISQLEntryPanel#hasFocus()
	 */
	public boolean hasFocus() {
		return _comp.hasFocus();
	}

	/**
	 * @see ISQLEntryPanel#requestFocus()
	 */
	public void requestFocus() {
		_comp.requestFocus();
	}

	private final class MyMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent evt) {
			if (evt.isPopupTrigger()) {
				displayPopupMenu(evt);
			}
		}
		public void mouseReleased(MouseEvent evt) {
			if (evt.isPopupTrigger()) {
				displayPopupMenu(evt);
			}
		}
		private void displayPopupMenu(MouseEvent evt) {
			_textPopupMenu.setTextComponent(_comp);
			_textPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	private class MyTextArea extends JTextArea {
		private IApplication _app;
//		private UndoAction _undo;
		//private RedoAction _redo;

		private MyTextArea(IApplication app) {//, UndoAction undo, RedoAction redo) {
			super();
			_app = app;
//			_undo = undo;
//			_redo = redo;
		}

		public void addNotify() {
			super.addNotify();

			DefaultSQLEntryPanel.this.addMouseListener(_sqlEntryMouseListener);
	
			//Resources res = _app.getResources();
				
			// ??Whe should also register them as a action to the textarea or this panel!?
//			registerKeyboardAction(_undo, res.getKeyStroke(_undo), _comp.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//			registerKeyboardAction(_redo, res.getKeyStroke(_redo), _comp.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
				
	//		_sqlEntry.addUndoableEditListener(_undoManager);
//			_comp.getDocument().addUndoableEditListener(_undoManager);
		}

		public void removeNotify() {
			DefaultSQLEntryPanel.this.removeMouseListener(_sqlEntryMouseListener);
			super.removeNotify();
		}
	}

}
