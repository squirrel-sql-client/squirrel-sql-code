/*
 * SyntaxView.java - jEdit's own Swing view implementation
 * Copyright (C) 1998, 1999 Slava Pestov
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */

package org.gjt.sp.jedit.syntax;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

/**
 * A Swing view implementation that colorizes lines of a
 * <code>SyntaxDocument</code> using a <code>TokenMarker</code>.<p>
 *
 * This class should not be used directly; a <code>SyntaxEditorKit</code>
 * should be used instead.
 *
 * @author Slava Pestov
 * @version $Id: SyntaxView.java,v 1.3 2003-03-04 11:46:02 colbell Exp $
 */
public class SyntaxView extends PlainView
{
	/**
	 * Creates a new <code>SyntaxView</code> for painting the specified
	 * element.
	 * @param elem The element
	 */
	public SyntaxView(Element elem)
	{
		super(elem);
		line = new Segment();
		newRect = new Rectangle();
	}
	
	/**
	 * Paints the specified line.
	 * <p>
	 * This method performs the following:
	 * <ul>
	 * <li>Gets the token marker and color table from the current document,
	 * typecast to a <code>SyntaxDocument</code>.
	 * <li>Tokenizes the required line by calling the
	 * <code>markTokens()</code> method of the token marker.
	 * <li>Paints each token, obtaining the color by looking up the
	 * the <code>Token.id</code> value in the color table.
	 * </ul>
	 * If either the document doesn't implement
	 * <code>SyntaxDocument</code>, or if the returned token marker is
	 * null, the line will be painted with no colorization.
	 *
	 * @param lineIndex The line number
	 * @param g The graphics context
	 * @param x The x co-ordinate where the line should be painted
	 * @param y The y co-ordinate where the line should be painted
	 */
	public void drawLine(int lineIndex, Graphics g, int x, int y)
	{
		SyntaxDocument document = (SyntaxDocument)getDocument();

		TokenMarker tokenMarker = document.getTokenMarker();
		TextAreaDefaults taDefaults = document.getTextAreaDefaults();

		FontMetrics metrics = g.getFontMetrics();
		Color def = getDefaultColor();

		try
		{
			Element lineElement = getElement()
				.getElement(lineIndex);
			int start = lineElement.getStartOffset();
			int end = lineElement.getEndOffset();

			document.getText(start,end - (start + 1),line);

			if(tokenMarker == null)
			{
				paintPlainLine(line,lineIndex,x,y,g,
					document,def, taDefaults);
			}
			else
			{
				paintSyntaxLine(line,lineIndex,x,y,g,
					document,tokenMarker,def, taDefaults);

				if(tokenMarker.isNextLineRequested())
					forceRepaint(metrics,x,y);
			}
		}
		catch(BadLocationException bl)
		{
			// shouldn't happen
			bl.printStackTrace();
		}
	}

	// protected members
	protected Color getDefaultColor()
	{
		return getContainer().getForeground();
	}

	// private members
	private Segment line;
	private Rectangle newRect;

	private void paintPlainLine(Segment line, int lineIndex, int x, int y,
		Graphics g, SyntaxDocument document, Color def, TextAreaDefaults taDefaults)
	{
		paintHighlight(g, lineIndex, y, taDefaults);

		g.setColor(def);
		Utilities.drawTabbedText(line,x,y,g,this,0);

		if (taDefaults.eolMarkers)
		{
			g.setColor(taDefaults.eolMarkerColor);
			g.drawString(".",x,y);
		}
	}

	private void paintSyntaxLine(Segment line, int lineIndex, int x, int y,
		Graphics g, SyntaxDocument document, TokenMarker tokenMarker,
		Color def, TextAreaDefaults taDefaults)
	{
//		Color[] colors = document.getColors();
//		Token tokens = tokenMarker.markTokens(line,lineIndex);
//
//		paintHighlight(g, lineIndex, y, taDefaults);
//
//		int offset = 0;
//		for(;;)
//		{
//			byte id = tokens.id;
//			if(id == Token.END)
//				break;
//
//			int length = tokens.length;
//			Color color;
//			if(id == Token.NULL)
//				color = def;
//			else
//				color = colors[id];
//			g.setColor(color == null ? def : color);
//
//			line.count = length;
//			x = Utilities.drawTabbedText(line,x,y,g,this,offset);
//			line.offset += length;
//			offset += length;
//
//			tokens = tokens.next;
//		}
//
//		if (taDefaults.eolMarkers)
//		{
//			g.setColor(taDefaults.eolMarkerColor);
//			g.drawString(".",x,y);
//		}
	}

