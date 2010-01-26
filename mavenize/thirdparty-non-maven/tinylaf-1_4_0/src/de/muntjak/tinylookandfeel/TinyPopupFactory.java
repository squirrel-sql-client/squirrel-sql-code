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
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

import de.muntjak.tinylookandfeel.borders.TinyPopupMenuBorder;

/**
 * TinyPopupFactory is responsible for creating special Popup instances,
 * able to capture screen images used as background for popup menus
 * having a shadow border.
 * 
 * @author Hans Bickel
 * @since 1.4.0
 *
 */
public class TinyPopupFactory extends PopupFactory {
	
	/* False for production builds. */
	private static final boolean DEBUG = false;
	
	/* False for production builds. */
	private static final boolean DEBUG_DIALOGS = false;

	/* Keys under which client properties can be stored. */
	public static final String SHADOW_POPUP_KEY = "SHADOW_POPUP_KEY";
	public static final String VERTICAL_IMAGE_KEY = "VERTICAL_IMAGE_KEY";
	public static final String HORIZONTAL_IMAGE_KEY = "HORIZONTAL_IMAGE_KEY";
	public static final String COMPONENT_ORIENTATION_KEY = "COMPONENT_ORIENTATION_KEY";

	/* dialogs will contain all non-modal dialogs concurrently showing
	 * in their current Z-order (the active dialog at highest index). */
	private static final Vector DIALOGS = new Vector();
	
	/* activationListener (created lazily) will be set on all non-modal
	 * dialogs. (see addDialog())*/
	private static WindowListener activationListener;
	
	/* ShadowPopup cache. */
	private static final Stack SHADOW_POPUP_CACHE = new Stack();

	private PopupFactory storedFactory;

	// Can not be instantiated
	private TinyPopupFactory(PopupFactory factory) {
		storedFactory = factory;
	}
	
	public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
		Popup popup = super.getPopup(owner, contents, x, y);
		boolean useShadowPopup = (contents instanceof JPopupMenu) &&
			(((JComponent)contents).getBorder() instanceof TinyPopupMenuBorder);
		
		if(useShadowPopup) {
			return ShadowPopup.getInstance(owner, popup, (JPopupMenu)contents, x, y);
		}

