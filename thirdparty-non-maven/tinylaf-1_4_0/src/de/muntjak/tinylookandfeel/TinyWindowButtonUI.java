/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import de.muntjak.tinylookandfeel.borders.TinyFrameBorder;
import de.muntjak.tinylookandfeel.borders.TinyInternalFrameBorder;
import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyWindowButtonUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyWindowButtonUI extends TinyButtonUI {
    
	// cache for already drawn buttons - speeds up drawing by a factor of 3
	// (new in 1.4.0)
	private static final HashMap cache = new HashMap();
	
	protected final static Dimension frameExternalButtonSize = new Dimension(21, 21);
	protected final static Dimension frameInternalButtonSize = new Dimension(17, 17);
	protected final static Dimension framePaletteButtonSize = new Dimension(13, 13);
	
    private int type;
    /** An icon to indicate that this button closes the windows */
    public final static int CLOSE = 0;
    /** An icon to indicate that this button maximizes the windows */
    public final static int MAXIMIZE = 1;
    /** An icon to indicate that this button minmizes / iconfies the windows */
    public final static int MINIMIZE = 2;
    
    public final static String EXTERNAL_FRAME_BUTTON_KEY = "externalFrameButton";
    public final static String DISABLED_WINDOW_BUTTON_KEY = "disabledWindowButton";
    
    public static void clearCache() {
    	cache.clear();
    }

	public static ComponentUI createUI(JComponent c) {
        throw new IllegalStateException("Must not be used this way.");
	}
    
    TinyWindowButtonUI(int type) {
        this.type = type;   
    }
    
    public void installDefaults(AbstractButton button) {
		super.installDefaults(button);
		button.setBorder(null);
		button.setFocusable(false);
	}

    protected void paintFocus(Graphics g, AbstractButton b,
    	Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {}
	
    public void paint(Graphics g, JComponent c) { 	
        AbstractButton button = (AbstractButton) c;

        boolean frameSelected = false;
        boolean frameMaximized = false;
        Container window = c.getParent();
        
        if(window instanceof TinyInternalFrameTitlePane) {
        	frameSelected = ((TinyInternalFrameTitlePane)window).isFrameSelected();
        	frameMaximized = ((TinyInternalFrameTitlePane)window).isFrameMaximized();
        }
        else if(window instanceof TinyTitlePane) {
        	frameSelected = ((TinyTitlePane)window).isSelected();
        	frameMaximized = ((TinyTitlePane)window).isFrameMaximized();
        }

        int w = button.getWidth();
		int h = button.getHeight();
			
		// content area
		Color col = null;
		
		if(!frameSelected) {
			if(button.isEnabled()) {
				if(button.getModel().isPressed()) {
					if(type == CLOSE) {
						col = Theme.frameButtClosePressedColor.getColor();
					}
					else {
						col = Theme.frameButtPressedColor.getColor();
					}
				}
				else {
					if(type == CLOSE) {
						col = Theme.frameButtCloseColor.getColor();
					}
					else {
						col = Theme.frameButtColor.getColor();
					}
				}
			}
			else {
				if(type == CLOSE) {
					col = Theme.frameButtCloseDisabledColor.getColor();
				}
				else {
					col = Theme.frameButtDisabledColor.getColor();
				}
			}
		}
		else if(button.getModel().isPressed()) {
			if(button.getModel().isRollover() || button.getModel().isArmed()) {
				if(type == CLOSE) {
					col = Theme.frameButtClosePressedColor.getColor();
				}
				else {
					col = Theme.frameButtPressedColor.getColor();
				}
			}
			else {
				if(type == CLOSE) {
					col = Theme.frameButtCloseColor.getColor();
				}
				else {
					col = Theme.frameButtColor.getColor();
				}
			}
		}
		else if(button.getModel().isRollover()) {
			if(type == CLOSE) {
				col = Theme.frameButtCloseRolloverColor.getColor();
			}
			else {
				col = Theme.frameButtRolloverColor.getColor();
			}
		}
		else {
			if(type == CLOSE) {
				col = Theme.frameButtCloseColor.getColor();
			}
			else {
				col = Theme.frameButtColor.getColor();
			}
		}
		
		g.setColor(col);
		
		if(TinyLookAndFeel.controlPanelInstantiated) {
			drawXpButtonNoCache(g, button, col, w, h, frameSelected, frameMaximized);
		}
		else {
			drawXpButton(g, button, col, w, h, frameSelected, frameMaximized);
		}
    }
 
    // Button for internal frames and dialogs
    private void drawXpButton(Graphics g, AbstractButton button,
    	Color c, int w, int h, boolean frameSelected, boolean frameMaximized)
    {
    	// We have 3 button sizes:
    	// 21x21 px. for frames and dialogs
    	// 17x17 px. for internal frames
    	// 13x13 px. for internal palettes
    	ButtonKey key = new ButtonKey(c, w, type,
    		(button.getModel().isRollover() || button.getModel().isArmed()),
    		button.getModel().isPressed(),
    		frameSelected, frameMaximized);
    	Object value = cache.get(key);

		if(value != null) {
			// image already cached - paint image
			g.drawImage((Image)value, 0, 0, button);
			return;
		}

		Image img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics imgGraphics = img.getGraphics();
		imgGraphics.setColor(g.getColor());

    	if(button.getClientProperty(EXTERNAL_FRAME_BUTTON_KEY) == Boolean.TRUE) {
    		drawXpLargeButton(imgGraphics, button, c, w, h, frameSelected);
    	}
    	else {
	    	imgGraphics.fillRect(1, 1, w - 2, h - 2);

			if(frameSelected) {
				imgGraphics.setColor(TinyInternalFrameBorder.frameUpperColor);
				imgGraphics.drawLine(0, 0, w - 1, 0);
				imgGraphics.drawLine(0, 1, 0, 1);				// ol
				imgGraphics.drawLine(w - 1, 1, w - 1, 1);		// or
				imgGraphics.setColor(TinyInternalFrameBorder.frameLowerColor);
				imgGraphics.drawLine(0, h - 1, w - 1, h - 1);
				imgGraphics.drawLine(0, h - 2, 0, h - 2);				// ul
				imgGraphics.drawLine(w - 1, h - 2, w - 1, h - 2);		// ur
			}
			else {
				imgGraphics.setColor(TinyInternalFrameBorder.disabledUpperColor);
				imgGraphics.drawLine(0, 0, w - 1, 0);
				imgGraphics.drawLine(0, 1, 0, 1);				// ol
				imgGraphics.drawLine(w - 1, 1, w - 1, 1);		// or
				imgGraphics.setColor(TinyInternalFrameBorder.disabledLowerColor);
				imgGraphics.drawLine(0, h - 1, w - 1, h - 1);
				imgGraphics.drawLine(0, h - 2, 0, h - 2);				// ul
				imgGraphics.drawLine(w - 1, h - 2, w - 1, h - 2);		// ur
			}   	
	
			Color col = null;
	    	if(!button.isEnabled()) {
	    		if(type == CLOSE) {
	    			col = Theme.frameButtCloseBorderDisabledColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtBorderDisabledColor.getColor();
	    		}
			}
			else {
				if(type == CLOSE) {
	    			col = Theme.frameButtCloseBorderColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtBorderColor.getColor();
	    		}
			}
	
			DrawRoutines.drawRoundedBorder(imgGraphics, col, 0, 0, w, h);
			
			if(!button.isEnabled()) {
	    		if(type == CLOSE) {
	    			col = Theme.frameButtCloseDisabledColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtDisabledColor.getColor();
	    		}
			}
			else {
				if(type == CLOSE) {
					col = Theme.frameButtCloseColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtColor.getColor();
	    		}
			}
	
			imgGraphics.setColor(col);
			imgGraphics.drawLine(2, 1, w - 3, 1);
			imgGraphics.drawLine(1, 2, 1, h - 3);
			
			if(!button.isEnabled()) {
	    		if(type == CLOSE) {
	    			col = Theme.frameButtCloseDisabledColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtDisabledColor.getColor();
	    		}
			}
			else {
				if(type == CLOSE) {
					col = ColorRoutines.darken(Theme.frameButtCloseColor.getColor(), 20);
	    		}
	    		else {
	    			col = ColorRoutines.darken(Theme.frameButtColor.getColor(), 20);
	    		}
			}
			
			imgGraphics.setColor(col);
			imgGraphics.drawLine(w - 2, 2, w - 2, h - 3);
			imgGraphics.drawLine(2, h - 2, w - 3, h - 2);
    	}
		
		// draw symbol
		if(!button.isEnabled()) {
			if(type == CLOSE) {
				imgGraphics.setColor(Theme.frameSymbolCloseDisabledColor.getColor());
			}
			else {
				imgGraphics.setColor(Theme.frameSymbolDisabledColor.getColor());
			}
		}
		else {
			if(type == CLOSE) {
				imgGraphics.setColor(Theme.frameSymbolCloseColor.getColor());
			}
			else {
				imgGraphics.setColor(Theme.frameSymbolColor.getColor());
			}
		}
		
		drawXpSymbol(imgGraphics, button, c, w, h, frameSelected, frameMaximized);
		
		// dispose of image graphics
		imgGraphics.dispose();
		
		// draw the image
		g.drawImage(img, 0, 0, button);
		
		// add the image to the cache
		cache.put(key, img);
		
		if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyWindowButtonUI.cache.size=" + cache.size());
		}
    }
    
    private void drawXpButtonNoCache(Graphics g, AbstractButton button,
    	Color c, int w, int h, boolean frameSelected, boolean frameMaximized)
    {
    	if(button.getClientProperty(EXTERNAL_FRAME_BUTTON_KEY) == Boolean.TRUE) {
    		drawXpLargeButton(g, button, c, w, h, frameSelected);
    	}
    	else {
	    	g.fillRect(1, 1, w - 2, h - 2);

			if(frameSelected) {
				g.setColor(TinyInternalFrameBorder.frameUpperColor);
				g.drawLine(0, 0, w - 1, 0);
				g.drawLine(0, 1, 0, 1);				// ol
				g.drawLine(w - 1, 1, w - 1, 1);		// or
				g.setColor(TinyInternalFrameBorder.frameLowerColor);
				g.drawLine(0, h - 1, w - 1, h - 1);
				g.drawLine(0, h - 2, 0, h - 2);				// ul
				g.drawLine(w - 1, h - 2, w - 1, h - 2);		// ur
			}
			else {
				g.setColor(TinyInternalFrameBorder.disabledUpperColor);
				g.drawLine(0, 0, w - 1, 0);
				g.drawLine(0, 1, 0, 1);				// ol
				g.drawLine(w - 1, 1, w - 1, 1);		// or
				g.setColor(TinyInternalFrameBorder.disabledLowerColor);
				g.drawLine(0, h - 1, w - 1, h - 1);
				g.drawLine(0, h - 2, 0, h - 2);				// ul
				g.drawLine(w - 1, h - 2, w - 1, h - 2);		// ur
			}   	
	
			Color col = null;
	    	if(!button.isEnabled()) {
	    		if(type == CLOSE) {
	    			col = Theme.frameButtCloseBorderDisabledColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtBorderDisabledColor.getColor();
	    		}
			}
			else {
				if(type == CLOSE) {
	    			col = Theme.frameButtCloseBorderColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtBorderColor.getColor();
	    		}
			}

	    	DrawRoutines.drawWindowButtonBorder(g, col, 0, 0, w, h);
			
			if(!button.isEnabled()) {
				if(type == CLOSE) {
	    			col = Theme.frameButtCloseDisabledColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtDisabledColor.getColor();
	    		}
			}
			else {
				if(type == CLOSE) {
					col = Theme.frameButtCloseColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtColor.getColor();
	    		}
			}
	
			g.setColor(col);
			g.drawLine(2, 1, w - 3, 1);
			g.drawLine(1, 2, 1, h - 3);
			
			if(!button.isEnabled()) {
				if(type == CLOSE) {
	    			col = Theme.frameButtCloseDisabledColor.getColor();
	    		}
	    		else {
	    			col = Theme.frameButtDisabledColor.getColor();
	    		}
			}
			else {
				if(type == CLOSE) {
					col = ColorRoutines.darken(Theme.frameButtCloseColor.getColor(), 20);
	    		}
	    		else {
	    			col = ColorRoutines.darken(Theme.frameButtColor.getColor(), 20);
	    		}
			}
			
			g.setColor(col);
			g.drawLine(w - 2, 2, w - 2, h - 3);
			g.drawLine(2, h - 2, w - 3, h - 2);
    	}
		
		// draw symbol
		if(!button.isEnabled()) {
			if(type == CLOSE) {
				g.setColor(Theme.frameSymbolCloseDisabledColor.getColor());
			}
			else {
				g.setColor(Theme.frameSymbolDisabledColor.getColor());
			}
		}
		else {
			if(type == CLOSE) {
				g.setColor(Theme.frameSymbolCloseColor.getColor());
			}
			else {
				g.setColor(Theme.frameSymbolColor.getColor());
			}
		}
		
		drawXpSymbol(g, button, c, w, h, frameSelected, frameMaximized);
    }
    
    private void drawXpLargeButton(Graphics g, AbstractButton b,
    	Color c, int w, int h, boolean frameSelected)
    {
    	g.drawLine(1, 2, 1, h - 2);
    	g.drawLine(1, 1, w - 2, 1);
    	g.drawLine(w - 2, h - 2, w - 2, h - 2);
    	
    	boolean isDisabledButton = b.getClientProperty(TinyWindowButtonUI.DISABLED_WINDOW_BUTTON_KEY) == Boolean.TRUE;

    	if(isDisabledButton) {
    		g.setColor(TinyFrameBorder.buttonUpperDisabledColor);
    	}
    	else {
    		g.setColor(TinyTitlePane.buttonUpperColor);
    	}
    	
    	g.drawLine(0, 0, w - 1, 0);
    	g.drawLine(0, 1, 0, 1);
    	g.drawLine(w - 1, 1, w - 1, 1);
 
    	if(isDisabledButton) {
    		g.setColor(TinyFrameBorder.buttonLowerDisabledColor);
    	}
    	else {
    		g.setColor(TinyTitlePane.buttonLowerColor);
    	}

    	g.drawLine(0, h - 1, w - 1, h - 1);
    	g.drawLine(0, h - 2, 0, h - 2);
    	g.drawLine(w - 1, h - 2, w - 1, h - 2);
    	
    	if(isDisabledButton) {
    		g.setColor(ColorRoutines.darken(c, 14));
    	}
    	else {
    		g.setColor(ColorRoutines.darken(c, 28));
    	}
    	
    	g.drawLine(w - 2, 2, w - 2, h - 3);
    	g.drawLine(2, h - 2, w - 3, h - 2);

		int spread1 = Theme.frameButtSpreadLight.getValue();
		int spread2 = Theme.frameButtSpreadDark.getValue();

		if(!b.isEnabled()) {
			if(type == CLOSE) {
				spread1 = Theme.frameButtCloseSpreadLightDisabled.getValue();
				spread2 = Theme.frameButtCloseSpreadDarkDisabled.getValue();
			}
			else {
				spread1 = Theme.frameButtSpreadLightDisabled.getValue();
				spread2 = Theme.frameButtSpreadDarkDisabled.getValue();
			}
		}
		else if(type == CLOSE) {
			spread1 = Theme.frameButtCloseSpreadLight.getValue();
			spread2 = Theme.frameButtCloseSpreadDark.getValue();
		}
		
		float spreadStep1 = 10.0f * spread1 / (h - 5);
		float spreadStep2 = 10.0f * spread2 / (h - 5);
		int halfY = h / 2;
		int yd;

		for(int y = 2; y < h - 2; y++) {
			if(y < halfY) {
				yd = halfY - y;
				g.setColor(ColorRoutines.lighten(c, (int)(yd * spreadStep1)));
			}
			else if(y == halfY) {
				g.setColor(c);
			}
			else {
				yd = y - halfY;
				g.setColor(ColorRoutines.darken(c, (int)(yd * spreadStep2)));
			}
			
			g.drawLine(2, y, w - 3, y);
		}

		if(!b.isEnabled()) {
			if(type == CLOSE) {
				DrawRoutines.drawWindowButtonBorder(g,
					Theme.frameButtCloseBorderDisabledColor.getColor(), 0, 0, w, h);
			}
			else {
				DrawRoutines.drawWindowButtonBorder(g,
					Theme.frameButtBorderDisabledColor.getColor(), 0, 0, w, h);
			}
		}
		else {
			if(type == CLOSE) {
				DrawRoutines.drawWindowButtonBorder(g,
					Theme.frameButtCloseBorderColor.getColor(), 0, 0, w, h);
			}
			else {
				DrawRoutines.drawWindowButtonBorder(g,
					Theme.frameButtBorderColor.getColor(), 0, 0, w, h);
			}
		}
    }

    private void drawXpSymbol(Graphics g, AbstractButton button, Color c,
    	int w, int h, boolean frameSelected, boolean frameMaximized)
    {
    	if(button.getClientProperty(EXTERNAL_FRAME_BUTTON_KEY) == Boolean.TRUE) {
    		// we are a frame
    		drawXpLargeSymbol(g, button, c, w, h, frameSelected, frameMaximized);
    		return;
    	}
    	
    	if(button.getParent() instanceof TinyInternalFrameTitlePane) {
			if(((TinyInternalFrameTitlePane)button.getParent()).isPalette()) {
				// we are an internal palette
				drawXpSmallSymbol(g, button, c, w, h, frameSelected, frameMaximized);
				return;
			}
		}
    	
    	// we are an internal frame
    	if(!frameSelected) {
    		if(button.isEnabled() && !button.getModel().isPressed()) {
    			if(type == CLOSE) {
    				g.setColor(Theme.frameSymbolCloseColor.getColor());
    			}
    			else {
    				g.setColor(Theme.frameSymbolColor.getColor());
    			}
    		}
    		else {
    			if(type == CLOSE) {
    				if(button.getModel().isPressed()) {
    					g.setColor(Theme.frameSymbolClosePressedColor.getColor());
    				}
    				else {
						g.setColor(Theme.frameSymbolCloseDisabledColor.getColor());
    				}
				}
				else {
    				if(button.getModel().isPressed()) {
    					g.setColor(Theme.frameSymbolPressedColor.getColor());
    				}
    				else {
						g.setColor(Theme.frameSymbolDisabledColor.getColor());
    				}
				}
    		}
    	}
    	else {
    		if(button.getModel().isPressed() &&
    			(button.getModel().isRollover() || button.getModel().isArmed()))
    		{
    			if(type == CLOSE) {
					g.setColor(Theme.frameSymbolClosePressedColor.getColor());
				}
				else {
    				g.setColor(Theme.frameSymbolPressedColor.getColor());
				}
    		}
    		else {
    			if(type == CLOSE) {
    				g.setColor(Theme.frameSymbolCloseColor.getColor());
    			}
    			else {
    				g.setColor(Theme.frameSymbolColor.getColor());
    			}
    		}
    	}
    	
    	int x = 0; int y = 0;
    	
    	switch(type) {
			case CLOSE:
				g.drawLine(x + 4, y + 3, x + 4, y + 3);
				g.drawLine(x + 12, y + 3, x + 12, y + 3);
				g.drawLine(x + 3, y + 4, x + 5, y + 4);
				g.drawLine(x + 11, y + 4, x + 13, y + 4);
				g.drawLine(x + 4, y + 5, x + 6, y + 5);
				g.drawLine(x + 10, y + 5, x + 12, y + 5);
				g.drawLine(x + 5, y + 6, x + 7, y + 6);
				g.drawLine(x + 9, y + 6, x + 11, y + 6);
				g.drawLine(x + 6, y + 7, x + 10, y + 7);
				g.drawLine(x + 7, y + 8, x + 9, y + 8);
				g.drawLine(x + 4, y + 13, x + 4, y + 13);
				g.drawLine(x + 12, y + 13, x + 12, y + 13);
				g.drawLine(x + 3, y + 12, x + 5, y + 12);
				g.drawLine(x + 11, y + 12, x + 13, y + 12);
				g.drawLine(x + 4, y + 11, x + 6, y + 11);
				g.drawLine(x + 10, y + 11, x + 12, y + 11);
				g.drawLine(x + 5, y + 10, x + 7, y + 10);
				g.drawLine(x + 9, y + 10, x + 11, y + 10);
				g.drawLine(x + 6, y + 9, x + 10, y + 9);
				break;
			case MAXIMIZE:
				if(frameMaximized) {
					g.fillRect(x + 5, y + 3, 8, 2);
					g.drawLine(x + 12, y + 5, x + 12, y + 9);
					g.drawLine(x + 11, y + 9, x + 11, y + 9);
					
					g.drawLine(x + 3, y + 6, x + 9, y + 6);
					g.drawRect(x + 3, y + 7, 6, 5);
				}
				else {
					g.fillRect(x + 3, y + 3, 11, 2);
					g.drawRect(x + 3, y + 5, 10, 8);
				}
				break;
			case MINIMIZE:
				g.fillRect(x + 3, y + 11, 7, 3);
				break;
		}
    }
    
    private void drawXpSmallSymbol(Graphics g, AbstractButton button, Color c,
    	int w, int h, boolean frameSelected, boolean frameMaximized)
	{
		if(!frameSelected) {
    		if(button.isEnabled() && !button.getModel().isPressed()) {
    			if(type == CLOSE) {
    				g.setColor(Theme.frameSymbolCloseColor.getColor());
    			}
    			else {
    				g.setColor(Theme.frameSymbolColor.getColor());
    			}
    		}
    		else {
    			if(type == CLOSE) {
    				if(button.getModel().isPressed()) {
    					g.setColor(Theme.frameSymbolClosePressedColor.getColor());
    				}
    				else {
						g.setColor(Theme.frameSymbolCloseDisabledColor.getColor());
    				}
				}
				else {
    				if(button.getModel().isPressed()) {
    					g.setColor(Theme.frameSymbolPressedColor.getColor());
    				}
    				else {
						g.setColor(Theme.frameSymbolDisabledColor.getColor());
    				}
				}
    		}
    	}
    	else {
    		if(button.getModel().isPressed() &&
    			(button.getModel().isRollover() || button.getModel().isArmed()))
    		{
    			if(type == CLOSE) {
					g.setColor(Theme.frameSymbolClosePressedColor.getColor());
				}
				else {
    				g.setColor(Theme.frameSymbolPressedColor.getColor());
				}
    		}
    		else {
    			if(type == CLOSE) {
    				g.setColor(Theme.frameSymbolCloseColor.getColor());
    			}
    			else {
    				g.setColor(Theme.frameSymbolColor.getColor());
    			}
    		}
    	}
    	
    	int x = 0; int y = 0;
    	
    	switch(type) {
			case CLOSE:
				g.drawLine(x + 3, y + 2, x + 3, y + 2);
				g.drawLine(x + 9, y + 2, x + 9, y + 2);
				g.drawLine(x + 2, y + 3, x + 4, y + 3);
				g.drawLine(x + 8, y + 3, x + 10, y + 3);
				g.drawLine(x + 3, y + 4, x + 5, y + 4);
				g.drawLine(x + 7, y + 4, x + 9, y + 4);
				g.drawLine(x + 4, y + 5, x + 8, y + 5);
				g.drawLine(x + 5, y + 6, x + 7, y + 6);			
				g.drawLine(x + 4, y + 7, x + 8, y + 7);
				g.drawLine(x + 3, y + 8, x + 5, y + 8);
				g.drawLine(x + 7, y + 8, x + 9, y + 8);
				g.drawLine(x + 2, y + 9, x + 4, y + 9);
				g.drawLine(x + 8, y + 9, x + 10, y + 9);
				g.drawLine(x + 3, y + 10, x + 3, y + 10);
				g.drawLine(x + 9, y + 10, x + 9, y + 10);
				break;
			case MAXIMIZE:
				if(frameMaximized) {
					g.drawRect(x + 3, y + 6, 4, 3);
					g.drawLine(x + 3, y + 5, x + 7, y + 5);
					
					g.fillRect(x + 5, y + 2, 5, y + 2);
					g.drawLine(x + 9, y + 4, x + 9, y + 7);					
				}
				else {
					g.drawLine(x + 3, y + 3, x + 9, y + 3);
					g.drawRect(x + 3, y + 4, 6, 5);
				}
				break;
			case MINIMIZE:
				g.fillRect(x + 3, y + 8, 5, 2);
				break;
		}
	}
    
    private void drawXpLargeSymbol(Graphics g, AbstractButton b, Color c,
    	int w, int h, boolean frameSelected, boolean frameMaximized)
	{
		if(!frameSelected) {
    		if(b.isEnabled() && !b.getModel().isPressed()) {
    			if(type == CLOSE) {
    				g.setColor(Theme.frameSymbolCloseColor.getColor());
    			}
    			else {
    				g.setColor(Theme.frameSymbolColor.getColor());
    			}
    		}
    		else {
    			if(type == CLOSE) {
    				if(b.getModel().isPressed()) {
    					g.setColor(Theme.frameSymbolClosePressedColor.getColor());
    				}
    				else {
						g.setColor(Theme.frameSymbolCloseDisabledColor.getColor());
    				}
				}
				else {
    				if(b.getModel().isPressed()) {
    					g.setColor(Theme.frameSymbolPressedColor.getColor());
    				}
    				else {
						g.setColor(Theme.frameSymbolDisabledColor.getColor());
    				}
				}
    		}
    	}
    	else {
    		if(b.getModel().isPressed() &&
    			(b.getModel().isRollover() || b.getModel().isArmed()))
    		{
    			if(type == CLOSE) {
					g.setColor(Theme.frameSymbolClosePressedColor.getColor());
				}
				else {
    				g.setColor(Theme.frameSymbolPressedColor.getColor());
				}
    		}
    		else {
    			if(type == CLOSE) {
    				g.setColor(Theme.frameSymbolCloseColor.getColor());
    			}
    			else {
    				g.setColor(Theme.frameSymbolColor.getColor());
    			}
    		}
    	}
    	
		// draw symbol color
    	int x = 0; int y = 0;
    	
    	switch(type) {
			case CLOSE:
				g.drawLine(x + 5, y + 5, x + 6, y + 5);
				g.drawLine(x + 14, y + 5, x + 15, y + 5);
				g.drawLine(x + 5, y + 6, x + 7, y + 6);
				g.drawLine(x + 13, y + 6, x + 15, y + 6);
				g.drawLine(x + 6, y + 7, x + 8, y + 7);
				g.drawLine(x + 12, y + 7, x + 14, y + 7);
				g.drawLine(x + 7, y + 8, x + 9, y + 8);
				g.drawLine(x + 11, y + 8, x + 13, y + 8);
				g.drawLine(x + 8, y + 9, x + 12, y + 9);
				g.drawLine(x + 9, y + 10, x + 11, y + 10);
				g.drawLine(x + 5, y + 15, x + 6, y + 15);
				g.drawLine(x + 14, y + 15, x + 15, y + 15);
				g.drawLine(x + 5, y + 14, x + 7, y + 14);
				g.drawLine(x + 13, y + 14, x + 15, y + 14);
				g.drawLine(x + 6, y + 13, x + 8, y + 13);
				g.drawLine(x + 12, y + 13, x + 14, y + 13);
				g.drawLine(x + 7, y + 12, x + 9, y + 12);
				g.drawLine(x + 11, y + 12, x + 13, y + 12);
				g.drawLine(x + 8, y + 11, x + 12, y + 11);
				break;
			case MAXIMIZE:
				if(frameMaximized) {
					g.drawLine(x + 8, y + 6, x + 13, y + 6);
					g.drawLine(x + 5, y + 10, x + 10, y + 10);
				}
				else {
					g.fillRect(x + 5, y + 6, 10, 2);
				}
				break;
			case MINIMIZE:
				g.fillRect(x + 5, y + 14, 6, 2);
				break;
		}
    	

    	// Note Close Button doesn't paint in light color
    	Color col = null;
    	
    	if(b.isEnabled()) {
    		col = Theme.frameSymbolLightColor.getColor();
    	}
    	else {
    		col = Theme.frameSymbolLightDisabledColor.getColor();
    	}

    	// draw light color
		g.setColor(col);
		
		switch(type) {
			case CLOSE:
				// nothing
				break;
			case MAXIMIZE:
				if(frameMaximized) {
					g.drawLine(x + 7, y + 6, x + 7, y + 8);
					g.drawLine(x + 9, y + 7, x + 13, y + 7);
					g.drawLine(x + 13, y + 8, x + 13, y + 10);
					g.drawLine(x + 12, y + 12, x + 14, y + 12);
					
					g.drawLine(x + 4, y + 10, x + 4, y + 16);
					g.drawLine(x + 5, y + 16, x + 11, y + 16);
					g.drawLine(x + 6, y + 11, x + 10, y + 11);
					g.drawLine(x + 10, y + 12, x + 10, y + 14);
				}
				else {
					g.drawLine(x + 4, y + 6, x + 4, y + 15);
					g.drawLine(x + 4, y + 16, x + 15, y + 16);
					g.drawLine(x + 6, y + 8, x + 13, y + 8);
					g.drawLine(x + 14, y + 8, x + 14, y + 14);
				}
				break;
			case MINIMIZE:
				g.drawLine(x + 4, y + 13, x + 4, y + 16);
				g.drawLine(x + 5, y + 16, x + 11, y + 16);
				break;
		}

		if(type == CLOSE) {
			if(b.isEnabled()) {
				col = Theme.frameSymbolCloseDarkColor.getColor();
			}
			else {
				col = Theme.frameSymbolCloseDarkDisabledColor.getColor();
			}
		}
		else {
			if(b.isEnabled()) {
				col = Theme.frameSymbolDarkColor.getColor();
			}
			else {
				col = Theme.frameSymbolDarkDisabledColor.getColor();
			}
		}

		// draw dark color
		g.setColor(col);
		
		switch(type) {
			case CLOSE:
				g.drawLine(x + 5, y + 4, x + 6, y + 4);
				g.drawLine(x + 14, y + 4, x + 15, y + 4);
				g.drawLine(x + 7, y + 5, x + 7, y + 5);
				g.drawLine(x + 13, y + 5, x + 13, y + 5);
				g.drawLine(x + 8, y + 6, x + 8, y + 6);
				g.drawLine(x + 12, y + 6, x + 12, y + 6);
				g.drawLine(x + 9, y + 7, x + 9, y + 7);
				g.drawLine(x + 11, y + 7, x + 11, y + 7);
				g.drawLine(x + 10, y + 8, x + 10, y + 8);
				g.drawLine(x + 8, y + 10, x + 8, y + 10);
				g.drawLine(x + 12, y + 10, x + 12, y + 10);
				g.drawLine(x + 7, y + 11, x + 7, y + 11);
				g.drawLine(x + 13, y + 11, x + 13, y + 11);
				g.drawLine(x + 6, y + 12, x + 6, y + 12);
				g.drawLine(x + 14, y + 12, x + 14, y + 12);
				g.drawLine(x + 5, y + 13, x + 5, y + 13);
				g.drawLine(x + 15, y + 13, x + 15, y + 13);
				g.drawLine(x + 4, y + 14, x + 4, y + 14);
				g.drawLine(x + 16, y + 14, x + 16, y + 14);
				break;
			case MAXIMIZE:
				if(frameMaximized) {
					g.drawLine(x + 8, y + 5, x + 14, y + 5);
					g.drawLine(x + 14, y + 6, x + 14, y + 11);
					g.drawLine(x + 12, y + 11, x + 13, y + 11);
					g.drawLine(x + 8, y + 7, x + 8, y + 8);
					
					g.drawLine(x + 5, y + 9, x + 11, y + 9);
					g.drawLine(x + 11, y + 10, x + 11, y + 15);
					g.drawLine(x + 5, y + 15, x + 10, y + 15);
					g.drawLine(x + 5, y + 11, x + 5, y + 14);
				}
				else {
					g.drawLine(x + 5, y + 5, x + 14, y + 5);
					g.drawLine(x + 15, y + 5, x + 15, y + 15);
					g.drawLine(x + 5, y + 15, x + 14, y + 15);
					g.drawLine(x + 5, y + 8, x + 5, y + 14);
				}
				break;
			case MINIMIZE:
				g.drawLine(x + 5, y + 13, x + 10, y + 13);
				g.drawLine(x + 11, y + 13, x + 11, y + 15);
				break;
		}
	}
    
    /**
     * Creates a new Window Button UI for the specified type
     * @param type one of MINIMIZE, MAXIMIZE, CLOSE
     * @return TinyWindowButtonUI
     */
	public static TinyWindowButtonUI createButtonUIForType(int type) {
        return new TinyWindowButtonUI(type);
	}
	
	/**
	 * @see javax.swing.plaf.basic.BasicButtonUI#getPreferredSize(javax.swing.JComponent)
	 */
	public Dimension getPreferredSize(JComponent c) {
		if(((AbstractButton)c).getClientProperty(EXTERNAL_FRAME_BUTTON_KEY) == Boolean.TRUE) {
			return frameExternalButtonSize;
		}
		else {
			if(c.getParent() instanceof TinyInternalFrameTitlePane) {
				if(((TinyInternalFrameTitlePane)c.getParent()).isPalette()) {
					return framePaletteButtonSize;
				}
			}
			
			return frameInternalButtonSize;
		}
	}
	
	private static class ButtonKey {
		
		private Color background;
		private int size;
		private int type;
		private boolean rollover;
		private boolean pressed;
		private boolean frameSelected;
		private boolean frameMaximized;
		
		ButtonKey(Color background, int size, int type,
			boolean rollover, boolean pressed,
			boolean frameSelected, boolean frameMaximized)
		{
			this.background = background;
			this.size = size;
			this.type = type + 1; // instead of {0, 1, 2} we want {1, 2, 3}
			this.rollover = rollover;
			this.pressed = pressed;
			this.frameSelected = frameSelected;
			this.frameMaximized = frameMaximized;
		}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof ButtonKey)) return false;

			ButtonKey other = (ButtonKey)o;
			
			return
				size == other.size &&
				type == other.type &&
				rollover == other.rollover &&
				pressed == other.pressed &&
				frameSelected == other.frameSelected &&
				frameMaximized == other.frameMaximized &&
				background.equals(other.background);
		}
		
		public int hashCode() {
			return background.hashCode() *
				type *
				size *
				(rollover ? 2 : 1) *
				(pressed ? 8 : 4) *
				(frameSelected ? 32 : 16) *
				(frameMaximized ? 128 : 64);
		}
	}
}
