package net.sourceforge.squirrel_sql.client.mainframe;
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import net.sourceforge.squirrel_sql.fw.gui.MemoryPanel;
import net.sourceforge.squirrel_sql.fw.gui.TimePanel;
/**
 * Statusbar component for the main frame.
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MainFrameStatusBar extends JPanel
{
	/** Label showing the message in the statusbar. */
	private JLabel _textLbl = new JLabel();

	/** Panel displaying memory status. */
	private MemoryPanel _mp;

	/** Panel displaying the current time. */
	private TimePanel _tp;

	/**
	 * Default ctor.
	 */
	public MainFrameStatusBar()
	{
		super(new GridBagLayout());
		createUserInterface();
	}

	/**
	 * Set the font for controls in this statusbar.
	 * 
	 * @param	font	The font to use.
	 * 
	 * @throws	IllegalArgumentException	if <TT>null</TT> <TT>Font</TT> passed.
	 */
	public synchronized void setFont(Font font)
	{
		if (font == null)
		{
			throw new IllegalArgumentException("Font == null");
		}
		super.setFont(font);
		updateContainerFont(this, font);
	}

	/**
	 * Set the text to display in the message label.
	 * 
	 * @param	text	Text to display in the message label.
	 */
	public synchronized void setText(String text)
	{
		String myText = null;
		if (text != null)
		{
			myText = text.trim();
		}
		if (myText != null && myText.length() > 0)
		{
			_textLbl.setText(myText);
		}
		else
		{
			clearText();
		}
	}

	public synchronized void clearText()
	{
		_textLbl.setText(" ");
	}

	private void createUserInterface()
	{
		clearText();

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = gbc.WEST;
		gbc.weightx = 1.0;
		gbc.fill = gbc.HORIZONTAL;
		gbc.gridy = 0;

		_textLbl.setBorder(createComponentBorder());
		gbc.gridx = 0;
		add(_textLbl, gbc);

		_mp = new MemoryPanel();
		_mp.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					System.gc();
					setText("Garbage collection requested");
				}
			}
		});
		_mp.setBorder(createComponentBorder());
		gbc.weightx = 0.0;
		gbc.anchor = gbc.CENTER;
		++gbc.gridx;
		add(_mp, gbc);

		_tp = new TimePanel();
		_tp.setBorder(createComponentBorder());
		++gbc.gridx;
		add(_tp, gbc);

	}

	public static Border createComponentBorder()
	{
		return BorderFactory.createCompoundBorder(
			BorderFactory.createBevelBorder(BevelBorder.LOWERED),
			BorderFactory.createEmptyBorder(0, 4, 0, 4));
	}

	private static void updateContainerFont(Container cont, Font font)
	{
		Component[] comps = cont.getComponents();
		for (int i = 0; i < comps.length; ++i)
		{
			comps[i].setFont(font);
			if (comps[i] instanceof Container)
			{
				updateContainerFont((Container) comps[i], font);
			}
		}
	}

}
