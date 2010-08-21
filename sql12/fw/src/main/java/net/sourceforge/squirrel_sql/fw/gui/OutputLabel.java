package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.awt.Dimension;

import javax.swing.JLabel;
/**
 * <TT>JLabel</TT> component that shows its contents in a tooltip
 * and has a default preferred width of <TT>PREF_WIDTH</TT>.
 * 
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OutputLabel extends JLabel
{
	/** Default preferred width. */
	public static final int PREF_WIDTH = 200;

	/**
	 * Default ctor.
	 */
	public OutputLabel()
	{
		super();
		commonCtor();
	}

	/**
	 * Ctor specifying text.
	 * 
	 * @param	text	text to display in label.
	 */
	public OutputLabel(String text)
	{
		super(text);
		commonCtor();
		setToolTipText(text);
	}

	/**
	 * Set labels text. Also set tooltip to the text.
	 * 
	 * @param	text	New text for label.
	 */
	public void setText(String text)
	{
		super.setText(text);
		setToolTipText(text);
	}

	/**
	 * Common ctor code.
	 */
	private void commonCtor()
	{
		Dimension ps = getPreferredSize();
		ps.width = PREF_WIDTH;
		setPreferredSize(ps);
	}
}