	/** Stupid hack that repaints from y to the end of the text component */
	private void forceRepaint(FontMetrics metrics, int x, int y)
	{
		Container host = getContainer();
		Dimension size = host.getSize();
		/**
		 * We repaint the next line only, instead of the
		 * entire viewscreen, since PlainView doesn't (yet)
		 * collapse multiple repaint requests.
		 */
		host.repaint(x,y,size.width - x,metrics.getHeight()
			+ metrics.getMaxAscent());
	}


	protected void paintHighlight(Graphics gfx, int line, int y,
									TextAreaDefaults taDefaults)
	{
		final JEditTextArea textArea = (JEditTextArea)getContainer();

		if(line >= textArea.getSelectionStartLine()
			&& line <= textArea.getSelectionEndLine())
			paintLineHighlight(gfx,line,y, taDefaults);

		if(taDefaults.bracketHighlight && line == textArea.getBracketLine())
			paintBracketHighlight(gfx,line,y, taDefaults);
	}

	protected void paintLineHighlight(Graphics gfx, int line, int y,
										TextAreaDefaults taDefaults)
	{
		final JEditTextArea textArea = (JEditTextArea)getContainer();
		FontMetrics fm = gfx.getFontMetrics();

		int height = fm.getHeight();
		y += fm.getLeading() + fm.getMaxDescent();

		int selectionStart = textArea.getSelectionStart();
		int selectionEnd = textArea.getSelectionEnd();

		if(selectionStart == selectionEnd)
		{
			if (taDefaults.lineHighlight)
			{
				gfx.setColor(taDefaults.lineHighlightColor);
				gfx.fillRect(0,y-height,textArea.getWidth(),height);
			}
		}
	}

	protected void paintBracketHighlight(Graphics gfx, int line, int y,
											TextAreaDefaults taDefaults)
	{
		final JEditTextArea textArea = (JEditTextArea)getContainer();
		FontMetrics fm = gfx.getFontMetrics();
		int position = textArea.getBracketPosition();
		if(position == -1)
			return;
//		y += fm.getLeading() + fm.getMaxDescent();
		int xpos = textArea.offsetToX(line,position);
		int ypos = textArea.offsetToY(line,position);
		gfx.setColor(taDefaults.bracketHighlightColor);
		// Hack!!! Since there is no fast way to get the character
		// from the bracket matching routine, we use ( since all
		// brackets probably have the same width anyway
//		gfx.drawRect(x,y,fm.charWidth('(') - 1, fm.getHeight() - 1);
		gfx.drawRect(xpos,ypos,fm.charWidth('(') - 1, fm.getHeight() - 1);
	}



}

/*
 * ChangeLog:
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2002/12/21 00:34:18  colbell
 * Add syntax styles
 *
 * Revision 1.1  2002/12/06 22:50:19  colbell
 * New jedit syntax control files
 *
 * Revision 1.1  2000/01/12 03:18:00  bruce
 *
 * Addition of Syntax Colour Highlighting Package to CVS tree.  This is LGPL code used in the Moe Editor to provide syntax highlighting.
 *
 * Revision 1.23  1999/06/05 00:22:58  sp
 * LGPL'd syntax package
 *
 * Revision 1.22  1999/05/28 02:00:25  sp
 * SyntaxView bug fix, faq update, MiscUtilities.isURL() method added
 *
 * Revision 1.21  1999/05/02 00:07:21  sp
 * Syntax system tweaks, console bugfix for Swing 1.1.1
 *
 * Revision 1.20  1999/05/01 02:21:12  sp
 * 1.6pre4
 *
 * Revision 1.19  1999/05/01 00:55:11  sp
 * Option pane updates (new, easier API), syntax colorizing updates
 *
 * Revision 1.18  1999/04/30 23:20:38  sp
 * Improved colorization of multiline tokens
 *
 * Revision 1.17  1999/04/19 05:38:20  sp
 * Syntax API changes
 *
 * Revision 1.16  1999/03/13 08:50:39  sp
 * Syntax colorizing updates and cleanups, general code reorganizations
 *
 * Revision 1.15  1999/03/12 23:51:00  sp
 * Console updates, uncomment removed cos it's too buggy, cvs log tags added
 *
 */
