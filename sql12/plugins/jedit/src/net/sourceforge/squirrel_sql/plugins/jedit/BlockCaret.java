package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * Block caret for edit area.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class BlockCaret extends DefaultCaret
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(BlockCaret.class);

	/** Width of caret. */
	private int _caretWidth = -1;

	/**
	 * Paint this caret.
	 *
	 * @param    g    Graphics environment to paint into.
	 */
	public void paint(Graphics g)
	{
		if (isVisible())
		{
			try
			{
				final JTextComponent textArea = getComponent();
				final int dot = getDot();
				final FontMetrics fm = g.getFontMetrics();
				_caretWidth = fm.charWidth('w');

				final Rectangle rc = textArea.modelToView(dot);
				g.setColor(textArea.getCaretColor());
				g.drawRect(rc.x, rc.y, _caretWidth, rc.height - 1);
			}
			catch (BadLocationException ex)
			{
				s_log.error("BadLocationException when drawing caret", ex);
			}
		}
	}

	protected synchronized void damage(Rectangle rc)
	{
		if (rc != null)
		{
			x = rc.x;
			y = rc.y;
			height = rc.height;
			width = _caretWidth + 1;
			repaint();
		}
	}
}
