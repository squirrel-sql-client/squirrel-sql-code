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
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.borders.TinyFrameBorder;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel;
import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * TinyTitlePane is responsible for painting the title bar of frames
 * and dialogs.
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyTitlePane extends JComponent {
	
	/* cache for already painted captions */
	private static final HashMap cache = new HashMap();
	
	/* Used to paint window buttons. */
	public static Color buttonUpperColor, buttonLowerColor;
	
    private static final int IMAGE_HEIGHT = 16;
    private static final int IMAGE_WIDTH = 16;
    
    private static TinyWindowButtonUI iconButtonUI;
    private static TinyWindowButtonUI maxButtonUI;
    private static TinyWindowButtonUI closeButtonUI;

    /**
     * PropertyChangeListener added to the JRootPane.
     */
    private PropertyChangeListener propertyChangeListener;

    /**
     * JMenuBar, typically renders the system menu items.
     */
    private JMenuBar menuBar;
    
    private Image systemIcon;
    
    /**
     * Action used to close the Window.
     */
    private Action closeAction;

    /**
     * Action used to iconify the Frame.
     */
    private Action iconifyAction;

    /**
     * Action to restore the Frame size.
     */
    private Action restoreAction;

    /**
     * Action to restore the Frame size.
     */
    private Action maximizeAction;

    /**
     * Button used to maximize or restore the Frame.
     */
    private JButton toggleButton;

    /**
     * Button used to maximize or restore the Frame.
     */
    private JButton iconifyButton;

    /**
     * Button used to maximize or restore the Frame.
     */
    private JButton closeButton;

    /**
     * Listens for changes in the state of the Window listener to update
     * the state of the widgets.
     */
    private WindowListener windowListener;
    
    private ComponentListener windowMoveListener;

    /**
     * Window we're currently in.
     */
    private Window window;

    /**
     * JRootPane rendering for.
     */
    private JRootPane rootPane;

    /**
     * Room remaining in title for bumps.
     */
    private int buttonsWidth;

    /**
     * Buffered Frame.state property. As state isn't bound, this is kept
     * to determine when to avoid updating widgets.
     */
    private int state;

    /**
     * RootPaneUI that created us.
     */
    private TinyRootPaneUI rootPaneUI;

    public TinyTitlePane(JRootPane root, TinyRootPaneUI ui) {
        rootPane = root;
        rootPaneUI = ui;
        state = -1;

        installSubcomponents();
        installDefaults();
        setLayout(createLayout());
    }
    
    public static void clearCache() {
    	cache.clear();
    }

    /**
     * Uninstalls the necessary state.
     */
    private void uninstall() {
        uninstallListeners();
        window = null;
        removeAll();
    }

    /**
     * Installs the necessary listeners.
     */
    private void installListeners() {
        if(window != null) {
            windowListener = createWindowListener();
            window.addWindowListener(windowListener);
            propertyChangeListener = createWindowPropertyChangeListener();
            window.addPropertyChangeListener(propertyChangeListener);
            windowMoveListener = new WindowMoveListener();
            window.addComponentListener(windowMoveListener);
            
            if(window instanceof JDialog) {
            	TinyPopupFactory.addDialog((JDialog)window);
            }
        }
    }

    /**
     * Uninstalls the necessary listeners.
     */
    private void uninstallListeners() {
        if(window != null) {
            window.removeWindowListener(windowListener);
            window.removePropertyChangeListener(propertyChangeListener);
            window.removeComponentListener(windowMoveListener);
        }
    }

    /**
     * Returns the <code>WindowListener</code> to add to the
     * <code>Window</code>.
     */
    private WindowListener createWindowListener() {
        return new WindowHandler();
    }

    /**
     * Returns the <code>PropertyChangeListener</code> to install on
     * the <code>Window</code>.
     */
    private PropertyChangeListener createWindowPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    /**
     * Returns the <code>JRootPane</code> this was created for.
     */
    public JRootPane getRootPane() {
        return rootPane;
    }

    /**
     * Returns the decoration style of the <code>JRootPane</code>.
     */
    private int getWindowDecorationStyle() {
        return getRootPane().getWindowDecorationStyle();
    }

    public void addNotify() {
        super.addNotify();

        uninstallListeners();

        window = SwingUtilities.getWindowAncestor(this);
        
        if(window != null) {
            if(window instanceof Frame) {
                setState(((Frame)window).getExtendedState());
            }
            else {
                setState(0);
            }
            
            setActive(window.isActive());
            installListeners();
            updateSystemIcon();
        }
    }

    public void removeNotify() {
        super.removeNotify();

        uninstallListeners();
        window = null;
    }

    /**
     * Adds any sub-Components contained in the <code>TinyTitlePane</code>.
     */
    private void installSubcomponents() {
    	int decorationStyle = getWindowDecorationStyle();
    	
    	// New in 1.4.0: Create system menu bar for frames and plain dialogs
        if(decorationStyle == JRootPane.FRAME) {
            createActions();
            menuBar = createMenuBar();
            add(menuBar);
            createButtons(decorationStyle);
            add(iconifyButton);
            add(toggleButton);
            add(closeButton);
            iconifyButton.putClientProperty(TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
            toggleButton.putClientProperty(TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
            closeButton.putClientProperty(TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
        }
        else if(decorationStyle == JRootPane.PLAIN_DIALOG ||
            decorationStyle == JRootPane.COLOR_CHOOSER_DIALOG ||
            decorationStyle == JRootPane.FILE_CHOOSER_DIALOG)
        {
        	createActions();
        	menuBar = createMenuBar();
            add(menuBar);
        	createButtons(decorationStyle);
        	add(closeButton);
        	closeButton.putClientProperty(TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
        }
        else if(decorationStyle == JRootPane.INFORMATION_DIALOG ||
            decorationStyle == JRootPane.ERROR_DIALOG ||
            decorationStyle == JRootPane.QUESTION_DIALOG ||
            decorationStyle == JRootPane.WARNING_DIALOG)
        {
        	createActions();
        	createButtons(decorationStyle);
        	add(closeButton);
        	closeButton.putClientProperty(TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
        }
    }

    /**
     * Installs the fonts and necessary properties on the TinyTitlePane.
     */
    private void installDefaults() {
        setFont(UIManager.getFont("Frame.titleFont", getLocale()));
    }
    
    /**
     * Uninstalls any previously installed UI values.
     */
    private void uninstallDefaults() {
    }

    /**
     * Returns the <code>JMenuBar</code> displaying the appropriate 
     * system menu items.
     */
    protected JMenuBar createMenuBar() {
        menuBar = new SystemMenuBar();
        menuBar.setFocusable(false);
        menuBar.setBorderPainted(true);
        menuBar.add(createMenu());
        
        return menuBar;
    }
    
    /**
     * Update the image used for the system icon
     */
private void updateSystemIcon() {
	systemIcon = getWindowIcon(getWindow());
//	System.out.println(getTitle() + ".updateSystemIcon: " + systemIcon +
//		" at " + System.currentTimeMillis());
//	new Exception(getTitle() + ".updateSystemIcon: " + systemIcon).printStackTrace();
}

private Image getWindowIcon(Window window) {
    if(window == null) return null;
    
    if(window instanceof Frame) {
        return ((Frame)window).getIconImage();
    }
    else {
        try {
            // available since java 1.6
            Method getIconImages = window.getClass().getMethod("getIconImages", (Class[])null); // NOI18N
            List icons = (List)getIconImages.invoke(window, (Object[])null);

            if(icons != null) {
            	if(icons.size() == 0) {
            		 return getWindowIcon(window.getOwner());
                }
                else if(icons.size() == 1) {
                    return (Image)icons.get(0);
                }
                else {
                    for(int i = 0; i < icons.size(); i++) {
                    	Image img = (Image)icons.get(i);
                    	
                    	if(img.getWidth(this) == IMAGE_WIDTH && img.getHeight(this) == IMAGE_HEIGHT) {
                    		return img;
                    	}
                    }
                    
                    return ((Image)icons.get(0)).getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
                } 
            }
        }
        catch (Exception ex) {
            // no hope to get icon for this window :(
            // return the parent icon
            return getWindowIcon(window.getOwner());
        }
    }
    
    return null;
}

    /**
     * Closes the Window.
     */
    private void close() {
        Window window = getWindow();

        if(window != null) {
            window.dispatchEvent(new WindowEvent(
                                 window, WindowEvent.WINDOW_CLOSING));
        }
    }

    /**
     * Iconifies the Frame.
     */
    private void iconify() {
        Frame frame = getFrame();

        if(frame != null) {
            frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED);
        }
    }

    /**
     * Maximizes the Frame.
     */
    private void maximize() {
        Frame frame = getFrame();

        if(frame != null) {
        	setMaximizeBounds(frame);
            frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
        }
    }

    protected void setMaximizeBounds(Frame frame) {
    	// Changed in 1.4.0 to calculate the available screen area each time.
    	Insets screenInsets = Toolkit.getDefaultToolkit().
    		getScreenInsets(getGraphicsConfiguration());
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	
    	// spare any Systemmenus or Taskbars or ??...
    	int w = screenSize.width - screenInsets.left - screenInsets.right;
    	int h = screenSize.height - screenInsets.top - screenInsets.bottom;
    	Rectangle maxBounds = new Rectangle(screenInsets.left, screenInsets.top, w, h);
//    	System.out.println(getTitle() + ".screenSize=" + screenSize +
//    		"\n screenInsets=" + screenInsets +
//    		"\n  maxBounds=" + maxBounds);
    	
    	frame.setMaximizedBounds(maxBounds);
    }

    /**
     * Restores the Frame size.
     */
    private void restore() {
        Frame frame = getFrame();

        if(frame == null) {
            return;
        }

        if((frame.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
            frame.setExtendedState(state & ~Frame.ICONIFIED);
        } else {
            frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
        }
    }

    /**
     * Create the <code>Action</code>s that get associated with the
     * buttons and menu items.
     */
    private void createActions() {
        closeAction = new CloseAction();
        iconifyAction = new IconifyAction();
        restoreAction = new RestoreAction();
        maximizeAction = new MaximizeAction();
    }

    /**
     * Returns the <code>JMenu</code> displaying the appropriate menu items
     * for manipulating the Frame.
     */
    private JMenu createMenu() {
        JMenu menu = new JMenu("");
        
        // New in 1.4.0: Don't paint rollovers on top menus
        // as long as a system menu is showing
        menu.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e) {
				if(windowHasMenuBar()) {
					TinyMenuUI.systemMenuShowing = true;
				}
			}
			
			public void menuDeselected(MenuEvent e) {
				TinyMenuUI.systemMenuShowing = false;
			}
			
			public void menuCanceled(MenuEvent e) {}
        });
        
        if(getWindowDecorationStyle() == JRootPane.FRAME) {
            addSystemMenuItems(menu, true);
            // We use this property to prevent the menu from drawing rollovers
            menu.putClientProperty(TinyMenuUI.IS_SYSTEM_MENU_KEY, Boolean.TRUE);
        }
        else if(getWindowDecorationStyle() != JRootPane.NONE) {
            addSystemMenuItems(menu, false);
            // we use this property to prevent the Menu from drawing rollovers
            menu.putClientProperty(TinyMenuUI.IS_SYSTEM_MENU_KEY, Boolean.TRUE);
        }
        
        return menu;
    }
    
    private boolean windowHasMenuBar() {
    	Window w = getWindow();

        if(w instanceof JFrame) {
            return ((JFrame)w).getJMenuBar() != null;
        }
        else if(w instanceof JDialog) {
            return ((JDialog)w).getJMenuBar() != null;
        }
        
        return false;
    }

    /**
     * Adds the necessary <code>JMenuItem</code>s to the passed in menu.
     */
    private void addSystemMenuItems(JMenu menu, boolean isFrame) {
        Locale locale = getRootPane().getLocale();
        JMenuItem item = null;
        
        if(isFrame) {
	        item = menu.add(restoreAction);
	        item.setIcon(MenuItemIconFactory.getSystemRestoreIcon());
	        int mnemonic = getInt("MetalTitlePane.restoreMnemonic", -1);
	        if(mnemonic != -1) {
	            item.setMnemonic(mnemonic);
	        }
	
	        item = menu.add(iconifyAction);
	        item.setIcon(MenuItemIconFactory.getSystemIconifyIcon());
	        mnemonic = getInt("MetalTitlePane.iconifyMnemonic", -1);
	        if(mnemonic != -1) {
	            item.setMnemonic(mnemonic);
	        }
	
	        if(Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
	            item = menu.add(maximizeAction);
	            item.setIcon(MenuItemIconFactory.getSystemMaximizeIcon());
	            mnemonic =
	                getInt("MetalTitlePane.maximizeMnemonic", -1);
	            if(mnemonic != -1) {
	                item.setMnemonic(mnemonic);
	            }
	        }
	
	        menu.addSeparator();
	    }

        item = menu.add(closeAction);
        item.setIcon(MenuItemIconFactory.getSystemCloseIcon());
        int mnemonic = getInt("MetalTitlePane.closeMnemonic", -1);
        
        if(mnemonic != -1) {
            item.setMnemonic(mnemonic);
        }
    }

  /**
   * Creates the buttons of the title pane and initializes their actions.
   */
  protected void createButtons(int decorationStyle) {
    if(iconButtonUI == null) {
        iconButtonUI = TinyWindowButtonUI.createButtonUIForType(TinyWindowButtonUI.MINIMIZE);
        maxButtonUI = TinyWindowButtonUI.createButtonUIForType(TinyWindowButtonUI.MAXIMIZE);
        closeButtonUI = TinyWindowButtonUI.createButtonUIForType(TinyWindowButtonUI.CLOSE);
    }
    
    iconifyButton = new SpecialUIButton(iconButtonUI);
    iconifyButton.setAction(iconifyAction);
    iconifyButton.setText(null);
    iconifyButton.setRolloverEnabled(true);

    toggleButton = new SpecialUIButton(maxButtonUI);
    toggleButton.setAction(maximizeAction);
    toggleButton.setText(null);
    toggleButton.setRolloverEnabled(true);
    

    closeButton = new SpecialUIButton(closeButtonUI);
    closeButton.setAction(closeAction);
    closeButton.setText(null);
    closeButton.setRolloverEnabled(true);
    
    closeButton.getAccessibleContext().setAccessibleName("Close");
    iconifyButton.getAccessibleContext().setAccessibleName("Iconify");
    toggleButton.getAccessibleContext().setAccessibleName("Maximize");
    
    // Note: Not checking the decorationStyle here was a bug before 1.4.0
    // because window buttons were set even if dialogs were created.
    if(TinyLookAndFeel.controlPanelInstantiated && decorationStyle == JRootPane.FRAME) {
    	ControlPanel.setWindowButtons(new JButton[] {iconifyButton, toggleButton, closeButton});
    }
  }

    /**
     * Returns the <code>LayoutManager</code> that should be installed on
     * the <code>TinyTitlePane</code>.
     */
    private LayoutManager createLayout() {
        return new TitlePaneLayout();
    }

    /**
     * Updates state dependant upon the Window's active state.
     */
    private void setActive(boolean isActive) {
        if(getWindowDecorationStyle() == JRootPane.FRAME) {
            Boolean activeB = isActive ? Boolean.TRUE : Boolean.FALSE;

            iconifyButton.putClientProperty("paintActive", activeB);
            closeButton.putClientProperty("paintActive", activeB);
            toggleButton.putClientProperty("paintActive", activeB);
            
            iconifyButton.setEnabled(isActive);
            closeButton.setEnabled(isActive);
            toggleButton.setEnabled(isActive);
        }
        // Repaint the whole thing as the Borders that are used have
        // different colors for active vs inactive
        getRootPane().repaint();
    }

    /**
     * Sets the state of the Window.
     */
    private void setState(int state) {
        setState(state, false);
    }

    /**
     * Sets the state of the window. If <code>updateRegardless</code> is
     * true and the state has not changed, this will update anyway.
     */
    private void setState(int state, boolean updateRegardless) {
        Window w = getWindow();

        if(w != null && getWindowDecorationStyle() == JRootPane.FRAME) {
            if(this.state == state && !updateRegardless) {
                return;
            }
            Frame frame = getFrame();

            if(frame != null) {
                JRootPane rootPane = getRootPane();

                if((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH &&
                              (rootPane.getBorder() == null ||
                              (rootPane.getBorder() instanceof UIResource)) &&
                              frame.isShowing()) {
                    //rootPane.setBorder(null);
                }
                else if((state & Frame.MAXIMIZED_BOTH) !=
                        Frame.MAXIMIZED_BOTH) {
                    // This is a croak, if state becomes bound, this can
                    // be nuked.
                    //rootPaneUI.installBorder(rootPane);
                }
                if(frame.isResizable()) {
                    if((state & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT ||
                            (state & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ)
                    {
                        updateToggleButton(restoreAction);
                        maximizeAction.setEnabled(false);
                        restoreAction.setEnabled(true);
                    }
                    else {
                        updateToggleButton(maximizeAction);
                        maximizeAction.setEnabled(true);
                        restoreAction.setEnabled(false);
                    }
                    if(toggleButton.getParent() == null ||
                        iconifyButton.getParent() == null) {
                        add(toggleButton);
                        add(iconifyButton);
                        revalidate();
                        repaint();
                    }
                    toggleButton.setText(null);
                }
                else {
                    maximizeAction.setEnabled(false);
                    restoreAction.setEnabled(false);
                    if(toggleButton.getParent() != null) {
                        remove(toggleButton);
                        revalidate();
                        repaint();
                    }
                }
            }
            else {
                // Not contained in a Frame
                maximizeAction.setEnabled(false);
                restoreAction.setEnabled(false);
                iconifyAction.setEnabled(false);
                remove(toggleButton);
                remove(iconifyButton);
                revalidate();
                repaint();
            }
            
            closeAction.setEnabled(true);
            this.state = state;
        }
    }

    /**
     * Updates the toggle button to contain the Icon <code>icon</code>, and
     * Action <code>action</code>.
     */
    private void updateToggleButton(Action action) {
        toggleButton.setAction(action);
        toggleButton.setText(null);
    }

    /**
     * Returns the Frame rendering in. This will return null if the
     * <code>JRootPane</code> is not contained in a <code>Frame</code>.
     */
    private Frame getFrame() {
        Window window = getWindow();

        if(window instanceof Frame) {        	
            return (Frame)window;
        }
        
        return null;
    }

    /**
     * Returns the <code>Window</code> the <code>JRootPane</code> is
     * contained in. This will return null if there is no parent ancestor
     * of the <code>JRootPane</code>.
     */
    private Window getWindow() {
        return window;
    }

    /**
     * Returns the String to display as the title.
     */
    private String getTitle() {
        Window w = getWindow();

        if(w instanceof Frame) {
            return ((Frame)w).getTitle();
        }
        else if(w instanceof Dialog) {
            return ((Dialog)w).getTitle();
        }
        
        return null;
    }
    
    public boolean isSelected() {
    	Window window = getWindow();
    	return (window == null) ? true : window.isActive();
    }
    
    public boolean isFrameMaximized() {
    	Frame frame = getFrame();
    	
        if(frame != null) {
            return ((frame.getExtendedState() &
            	Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH);
        }
        
    	return false;
    }

    /**
     * Renders the TitlePane.
     */
    public void paintComponent(Graphics g) {
		// As state isn't bound, we need a convenience place to check
		// if it has changed.
		if(getFrame() != null) {
			setState(getFrame().getExtendedState());
		}

		Window window = getWindow();
		boolean leftToRight = (window == null) ? getRootPane()
			.getComponentOrientation().isLeftToRight() : window
			.getComponentOrientation().isLeftToRight();
		boolean isActive = (window == null) ? true : window.isActive();
		int width = getWidth();
		int height = getHeight();
		int xOffset = leftToRight ? 5 : width - 5;
		
		// New in 1.4.0: Since JRE 1.6.0_10-beta we must paint the
		// frame caption from here instead of painting it as part
		// of the frame border
		paintCaption(g, width, height, isActive, TinyFrameBorder.FRAME_TITLE_HEIGHT, window);

		// Changed with 1.4.0: Non-optionPane dialogs have a system menu
		int decorationStyle = getWindowDecorationStyle();
		
		if(decorationStyle == JRootPane.FRAME ||
			decorationStyle == JRootPane.PLAIN_DIALOG ||
            decorationStyle == JRootPane.COLOR_CHOOSER_DIALOG ||
            decorationStyle == JRootPane.FILE_CHOOSER_DIALOG)
		{
			xOffset += leftToRight ? IMAGE_WIDTH + 5 : -IMAGE_WIDTH - 5;
		}

		String theTitle = getTitle();

		if(theTitle != null) {
			FontMetrics fm = g.getFontMetrics();
			
			// Changed with 1.4.0: For frames and dialogs, move text two pixels down.
			int yd = (height == TinyFrameBorder.FRAME_TITLE_HEIGHT ? 2 : 0);
			int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent() + yd;
			Rectangle rect = new Rectangle(0, 0, 0, 0);

			if(iconifyButton != null && iconifyButton.getParent() != null) {
				rect = iconifyButton.getBounds();
			}

			int titleW;

			if(leftToRight) {
				if(rect.x == 0) {
					rect.x = window.getWidth() - window.getInsets().right - 2;
				}

				titleW = rect.x - xOffset - 4;
				theTitle = clippedText(theTitle, fm, titleW);
			}
			else {
				titleW = xOffset - rect.x - rect.width - 4;
				theTitle = clippedText(theTitle, fm, titleW);
				xOffset -= SwingUtilities.computeStringWidth(fm, theTitle);
			}

			int titleLength = SwingUtilities.computeStringWidth(fm, theTitle);

			// New in 1.4.0: Title text painted antialiased
			if(g instanceof Graphics2D) {
				Graphics2D g2d = (Graphics2D)g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}

			if(isActive) {
				// Paint shadow
				g.setColor(Theme.frameTitleShadowColor.getColor());
				g.drawString(theTitle, xOffset + 1, yOffset + 1);

				g.setColor(Theme.frameTitleColor.getColor());
				g.drawString(theTitle, xOffset, yOffset);
			}
			else {
				// for an inactive window
				g.setColor(Theme.frameTitleDisabledColor.getColor());
				g.drawString(theTitle, xOffset, yOffset);
			}
		}
	}
    
    private void paintCaption(Graphics g, int w, int h, boolean isActive, int titleHeight, Window window) {
    	if(TinyLookAndFeel.controlPanelInstantiated) {
    		paintXPCaptionNoCache(g, w, h, isActive, titleHeight, window);
    	}
    	else {
    		paintXPCaption(g, w, h, isActive, titleHeight, window);
    	}
    }
    
    private void paintXPCaption(Graphics g, int w, int h, boolean isActive, int titleHeight, Window window) {
    	Color c = null;
    	
		if(isActive) {
    		c = Theme.frameCaptionColor.getColor();
    	}
    	else {
    		c = Theme.frameCaptionDisabledColor.getColor();
    	}
		
    	g.setColor(c);
    	
    	int x = 0;
    	int y = 0;
    	int spread1 = Theme.frameSpreadDarkDisabled.getValue();
		int spread2 = Theme.frameSpreadLightDisabled.getValue();
		int y2 = y;
		Color borderColor = null;

		if(isActive) {
			borderColor = Theme.frameBorderColor.getColor();
    		spread1 = Theme.frameSpreadDark.getValue();
    		spread2 = Theme.frameSpreadLight.getValue();
    	}
    	else {
    		borderColor = Theme.frameBorderDisabledColor.getColor();
    	}

		// always paint the semi-transparent parts
// 1
		// blend
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 82));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 156));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 215));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);		
		y2 ++;
// 2
		Color c2 = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(c2);
		g.drawLine(x, y2, x + 2, y2);	// left
		g.drawLine(x + w - 3, y2, x + w - 1, y2);	// right
		y2 ++;
// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);	// left
		g.drawLine(x + w - 3, y2, x + w - 1, y2);	// right
		// darker border
		g.setColor(c);
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);		
		y2 ++;
