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
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

/**
 * This is a statusbar component with a text control for messages.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class StatusBar extends JPanel {
	/**
	 * Message to display if there is no msg to display. Defaults to a
	 * blank string.
	 */
	private String _msgWhenEmpty = " ";

	/** Label showing the message in the statusbar. */
	private JLabel _textLbl = new MyLabel();

	/** Font for controls. */
	private Font _font;

	/**
	 * Default ctor.
	 */
	public StatusBar() {
		super(new java.awt.BorderLayout());
		createUserInterface();
	}

	/**
	 * Set the font for controls in this statusbar.
	 * 
	 * @param	font	The font to use.
	 * 
	 * @throws	IllegalArgumentException	if <TT>null</TT> <TT>Font</TT> passed.
	 */
	public synchronized void setFont(Font font) {
		if (font == null) {
			throw new IllegalArgumentException("Font == null");
		}
		super.setFont(font);
		_font = font;
		if (_textLbl != null) {
			_textLbl.setFont(font);
		}
	}

	/**
	 * Set the text to display in the message label.
	 * 
	 * @param	text	Text to display in the message label.
	 */
	public synchronized void setText(String text) {
		String myText = null;
		if (text != null) {
			myText = text.trim();
		}
		if (myText != null && myText.length() > 0) {
			_textLbl.setText(myText);
		} else {
			clearText();
		}
	}

	public synchronized void clearText() {
		_textLbl.setText(_msgWhenEmpty);
	}

	public synchronized void setTextWhenEmpty(String value) {
		final boolean wasEmpty = _textLbl.getText().equals(_msgWhenEmpty);
		if (value != null && value.length() > 0) {
			_msgWhenEmpty = value;
		} else {
			_msgWhenEmpty = " ";
		}
		if (wasEmpty) {
			clearText();
		}
	}

	private void createUserInterface() {
		_textLbl.setFont(_font);
//		setLayout(new GridBagLayout());
		add(_textLbl, java.awt.BorderLayout.CENTER);//, new TextConstraints());
		clearText();
	}

	private static class MyLabel extends JLabel {
		MyLabel() {
			super();
			setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createBevelBorder(BevelBorder.LOWERED),
						BorderFactory.createEmptyBorder(0, 4, 0, 4)));
		}
	}

/*	private static abstract class BaseConstraints extends GridBagConstraints {
		BaseConstraints() {
			super();
			insets = new Insets(1, 1, 1, 1);
			anchor = GridBagConstraints.WEST;
			gridheight = 1;
			gridwidth = 1;
			gridy = 0;
			weighty = 0.0;
		}
	}

	private static final class TextConstraints extends BaseConstraints {
		TextConstraints() {
			super();
			gridx = 0;
			fill = GridBagConstraints.HORIZONTAL;
			weightx = 1.0;
		}
	}
*/
}
