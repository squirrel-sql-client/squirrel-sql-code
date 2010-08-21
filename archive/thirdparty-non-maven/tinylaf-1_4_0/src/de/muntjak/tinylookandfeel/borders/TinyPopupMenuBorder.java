/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.borders;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.TinyLookAndFeel;
import de.muntjak.tinylookandfeel.TinyPopupFactory;

/**
 * TinyPopupMenuBorder
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyPopupMenuBorder extends AbstractBorder implements UIResource {
	
	public static final int SHADOW_SIZE = 5;
	
	private static final Insets INSETS_NO_SHADOW = new Insets(2, 2, 2, 2);
	private static final Insets INSETS_SHADOW_LEFT_TO_RIGHT = new Insets(2, 2, 7, 7);
	private static final Insets INSETS_SHADOW_RIGHT_TO_LEFT = new Insets(2, 7, 7, 2);
	
	public static final Image LEFT_TO_RIGHT_SHADOW_MASK =
		TinyLookAndFeel.loadIcon("leftToRightShadow.png").getImage();
	public static final Image RIGHT_TO_LEFT_SHADOW_MASK =
		TinyLookAndFeel.loadIcon("rightToLeftShadow.png").getImage();

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		boolean hasShadow = Boolean.TRUE.equals(
			((JComponent)c).getClientProperty(TinyPopupFactory.SHADOW_POPUP_KEY));
		boolean isLeftToRight = isOrientationLeftToRight(c);

		g.translate(x, y);
		
		int bx = 0;
		int bw = w;
		int bh = h;

		if(hasShadow) {
			if(isLeftToRight) {
				bw -= SHADOW_SIZE;
				bh -= SHADOW_SIZE;
				
				// Draw background images (unscaled)
				BufferedImage img = (BufferedImage)((JComponent)c).getClientProperty(TinyPopupFactory.VERTICAL_IMAGE_KEY);
				g.drawImage(img, bw, 0, c);
				
				img = (BufferedImage)((JComponent)c).getClientProperty(TinyPopupFactory.HORIZONTAL_IMAGE_KEY);
				g.drawImage(img, 0, bh, c);
			}
			else {
				bh -= SHADOW_SIZE;
				bx = SHADOW_SIZE;
				
				// Draw background images (unscaled)
				BufferedImage img = (BufferedImage)((JComponent)c).getClientProperty(TinyPopupFactory.VERTICAL_IMAGE_KEY);
				
				if(img != null) g.drawImage(img, 0, 0, c);
				
				img = (BufferedImage)((JComponent)c).getClientProperty(TinyPopupFactory.HORIZONTAL_IMAGE_KEY);
				
				if(img != null) g.drawImage(img, 0, bh, c);
			}
		}
		
		// Note: With right-to-left orientation, we flip
		// inner highlight and inner shadow horizontally,
		// BUT this works only with shadow popups (because
		// else we have no information about the popup's
		// orientation)
		
		// Inner highlight
		g.setColor(Theme.menuInnerHilightColor.getColor());
		g.drawLine(bx + 1, 1, bw - 3, 1);
		
		if(isLeftToRight) {
			g.drawLine(bx + 1, 1, bx + 1, bh - 3);
		}
		else {
			g.drawLine(bw - 2, 1, bw - 2, bh - 2);
		}

		// Inner shadow
		g.setColor(Theme.menuInnerShadowColor.getColor());
		if(isLeftToRight) {
			g.drawLine(bw - 2, 1, bw - 2, bh - 2);
		}
		else {
			g.drawLine(bx + 1, 1, bx + 1, bh - 2);
		}
		g.drawLine(bx + 1, bh - 2, bw - 2, bh - 2);

		// Outer highlight
		g.setColor(Theme.menuOuterHilightColor.getColor());
		g.drawLine(bx, 0, bw - 2, 0);
		g.drawLine(bx, 0, bx, bh - 1);

		// Outer shadow
		g.setColor(Theme.menuOuterShadowColor.getColor());
		g.drawLine(bw - 1, 0, bw - 1, bh - 1);
		g.drawLine(bx, bh - 1, bw - 1, bh - 1);

		if(hasShadow) {
			// paint shadows
			if(isLeftToRight) {
				// non-scaled
				g.drawImage(LEFT_TO_RIGHT_SHADOW_MASK,
					bw, 4, bw + SHADOW_SIZE, 8,
					6, 0, 11, 4, c);
				g.drawImage(LEFT_TO_RIGHT_SHADOW_MASK,
					4, bh, 8, bh + SHADOW_SIZE,
					0, 6, 4, 11, c);
				g.drawImage(LEFT_TO_RIGHT_SHADOW_MASK,
					bw, bh, bw + SHADOW_SIZE, bh + SHADOW_SIZE,
					6, 6, 11, 11, c);
				// scaled
				g.drawImage(LEFT_TO_RIGHT_SHADOW_MASK,
					bw, 8, bw + SHADOW_SIZE, bh,
					6, 4, 11, 5, c);
				g.drawImage(LEFT_TO_RIGHT_SHADOW_MASK,
					8, bh, bw, bh + SHADOW_SIZE,
					4, 6, 5, 11, c);
			}
			else {
				// non-scaled
				g.drawImage(RIGHT_TO_LEFT_SHADOW_MASK,
					0, 4, SHADOW_SIZE, 8,
					0, 0, 5, 4, c);
				g.drawImage(RIGHT_TO_LEFT_SHADOW_MASK,
					bw - 8, bh, bw - 4, bh + SHADOW_SIZE,
					7, 6, 11, 11, c);
				g.drawImage(RIGHT_TO_LEFT_SHADOW_MASK,
					0, bh, SHADOW_SIZE, bh + SHADOW_SIZE,
					0, 6, 6, 11, c);
//				// scaled
				g.drawImage(RIGHT_TO_LEFT_SHADOW_MASK,
					0, 8, SHADOW_SIZE, bh,
					0, 4, 5, 5, c);
				g.drawImage(RIGHT_TO_LEFT_SHADOW_MASK,
					SHADOW_SIZE, bh, bw - 8, bh + SHADOW_SIZE,
					5, 6, 6, 11, c);
			}
		}
		
		g.translate(-x, -y);
	}

	/**
	 * Gets the border insets for a given component.
	 *
	 * @param c The component to get its border insets.
	 * @return different insets for shadow and non-shadow popups
	 */
	public Insets getBorderInsets(Component c) {
		if(TinyPopupFactory.isPopupShadowEnabled()) {
			if(isOrientationLeftToRight(c)) {
				return INSETS_SHADOW_LEFT_TO_RIGHT;
			}

			return INSETS_SHADOW_RIGHT_TO_LEFT;
		}
		
		return INSETS_NO_SHADOW;
	}
	
	private boolean isOrientationLeftToRight(Component c) {
		if(!(c instanceof JComponent)) {
			return true;
		}
		
		Object co = ((JComponent)c).getClientProperty(TinyPopupFactory.COMPONENT_ORIENTATION_KEY);

		if(co == null && (c instanceof JPopupMenu)) {
			Component invoker = ((JPopupMenu)c).getInvoker();
			
			if(invoker != null) {
				co = invoker.getComponentOrientation();
			}
		}

		return (co == null ? true : ((ComponentOrientation)co).isLeftToRight());
	}
	
	public static String componentOrientationToString(ComponentOrientation co) {
		if(co == null) return "<null>";
		
		return (co.isLeftToRight() ? "left-to-right" : "right-to-left");
	}
}