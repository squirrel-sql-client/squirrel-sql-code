package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001-2002 Colin Bell
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

import javax.swing.JEditorPane;
import javax.swing.text.Caret;
import javax.swing.text.Document;

import org.gjt.sp.jedit.syntax.DefaultSyntaxDocument;
import org.gjt.sp.jedit.syntax.SyntaxUtilities;
import org.gjt.sp.jedit.syntax.TextAreaDefaults;
import org.gjt.sp.jedit.syntax.Token;

class JeditTextAreaDefaults extends TextAreaDefaults
{
	/** Default blink rate for the caret. */
	private int _defaultCaretBlinkRate;

	/** Default caret. */
	private Caret _defaultCaret;

	JeditTextAreaDefaults(JEditorPane textArea, JeditPreferences prefs)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("Null JEditPreferences passed");
		}
		if (textArea == null)
		{
			throw new IllegalArgumentException("Null JEditorPane passed");
		}

		_defaultCaret = textArea.getCaret();
		_defaultCaretBlinkRate = _defaultCaret.getBlinkRate();

		document = new DefaultSyntaxDocument();

		caretVisible = true;

		colors = SyntaxUtilities.getDefaultSyntaxColors();

		updateFromPreferences(prefs);
	}

	void updateFromPreferences(JeditPreferences prefs)
		throws IllegalArgumentException
	{
		if (prefs == null)
		{
			throw new IllegalArgumentException("Null JEditPreferences passed");
		}
		colors[Token.KEYWORD1] = new Color(prefs.getKeyword1RGB());
		colors[Token.KEYWORD2] = new Color(prefs.getKeyword2RGB());
		colors[Token.KEYWORD3] = new Color(prefs.getKeyword3RGB());
		colors[Token.COLUMN] = new Color(prefs.getColumnRGB());
		colors[Token.TABLE] = new Color(prefs.getTableRGB());
		blockCaret = prefs.isBlockCaretEnabled();
		eolMarkers = prefs.getEOLMarkers();
		bracketHighlight = prefs.getBracketHighlighting();
		lineHighlight = prefs.getCurrentLineHighlighting();
		caretBlinks = prefs.getBlinkCaret();
		caretColor = new Color(prefs.getCaretRGB());
		selectionColor = new Color(prefs.getSelectionRGB());
		lineHighlightColor = new Color(prefs.getLineHighlightRGB());
		eolMarkerColor = new Color(prefs.getEOLMarkerRGB());
		bracketHighlightColor = new Color(prefs.getBracketHighlightRGB());
	}

	void updateControl(JEditorPane textArea)
	{
		final Document doc = textArea.getDocument();
		if (doc instanceof DefaultSyntaxDocument)
		{
			final DefaultSyntaxDocument syntaxDoc = ((DefaultSyntaxDocument)doc);
			syntaxDoc.setColors(colors);
		}

		textArea.setCaret(blockCaret ? new BlockCaret() : _defaultCaret);
		textArea.setCaretColor(caretColor);
		textArea.getCaret().setBlinkRate(caretBlinks ? _defaultCaretBlinkRate : 0);
		textArea.setSelectionColor(selectionColor);	}
}