// 4
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(ColorRoutines.lighten(c, 7 * spread2));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		g.setColor(ColorRoutines.lighten(c, 3 * spread2));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		y2 ++;
// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 2, y2, x + 2, y2);	// left
		g.drawLine(x + x + w - 3, y2, x + w - 3, y2);	// right
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(c);
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2 ++;

		// now either paint from cache or create cached image
		CaptionKey key = new CaptionKey(
			Theme.frameCaptionColor.getColor(), isActive, titleHeight);
		Object value = cache.get(key);
		
		if(value != null) {
			// image is cached - paint and return
			g.drawImage((Image)value,
				x + 3, y, x + w - 3, y + 5,
				0, 0, 1, 5,
				window);
			g.drawImage((Image)value,
				x, y + 5, x + w, y + titleHeight,
				0, 5, 1, titleHeight,
				window);
				
			// store button colors
			buttonUpperColor = ColorRoutines.darken(c, 4 * spread1);
			buttonLowerColor = ColorRoutines.lighten(c, 10 * spread2);
			
			return;
		}

		Image img = new BufferedImage(1, titleHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics imgGraphics = img.getGraphics();
// 1
		imgGraphics.setColor(borderColor);
		imgGraphics.drawLine(0, 0, 1, 0);
// 2
		imgGraphics.setColor(ColorRoutines.darken(c, 4 * spread1));
		imgGraphics.drawLine(0, 1, 1, 1);
// 3
		imgGraphics.setColor(ColorRoutines.lighten(c, 10 * spread2));
		imgGraphics.drawLine(0, 2, 1, 2);
// 4
		imgGraphics.setColor(c);
		imgGraphics.drawLine(0, 3, 1, 3);
// 5
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 4, 1, 4);
// 6
		buttonUpperColor = ColorRoutines.darken(c, 4 * spread1);
		imgGraphics.setColor(buttonUpperColor);
		imgGraphics.drawLine(0, 5, 1, 5);
