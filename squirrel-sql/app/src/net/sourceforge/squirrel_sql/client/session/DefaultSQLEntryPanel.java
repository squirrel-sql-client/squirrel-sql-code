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
import java.awt.Component;

import java.awt.event.MouseListener;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DefaultSQLEntryPanel implements ISQLEntryPanel {
	/** Text area control. */
	private JTextArea _comp = new JTextArea();

	/** Scroll pane for text control. */
	private JScrollPane _scroller = new JScrollPane(_comp);

	/**
	 * Return the text area control. In this case an instance of <TT>JTextArea</TT>.
	 * 
	 * @return	an instance of <TT>JTextArea</TT>.
	 */
	public Component getComponent() {
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
	 * @see ISQLEntryPanel#setRows(int)
	 */
	public void setRows(int rowCount) {
		_comp.setRows(rowCount);
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

}

