package org.gjt.sp.jedit.syntax;

/*
 * SyntaxStyle.java - A simple text style class
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;


/**
 * A simple text style class. It can specify the color, italic flag,
 * and bold flag of a run of text.
 * @author Slava Pestov
 * @version $Id: SyntaxStyle.java,v 1.4 2003-03-25 03:28:34 colbell Exp $
 */
public class SyntaxStyle
{
	// private members
	private Color textColor;
	private Color backgroundColor;
	private boolean italic;
	private boolean bold;
	private Font lastFont;
	private Font lastStyledFont;
	private FontMetrics fontMetrics;

	/**
	 * Default ctor.
	 */
	public SyntaxStyle()
	{
		this(Color.black, Color.white, false, false);
	}

	/**
	 * Copy ctor.
	 * @param base    Object to copy properties from.
	 */
	public SyntaxStyle(SyntaxStyle base)
	{
		this(base.getTextColor(), base.getBackgroundColor(), base.isItalic(),
			base.isBold());
	}

	/**
	 * Creates a new SyntaxStyle.
	 * @param color The text color
	 * @param italic True if the text should be italics
	 * @param bold True if the text should be bold
	 */
	public SyntaxStyle(Color color, boolean italic, boolean bold)
	{
		this(color, Color.white, italic, bold);
	}

	/**
	 * Creates a new SyntaxStyle.
	 * @param color The text color
	 * @param background color The background color
	 * @param italic True if the text should be italics
	 * @param bold True if the text should be bold
	 */
	public SyntaxStyle(Color color, Color backgroundColor, boolean italic,
		boolean bold)
	{
		this.textColor = color;
		this.backgroundColor = backgroundColor;
		this.italic = italic;
		this.bold = bold;
	}

	/**
	 * Returns the color specified in this style.
	 */
	public Color getTextColor()
	{
		return textColor;
	}

	/**
	 * Returns the background color specified in this style.
	 */
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	/**
	 * Returns true if no font styles are enabled.
	 */
	public boolean isPlain()
	{
		return !(bold || italic);
	}

	/**
	 * Returns true if italics is enabled for this style.
	 */
	public boolean isItalic()
	{
		return italic;
	}

	public void setItalic(boolean value)
	{
		italic = value;
	}

	/**
	 * Returns true if boldface is enabled for this style.
	 */
	public boolean isBold()
	{
		return bold;
	}

	public void setBold(boolean value)
	{
		bold = value;
	}

	public int getTextRGB()
	{
		return (textColor != null) ? textColor.getRGB() : 0;
	}

	public void setTextRGB(int value)
	{
		textColor = new Color(value);
	}

	public int getBackgroundRGB()
	{
		return (backgroundColor != null) ? backgroundColor.getRGB() : 0;
	}

	public void setBackgroundRGB(int value)
	{
		backgroundColor = new Color(value);
	}

	/**
	 * Returns the specified font, but with the style's bold and
	 * italic flags applied.
	 */
	public Font getStyledFont(Font font)
	{
		if (font == null)
		{
			throw new NullPointerException("font param must not" + " be null");
		}

		if (font.equals(lastFont))
		{
			return lastStyledFont;
		}

		lastFont = font;
		lastStyledFont = new Font(font.getFamily(),
				(bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0),
				font.getSize());

		return lastStyledFont;
	}

	public Font createStyledFont(Font font)
	{
		if (font == null)
		{
			throw new NullPointerException("font param must not" + " be null");
		}

		return new Font(font.getFamily(),
			(bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0), font.getSize());
	}

	/**
	 * Returns the font metrics for the styled font.
	 */
	public FontMetrics getFontMetrics(Font font)
	{
		if (font == null)
		{
			throw new NullPointerException("font param must not" + " be null");
		}

		if (font.equals(lastFont) && (fontMetrics != null))
		{
			return fontMetrics;
		}

		lastFont = font;
		lastStyledFont = new Font(font.getFamily(),
				(bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0),
				font.getSize());
		fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(lastStyledFont);

		return fontMetrics;
	}

	/**
	 * Sets the foreground color and font of the specified graphics
	 * context to that specified in this style.
	 * @param gfx The graphics context
	 * @param font The font to add the styles to
	 */
	public void setGraphicsFlags(Graphics gfx, Font font)
	{
		Font _font = getStyledFont(font);
		gfx.setFont(_font);
		gfx.setColor(textColor);
	}

	/**
	 * Returns a string representation of this object.
	 */
	public String toString()
	{
		return getClass().getName() + "[color=" + textColor +
		(italic ? ",italic" : "") + (bold ? ",bold" : "") + "]";
	}

	/** Property names for this javaBean. */
	public interface IPropertyNames
	{
		String BACKGROUND_RGB = "backgroundRGB";
		String TEXT_RGB = "textRGB";
		String ITALIC = "italic";
		String BOLD = "bold";
	}
}
