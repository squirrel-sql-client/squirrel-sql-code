package net.sourceforge.squirrel_sql.plugins.jedit;
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
import java.awt.Color;

import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxDocument;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxStyle;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxUtilities;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.TextAreaDefaults;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.Token;

class JeditTextAreaDefaults extends TextAreaDefaults {
	JeditTextAreaDefaults(JeditPreferences prefs) throws IllegalArgumentException {
		super();
		if (prefs == null) {
			throw new IllegalArgumentException("Null JEditPreferences passed");
		}

		inputHandler = new JeditInputHandler();
		document = new SyntaxDocument();
		editable = true;

		caretVisible = true;
		caretBlinks = true;
		electricScroll = 3;

		cols = 1;
		rows = 1;

		styles = SyntaxUtilities.getDefaultSyntaxStyles();

		blockCaret = true;
		caretColor = Color.red;
		selectionColor = new Color(0xccccff);
		lineHighlightColor = new Color(0xe0e0e0);
		lineHighlight = true;
		bracketHighlightColor = Color.black;
		bracketHighlight = true;
		eolMarkerColor = new Color(0x009999);
		eolMarkers = false;
		paintInvalid = false;

		updateFromPreferences(prefs);
	}

	private void updateFromPreferences(JeditPreferences prefs) throws IllegalArgumentException {
		if (prefs == null) {
			throw new IllegalArgumentException("Null JEditPreferences passed");
		}
		styles[Token.KEYWORD1] = new SyntaxStyle(new Color(prefs.getKeyword1RGB()), false, true);
		styles[Token.KEYWORD2] = new SyntaxStyle(new Color(prefs.getKeyword2RGB()), false, true);
		styles[Token.KEYWORD3] = new SyntaxStyle(new Color(prefs.getKeyword3RGB()), false, true);
	}
}

