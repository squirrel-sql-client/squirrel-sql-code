package net.sourceforge.squirrel_sql.client.gui;
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
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

/**
 * Base functionality for Squirrels internal frames.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class BaseSheet extends JInternalFrame {
	/**
	 * Creates a non-resizable, non-closable, non-maximizable,
	 * non-iconifiable JInternalFrame with no title.
	 */
	public BaseSheet() {
		super();
	}

	/**
	 * Creates a non-resizable, non-closable, non-maximizable,
	 * non-iconifiable JInternalFrame with the specified title.
	 *
	 * @param	title	Title for internal frame.
	 */
	public BaseSheet(String title) {
		super(title);
	}

	/**
	 * Creates a non-closable, non-maximizable, non-iconifiable
	 * JInternalFrame with the specified title and with
	 * resizability specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 */
	public BaseSheet(String title, boolean resizable) {
		super(title, resizable);
	}

	/**
	 * Creates a non-maximizable, non-iconifiable JInternalFrame
	 * with the specified title and with resizability and closability
	 * specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 * @param	closeable	<TT>true</TT> if frame can be closed.
	 */
	public BaseSheet(String title, boolean resizable, boolean closable) {
		super(title, resizable, closable);
	}

	/**
	 * Creates a non-iconifiable JInternalFrame with the specified title
	 * and with resizability, closability, and maximizability specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 * @param	closeable	<TT>true</TT> if frame can be closed.
	 * @param	maximizable	<TT>true</TT> if frame can be maximized.
	 */
	public BaseSheet(String title, boolean resizable, boolean closable,
						boolean maximizable) {
		super(title, resizable, closable, maximizable);
	}

	/**
	 * Creates a JInternalFrame with the specified title and with
	 * resizability, closability, maximizability and
	 * iconifability specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 * @param	closeable	<TT>true</TT> if frame can be closed.
	 * @param	maximizable	<TT>true</TT> if frame can be maximized.
	 * @param	iconifiable	<TT>true</TT> if frame can be iconified.
	 */
	public BaseSheet(String title, boolean resizable, boolean closable,
						boolean maximizable, boolean iconifiable)  {
		super(title, resizable, closable, maximizable, iconifiable);
	}

	/**
	 *
	 * Modifed version of code from Slavas View class in jEdit.
	 */
	public void processKeyEvent(KeyEvent evt) {
		// Superclass method is protected.
		super.processKeyEvent(evt);
/*
 * 		if (isClosed())
			return;

		// JTextComponents don't consume events...
		if (getFocusOwner() instanceof JTextComponent) {
			// fix for the bug where key events in JTextComponents
			// inside views are also handled by the input handler
			if (evt.getID() == KeyEvent.KEY_PRESSED) {
				switch (evt.getKeyCode()) {
					case KeyEvent.VK_BACK_SPACE :
					case KeyEvent.VK_TAB :
					case KeyEvent.VK_ENTER :
						return;
				}
			}

			Keymap keymap = ((JTextComponent) getFocusOwner()).getKeymap();
			if (keymap.getAction(KeyStroke.getKeyStrokeForEvent(evt)) != null)
				return;
		}

		if (evt.isConsumed())
			return;

		if (!evt.isConsumed())
			super.processKeyEvent(evt);
*/
	}

}