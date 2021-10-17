package net.sourceforge.squirrel_sql.fw.gui.statusbar;
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.LogPanel;
import net.sourceforge.squirrel_sql.client.gui.MemoryPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.TimePanel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * Statusbar component for the main frame.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MainFrameStatusBar extends JPanel
{
	private Font _font;

	private final GridBagConstraints _gbc = new GridBagConstraints();
	private JTextField _textLbl = new JTextField();


	public MainFrameStatusBar(IApplication app)
	{
		super(new GridBagLayout());
		createGUI(app);
	}

	private void createGUI(IApplication app)
	{
		// The message area is on the right of the statusbar and takes
		// up all available space.
		_gbc.anchor = GridBagConstraints.WEST;
		_gbc.weightx = 1.0;
		_gbc.fill = GridBagConstraints.HORIZONTAL;
		_gbc.gridy = 0;
		_gbc.gridx = 0;

		_textLbl.setEditable(false);
		GUIUtils.inheritBackground(_textLbl);
		addJComponent(_textLbl);

		// Any other components are on the right.
		_gbc.weightx = 0.0;
		_gbc.anchor = GridBagConstraints.CENTER;
		_gbc.gridx = GridBagConstraints.RELATIVE;
		_gbc.insets.left = 2;


		addJComponent(new LogPanel(app));
		addJComponent(new MemoryPanel(app));
		addJComponent(new TimePanel());
	}

	public void addJComponent(JComponent comp)
	{
		comp.setBorder(StatusBarUtil.createComponentBorder());
		if (_font != null)
		{
			comp.setFont(_font);
			StatusBarUtil.updateSubcomponentsFont(comp, _font);
		}
		GUIUtils.inheritBackground(comp);
		super.add(comp, _gbc);
	}

	public void setText(String text)
	{
		_textLbl.setText(text);
	}


	/**
	 * Set the font for controls in this statusbar.
	 *
	 * @param	font	The font to use.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT> <TT>Font</TT> passed.
	 */
	public void setFont(Font font)
	{
		super.setFont(font);
		_font = font;
		StatusBarUtil.updateSubcomponentsFont(this, _font);
	}


}
