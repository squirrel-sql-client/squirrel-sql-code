package org.gjt.sp.jedit.syntax;

/*
 * JEditTextArea.java - jEdit's text component
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */
import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

public class JEditTextArea extends JEditorPane
{
	private MyCaretListener _lis;
	private int _bracketPosition = -1;
	private int _bracketLine = -1;

	public JEditTextArea()
	{
		super();
	}

	/**
	 * @see javax.swing.JComponent#addNotify()
	 */
	public void addNotify()
	{
		super.addNotify();
		_lis = new MyCaretListener();
		addCaretListener(_lis);
	}

	/**
	 * @see javax.swing.text.JTextComponent#removeNotify()
	 */
	public void removeNotify()
	{
		super.removeNotify();

		if (_lis != null)
		{
			removeCaretListener(_lis);
			_lis = null;
		}
	}

	/**
	 * Returns the document this text area is editing.
	 */
	public final SyntaxDocument getSyntaxDocument()
	{
		return (SyntaxDocument)getDocument();
	}

	/**
	 * Returns the document's token marker. Equivalent to calling
	 * <code>getSyntaxDocument().getTokenMarker()</code>.
	 */
	public final TokenMarker getTokenMarker()
	{
		return getSyntaxDocument().getTokenMarker();
	}

	/**
	 * Returns the selection start line.
	 */
	public final int getSelectionStartLine()
	{
		final DefaultSyntaxDocument doc = (DefaultSyntaxDocument)getDocument();

		return doc.getDefaultRootElement().getElementIndex(getSelectionStart());
	}

	/**
	 * Returns the selection end line.
	 */
	public final int getSelectionEndLine()
	{
		final DefaultSyntaxDocument doc = (DefaultSyntaxDocument)getDocument();

		return doc.getDefaultRootElement().getElementIndex(getSelectionEnd());
	}

	/**
	 * Returns the line for the passed offset.
	 */
	public final int getLineOfOffset(int offset)
	{
		final DefaultSyntaxDocument doc = (DefaultSyntaxDocument)getDocument();

		return doc.getDefaultRootElement().getElementIndex(offset);
	}

	/**
	 * Returns the start offset of the specified line.
	 * @param line The line
	 * @return The start offset of the specified line, or -1 if the line is
	 * invalid
	 */
	public int getLineStartOffset(int line)
	{
		Element lineElement = getDocument().getDefaultRootElement().getElement(line);

		if (lineElement == null)
		{
			return -1;
		}
		else
		{
			return lineElement.getStartOffset();
		}
	}

	/**
	 * Returns the position of the highlighted bracket (the bracket
	 * matching the one before the caret)
	 */
	public final int getBracketPosition()
	{
		return _bracketPosition;
	}

	/**
	 * Converts an offset in a line into an x co-ordinate.
	 * @param line The line
	 * @param offset The offset, from the start of the line
	 */
	public final int offsetToX(int line, int offset)
	{
		Element elem = getDocument().getDefaultRootElement().getElement(line);

		try
		{
			return modelToView(elem.getStartOffset() + offset).x;
		}
		catch (BadLocationException ex)
		{
			ex.printStackTrace();

			return -1;
		}
	}

	/**
	 * Converts an offset in a line into an y co-ordinate.
	 * @param line The line
	 * @param offset The offset, from the start of the line
	 */
	public final int offsetToY(int line, int offset)
	{
		Element elem = getDocument().getDefaultRootElement().getElement(line);

		try
		{
			return modelToView(elem.getStartOffset() + offset).y;
		}
		catch (BadLocationException ex)
		{
			ex.printStackTrace();

			return -1;
		}
	}

	/**
	 * Returns the line of the highlighted bracket (the bracket
	 * matching the one before the caret)
	 */
	public final int getBracketLine()
	{
		return _bracketLine;
	}

	protected void updateBracketHighlight(int newCaretPosition)
	{
		if (newCaretPosition == 0)
		{
			_bracketPosition = _bracketLine = -1;

			return;
		}

		try
		{
			int offset = TextUtilities.findMatchingBracket(getDocument(),
					newCaretPosition);

			if (offset != -1)
			{
				_bracketLine = getLineOfOffset(offset);
				_bracketPosition = offset - getLineStartOffset(_bracketLine);

				return;
			}
		}
		catch (BadLocationException bl)
		{
			bl.printStackTrace();
		}

		_bracketLine = _bracketPosition = -1;
	}

	private final class MyCaretListener implements CaretListener
	{
		public void caretUpdate(CaretEvent evt)
		{
			updateBracketHighlight(evt.getDot());

			// This is to force the redraw of the current line
			// so that if Highlight current line is specified then
			// the highlighting is done.
			repaint();
		}
	}
}
