package net.sourceforge.squirrel_sql.fw.gui;
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
import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
/**
 * This is a statusbar component with a text control for messages.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class StatusBar extends JPanel
{
	/**
	 * Message to display if there is no msg to display. Defaults to a
	 * blank string.
	 */
	private String _msgWhenEmpty = " ";

	/** Label showing the message in the statusbar. */
	private JLabel _textLbl = new StatusBarLabel();

	/** Font for controls. */
	private Font _font;

	/**
	 * Default ctor.
	 */
	public StatusBar()
	{
		super(new BorderLayout());
		createUserInterface();
	}

	/**
	 * Set the font for controls in this statusbar.
	 * 
	 * @param	font	The font to use.
	 * 
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT> <TT>Font</TT> passed.
	 */
	public synchronized void setFont(Font font)
	{
		if (font == null)
		{
			throw new IllegalArgumentException("Font == null");
		}
		super.setFont(font);
		_font = font;
		if (_textLbl != null)
		{
			_textLbl.setFont(font);
		}
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
		_textLbl.setText(_msgWhenEmpty);
	}

	public synchronized void setTextWhenEmpty(String value)
	{
		final boolean wasEmpty = _textLbl.getText().equals(_msgWhenEmpty);
		if (value != null && value.length() > 0)
		{
			_msgWhenEmpty = value;
		}
		else
		{
			_msgWhenEmpty = " ";
		}
		if (wasEmpty)
		{
			clearText();
		}
	}

	private void createUserInterface()
	{
		_textLbl.setFont(_font);
		add(_textLbl, BorderLayout.CENTER);
		clearText();
	}

	private static class StatusBarLabel extends JLabel
	{
		StatusBarLabel()
		{
			super();
			setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createBevelBorder(BevelBorder.LOWERED),
							BorderFactory.createEmptyBorder(0, 4, 0, 4)));
		}
	}
}
