package net.sourceforge.squirrel_sql.plugins.jedit;
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
import java.awt.Color;

import javax.swing.JEditorPane;
import javax.swing.text.Caret;
import javax.swing.text.Document;

import org.gjt.sp.jedit.syntax.DefaultSyntaxDocument;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.SyntaxUtilities;
import org.gjt.sp.jedit.syntax.TextAreaDefaults;
import org.gjt.sp.jedit.syntax.Token;

class JeditTextAreaDefaults extends TextAreaDefaults
{
	/** Default blink rate for the caret. */
	private int _defaultCaretBlinkRate;

	/** Default caret. */
	private Caret _defaultCaret;

	/** If <TT>true</TT> then show line numbers. */
	private boolean _showlineNumbers = true;

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

		styles = SyntaxUtilities.getDefaultSyntaxStyles();

		updateFromPreferences(prefs);
	}

	void updateFromPreferences(JeditPreferences prefs)
		throws IllegalArgumentException
	{
		if (prefs == null)
		{
			throw new IllegalArgumentException("Null JEditPreferences passed");
		}

		styles[Token.KEYWORD] = prefs.getKeyword1Style();
		styles[Token.DATA_TYPE] = prefs.getKeyword2Style();
		styles[Token.FUNCTION] = prefs.getKeyword3Style();
		styles[Token.TABLE] = prefs.getTableStyle();
		styles[Token.COLUMN] = prefs.getColumnStyle();
		styles[Token.COMMENT1] = prefs.getCommentStyle();
		styles[Token.COMMENT2] = styles[Token.COMMENT1];
		styles[Token.COMMENT3] = styles[Token.COMMENT1];
		styles[Token.LITERAL1] = prefs.getLiteralStyle();
		styles[Token.LITERAL2] = styles[Token.LITERAL1];
		styles[Token.LABEL] = prefs.getLabelStyle();
		styles[Token.OPERATOR] = prefs.getOperatorStyle();
		styles[Token.NULL] = prefs.getOtherStyle();

		blockCaret = prefs.isBlockCaretEnabled();
		eolMarkers = prefs.getEOLMarkers();
		bracketHighlight = prefs.getBracketHighlighting();
		lineHighlight = prefs.getCurrentLineHighlighting();
		caretBlinks = prefs.getBlinkCaret();
		_showlineNumbers = prefs.getShowLineNumbers();

		caretColor = new Color(prefs.getCaretRGB());
		selectionColor = new Color(prefs.getSelectionRGB());
		lineHighlightColor = new Color(prefs.getCurrentLineHighlightRGB());
		eolMarkerColor = new Color(prefs.getEOLMarkerRGB());
		bracketHighlightColor = new Color(prefs.getBracketHighlightRGB());
		lineNumberColor = new Color(prefs.getLineNumberRGB());
	}

	void updateControl(JEditorPane textArea)
	{
		final Document doc = textArea.getDocument();

		if (doc instanceof DefaultSyntaxDocument)
		{
			final DefaultSyntaxDocument syntaxDoc = ((DefaultSyntaxDocument)doc);
			syntaxDoc.setStyles(styles);
		}

		textArea.setCaret(blockCaret ? new BlockCaret() : _defaultCaret);
		textArea.setCaretColor(caretColor);
		textArea.getCaret().setBlinkRate(caretBlinks ? _defaultCaretBlinkRate : 0);
		textArea.setSelectionColor(selectionColor);

		textArea.setFont(styles[Token.NULL].createStyledFont(textArea.getFont()));
		textArea.revalidate();
	}
}
