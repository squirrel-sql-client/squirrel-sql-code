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
import javax.swing.plaf.TextUI;
import java.awt.Color;

public class MultipleLineLabel extends JTextArea
{
	private static final Color TRANSPARENT = new Color(0x00FFFFFF, true);

	public MultipleLineLabel()
	{
		this("");
	}

	public MultipleLineLabel(String title)
	{
		setEditable(false);
		setLineWrap(true);
		setWrapStyleWord(true);
		setText(title);
		setBackground(TRANSPARENT);
		setOpaque(false);
	}

	@Override
	public void setUI(TextUI ui)
	{
		super.setUI(ui);
		LookAndFeel.installBorder(this, "Label.border");
		LookAndFeel.installColorsAndFont(this, "Label.background", "Label.foreground", "Label.font");
	}
}