// 7 - 8
		imgGraphics.setColor(ColorRoutines.darken(c, 4 * spread1));
		imgGraphics.drawLine(0, 6, 1, 6);
		imgGraphics.drawLine(0, 7, 1, 7);
// 9 - 12
		imgGraphics.setColor(ColorRoutines.darken(c, 3 * spread1));
		imgGraphics.drawLine(0, 8, 1, 8);
		imgGraphics.drawLine(0, 9, 1, 9);
		imgGraphics.drawLine(0, 10, 1, 10);
		imgGraphics.drawLine(0, 11, 1, 11);
// 13 - 15
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 12, 1, 12);
		imgGraphics.drawLine(0, 13, 1, 13);
		imgGraphics.drawLine(0, 14, 1, 14);
// 16 - 17
		imgGraphics.setColor(ColorRoutines.darken(c, spread1));
		imgGraphics.drawLine(0, 15, 1, 15);
		imgGraphics.drawLine(0, 16, 1, 16);
// 18 - 19
		imgGraphics.setColor(c);
		imgGraphics.drawLine(0, 17, 1, 17);
		imgGraphics.drawLine(0, 18, 1, 18);
// 20...
		imgGraphics.setColor(ColorRoutines.lighten(c, 2 * spread2));
		imgGraphics.drawLine(0, 19, 1, 19);
		imgGraphics.setColor(ColorRoutines.lighten(c, 4 * spread2));
		imgGraphics.drawLine(0, 20, 1, 20);
		imgGraphics.setColor(ColorRoutines.lighten(c, 5 * spread2));
		imgGraphics.drawLine(0, 21, 1, 21);
		imgGraphics.setColor(ColorRoutines.lighten(c, 6 * spread2));
		imgGraphics.drawLine(0, 22, 1, 22);
		imgGraphics.setColor(ColorRoutines.lighten(c, 8 * spread2));
		imgGraphics.drawLine(0, 23, 1, 23);
		imgGraphics.setColor(ColorRoutines.lighten(c, 9 * spread2));
		imgGraphics.drawLine(0, 24, 1, 24);
		// Note: Not specifying buttonLowerColor here was
		// a bug before 1.4.0
		buttonLowerColor = ColorRoutines.lighten(c, 10 * spread2);
		imgGraphics.setColor(buttonLowerColor);
		imgGraphics.drawLine(0, 25, 1, 25);
