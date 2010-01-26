/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.util;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.TinyLookAndFeel;
import de.muntjak.tinylookandfeel.controlpanel.SBChooser;

/**
 * DrawRoutines is a collection of static methods related
 * to drawing.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class DrawRoutines {
	
	static final int[][] checkA = {
		{53, 66, 78, 99, 115, 136, 144, 156, 165, 177, 189},
		{66, 78, 99, 115, 136, 144, 156, 165, 177, 189, 202},
		{78, 99, 0, 0, 0, 0, 0, 0, 0, 202, 210},
		{99, 115, 0, 0, 0, 0, 0, 0, 0, 210, 214},
		{115, 136, 0, 0, 0, 0, 0, 0, 0, 214, 226},
		{136, 144, 0, 0, 0, 0, 0, 0, 0, 226, 230},
		{144, 156, 0, 0, 0, 0, 0, 0, 0, 230, 239},
		{156, 165, 0, 0, 0, 0, 0, 0, 0, 239, 243},
		{165, 177, 0, 0, 0, 0, 0, 0, 0, 243, 247},
		{177, 189, 202, 210, 214, 226, 230, 239, 243, 247, 251},
		{189, 202, 210, 214, 226, 230, 239, 243, 247, 251, 255}
	};
	
	static final int[][] radioA = {
		{0, 0, 78, 99, 115, 136, 144, 156, 165, 0, 0},
		{0, 78, 99, 115, 136, 144, 156, 165, 177, 189, 0},
		{78, 99, 115, 136, 92, 48, 92, 177, 189, 202, 210},
		{99, 115, 136, 0, 0, 0, 0, 0, 202, 210, 214},
		{115, 136, 92, 0, 0, 0, 0, 0, 128, 214, 226},
		{136, 144, 48, 0, 0, 0, 0, 0, 64, 226, 230},
		{144, 156, 92, 0, 0, 0, 0, 0, 128, 230, 239},
		{156, 165, 177, 0, 0, 0, 0, 0, 230, 239, 243},
		{165, 177, 189, 202, 128, 64, 128, 230, 239, 243, 247},
		{0, 189, 202, 210, 214, 226, 230, 239, 243, 247, 0},
		{0, 0, 210, 214, 226, 230, 239, 243, 247, 0, 0}
	};
	
	static GraphicsConfiguration graphicsConfiguration;
	
	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsConfiguration = ge.getDefaultScreenDevice().getDefaultConfiguration();
	}

	public static void drawBorder(Graphics g, Color c, int x, int y, int w, int h) {
		g.setColor(c);
		g.drawRect(x, y, w - 1, h - 1);
	}
	
	public static void drawEditableComboBorder(
		Graphics g, Color c, int x, int y, int w, int h)
	{
		// changed this in 1.3 so the border paints like a
		// rounded border without a right side
		g.setColor(c);
		// rect - no right side
		g.drawLine(x, y + 3, x, h - 4);			// left
		g.drawLine(x + 3, y, w - 1, y);			// top
		g.drawLine(x + 3, h - 1, w - 1, h - 1);	// bottom
		
		// edges verlängerungen 1
		Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 216);
		g.setColor(c2);
		// oben
		g.drawLine(x + 2, y, x + 2, y);
		g.drawLine(x + w - 3, y, x + w - 3, y);
		// links
		g.drawLine(x, y + 2, x, y + 2);
		g.drawLine(x, y + h - 3, x, y + h - 3);
		// unten
		g.drawLine(x + 2, y + h - 1, x + 2, y + h - 1);
		g.drawLine(x + w - 3, y + h - 1, x + w - 3, y + h - 1);
		
		// edges verlängerungen 2
		c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 130);
		g.setColor(c2);
		// oben
		g.drawLine(x + 1, y, x + 1, y);
		// links
		g.drawLine(x, y + 1, x, y + 1);
		g.drawLine(x, y + h - 2, x, y + h - 2);
		// unten
		g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);

		// edges aussen
		c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 24);
		g.setColor(c2);
		// lo
		g.drawLine(x, y, x, y);
		// lu
		g.drawLine(x, y + h - 1, x, y + h - 1);

		// edges innen
		c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 112);
		g.setColor(c2);
		// lo
		g.drawLine(x + 1, y + 1, x + 1, y + 1);
		
		c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 104);
		g.setColor(c2);
		// lu
		g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
	}
	
	public static void drawRoundedBorder(Graphics g, Color c, int x, int y, int w, int h) {
		g.setColor(c);
		// rect
		g.drawLine(x + 3, y, x + w - 4, y);					// top
		g.drawLine(x + 3, y + h - 1, x + w - 4, y + h - 1);	// bottom
		g.drawLine(x, y + 3, x, y + h - 4);					// left
		g.drawLine(x + w - 1, y + 3, x + w - 1, y + h - 4);	// right
		
		// edges verlängerungen 1
		Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 216);
		g.setColor(c2);
		// oben
		g.drawLine(x + 2, y, x + 2, y);
		g.drawLine(x + w - 3, y, x + w - 3, y);
		// links
		g.drawLine(x, y + 2, x, y + 2);
		g.drawLine(x, y + h - 3, x, y + h - 3);
		// unten
		g.drawLine(x + 2, y + h - 1, x + 2, y + h - 1);
		g.drawLine(x + w - 3, y + h - 1, x + w - 3, y + h - 1);
		// rechts
		g.drawLine(x + w - 1, y + 2, x + w - 1, y + 2);
		g.drawLine(x + w - 1, y + h - 3, x + w - 1, y + h - 3);
		
		// edges verlängerungen 2
		c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 130);
		g.setColor(c2);
		// oben
		g.drawLine(x + 1, y, x + 1, y);
		g.drawLine(x + w - 2, y, x + w - 2, y);
		// links
		g.drawLine(x, y + 1, x, y + 1);
		g.drawLine(x, y + h - 2, x, y + h - 2);
		// unten
		g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
		g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
		// rechts
		g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);
		g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);
		
		// edges aussen
		c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 24);
		g.setColor(c2);
		// lo
		g.drawLine(x, y, x, y);
		// ro
		g.drawLine(x + w - 1, y, x + w - 1, y);
		// lu
		g.drawLine(x, y + h - 1, x, y + h - 1);
		// ru
		g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1);

		// edges innen
		c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 112);
		g.setColor(c2);
		// lo
		g.drawLine(x + 1, y + 1, x + 1, y + 1);
		// ro
		g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
		
		c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 104);
		g.setColor(c2);
		// lu
		g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
		// ru
		g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
	}
	
	public static void drawWindowButtonBorder(Graphics g, Color c, int x, int y, int w, int h) {
		g.setColor(c);
		// rect
		g.drawLine(x + 2, y, x + w - 3, y);					// top
		g.drawLine(x + 2, y + h - 1, x + w - 3, y + h - 1);	// bottom
		g.drawLine(x, y + 2, x, y + h - 3);					// left
		g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);	// right

		// edges verlängerungen
		Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 100);
		g.setColor(c2);
		// oben
		g.drawLine(x + 1, y, x + 1, y);
		g.drawLine(x + w - 2, y, x + w - 2, y);
		// links
		g.drawLine(x, y + 1, x, y + 1);
		g.drawLine(x, y + h - 2, x, y + h - 2);
		// unten
		g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
		g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
		// rechts
		g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);
		g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);
		
		// edges innen
		c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 216);
		g.setColor(c2);
		// lo
		g.drawLine(x + 1, y + 1, x + 1, y + 1);
		// ro
		g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
		// lu
		g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
		// ru
		g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
	}
	
	public static synchronized void drawProgressBarBorder(
		Graphics g, Color c, int x, int y, int w, int h)
	{
		g.setColor(c);
		// rect
		g.drawLine(x + 1, y, x + w - 2, y);
		g.drawLine(x + 1, y + h - 1, x + w - 2, y + h - 1);
		g.drawLine(x, y + 1, x, y + h - 2);
		g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 2);

		// edges innen
		// lo
		g.drawLine(x + 1, y + 1, x + 1, y + 1);
		// ro
		g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
		// lu
		g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
		// ru
		g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
	}
	
	/**
	 * changed in 1.3
	 */
	public static void drawRolloverBorder(Graphics g, Color c, int x, int y, int w, int h) {
		// lowest row
		g.setColor(ColorRoutines.darken(c, 10));
		g.drawLine(x + 2, y + h - 2, x + w - 3, y + h - 2);

		// lowest row - 1
		g.setColor(c);
		g.drawLine(x + 1, y + h - 3, x + w - 2, y + h - 3);
		
		// highest row + 1
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 144));
		g.drawLine(x + 2, y + 2, x + w - 3, y + 2);
		
		// highest row
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 64));
		g.drawLine(x + 2, y + 1, x + w - 3, y + 1);
		
		// Was a bug before 1.4.0 (JDK 1.6 only)
		if(h <= 6) return;
		
		// outer left and right
		// both paint a gradient from c.alpha 255 to c.alpha 64
		int inc = (255 - 64) / (h - 5);	// distance to paint
		int val = 64 + inc;
		for(int i = y + 2; i < y + h - 3; i++) {
			g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), val));
			g.drawLine(x + 1, i, x + 1, i);
			g.drawLine(x + w - 2, i, x + w - 2, i);
			val += inc;
		}
		
		// inner left and right
		// both paint a gradient from c.alpha 255 to c.alpha 144
		inc = (255 - 144) / (h - 6);	// distance to paint
		val = 144 + inc;
		
		for(int i = y + 3; i < y + h - 3; i++) {
			g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), val));
			g.drawLine(x + 2, i, x + 2, i);
			g.drawLine(x + w - 3, i, x + w - 3, i);
			val += inc;
		}
	}

	public static void drawRolloverCheckBorder(Graphics g, Color c, int x, int y, int w, int h) {
		g.translate(x, y);
		Color color;
		
		for (int row = 0; row < 11; row++) {
			for (int col = 0; col < 11; col++) {
				if(checkA[row][col] > 0) {
					color = new Color(c.getRed(), c.getGreen(), c.getBlue(), checkA[row][col]);
					g.setColor(color);
					g.drawLine(col + 1, row + 1, col + 1, row + 1);
				}
			}
		}

		g.translate(-x, -y);
	}
	
	public static void drawSelectedXpTabBorder(
		Graphics g, Color c, int x, int y, int w, int h, int tabPlacement)
	{
		Color c2 = ColorRoutines.getAdjustedColor(
			Theme.tabRolloverColor.getColor(), 20, -30);
		g.setColor(c2);
		Color c3 = ColorRoutines.getAverage(Theme.backColor.getColor(), c2);
		
		switch (tabPlacement) {
			case SwingConstants.LEFT:
				h -= 1;
				
				g.drawLine(x, y + 2, x, y + h - 3);
				// edges
				g.drawLine(x + 1, y + 1, x + 1, y + 1);
				g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
				g.drawLine(x + 2, y, x + 2, y);
				g.drawLine(x + 2, y + h - 1, x + 2, y + h - 1);
				
				g.setColor(Theme.tabRolloverColor.getColor());
				g.drawLine(x + 1, y + 2, x + 1, y + h - 3);
				g.drawLine(x + 2, y + 1, x + 2, y + h - 2);
				
				// edges
				g.setColor(c3);				
				// lo
				g.drawLine(x, y + 1, x, y + 1);
				g.drawLine(x + 1, y, x + 1, y);
				// lu
				g.drawLine(x, y + h - 2, x, y + h - 2);
				g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
				
				// seiten
				g.setColor(c);
				// Was bug until 1.4.0 - lines were 1 px too short
				g.drawLine(x + 3, y, x + w - 1, y);
				g.drawLine(x + 3, y + h - 1, x + w - 1, y + h - 1);
				break;
			case SwingConstants.RIGHT:
				h -= 1;
				x -= 2;
				g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);
				// edges
				g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
				g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
				g.drawLine(x + w - 3, y, x + w - 3, y);
				g.drawLine(x + w - 3, y + h - 1, x + w - 3, y + h - 1);
				
				g.setColor(Theme.tabRolloverColor.getColor());
				g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 3);
				g.drawLine(x + w - 3, y + 1, x + w - 3, y + h - 2);
				
				// edges
				g.setColor(c3);	
				// ro
				g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);
				g.drawLine(x + w - 2, y, x + w - 2, y);
				// ru
				g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);
				g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
				
				// seiten
				g.setColor(c);	
				g.drawLine(x, y, x + w - 4, y);
				g.drawLine(x, y + h - 1, x + w - 4, y + h - 1);
				break;
			case SwingConstants.BOTTOM:
				w -= 1;
				y -= 2;
				
				g.drawLine(x + 2, y + h - 1, x + w - 3, y + h - 1);
				// edges
				g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
				g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
				g.drawLine(x, y + h - 3, x, y + h - 3);
				g.drawLine(x + w - 1, y + h - 3, x + w - 1, y + h - 3);
				g.setColor(Theme.tabRolloverColor.getColor());
				g.drawLine(x + 2, y + h - 2, x + w - 3, y + h - 2);
				g.drawLine(x + 1, y + h - 3, x + w - 2, y + h - 3);
				
				// seiten
				g.setColor(c);	
				g.drawLine(x, y, x, y + h - 4);
				g.drawLine(x + w - 1, y, x + w - 1, y + h - 4);
			
				// edges
				g.setColor(c3);
				// lu
				g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
				g.drawLine(x, y + h - 2, x, y + h - 2);
				// ru
				g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
				g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);
				break;
			case SwingConstants.TOP:
			default :
				w -= 1;
				g.drawLine(x + 2, y, x + w - 3, y);
				// edges
				g.drawLine(x + 1, y + 1, x + 1, y + 1);
				g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
				g.drawLine(x, y + 2, x, y + 2);
				g.drawLine(x + w - 1, y + 2, x + w - 1, y + 2);
				g.setColor(Theme.tabRolloverColor.getColor());
				g.drawLine(x + 2, y + 1, x + w - 3, y + 1);
				g.drawLine(x + 1, y + 2, x + w - 2, y + 2);
				
				// edges
				g.setColor(c3);
				// lo
				g.drawLine(x + 1, y, x + 1, y);
				g.drawLine(x, y + 1, x, y + 1);
				// ro
				g.drawLine(x + w - 2, y, x + w - 2, y);
				g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);
				
				// seiten
				g.setColor(c);				
				g.drawLine(x, y + 3, x, y + h - 1);
				g.drawLine(x + w - 1, y + 3, x + w - 1, y + h - 1);
		}
	}
	
	public static void drawXpTabBorder(
		Graphics g, Color c, int x, int y, int w, int h, int tabPlacement)
	{
		Color c2 = null;
		g.setColor(c);
		
		switch (tabPlacement){
			case SwingConstants.LEFT:
				h -= 1;				
				
				g.drawLine(x + 2, y, x + w - 1, y);
				g.drawLine(x + 2, y + h - 1, x + w - 1, y + h - 1);
				g.drawLine(x, y + 2, x, y + h - 3);
			
				// edges
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 56);
				g.setColor(c2);
				// lo
				g.drawLine(x, y, x, y);
				// lu
				g.drawLine(x, y + h - 1, x, y + h - 1);
			
				// edges verlängerungen
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 183);
				g.setColor(c2);
				// oben
				g.drawLine(x + 1, y, x + 1, y);
				// links
				g.drawLine(x, y + 1, x, y + 1);
				g.drawLine(x, y + h - 2, x, y + h - 2);
				// unten
				g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
			
				// edges innen
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 76);
				g.setColor(c2);
				// lo
				g.drawLine(x + 1, y + 1, x + 1, y + 1);
				// lu
				g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
				break;
			case SwingConstants.RIGHT:
				h -= 1;
				x -= 2;
				
				g.drawLine(x, y, x + w - 3, y);
				g.drawLine(x, y + h - 1, x + w - 3, y + h - 1);
				g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);
			
				// edges
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 56);
				g.setColor(c2);
				// ro
				g.drawLine(x + w - 1, y, x + w - 1, y);
				// ru
				g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1);
			
				// edges verlängerungen
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 183);
				g.setColor(c2);
				// oben
				g.drawLine(x + w - 2, y, x + w - 2, y);
				// unten
				g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
				// rechts
				g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);
				g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);
			
				// edges innen
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 76);
				g.setColor(c2);
				// ro
				g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
				// ru
				g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
				break;
			case SwingConstants.BOTTOM:
				w -= 1;
				y -= 2;
				
				g.drawLine(x + 2, y + h - 1, x + w - 3, y + h - 1);
				g.drawLine(x, y, x, y + h - 3);
				g.drawLine(x + w - 1, y, x + w - 1, y + h - 3);
			
				// edges
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 56);
				g.setColor(c2);
				// lu
				g.drawLine(x, y + h - 1, x, y + h - 1);
				// ru
				g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1);
			
				// edges verlängerungen
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 183);
				g.setColor(c2);
				// links
				g.drawLine(x, y + h - 2, x, y + h - 2);
				// unten
				g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
				g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
				// rechts
				g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);
			
				// edges innen
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 76);
				g.setColor(c2);
				// lu
				g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
				// ru
				g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
				break;
			case SwingConstants.TOP:
			default:
				w -= 1;
				g.drawLine(x + 2, y, x + w - 3, y);
				g.drawLine(x, y + 2, x, y + h - 1);
				g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 1);
			
				// edges
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 56);
				g.setColor(c2);
				// lo
				g.drawLine(x, y, x, y);
				// ro
				g.drawLine(x + w - 1, y, x + w - 1, y);
			
				// edges verlängerungen
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 183);
				g.setColor(c2);
				// oben
				g.drawLine(x + 1, y, x + 1, y);
				g.drawLine(x + w - 2, y, x + w - 2, y);
				// links
				g.drawLine(x, y + 1, x, y + 1);
				// rechts
				g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);
			
				// edges innen
				c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 76);
				g.setColor(c2);
				// lo
				g.drawLine(x + 1, y + 1, x + 1, y + 1);
				// ro
				g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
		}
	}

	public static void drawXpRadioRolloverBorder(Graphics g, Color c,
		int x, int y, int w, int h)
	{
		g.translate(x, y);
		Color color;
		
		for (int row = 0; row < 11; row++) {
			for (int col = 0; col < 11; col++) {
				if(radioA[row][col] > 0) {
					color = new Color(c.getRed(), c.getGreen(), c.getBlue(), radioA[row][col]);
					g.setColor(color);
					g.drawLine(col + 1, row + 1, col + 1, row + 1);
				}
			}
		}

		g.translate(-x, -y);
	}
	
	public static void drawXpRadioBorder(Graphics g, Color c, int x, int y, int w, int h) {
		g.setColor(c);
		g.drawLine(x + 6, y, x + 6, y);
		g.drawLine(x + 3, y + 1, x + 3, y + 1);
		g.drawLine(x + 9, y + 1, x + 9, y + 1);
		g.drawLine(x + 1, y + 3, x + 1, y + 3);
		g.drawLine(x + 11, y + 3, x + 11, y + 3);
		g.drawLine(x, y + 6, x, y + 6);
		g.drawLine(x + 12, y + 6, x + 12, y + 6);
		g.drawLine(x + 1, y + 9, x + 1, y + 9);
		g.drawLine(x + 11, y + 9, x + 11, y + 9);
		g.drawLine(x + 3, y + 11, x + 3, y + 11);
		g.drawLine(x + 9, y + 11, x + 9, y + 11);
		g.drawLine(x + 6, y + 12, x + 6, y + 12);
		
		// changed alpha value from 193 to 168 in 1.3.05
		g.setColor(ColorRoutines.getAlphaColor(c, 168));
		g.drawLine(x + 5, y, x + 5, y);
		g.drawLine(x + 7, y, x + 7, y);
		g.drawLine(x + 4, y + 1, x + 4, y + 1);
		g.drawLine(x + 8, y + 1, x + 8, y + 1);
		g.drawLine(x + 2, y + 2, x + 2, y + 2);
		g.drawLine(x + 10, y + 2, x + 10, y + 2);
		g.drawLine(x + 1, y + 4, x + 1, y + 4);
		g.drawLine(x + 11, y + 4, x + 11, y + 4);
		g.drawLine(x, y + 5, x, y + 5);
		g.drawLine(x + 12, y + 5, x + 12, y + 5);
		g.drawLine(x, y + 7, x, y + 7);
		g.drawLine(x + 12, y + 7, x + 12, y + 7);
		g.drawLine(x + 1, y + 8, x + 1, y + 8);
		g.drawLine(x + 11, y + 8, x + 11, y + 8);
		g.drawLine(x + 2, y + 10, x + 2, y + 10);
		g.drawLine(x + 10, y + 10, x + 10, y + 10);
		g.drawLine(x + 4, y + 11, x + 4, y + 11);
		g.drawLine(x + 8, y + 11, x + 8, y + 11);
		g.drawLine(x + 5, y + 12, x + 5, y + 12);
		g.drawLine(x + 7, y + 12, x + 7, y + 12);
		
		g.setColor(ColorRoutines.getAlphaColor(c, 64));
		g.drawLine(x + 4, y, x + 4, y);
		g.drawLine(x + 8, y, x + 8, y);
		g.drawLine(x + 2, y + 1, x + 2, y + 1);
		g.drawLine(x + 2, y + 3, x + 2, y + 3);
		g.drawLine(x + 10, y + 1, x + 10, y + 1);
		g.drawLine(x + 10, y + 3, x + 10, y + 3);
		g.drawLine(x + 5, y + 1, x + 5, y + 1);
		g.drawLine(x + 7, y + 1, x + 7, y + 1);
		g.drawLine(x + 1, y + 2, x + 1, y + 2);
		g.drawLine(x + 1, y + 5, x + 1, y + 5);
		g.drawLine(x + 1, y + 7, x + 1, y + 7);
		g.drawLine(x + 11, y + 2, x + 11, y + 2);
		g.drawLine(x + 3, y + 2, x + 3, y + 2);
		g.drawLine(x + 9, y + 2, x + 9, y + 2);
		g.drawLine(x, y + 4, x, y + 4);
		g.drawLine(x + 12, y + 4, x + 12, y + 4);
		g.drawLine(x, y + 8, x, y + 8);
		g.drawLine(x + 12, y + 8, x + 12, y + 8);
		g.drawLine(x + 2, y + 9, x + 2, y + 9);
		g.drawLine(x + 10, y + 9, x + 10, y + 9);
		g.drawLine(x + 1, y + 10, x + 1, y + 10);
		g.drawLine(x + 11, y + 5, x + 11, y + 5);
		g.drawLine(x + 11, y + 7, x + 11, y + 7);
		g.drawLine(x + 11, y + 10, x + 11, y + 10);
		g.drawLine(x + 3, y + 10, x + 3, y + 10);
		g.drawLine(x + 9, y + 10, x + 9, y + 10);
		g.drawLine(x + 2, y + 11, x + 2, y + 11);
		g.drawLine(x + 10, y + 11, x + 10, y + 11);
		g.drawLine(x + 5, y + 11, x + 5, y + 11);
		g.drawLine(x + 7, y + 11, x + 7, y + 11);
		g.drawLine(x + 4, y + 12, x + 4, y + 12);
		g.drawLine(x + 8, y + 12, x + 8, y + 12);

		// changed alpha value from 43 to 16 in 1.3.05
		g.setColor(ColorRoutines.getAlphaColor(c, 16));
		g.drawLine(x + 3, y, x + 3, y);
		g.drawLine(x + 9, y, x + 9, y);
		g.drawLine(x, y + 3, x, y + 3);
		g.drawLine(x + 12, y + 3, x + 12, y + 3);
		g.drawLine(x, y + 9, x, y + 9);
		g.drawLine(x + 12, y + 9, x + 12, y + 9);
		g.drawLine(x + 3, y + 12, x + 3, y + 12);
		g.drawLine(x + 9, y + 12, x + 9, y + 12);
	}

	public static ImageIcon colorizeIcon(Image img, HSBReference hsbRef) {
		ColorRoutines nc = new ColorRoutines(hsbRef);
		
//		long t = System.nanoTime();
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		
		BufferedImage bufferedImg =
			graphicsConfiguration.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		
		int[] pixels = new int[w * h];
		PixelGrabber grabber = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
		
		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("PixelGrabber interrupted waiting for pixels");
		}

		if((grabber.getStatus() & ImageObserver.ABORT) != 0) {
			System.err.println("Image fetch aborted or errored.");
		}
		else {	
			for(int y = 0; y < h; y++) {
				for(int x = 0; x < w; x++) {
					bufferedImg.setRGB(x, y, colorizePixel(pixels[y * w + x], nc));
				}
			}
			
//			t = System.nanoTime() - t;
//			System.out.println(w + " x " + h + " : " + t + " Nano-sec. (" +
//				(double)(t / 1000000000.0) + ")");
		}

		return new ImageIcon(bufferedImg);
	}
	
	private static int colorizePixel(int px, ColorRoutines nc) {
		int a = (px >> 24) & 0xff;
		if(a == 0) return px;
		
		int r = (px >> 16) & 0xff;
		int g = (px >> 8) & 0xff;
		int b = px & 0xff;
		
		return nc.colorize(r, g, b, a);
	}
}
