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
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;

public interface ISQLEntryPanel {
	JComponent getJComponent();

	String getText();
	String getSelectedText();
	void setText(String text);
	void appendText(String text);
	String getSQLToBeExecuted();

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

	void addMouseListener(MouseListener lis);
	void removeMouseListener(MouseListener lis);

	boolean hasOwnUndoableManager();

	void addUndoableEditListener(UndoableEditListener listener);

	void removeUndoableEditListener(UndoableEditListener listener);

	void setUndoActions(Action undo, Action redo);

	void addCaretListener(CaretListener lis);
	void removeCaretListener(CaretListener lis);
}