		// For tooltips, comboboxes (...) return default popup
		return popup;
	}
	
	public static void addDialog(JDialog dialog) {
		if(TinyUtils.isOSMac()) return;
		
		if(!dialog.isModal()) {
			DIALOGS.add(dialog);
			
			if(activationListener == null) {
				activationListener = new WindowAdapter() {
					public void windowActivated(WindowEvent e) {
						Window w = e.getWindow();
						
						DIALOGS.remove(w);
						DIALOGS.add(w);
						
						if(DEBUG_DIALOGS) {
							System.out.println("windowActivated");
							printDialogs();
						}
					}
					
					public void windowClosed(WindowEvent e) {
						Window w = e.getWindow();
						
						w.removeWindowListener(activationListener);
						DIALOGS.remove(w);
						if(DEBUG_DIALOGS) System.out.println("windowClosed - " + DIALOGS.size() + " dialogs");
					}
				};
			}
			
			dialog.addWindowListener(activationListener);
			if(DEBUG_DIALOGS) System.out.println("addDialog - " + DIALOGS.size() + " dialogs");
		}
	}
	
	/**
	 * Closes all non-modal dialogs currently showing.
	 *
	 */
	public static void closeDialogs() {
		Iterator ii = DIALOGS.iterator();
		while(ii.hasNext()) {
			JDialog d = (JDialog)ii.next();
			d.dispose();
		}
	}
	
	private static void printDialogs() {
		System.out.println();
		String indent = "";
		
		Iterator ii = DIALOGS.iterator();
		while(ii.hasNext()) {
			JDialog d = (JDialog)ii.next();
			System.out.println(indent + d.getTitle());
			indent += "  ";
		}
	}

	public static void install() {
		if(!isPopupShadowEnabled()) return;

        PopupFactory factory = PopupFactory.getSharedInstance();
        
        if(factory instanceof TinyPopupFactory) return;
        
        // We are installed if the TinyLookAndFeel is being installed.
        // Currently displayed dialogs are invalid (but cannot be
        // disposed from here)
        if(!DIALOGS.isEmpty()) {
    		DIALOGS.clear();
        }
 
        PopupFactory.setSharedInstance(new TinyPopupFactory(factory));
        if(DEBUG) System.out.println("TinyPopupFactory installed.");
    }
	
	public static boolean isPopupShadowEnabled() {
		return !(TinyUtils.isOSMac() ||
			!Theme.menuPopupShadow.getValue() ||
			TinyLookAndFeel.ROBOT == null);
	}
	
	public static void uninstall() {
		while(!SHADOW_POPUP_CACHE.empty()) {
			Object o = SHADOW_POPUP_CACHE.pop();
			o = null;
		}
		
		while(!DIALOGS.isEmpty()) {
			Window w = (Window)DIALOGS.get(0);
			w.removeWindowListener(activationListener);
			DIALOGS.remove(w);
		}
		
        PopupFactory factory = PopupFactory.getSharedInstance();
       
        if(!(factory instanceof TinyPopupFactory)) return;

        PopupFactory.setSharedInstance(((TinyPopupFactory)factory).storedFactory);
        if(DEBUG) System.out.println("TinyPopupFactory uninstalled.");
    }

	private static class ShadowPopup extends Popup {
		
		/* Reusable Rectangle for screen captures. */
		private static final Rectangle RECT = new Rectangle();
		/* Reusable Point for screen captures. */
		private static final Point POINT = new Point();
		/* Reusable Dimension for screen captures. */
		private static final Dimension SIZE = new Dimension();
		
		private Component owner;
		private Popup delegate;
		private JPopupMenu contents;
		private int x;
		private int y;
		private BufferedImage vertImg;
		private BufferedImage horzImg;
		
		private ShadowPopup(Component owner, Popup delegate, JPopupMenu contents, int x, int y) {
			init(owner, delegate, contents, x, y);
		}
		
		static ShadowPopup getInstance(Component owner, Popup delegate, JPopupMenu contents, int x, int y) {
			if(SHADOW_POPUP_CACHE.empty()) {
				return new ShadowPopup(owner, delegate, contents, x, y);
			}
			
			ShadowPopup popup = (ShadowPopup)SHADOW_POPUP_CACHE.pop();
			
			popup.init(owner, delegate, contents, x, y);
			if(DEBUG) System.out.println("ShadowPopup.getInstance, cache.size=" + SHADOW_POPUP_CACHE.size() +
				", orientation is " + (owner.getComponentOrientation().isLeftToRight() ? "left-to-right" : "right-to-left"));
			
			return popup;
		}
		
		private void init(Component owner, Popup delegate, JPopupMenu contents, int x, int y) {
			this.owner = owner;
			this.delegate = delegate;
			this.contents = contents;
			this.x = x;
			this.y = y;

			ComponentOrientation co = ComponentOrientation.LEFT_TO_RIGHT;
			if(owner != null) {
				co = owner.getComponentOrientation();
			}
			
			contents.putClientProperty(SHADOW_POPUP_KEY, Boolean.TRUE);
			contents.putClientProperty(COMPONENT_ORIENTATION_KEY, co);
		}
		
		public void show() {
			makeSnapshot();
			delegate.show();
		}
		
		public void hide() {
			delegate.hide();
			this.uninstall();
		}
		
		private void makeSnapshot() {
			SIZE.setSize(contents.getPreferredSize());
			
			if(SIZE.width < TinyPopupMenuBorder.SHADOW_SIZE ||
				SIZE.height < TinyPopupMenuBorder.SHADOW_SIZE) return;
			
			Object co = contents.getClientProperty(COMPONENT_ORIENTATION_KEY);
			boolean isLeftToRight = (co == null ? true : ((ComponentOrientation)co).isLeftToRight());

			if(isLeftToRight) {
				// Capture vertical rect
				RECT.setBounds(x + SIZE.width - TinyPopupMenuBorder.SHADOW_SIZE, y, 5, SIZE.height);
				vertImg = TinyLookAndFeel.ROBOT.createScreenCapture(RECT);
				contents.putClientProperty(VERTICAL_IMAGE_KEY, vertImg);
				
				// Capture horizontal rect
				RECT.setBounds(x, y + SIZE.height - TinyPopupMenuBorder.SHADOW_SIZE, SIZE.width, 5);
				horzImg = TinyLookAndFeel.ROBOT.createScreenCapture(RECT);
				contents.putClientProperty(HORIZONTAL_IMAGE_KEY, horzImg);

				JRootPane rootPane = SwingUtilities.getRootPane(owner);
				
				if(rootPane != null) {
					JLayeredPane layeredPane = rootPane.getLayeredPane();
					
					if(layeredPane != null) {
						int layeredPaneWidth = layeredPane.getWidth();
			            int layeredPaneHeight = layeredPane.getHeight();
			            POINT.x = x;
			            POINT.y = y;
			            SwingUtilities.convertPointFromScreen(POINT, layeredPane);
	
			            // If needed paint dirty region of the horizontal snapshot.
			            RECT.x = POINT.x;
			            RECT.y = POINT.y + SIZE.height - TinyPopupMenuBorder.SHADOW_SIZE;
			            RECT.width = SIZE.width;
			            RECT.height = TinyPopupMenuBorder.SHADOW_SIZE;
	
			            if((RECT.x + RECT.width) > layeredPaneWidth) {
			                RECT.width = layeredPaneWidth - RECT.x;
			            }
			            
			            if((RECT.y + RECT.height) > layeredPaneHeight) {
			                RECT.height = layeredPaneHeight - RECT.y;
			            }
			            
			            Graphics g = horzImg.createGraphics();

			            if(!RECT.isEmpty()) {
			                g.translate(-RECT.x, -RECT.y);
			                g.setClip(RECT);
			                
			                if(layeredPane instanceof JComponent) {
			                    JComponent c = (JComponent) layeredPane;
			                    boolean doubleBuffered = c.isDoubleBuffered();
			                    c.setDoubleBuffered(false);
			                    c.paintAll(g);
			                    c.setDoubleBuffered(doubleBuffered);
			                }
			                else {
			                    layeredPane.paintAll(g);
			                }
			                
			                g.translate(RECT.x, RECT.y);
			            }
			            
			            // Consider non-modal dialogs, if any
			            Iterator ii = DIALOGS.iterator();
	            		while(ii.hasNext()) {
	            			Window window = (Window)ii.next();

	            			int windowWidth = window.getWidth();
	            			int windowHeight = window.getHeight();
	            			POINT.x = x;
				            POINT.y = y;
				            SwingUtilities.convertPointFromScreen(POINT, window);
		
				            RECT.x = POINT.x;
				            RECT.y = POINT.y + SIZE.height - TinyPopupMenuBorder.SHADOW_SIZE;
				            RECT.width = SIZE.width;
				            RECT.height = TinyPopupMenuBorder.SHADOW_SIZE;

				            if((RECT.x + RECT.width) > windowWidth) {
				                RECT.width = windowWidth - RECT.x;
				            }
				            
				            if((RECT.y + RECT.height) > windowHeight) {
				                RECT.height = windowHeight - RECT.y;
				            }

				            if(!RECT.isEmpty()) {
				            	g.translate(-RECT.x, -RECT.y);
				                g.setClip(RECT);
				                
				                window.paintAll(g);
				                g.translate(RECT.x, RECT.y);	// Next window ...
				            }
	            		}
			            
			            g.dispose();
	
			            // If needed paint dirty region of the vertical snapshot.
			            POINT.x = x;
			            POINT.y = y;
			            SwingUtilities.convertPointFromScreen(POINT, layeredPane);
			            
			            RECT.x = POINT.x + SIZE.width - TinyPopupMenuBorder.SHADOW_SIZE;
			            RECT.y = POINT.y;
			            RECT.width = TinyPopupMenuBorder.SHADOW_SIZE;
			            RECT.height = SIZE.height;
	
			            if((RECT.x + RECT.width) > layeredPaneWidth) {
			                RECT.width = layeredPaneWidth - RECT.x;
			            }
			            if((RECT.y + RECT.height) > layeredPaneHeight) {
			                RECT.height = layeredPaneHeight - RECT.y;
			            }
			            
			            g = vertImg.createGraphics();
			            
			            if(!RECT.isEmpty()) {
			                g.translate(-RECT.x, -RECT.y);
			                g.setClip(RECT);
			                
			                if(layeredPane instanceof JComponent) {
			                    JComponent c = (JComponent) layeredPane;
			                    boolean doubleBuffered = c.isDoubleBuffered();
			                    c.setDoubleBuffered(false);
			                    c.paintAll(g);
			                    c.setDoubleBuffered(doubleBuffered);
			                }
			                else {
			                    layeredPane.paintAll(g);
			                }
			                
			                g.translate(RECT.x, RECT.y);
			            }
			            
			            // Consider non-modal dialogs, if any
			            ii = DIALOGS.iterator();
	            		while(ii.hasNext()) {
	            			Window window = (Window)ii.next();

	            			int windowWidth = window.getWidth();
	            			int windowHeight = window.getHeight();
	            			POINT.x = x;
				            POINT.y = y;
				            SwingUtilities.convertPointFromScreen(POINT, window);
		
				            RECT.x = POINT.x + SIZE.width - TinyPopupMenuBorder.SHADOW_SIZE;
				            RECT.y = POINT.y;
				            RECT.width = TinyPopupMenuBorder.SHADOW_SIZE;
				            RECT.height = SIZE.height;

				            if((RECT.x + RECT.width) > windowWidth) {
				                RECT.width = windowWidth - RECT.x;
				            }
				            
				            if((RECT.y + RECT.height) > windowHeight) {
				                RECT.height = windowHeight - RECT.y;
				            }

				            if(!RECT.isEmpty()) {
				            	g.translate(-RECT.x, -RECT.y);
				                g.setClip(RECT);
				                
				                window.paintAll(g);
				                g.translate(RECT.x, RECT.y);	// Next window ...
				            }
	            		}
			            
			            g.dispose();
					}
				}
			}
			else {	// right-to-left
				// Capture vertical rect
				RECT.setBounds(x, y, 5, SIZE.height);
				vertImg = TinyLookAndFeel.ROBOT.createScreenCapture(RECT);
				contents.putClientProperty(VERTICAL_IMAGE_KEY, vertImg);
				
				// Capture horizontal rect
				RECT.setBounds(x, y + SIZE.height - TinyPopupMenuBorder.SHADOW_SIZE, SIZE.width, 5);
				horzImg = TinyLookAndFeel.ROBOT.createScreenCapture(RECT);
				contents.putClientProperty(HORIZONTAL_IMAGE_KEY, horzImg);

				JRootPane rootPane = SwingUtilities.getRootPane(owner);
				
				if(rootPane != null) {
					JLayeredPane layeredPane = rootPane.getLayeredPane();
					
					if(layeredPane != null) {
			            // If needed paint dirty region of the horizontal snapshot.
						int layeredPaneWidth = layeredPane.getWidth();
			            int layeredPaneHeight = layeredPane.getHeight();
			            POINT.x = x;
			            POINT.y = y;
			            SwingUtilities.convertPointFromScreen(POINT, layeredPane);
			            
			            RECT.x = POINT.x;
			            RECT.y = POINT.y + SIZE.height - TinyPopupMenuBorder.SHADOW_SIZE;
			            RECT.width = SIZE.width;
			            RECT.height = TinyPopupMenuBorder.SHADOW_SIZE;
	
			            if((RECT.x + RECT.width) > layeredPaneWidth) {
			                RECT.width = layeredPaneWidth - RECT.x;
			            }
			            
			            if((RECT.y + RECT.height) > layeredPaneHeight) {
			                RECT.height = layeredPaneHeight - RECT.y;
			            }
			            
			            Graphics g = horzImg.createGraphics();
			            
			            if(!RECT.isEmpty()) {
			                g.translate(-RECT.x, -RECT.y);
			                g.setClip(RECT);
			                
			                if(layeredPane instanceof JComponent) {
			                    JComponent c = (JComponent) layeredPane;
			                    boolean doubleBuffered = c.isDoubleBuffered();
			                    c.setDoubleBuffered(false);
			                    c.paintAll(g);
			                    c.setDoubleBuffered(doubleBuffered);
			                }
			                else {
			                    layeredPane.paintAll(g);
			                }
			                
			                g.translate(RECT.x, RECT.y);
			            }

			            // Consider non-modal dialogs, if any
			            Iterator ii = DIALOGS.iterator();
	            		while(ii.hasNext()) {
	            			Window window = (Window)ii.next();

	            			int windowWidth = window.getWidth();
	            			int windowHeight = window.getHeight();
	            			POINT.x = x;
				            POINT.y = y;
				            SwingUtilities.convertPointFromScreen(POINT, window);
		
				            RECT.x = POINT.x;
				            RECT.y = POINT.y + SIZE.height - TinyPopupMenuBorder.SHADOW_SIZE;
				            RECT.width = SIZE.width;
				            RECT.height = TinyPopupMenuBorder.SHADOW_SIZE;

				            if((RECT.x + RECT.width) > windowWidth) {
				                RECT.width = windowWidth - RECT.x;
				            }
				            
				            if((RECT.y + RECT.height) > windowHeight) {
				                RECT.height = windowHeight - RECT.y;
				            }

				            if(!RECT.isEmpty()) {
				            	g.translate(-RECT.x, -RECT.y);
				                g.setClip(RECT);
				                
				                window.paintAll(g);
				                g.translate(RECT.x, RECT.y);	// Next window ...
				            }
	            		}
			            
			            g.dispose();

			            // If needed paint dirty region of the vertical snapshot.
			            POINT.x = x;
			            POINT.y = y;
			            SwingUtilities.convertPointFromScreen(POINT, layeredPane);
			            
			            RECT.x = POINT.x;
			            RECT.y = POINT.y;
			            RECT.width = TinyPopupMenuBorder.SHADOW_SIZE;
			            RECT.height = SIZE.height;
	
			            if((RECT.x + RECT.width) > layeredPaneWidth) {
			                RECT.width = layeredPaneWidth - RECT.x;
			            }
			            
			            if((RECT.y + RECT.height) > layeredPaneHeight) {
			                RECT.height = layeredPaneHeight - RECT.y;
			            }
			            
			            g = vertImg.createGraphics();
			            
			            if(!RECT.isEmpty()) {
			                g.translate(-RECT.x, -RECT.y);
			                g.setClip(RECT);
			                
			                if(layeredPane instanceof JComponent) {
			                    JComponent c = (JComponent) layeredPane;
			                    boolean doubleBuffered = c.isDoubleBuffered();
			                    c.setDoubleBuffered(false);
			                    c.paintAll(g);
			                    c.setDoubleBuffered(doubleBuffered);
			                }
			                else {
			                    layeredPane.paintAll(g);
			                }
			                
			                g.translate(RECT.x, RECT.y);
			            }
			            
			            // Consider non-modal dialogs, if any
			            ii = DIALOGS.iterator();
	            		while(ii.hasNext()) {
	            			Window window = (Window)ii.next();

	            			int windowWidth = window.getWidth();
	            			int windowHeight = window.getHeight();
	            			POINT.x = x;
				            POINT.y = y;
				            SwingUtilities.convertPointFromScreen(POINT, window);
		
				            RECT.x = POINT.x;
				            RECT.y = POINT.y;
				            RECT.width = TinyPopupMenuBorder.SHADOW_SIZE;
				            RECT.height = SIZE.height;

				            if((RECT.x + RECT.width) > windowWidth) {
				                RECT.width = windowWidth - RECT.x;
				            }
				            
				            if((RECT.y + RECT.height) > windowHeight) {
				                RECT.height = windowHeight - RECT.y;
				            }

				            if(!RECT.isEmpty()) {
				            	g.translate(-RECT.x, -RECT.y);
				                g.setClip(RECT);
				                
				                window.paintAll(g);
				                g.translate(RECT.x, RECT.y);	// Next window ...
				            }
	            		}
			            
			            g.dispose();
					}
				}
			}
		}

		private void uninstall() {
			contents.putClientProperty(SHADOW_POPUP_KEY, null);
			contents.putClientProperty(COMPONENT_ORIENTATION_KEY, null);
			contents.putClientProperty(VERTICAL_IMAGE_KEY, null);
			contents.putClientProperty(HORIZONTAL_IMAGE_KEY, null);
			
			contents = null;
			delegate = null;
			vertImg = null;
			horzImg = null;
			
			SHADOW_POPUP_CACHE.push(this);
			
			if(DEBUG) System.out.println("ShadowPopup.uninstall, cache.size=" + SHADOW_POPUP_CACHE.size());
		}
	}
}
