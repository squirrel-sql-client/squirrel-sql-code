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
import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IUndoHandler;

public interface ISQLEntryPanel extends IHasIdentifier
{
	/**
	 * Retrieve the text area component. Normally this would be a subclass
	 * of <TT>javax.swing.text.JTextComponent</TT> but a plugin may use a
	 * class other than a Swing text control.
	 *
	 * @return	The text area component.
	 */
	JTextComponent getTextComponent();

	/**
	 * If the component returned by <TT>getTextComponent</TT> contains
	 * its own scroll bars return <TT>true</TT> other wise this component
	 * will be wrapped in the scroll pane when added to the SQL panel.
	 *
	 * @return	<TT>true</TT> if text component already handles scrolling.
	 */
	boolean getDoesTextComponentHaveScroller();

	String getText();
	String getSelectedText();

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script without selecting it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 */
	void setText(String sqlScript);

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script and specify whether to select it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	void setText(String sqlScript, boolean select);

	/**
	 * Append the passed SQL script to the SQL entry area but don't select
	 * it.
	 *
	 * @param	sqlScript	The script to be appended.
	 */
	void appendText(String text);

	/**
	 * Append the passed SQL script to the SQL entry area and specify
	 * whether it should be selected.
	 *
	 * @param	sqlScript	The script to be appended.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	void appendText(String sqlScript, boolean select);

	/**
	 * Replace the currently selected text in the SQL entry area
	 * with the passed text.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 */
	void replaceSelection(String sqlScript);

	String getSQLToBeExecuted();
   int[] getBoundsOfSQLToBeExecuted();

	void moveCaretToPreviousSQLBegin();

	void moveCaretToNextSQLBegin();

   void selectCurrentSql();


	int getSelectionStart();
	void setSelectionStart(int pos);

	int getSelectionEnd();
	void setSelectionEnd(int pos);

	int getCaretPosition();
	void setCaretPosition(int pos);

	/**
	 * Return the zero-based line number that the caret is currently on.
	 *
	 * @return	the zero-based line number that the caret is currently on.
	 */
	int getCaretLineNumber();

	int getCaretLinePosition();

	boolean hasFocus();
	void requestFocus();

	void setFont(Font font);
	void setTabSize(int tabSize);

	/**
	 * Add a hierarchical menu to the SQL Entry Area popup menu.
	 *
	 * @param	menu	The menu that will be added.
	 */
	void addToSQLEntryAreaMenu(JMenu menu);

	/**
	 * Add an <TT>Action</TT> to the SQL Entry Area popup menu.
	 *
	 * @param	action	The action to be added.
	 *
	 * @return	The newly created menu item.
	 */
	JMenuItem addToSQLEntryAreaMenu(Action action);

	void addMouseListener(MouseListener lis);
	void removeMouseListener(MouseListener lis);

	boolean hasOwnUndoableManager();

   /**
    * Will only be called if hasOwnUndoableManager() returns true
    */
   IUndoHandler createUndoHandler();

   /**
    * Will only be called if hasOwnUndoableManager() returns false
    */
    void setUndoManager(UndoManager manager);
    
	void addUndoableEditListener(UndoableEditListener listener);

	void removeUndoableEditListener(UndoableEditListener listener);

	void addRedoUndoActionsToSQLEntryAreaMenu(Action undo, Action redo);

	void addCaretListener(CaretListener lis);
	void removeCaretListener(CaretListener lis);

	void addSQLTokenListener(SQLTokenListener tl);
	void removeSQLTokenListener(SQLTokenListener tl);

	void dispose();

   ISession getSession();

   String getWordAtCursor();

   JScrollPane createScrollPane(JTextComponent textComponent);
}