// 27
		imgGraphics.setColor(ColorRoutines.lighten(c, 4 * spread2));
		imgGraphics.drawLine(0, 26, 1, 26);
// 28
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 27, 1, 27);
// 29		
		if(isActive) {
    		imgGraphics.setColor(Theme.frameLightColor.getColor());
    	}
    	else {
    		imgGraphics.setColor(Theme.frameLightDisabledColor.getColor());
    	}
		imgGraphics.drawLine(0, 28, 1, 28);

		// dispose of image graphics
		imgGraphics.dispose();
		
		// paint image
		g.drawImage(img,
			x + 3, y, x + w - 3, y + 5,
			0, 0, 1, 5,
			window);
		g.drawImage(img,
			x, y + 5, x + w, y + titleHeight,
			0, 5, 1, titleHeight,
			window);
		
		
		// add the image to the cache
		cache.put(key, img);
		
		if(TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyFrameBorder.cache.size=" + cache.size());
		}
    }
    
    private void paintXPCaptionNoCache(Graphics g, int w, int h, boolean isActive, int titleHeight, Window window) {
    	Color c = null;
    	
		if(isActive) {
    		c = Theme.frameCaptionColor.getColor();
    	}
    	else {
    		c = Theme.frameCaptionDisabledColor.getColor();
    	}
		
    	g.setColor(c);
    	
    	int x = 0;
    	int y = 0;
    	int spread1 = Theme.frameSpreadDarkDisabled.getValue();
		int spread2 = Theme.frameSpreadLightDisabled.getValue();
		int y2 = y;
		Color borderColor = null;

		if(isActive) {
			borderColor = Theme.frameBorderColor.getColor();
    		spread1 = Theme.frameSpreadDark.getValue();
    		spread2 = Theme.frameSpreadLight.getValue();
    	}
    	else {
    		borderColor = Theme.frameBorderDisabledColor.getColor();
    	}

		// paint the semi-transparent parts
// 1
		// blend
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 82));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 156));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 215));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);		
		y2 ++;
