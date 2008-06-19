/*
 * Copyright (C) 2008 Michael Romankiewicz
 * mirommail(at)web.de
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
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.comp;

import java.awt.Cursor;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * Component class with global configured JButton for firebirdmanager frames  
 * @author Michael Romankiewicz
 */
public class FBButton extends JButton {
	private static final long serialVersionUID = -8734222381715897845L;

	public FBButton() {
		super();
		initButton(false);
	}

	public FBButton(Action a) {
		super(a);
		initButton(false);
	}

	public FBButton(Icon icon) {
		super(icon);
		initButton(false);
	}

	public FBButton(String text, Icon icon) {
		super(text, icon);
		initButton(false);
	}

	public FBButton(String text) {
		super(text);
		initButton(false);
	}

	public FBButton(boolean withdefaultBorder) {
		super();
		initButton(withdefaultBorder);
	}

	private void initButton(boolean withdefaultBorder) {
		if (!withdefaultBorder) {
			Border borderButton = BorderFactory.createEmptyBorder(5, 5, 5, 5);
			this.setBorder(borderButton);
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
}
