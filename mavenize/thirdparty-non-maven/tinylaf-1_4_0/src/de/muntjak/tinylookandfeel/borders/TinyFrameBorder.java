/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.borders;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.TinyLookAndFeel;
import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * TinyFrameBorder
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyFrameBorder extends AbstractBorder implements UIResource {
	
	public static final int FRAME_BORDER_WIDTH = 3;
	public static final int FRAME_TITLE_HEIGHT = 29;
	public static final int FRAME_INTERNAL_TITLE_HEIGHT = 25;
	public static final int FRAME_PALETTE_TITLE_HEIGHT = 21;

	// Note: These are set at ControlPanel.DisabledFramePanel.paintComponent
	public static Color buttonUpperDisabledColor, buttonLowerDisabledColor;
	
	// Reusable rectangle for capturing screen rects
	private static final Rectangle theRect = new Rectangle();
	
	private static TinyFrameBorder onlyInstance;
	private Window window;
	private int titleHeight;
    private boolean isActive;

    public static TinyFrameBorder getInstance() {
    	if(onlyInstance == null) {
    		onlyInstance = new TinyFrameBorder();
    	}
    	
    	return onlyInstance;
    }

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
//		System.out.println("paintBorder(...), c=" + c);
		window = SwingUtilities.getWindowAncestor(c);
		isActive = window.isActive();
		
		if(window instanceof JFrame) {
			titleHeight = FRAME_TITLE_HEIGHT;
		}
		else if(window instanceof JDialog) {
			titleHeight = FRAME_TITLE_HEIGHT;
		}
		else {
			titleHeight = FRAME_INTERNAL_TITLE_HEIGHT;
		}

		if(isActive) {
    		g.setColor(Theme.frameBorderColor.getColor());
    	}
    	else {
    		g.setColor(Theme.frameBorderDisabledColor.getColor());
    	}
    	
		drawXpBorder(g, x, y, w, h);
	}

	private void drawXpBorder(Graphics g, int x, int y, int w, int h) {
		// left
		g.drawLine(x, y + 6, x, y + h - 1);
		g.drawLine(x + 2, y + titleHeight, x + 2, y + h - 3);
		// right
		g.drawLine(x + w - 1, y + 6, x + w - 1, y + h - 1);
		g.drawLine(x + w - 3, y + titleHeight, x + w - 3, y + h - 3);
		// bottom
		g.drawLine(x + 2, y + h - 3, x + w - 3, y + h - 3);
		g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

		if(TinyLookAndFeel.ROBOT != null) {
			// Copy a 4 x 4 rect to left and right corners
			int wx = window.getLocationOnScreen().x - 4;
			int wy = window.getLocationOnScreen().y;

			theRect.setBounds(wx, wy, 4, 4);
			g.drawImage(TinyLookAndFeel.ROBOT.createScreenCapture(theRect), x, y, null);
			
			wx = window.getLocationOnScreen().x + window.getWidth() + 1;
			theRect.setBounds(wx, wy, 4, 4);
			g.drawImage(TinyLookAndFeel.ROBOT.createScreenCapture(theRect), x + w - 4, y, null);
		}
		else {
			g.setColor(Theme.backColor.getColor());
			g.fillRect(0, 0, w, 3);
		}
		
		if(isActive) {
    		g.setColor(Theme.frameCaptionColor.getColor());
    	}
    	else {
    		g.setColor(Theme.frameCaptionDisabledColor.getColor());
    	}
		
    	// left
		g.drawLine(x + 1, y + titleHeight, x + 1, y + h - 2);
		// right
		g.drawLine(x + w - 2, y + titleHeight, x + w - 2, y + h - 2);
		// bottom
		g.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
		
		Color c = null;
		if(isActive) {
    		c = Theme.frameBorderColor.getColor();
    	}
    	else {
    		c = Theme.frameBorderDisabledColor.getColor();
    	}

    	g.setColor(ColorRoutines.getAlphaColor(c, 82));
    	g.drawLine(x, y + 3, x, y + 3);
    	g.drawLine(x + w - 1, y + 3, x + w - 1, y + 3);
    	g.setColor(ColorRoutines.getAlphaColor(c, 156));
    	g.drawLine(x, y + 4, x, y + 4);
    	g.drawLine(x + w - 1, y + 4, x + w - 1, y + 4);
    	g.setColor(ColorRoutines.getAlphaColor(c, 215));
    	g.drawLine(x, y + 5, x, y + 5);
    	g.drawLine(x + w - 1, y + 5, x + w - 1, y + 5);
    	
    	// new in 1.4.0: Paint pixels x=1 and x=2 and x=w-2 and x=w-3
    	if(isActive) {
    		c = Theme.frameCaptionColor.getColor();
    	}
    	else {
    		c = Theme.frameCaptionDisabledColor.getColor();
    	}
    	
    	int spread1 = Theme.frameSpreadDarkDisabled.getValue();
		int spread2 = Theme.frameSpreadLightDisabled.getValue();
		Color borderColor = null;

		if(isActive) {
			borderColor = Theme.frameBorderColor.getColor();
    		spread1 = Theme.frameSpreadDark.getValue();
    		spread2 = Theme.frameSpreadLight.getValue();
    	}
		
    	int y2 = 1;
    	// paint semi-transparent pixels
//    	 2
		Color c2 = ColorRoutines.darken(c, 4 * spread1);
		// blend
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		g.setColor(ColorRoutines.getAlphaColor(c2, 23));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
//    	 3
		c2 = ColorRoutines.darken(c, 6 * spread1);
		g.setColor(c2);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		// blend		
		g.setColor(ColorRoutines.getAlphaColor(c2, 139));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
//    	 4
		// darker border
		g.setColor(c);
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);		
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
//    	 5
		// darker border
		g.setColor(ColorRoutines.darken(c, 6 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		y2 ++;
		
		// paint solid pixels
//		 6
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		// lighten little
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;
// 7 - 8
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.fillRect(x + 1, y2, 2, 2);
		g.fillRect(x + w - 3, y2, 2, 2);
		y2 += 2;
// 9 - 12
		g.setColor(ColorRoutines.darken(c, 3 * spread1));
		g.fillRect(x + 1, y2, 2, 4);
		g.fillRect(x + w - 3, y2, 2, 4);
		y2 += 4;
// 13 - 15
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.fillRect(x + 1, y2, 2, 3);
		g.fillRect(x + w - 3, y2, 2, 3);
		y2 += 3;
// 16 - 17
		g.setColor(ColorRoutines.darken(c, 1 * spread1));
		g.fillRect(x + 1, y2, 2, 2);
		g.fillRect(x + w - 3, y2, 2, 2);
		y2 += 2;
// 18 - 19
		g.setColor(c);
		g.fillRect(x + 1, y2, 2, 2);
		g.fillRect(x + w - 3, y2, 2, 2);
		y2 += 2;
// 20...
		g.setColor(ColorRoutines.lighten(c, 2 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 6 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 8 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 9 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
		y2 ++;
// 27
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
		y2 ++;
// 28
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
		y2 ++;
// 29		
		if(isActive) {
    		g.setColor(Theme.frameLightColor.getColor());
    	}
    	else {
    		g.setColor(Theme.frameLightDisabledColor.getColor());
    	}
		
		g.drawLine(x + 1, y2, x + 2, y2);
		g.drawLine(x + w - 2, y2, x + w - 3, y2);
	}

    /**
     * 
     * @see javax.swing.border.Border#getBorderInsets(Component)
     */
	public Insets getBorderInsets(Component c) {
		Window w = SwingUtilities.getWindowAncestor(c);
		
		if(w != null && (w instanceof Frame)) {
			Frame f = (Frame)w;

			// if the frame is maximized, the border should not be visible
			if(f.getExtendedState() == (f.getExtendedState() | Frame.MAXIMIZED_BOTH)) {
				return new Insets(0, 0, 0, 0);
			}
		}
		
		return new Insets(0,
			FRAME_BORDER_WIDTH,
			FRAME_BORDER_WIDTH,
			FRAME_BORDER_WIDTH);
	}
}