// 2
		Color c2 = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(c2);
		g.drawLine(x, y2, x + 2, y2);	// left
		g.drawLine(x + w - 3, y2, x + w - 1, y2);	// right
		y2 ++;
// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2);	// left
		g.drawLine(x + w - 3, y2, x + w - 1, y2);	// right
		// darker border
		g.setColor(c);
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);		
		y2 ++;
// 4
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(ColorRoutines.lighten(c, 7 * spread2));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		g.setColor(ColorRoutines.lighten(c, 3 * spread2));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		y2 ++;
// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 2, y2, x + 2, y2);	// left
		g.drawLine(x + x + w - 3, y2, x + w - 3, y2);	// right
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(c);
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		
		// paint solid lines
		y2 = y;
// 1
		g.setColor(borderColor);
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2 ++;
// 2
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2 ++;
		
// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2 ++;
// 4
		g.setColor(c);
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2 ++;
// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2 ++;
//		 6
		buttonUpperColor = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(buttonUpperColor);
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
// 7 - 8
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.fillRect(x, y2, x + w, 2);
		y2 += 2;
// 9 - 12
		g.setColor(ColorRoutines.darken(c, 3 * spread1));
		g.fillRect(x, y2, x + w, 4);
		y2 += 4;
// 13 - 15
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.fillRect(x, y2, x + w, 3);
		y2 += 3;
