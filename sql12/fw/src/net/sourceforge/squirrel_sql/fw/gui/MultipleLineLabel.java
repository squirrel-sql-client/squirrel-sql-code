package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;

public class MultipleLineLabel extends JTextArea
{
	public MultipleLineLabel()
	{
		this("");
	}

	public MultipleLineLabel(String title)
	{
		super();
		setEditable(false);
		setLineWrap(true);
		setWrapStyleWord(true);
		setText(title);
	}

	public void updateUI()
	{
		// installColorsAndFont needs to be run twice. Background and foreground
		// colors only work if run after the super call but font only works if run
		// before the super call. May be a bug in JDK 1.4
		LookAndFeel.installBorder(this, "Label.border");
		LookAndFeel.installColorsAndFont(this, "Label.background", "Label.foreground",
											"Label.font");
		super.updateUI();
		LookAndFeel.installColorsAndFont(this, "Label.background", "Label.foreground",
											"Label.font");
	}
}