// 16 - 17
		g.setColor(ColorRoutines.darken(c, 1 * spread1));
		g.fillRect(x, y2, x + w, 2);
		y2 += 2;
// 18 - 19
		g.setColor(c);
		g.fillRect(x, y2, x + w, 2);
		y2 += 2;
// 20...
		g.setColor(ColorRoutines.lighten(c, 2 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 6 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 8 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
		g.setColor(ColorRoutines.lighten(c, 9 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
		buttonLowerColor = ColorRoutines.lighten(c, 10 * spread2);
		g.setColor(buttonLowerColor);
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
// 27
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
// 28
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x, y2, x + w - 1, y2);
		y2 ++;
// 29		
		if(isActive) {
    		g.setColor(Theme.frameLightColor.getColor());
    	}
    	else {
    		g.setColor(Theme.frameLightDisabledColor.getColor());
    	}
		
		g.drawLine(x, y2, x + w - 1, y2);
    }

    /**
	 * Convenience method to clip the passed in text to the specified size.
	 */
    private String clippedText(String text, FontMetrics fm,
                                 int availTextWidth) {
        if((text == null) || (text.equals("")))  {
            return "";
        }
        int textWidth = SwingUtilities.computeStringWidth(fm, text);
        String clipString = "...";
        if(textWidth > availTextWidth) {
            int totalWidth = SwingUtilities.computeStringWidth(fm, clipString);
            int nChars;
            for(nChars = 0; nChars < text.length(); nChars++) {
                totalWidth += fm.charWidth(text.charAt(nChars));
                if(totalWidth > availTextWidth) {
                    break;
                }
            }
            text = text.substring(0, nChars) + clipString;
        }
        return text;
    }
    
    private int getInt(Object key, int defaultValue) {
        Object value = UIManager.get(key);

        if(value instanceof Integer) {
            return ((Integer)value).intValue();
        }
        if(value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            } catch (NumberFormatException nfe) {}
        }
        return defaultValue;
    }



    /**
     * Actions used to <code>close</code> the <code>Window</code>.
     */
    private class CloseAction extends AbstractAction {
        public CloseAction() {
            super(UIManager.getString("MetalTitlePane.closeTitle",
                                      getLocale()));
            
//            System.out.println("closeTitle=" + 
//        		UIManager.getString("MetalTitlePane.closeTitle",
//                    getLocale()));
        }

        public void actionPerformed(ActionEvent e) {
            close();
        }      
    }


    /**
     * Actions used to <code>iconfiy</code> the <code>Frame</code>.
     */
    private class IconifyAction extends AbstractAction {
        public IconifyAction() {
            super(UIManager.getString("MetalTitlePane.iconifyTitle",
                                      getLocale()));
        }

        public void actionPerformed(ActionEvent e) {
            iconify();
        }
    } 


    /**
     * Actions used to <code>restore</code> the <code>Frame</code>.
     */
    private class RestoreAction extends AbstractAction {
        public RestoreAction() {
            super(UIManager.getString
                  ("MetalTitlePane.restoreTitle", getLocale()));
        }

        public void actionPerformed(ActionEvent e) {
            restore();
        }
    }


    /**
     * Actions used to <code>restore</code> the <code>Frame</code>.
     */
    private class MaximizeAction extends AbstractAction {
    	
        public MaximizeAction() {
            super(UIManager.getString("MetalTitlePane.maximizeTitle", getLocale()));
        }

        public void actionPerformed(ActionEvent e) {
            maximize();
        }
    }


    /**
     * Class responsible for drawing the system menu. Looks up the
     * image to draw from the Frame associated with the
     * <code>JRootPane</code>.
     */
    private class SystemMenuBar extends JMenuBar {
    	
        public void paint(Graphics g) {
        	int height = getHeight();
        	
            if(systemIcon != null) {
                g.drawImage(systemIcon, 0, (height - IMAGE_HEIGHT) / 2 + 1, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            }
            else {
            	Icon icon = UIManager.getIcon("InternalFrame.icon");
            	
                if(icon != null) {
      				icon.paintIcon(this, g, 0, (height - icon.getIconHeight()) / 2 + 1);
                }
            }
        }
        
        private Window getOwningFrame(Dialog dialog) {
        	while(true) {
        		Window w = ((Dialog)dialog).getOwner();
        		
        		if(w == null) return w;
        		
        		if(!(w instanceof Dialog)) return w;
        		
        		dialog = (Dialog)w;
        	}
        }
 
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
        
        public Dimension getPreferredSize() {
        	if(systemIcon != null) {
        		return new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT);
            }
            else {
            	Icon icon = UIManager.getIcon("InternalFrame.icon");
            	
                if(icon != null) {
                	return new Dimension(icon.getIconWidth(), icon.getIconHeight());
                }
                
                Dimension size = super.getPreferredSize();

                return new Dimension(Math.max(IMAGE_WIDTH, size.width),
                                     Math.max(size.height, IMAGE_HEIGHT));
            }
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    private class TitlePaneLayout implements LayoutManager {
    	
        public void addLayoutComponent(String name, Component c) {}
        public void removeLayoutComponent(Component c) {}
        
        public Dimension preferredLayoutSize(Container c)  {
            return new Dimension(TinyLookAndFeel.MINIMUM_FRAME_WIDTH, computeHeight());
        }
        
        public Dimension minimumLayoutSize(Container c) {
            return preferredLayoutSize(c);
        } 
    
        private int computeHeight() {
        	Window window = getWindow();

            if(window instanceof Frame) {
        		setMaximizeBounds(getFrame());
        		
            	return TinyFrameBorder.FRAME_TITLE_HEIGHT;
        	}
            else if(window instanceof Dialog) {
            	return TinyFrameBorder.FRAME_TITLE_HEIGHT;
            }
        	else {
        		return TinyFrameBorder.FRAME_INTERNAL_TITLE_HEIGHT;
        	}
        }    
                    
        public void layoutContainer(Container c) {
            if(getWindowDecorationStyle() == JRootPane.NONE) {
                buttonsWidth = 0;
                return;
            }
            
            boolean leftToRight = (window == null) ?
                               getRootPane().getComponentOrientation().isLeftToRight() :
                               window.getComponentOrientation().isLeftToRight();

            int w = getWidth();
            int x; 
            int spacing;
            int buttonHeight; 
            int buttonWidth;
            
            if(closeButton != null) {
                buttonHeight = closeButton.getPreferredSize().height;
                buttonWidth = closeButton.getPreferredSize().width;
            }
            else {
                buttonHeight = IMAGE_HEIGHT;
                buttonWidth = IMAGE_WIDTH;
            }
            
            // Changed with 1.4.0: For frames and dialogs, move buttons one pixel down.
            int yd = (getHeight() == TinyFrameBorder.FRAME_TITLE_HEIGHT ? 1 : 0);
            int y = (getHeight() - buttonHeight) / 2 + yd;

            // assumes all buttons have the same dimensions,
            // these dimensions include the borders
            spacing = 5;
            x = leftToRight ? spacing : w - buttonWidth - spacing;
            
            if(menuBar != null) {
            	menuBar.setBounds(x, y, buttonWidth, buttonHeight);
            }

            x = leftToRight ? w : 0;
            spacing = 2;
            x += leftToRight ? -spacing -buttonWidth : spacing;
            
            if(closeButton != null) {
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);
            }

            if(!leftToRight) x += buttonWidth;

            if(Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
                if(toggleButton.getParent() != null) {
                    x += leftToRight ? -spacing -buttonWidth : spacing;
                    toggleButton.setBounds(x, y, buttonWidth, buttonHeight);
                    
                    if(!leftToRight) {
                        x += buttonWidth;
                    }
                }
            }

            if(iconifyButton != null && iconifyButton.getParent() != null) {
                x += leftToRight ? -spacing -buttonWidth : spacing;
                iconifyButton.setBounds(x, y, buttonWidth, buttonHeight);
                
                if(!leftToRight) {
                    x += buttonWidth;
                }
            }
            
            buttonsWidth = leftToRight ? w - x : x;
        }
    }



    /**
     * PropertyChangeListener installed on the Window. Updates the necessary
     * state as the state of the Window changes.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent pce) {
            String name = pce.getPropertyName();

            // Frame.state isn't currently bound.
            if("resizable".equals(name) || "state".equals(name)) {
                Frame frame = getFrame();

                if(frame != null) {
                    setState(frame.getExtendedState(), true);
                }
                
                if("resizable".equals(name)) {
                    getRootPane().repaint();
                }
            }
            else if("title".equals(name)) {
                repaint();
            }
            else if("componentOrientation".equals(name)) {
                revalidate();
                repaint();
            }
            else if("iconImage".equals(name)) {
                updateSystemIcon();
                revalidate();
                repaint();
            }
        }
    }


    /**
     * WindowListener installed on the Window, updates the state as necessary.
     */
    private class WindowHandler extends WindowAdapter {
        public void windowActivated(WindowEvent ev) {
            setActive(true);
        }

        public void windowDeactivated(WindowEvent ev) {
            setActive(false);
        }
    }
    
    class WindowMoveListener extends ComponentAdapter {

		public void componentMoved(ComponentEvent e) {
			if(getWindowDecorationStyle() == JRootPane.NONE) return;
			
			// paint the non-opaque upper edges
			Window w = getWindow();
			
			if(!w.isShowing()) return;

			w.repaint(0, 0, w.getWidth(), 5);
		}

		public void componentResized(ComponentEvent e) {
			if(getWindowDecorationStyle() == JRootPane.NONE) return;
			
			// paint the non-opaque upper edges
			Window w = getWindow();
			
			if(!w.isShowing()) return;
			
			w.repaint(0, 0, w.getWidth(), 5);
		}
    }
    
    /**
	 * CaptionKey is used as key in the cache HashMap.
	 * Overrides equals() and hashCode().
	 * Note: With 1.4.0 additionally the frame caption
	 * color is considered, else theme switching doesn't
	 * work well.
	 */
	private static class CaptionKey {

		private Color frameCaptionColor;
		private boolean isActive;
		private int titleHeight;

		CaptionKey(Color frameCaptionColor, boolean isActive, int titleHeight) {
			this.frameCaptionColor = frameCaptionColor;
			this.isActive = isActive;
			this.titleHeight = titleHeight;
		}
		
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof CaptionKey)) return false;

			CaptionKey other = (CaptionKey)o;
			
			return frameCaptionColor.equals(other.frameCaptionColor) &&
				isActive == other.isActive &&
				titleHeight == other.titleHeight;
		}
		
		public int hashCode() {
			return frameCaptionColor.hashCode() *
				(isActive ? 1 : 2) * titleHeight;
		}
	}
}  
