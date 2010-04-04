/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

import sun.swing.UIAction;

/**
 * TinyRootPaneUI
 * 
 * 10.9.07 Added mechanism to substitute actions added in BasicPopupUI
 * to make menus work as in XP (the hook is the FocusListener registered
 * at installUI()).
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyRootPaneUI extends BasicRootPaneUI implements FocusListener {

	/**
	 * Keys to lookup borders in defaults table.
	 */
	private static final String[] borderKeys = new String[] { null,
			"RootPane.frameBorder", "RootPane.plainDialogBorder",
			"RootPane.informationDialogBorder", "RootPane.errorDialogBorder",
			"RootPane.colorChooserDialogBorder",
			"RootPane.fileChooserDialogBorder",
			"RootPane.questionDialogBorder", "RootPane.warningDialogBorder" };

	/**
	 * The amount of space (in pixels) that the cursor is changed on.
	 */
	private static final int CORNER_DRAG_WIDTH = 16;

	/**
	 * Region from edges that dragging is active from.
	 */
	private static final int BORDER_DRAG_THICKNESS = 5;

	/**
	 * Window the <code>JRootPane</code> is in.
	 */
	private Window window;

	/**
	 * <code>JComponent</code> providing window decorations. This will be null
	 * if not providing window decorations.
	 */
	private JComponent titlePane;

	/**
	 * <code>MouseInputListener</code> that is added to the parent
	 * <code>Window</code> the <code>JRootPane</code> is contained in.
	 */
	private MouseInputListener mouseInputListener;

	/**
	 * The <code>LayoutManager</code> that is set on the
	 * <code>JRootPane</code>.
	 */
	private LayoutManager layoutManager;

	/**
	 * <code>LayoutManager</code> of the <code>JRootPane</code> before we
	 * replaced it.
	 */
	private LayoutManager savedOldLayout;

	/**
	 * <code>JRootPane</code> providing the look and feel for.
	 */
	private JRootPane root;

	/**
	 * <code>Cursor</code> used to track the cursor set by the user. This is
	 * initially <code>Cursor.DEFAULT_CURSOR</code>.
	 */
	private Cursor lastCursor = Cursor
		.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	/**
	 * The following support key presses on menus to work as in XP
	 */
	private AWTEventListener mouseHandler = new MouseHandler();

	private KeyEventPostProcessor keyPostProcessor = new KeyPostProcessor();

	private boolean topMenuToClose = false;

	private Vector registeredKeyCodes;

	private static final boolean PARENT = false;

	private static final boolean CHILD = true;

	private static final boolean FORWARD = true;

	private static final boolean BACKWARD = false;

	private static final String CANCEL = "cancel";
	
	private static final String RETURN = "return";

	private static final String SELECT_PARENT = "selectParent";

	private static final String SELECT_CHILD = "selectChild";

	/**
	 * Creates a UI for a <code>JRootPane</code>.
	 * 
	 * @param c
	 *            the JRootPane the RootPaneUI will be created for
	 * @return the RootPaneUI implementation for the passed in JRootPane
	 */
	public static ComponentUI createUI(JComponent c) {
		return new TinyRootPaneUI();
	}

	/**
	 * Invokes supers implementation of <code>installUI</code> to install the
	 * necessary state onto the passed in <code>JRootPane</code> to render the
	 * metal look and feel implementation of <code>RootPaneUI</code>. If the
	 * <code>windowDecorationStyle</code> property of the
	 * <code>JRootPane</code> is other than <code>JRootPane.NONE</code>,
	 * this will add a custom <code>Component</code> to render the widgets to
	 * <code>JRootPane</code>, as well as installing a custom
	 * <code>Border</code> and <code>LayoutManager</code> on the
	 * <code>JRootPane</code>.
	 * 
	 * @param c the JRootPane to install state onto
	 */
	public void installUI(JComponent c) {
		super.installUI(c);

		root = (JRootPane)c;
		int style = root.getWindowDecorationStyle();
		
		if(style != JRootPane.NONE) {
			installClientDecorations(root);
		}
	}

	private Vector getRegisteredKeyCodes(JRootPane root) {
		Vector v = new Vector();

		InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		if(im == null) return v;

		KeyStroke[] keys = im.allKeys();

		if(keys == null) return v;

		for(int i = 0; i < keys.length; i++) {
			v.add(new Integer(keys[i].getKeyCode()));
		}

		return v;
	}

	private ActionMap getMapForKey(ActionMap am, String key) {
		if(am == null) return null;

		if(am != null && am.keys() != null) {
			Object[] keys = am.keys();

			// for(int i = 0; i < keys.length; i++) {
			// System.out.println(keys[i] + " -> " + am.get(keys[i]));
			// }

			for(int i = 0; i < keys.length; i++) {
				if(key.equals(keys[i])) return am;
			}
		}

		return getMapForKey(am.getParent(), key);
	}

	boolean isTopMenuToClose() {
		return topMenuToClose;
	}

	private void addEscapeMenuHandlers() {
		// We add a mouse handler which will deactivate
		// any selected top menu as soon as the mouse is
		// moved or mouse button is pressed
		java.security.AccessController
			.doPrivileged(new java.security.PrivilegedAction() {
				public Object run() {
					Toolkit.getDefaultToolkit().addAWTEventListener(
						mouseHandler,
						AWTEvent.MOUSE_EVENT_MASK
							| AWTEvent.MOUSE_MOTION_EVENT_MASK
							| AWTEvent.MOUSE_WHEEL_EVENT_MASK);

//					System.out.println("escapeMenuHandler added");
					return null;
				}
			});

		// We add a key handler which will deactivate
		// any selected top menu as soon as a key is pressed
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
			.addKeyEventPostProcessor(keyPostProcessor);
	}

	private void removeEscapeMenuHandlers() {
		// Remove the handlers registered at addEscapeMenuHandlers()
		java.security.AccessController
			.doPrivileged(new java.security.PrivilegedAction() {

				public Object run() {
					Toolkit.getDefaultToolkit().removeAWTEventListener(
						mouseHandler);

//					System.out.println("escapeMenuHandler removed");
					return null;
				}
			});

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
			.removeKeyEventPostProcessor(keyPostProcessor);
	}

	/**
	 * Invokes supers implementation to uninstall any of its state. This will
	 * also reset the <code>LayoutManager</code> of the <code>JRootPane</code>.
	 * If a <code>Component</code> has been added to the
	 * <code>JRootPane</code> to render the window decoration style, this
	 * method will remove it. Similarly, this will revert the Border and
	 * LayoutManager of the <code>JRootPane</code> to what it was before
	 * <code>installUI</code> was invoked.
	 * 
	 * @param c
	 *            the JRootPane to uninstall state from
	 */
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		uninstallClientDecorations(root);

		layoutManager = null;
		mouseInputListener = null;
		root = null;
	}
	
	protected void installListeners(JRootPane root) {
		super.installListeners(root);
		
		root.addFocusListener(this);
	}
	
	protected void uninstallListeners(JRootPane root) {
		super.uninstallListeners(root);
		
		root.removeFocusListener(this);
	}

	/**
	 * Installs the appropriate <code>Border</code> onto the
	 * <code>JRootPane</code>.
	 */
	void installBorder(JRootPane root) {
		int style = root.getWindowDecorationStyle();

		if(style == JRootPane.NONE) {
			LookAndFeel.uninstallBorder(root);
		}
		else {
			// installs an instance of TinyFrameBorder
			LookAndFeel.installBorder(root, borderKeys[style]);
		}
	}

	/**
	 * Removes any border that may have been installed.
	 */
	private void uninstallBorder(JRootPane root) {
		LookAndFeel.uninstallBorder(root);
	}

	/**
	 * Installs the necessary Listeners on the parent <code>Window</code>, if
	 * there is one.
	 * <p>
	 * This takes the parent so that cleanup can be done from
	 * <code>removeNotify</code>, at which point the parent hasn't been reset
	 * yet.
	 * 
	 * @param parent
	 *            The parent of the JRootPane
	 */
	private void installWindowListeners(JRootPane root, Component parent) {
		if(parent instanceof Window) {
			window = (Window)parent;
		}
		else {
			window = SwingUtilities.getWindowAncestor(parent);
		}

		if(window != null) {
			if(mouseInputListener == null) {
				mouseInputListener = createWindowMouseInputListener(root);
			}
			window.addMouseListener(mouseInputListener);
			window.addMouseMotionListener(mouseInputListener);
		}
	}

	/**
	 * Uninstalls the necessary Listeners on the <code>Window</code> the
	 * Listeners were last installed on.
	 */
	private void uninstallWindowListeners(JRootPane root) {
		if(window != null) {
			window.removeMouseListener(mouseInputListener);
			window.removeMouseMotionListener(mouseInputListener);
		}
	}

	/**
	 * Installs the appropriate LayoutManager on the <code>JRootPane</code> to
	 * render the window decorations.
	 */
	private void installLayout(JRootPane root) {
		if(layoutManager == null) {
			layoutManager = createLayoutManager();
		}
		savedOldLayout = root.getLayout();
		root.setLayout(layoutManager);
	}

	/**
	 * Uninstalls the previously installed <code>LayoutManager</code>.
	 */
	private void uninstallLayout(JRootPane root) {
		if(savedOldLayout != null) {
			root.setLayout(savedOldLayout);
			savedOldLayout = null;
		}
	}

	/**
	 * Installs the necessary state onto the JRootPane to render client
	 * decorations. This is ONLY invoked if the <code>JRootPane</code> has a
	 * decoration style other than <code>JRootPane.NONE</code>.
	 */
	private void installClientDecorations(JRootPane root) {
		installBorder(root);

		JComponent titlePane = createTitlePane(root);

		setTitlePane(root, titlePane);
		installWindowListeners(root, root.getParent());
		installLayout(root);

		if(window != null) {
			root.revalidate();
			root.repaint();
		}
	}

	/**
	 * Uninstalls any state that <code>installClientDecorations</code> has
	 * installed.
	 * <p>
	 * NOTE: This may be called if you haven't installed client decorations yet
	 * (ie before <code>installClientDecorations</code> has been invoked).
	 */
	private void uninstallClientDecorations(JRootPane root) {
		uninstallBorder(root);
		uninstallWindowListeners(root);
		setTitlePane(root, null);
		uninstallLayout(root);
		root.repaint();
		root.revalidate();
		// Reset the cursor, as we may have changed it to a resize cursor
		if(window != null) {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		window = null;
	}

	/**
	 * Returns the <code>JComponent</code> to render the window decoration
	 * style.
	 */
	private JComponent createTitlePane(JRootPane root) {
		return new TinyTitlePane(root, this);
	}

	/**
	 * Returns a <code>MouseListener</code> that will be added to the
	 * <code>Window</code> containing the <code>JRootPane</code>.
	 */
	private MouseInputListener createWindowMouseInputListener(JRootPane root) {
		return new MouseInputHandler();
	}

	/**
	 * Returns a <code>LayoutManager</code> that will be set on the
	 * <code>JRootPane</code>.
	 */
	private LayoutManager createLayoutManager() {
		return new MetalRootLayout();
	}

	/**
	 * Sets the window title pane -- the JComponent used to provide a plaf a way
	 * to override the native operating system's window title pane with one
	 * whose look and feel are controlled by the plaf. The plaf creates and sets
	 * this value; the default is null, implying a native operating system
	 * window title pane.
	 * 
	 * @param content
	 *            the <code>JComponent</code> to use for the window title
	 *            pane.
	 */
	private void setTitlePane(JRootPane root, JComponent titlePane) {
		JLayeredPane layeredPane = root.getLayeredPane();
		JComponent oldTitlePane = getTitlePane();

		if(oldTitlePane != null) {
			oldTitlePane.setVisible(false);
			layeredPane.remove(oldTitlePane);
		}
		
		if(titlePane != null) {
			layeredPane.add(titlePane, JLayeredPane.FRAME_CONTENT_LAYER);
			titlePane.setVisible(true);
		}
		
		this.titlePane = titlePane;
		root.validate();
		root.repaint();
	}

	/**
	 * Returns the <code>JComponent</code> rendering the title pane. If this
	 * returns null, it implies there is no need to render window decorations.
	 * 
	 * @return the current window title pane, or null
	 * @see #setTitlePane
	 */
	private JComponent getTitlePane() {
		return titlePane;
	}

	/**
	 * Returns the <code>JRootPane</code> we're providing the look and feel
	 * for.
	 */
	private JRootPane getRootPane() {
		return root;
	}

	/**
	 * Invoked when a property changes. <code>TinyRootPaneUI</code> is
	 * primarily interested in events originating from the
	 * <code>JRootPane</code> it has been installed on identifying the
	 * property <code>windowDecorationStyle</code>. If the
	 * <code>windowDecorationStyle</code> has changed to a value other than
	 * <code>JRootPane.NONE</code>, this will add a <code>Component</code>
	 * to the <code>JRootPane</code> to render the window decorations, as well
	 * as installing a <code>Border</code> on the <code>JRootPane</code>.
	 * On the other hand, if the <code>windowDecorationStyle</code> has
	 * changed to <code>JRootPane.NONE</code>, this will remove the
	 * <code>Component</code> that has been added to the
	 * <code>JRootPane</code> as well resetting the Border to what it was
	 * before <code>installUI</code> was invoked.
	 * 
	 * @param e
	 *            A PropertyChangeEvent object describing the event source and
	 *            the property that has changed.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		super.propertyChange(e);

		String propertyName = e.getPropertyName();
		if(propertyName == null) {
			return;
		}

		if(propertyName.equals("windowDecorationStyle")) {
			JRootPane root = (JRootPane)e.getSource();
			int style = root.getWindowDecorationStyle();

			// This is potentially more than needs to be done,
			// but it rarely happens and makes the install/uninstall process
			// simpler. MetalTitlePane also assumes it will be recreated if
			// the decoration style changes.
			uninstallClientDecorations(root);
			
			if(style != JRootPane.NONE) {
				installClientDecorations(root);
			}
		}
		else if(propertyName.equals("ancestor")) {
			uninstallWindowListeners(root);
			
			if(((JRootPane)e.getSource()).getWindowDecorationStyle() != JRootPane.NONE) {
				installWindowListeners(root, root.getParent());
			}
		}
		return;
	}
	
	// FocusListener impl
	public void focusGained(FocusEvent e) {
		if(topMenuToClose) {
			topMenuToClose = false;
			removeEscapeMenuHandlers();
		}
		else {
			JRootPane root = (JRootPane)e.getSource();
			
			// store a copy of all registered key codes
			registeredKeyCodes = getRegisteredKeyCodes(root);
			
			// Replace some actions from BasicPopupMenuUI.
			// We assume that there is a corresponding entry 
			// for each key in input map.
			ActionMap am = getMapForKey(root.getActionMap(), CANCEL);

			if(am != null) {
				am.put(CANCEL, new MenuActions(CANCEL));
				am.put(RETURN, new MenuActions(RETURN));
				am.put(SELECT_PARENT, new MenuActions(SELECT_PARENT));
				am.put(SELECT_CHILD, new MenuActions(SELECT_CHILD));
			}
		}
	}

	public void focusLost(FocusEvent e) {}

	/**
	 * A custom layout manager that is responsible for the layout of
	 * layeredPane, glassPane, menuBar and titlePane, if one has been installed.
	 */
	// NOTE: Ideally this would extends JRootPane.RootLayout, but that
	// would force this to be non-static.
	private static class MetalRootLayout implements LayoutManager2 {

		/**
		 * Returns the amount of space the layout would like to have.
		 * 
		 * @param the
		 *            Container for which this layout manager is being used
		 * @return a Dimension object containing the layout's preferred size
		 */
		public Dimension preferredLayoutSize(Container parent) {
			Dimension cpd, mbd, tpd;
			int cpWidth = 0;
			int cpHeight = 0;
			int mbWidth = 0;
			int mbHeight = 0;
			int tpWidth = 0;
			int tpHeight = 0;
			Insets i = parent.getInsets();
			JRootPane root = (JRootPane)parent;

			if(root.getContentPane() != null) {
				cpd = root.getContentPane().getPreferredSize();
			}
			else {
				cpd = root.getSize();
			}

			if(cpd != null) {
				cpWidth = cpd.width;
				cpHeight = cpd.height;
			}

			if(root.getJMenuBar() != null) {
				mbd = root.getJMenuBar().getPreferredSize();

				if(mbd != null) {
					mbWidth = mbd.width;
					mbHeight = mbd.height;
				}
			}

			if(root.getWindowDecorationStyle() != JRootPane.NONE
				&& (root.getUI() instanceof TinyRootPaneUI))
			{
				JComponent titlePane = ((TinyRootPaneUI)root.getUI()).getTitlePane();
				
				if(titlePane != null) {
					tpd = titlePane.getPreferredSize();

					if(tpd != null) {
						tpWidth = tpd.width;
						tpHeight = tpd.height;
					}
				}
			}

			return new Dimension(Math.max(Math.max(cpWidth, mbWidth), tpWidth)
				+ i.left + i.right, cpHeight + mbHeight + tpHeight + i.top
				+ i.bottom);
		}

		/**
		 * Returns the minimum amount of space the layout needs.
		 * 
		 * @param the
		 *            Container for which this layout manager is being used
		 * @return a Dimension object containing the layout's minimum size
		 */
		public Dimension minimumLayoutSize(Container parent) {
			Dimension cpd, mbd, tpd;
			int cpWidth = 0;
			int cpHeight = 0;
			int mbWidth = 0;
			int mbHeight = 0;
			int tpWidth = 0;
			int tpHeight = 0;
			Insets i = parent.getInsets();
			JRootPane root = (JRootPane)parent;

			if(root.getContentPane() != null) {
				cpd = root.getContentPane().getMinimumSize();
			}
			else {
				cpd = root.getSize();
			}

			if(cpd != null) {
				cpWidth = cpd.width;
				cpHeight = cpd.height;
			}

			if(root.getJMenuBar() != null) {
				mbd = root.getJMenuBar().getMinimumSize();

				if(mbd != null) {
					mbWidth = mbd.width;
					mbHeight = mbd.height;
				}
			}
			if(root.getWindowDecorationStyle() != JRootPane.NONE
				&& (root.getUI() instanceof TinyRootPaneUI)) {
				JComponent titlePane = ((TinyRootPaneUI)root.getUI())
					.getTitlePane();
				if(titlePane != null) {
					tpd = titlePane.getMinimumSize();

					if(tpd != null) {
						tpWidth = tpd.width;
						tpHeight = tpd.height;
					}
				}
			}

			return new Dimension(Math.max(Math.max(cpWidth, mbWidth), tpWidth)
				+ i.left + i.right, cpHeight + mbHeight + tpHeight + i.top
				+ i.bottom);
		}

		/**
		 * Returns the maximum amount of space the layout can use.
		 * 
		 * @param the
		 *            Container for which this layout manager is being used
		 * @return a Dimension object containing the layout's maximum size
		 */
		public Dimension maximumLayoutSize(Container target) {
			Dimension cpd, mbd, tpd;
			int cpWidth = Integer.MAX_VALUE;
			int cpHeight = Integer.MAX_VALUE;
			int mbWidth = Integer.MAX_VALUE;
			int mbHeight = Integer.MAX_VALUE;
			int tpWidth = Integer.MAX_VALUE;
			int tpHeight = Integer.MAX_VALUE;
			Insets i = target.getInsets();
			JRootPane root = (JRootPane)target;

			if(root.getContentPane() != null) {
				cpd = root.getContentPane().getMaximumSize();
				if(cpd != null) {
					cpWidth = cpd.width;
					cpHeight = cpd.height;
				}
			}

			if(root.getJMenuBar() != null) {
				mbd = root.getJMenuBar().getMaximumSize();
				if(mbd != null) {
					mbWidth = mbd.width;
					mbHeight = mbd.height;
				}
			}

			if(root.getWindowDecorationStyle() != JRootPane.NONE
				&& (root.getUI() instanceof TinyRootPaneUI)) {
				JComponent titlePane = ((TinyRootPaneUI)root.getUI())
					.getTitlePane();
				if(titlePane != null) {
					tpd = titlePane.getMaximumSize();
					if(tpd != null) {
						tpWidth = tpd.width;
						tpHeight = tpd.height;
					}
				}
			}

			int maxHeight = Math.max(Math.max(cpHeight, mbHeight), tpHeight);
			// Only overflows if 3 real non-MAX_VALUE heights, sum to >
			// MAX_VALUE
			// Only will happen if sums to more than 2 billion units. Not
			// likely.
			if(maxHeight != Integer.MAX_VALUE) {
				maxHeight = cpHeight + mbHeight + tpHeight + i.top + i.bottom;
			}

			int maxWidth = Math.max(Math.max(cpWidth, mbWidth), tpWidth);
			// Similar overflow comment as above
			if(maxWidth != Integer.MAX_VALUE) {
				maxWidth += i.left + i.right;
			}

			return new Dimension(maxWidth, maxHeight);
		}

		/**
		 * Instructs the layout manager to perform the layout for the specified
		 * container.
		 * 
		 * @param the
		 *            Container for which this layout manager is being used
		 */
		public void layoutContainer(Container parent) {
			JRootPane root = (JRootPane)parent;
			Rectangle b = root.getBounds();
			Insets i = root.getInsets();
			int nextY = 0;
			int w = b.width - i.right - i.left;
			int h = b.height - i.top - i.bottom;

			if(root.getLayeredPane() != null) {
				root.getLayeredPane().setBounds(i.left, i.top, w, h);
			}
			if(root.getGlassPane() != null) {
				root.getGlassPane().setBounds(i.left, i.top, w, h);
			}
			// Note: This is laying out the children in the layeredPane,
			// technically, these are not our children.
			if(root.getWindowDecorationStyle() != JRootPane.NONE
				&& (root.getUI() instanceof TinyRootPaneUI)) {
				JComponent titlePane = ((TinyRootPaneUI)root.getUI())
					.getTitlePane();
				if(titlePane != null) {
					Dimension tpd = titlePane.getPreferredSize();
					if(tpd != null) {
						int tpHeight = tpd.height;
						titlePane.setBounds(0, 0, w, tpHeight);
						nextY += tpHeight;
					}
				}
			}
			if(root.getJMenuBar() != null) {
				Dimension mbd = root.getJMenuBar().getPreferredSize();
				root.getJMenuBar().setBounds(0, nextY, w, mbd.height);
				nextY += mbd.height;
			}
			if(root.getContentPane() != null) {
				Dimension cpd = root.getContentPane().getPreferredSize();
				root.getContentPane().setBounds(0, nextY, w,
					h < nextY ? 0 : h - nextY);
			}
		}

		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public void addLayoutComponent(Component comp, Object constraints) {
		}

		public float getLayoutAlignmentX(Container target) {
			return 0.0f;
		}

		public float getLayoutAlignmentY(Container target) {
			return 0.0f;
		}

		public void invalidateLayout(Container target) {
		}
	}

	/**
	 * Maps from positions to cursor type. Refer to calculateCorner and
	 * calculatePosition for details of this.
	 */
	private static final int[] cursorMapping = new int[] {
			Cursor.NW_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR,
			Cursor.N_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
			Cursor.NE_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, 0, 0, 0,
			Cursor.NE_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR, 0, 0, 0,
			Cursor.E_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, 0, 0, 0,
			Cursor.SE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
			Cursor.SW_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
			Cursor.SE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR };

	private class MouseHandler implements AWTEventListener {

		public void eventDispatched(AWTEvent e) {
			if(!topMenuToClose) return;

			// We don't have to check for the kind of event,
			// the only important thing is that a top menu
			// can be deactivated
			MenuSelectionManager.defaultManager().clearSelectedPath();

			topMenuToClose = false;
			removeEscapeMenuHandlers();
		}
	}

	private class KeyPostProcessor implements KeyEventPostProcessor {

		public boolean postProcessKeyEvent(KeyEvent e) {
			if(!topMenuToClose) return false;
			if(e.getID() != KeyEvent.KEY_PRESSED) return false;
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) return false;

			MenuElement path[] = MenuSelectionManager.defaultManager()
				.getSelectedPath();
			boolean isRegisteredKeyStroke = false;

			Iterator ii = registeredKeyCodes.iterator();
			while(ii.hasNext()) {
				int keyCode = ((Integer)ii.next()).intValue();

				if(keyCode == e.getKeyCode()) {
					if(path.length > 2) {
						// Key press re-opened popup
						topMenuToClose = false;
						removeEscapeMenuHandlers();
					}

					return false;
				}
			}

			// Key pressed is not registered in InputMap
			if(path.length == 2) {
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}

			topMenuToClose = false;
			removeEscapeMenuHandlers();

			return false;
		}
	}

	/**
	 * MouseInputHandler is responsible for handling resize/moving of the
	 * Window. It sets the cursor directly on the Window when then mouse moves
	 * over a hot spot.
	 */
	private class MouseInputHandler implements MouseInputListener {

		/**
		 * Set to true if the drag operation is moving the window.
		 */
		private boolean isMovingWindow;

		/**
		 * Used to determine the corner the resize is occuring from.
		 */
		private int dragCursor;

		/**
		 * X location the mouse went down on for a drag operation.
		 */
		private int dragOffsetX;

		/**
		 * Y location the mouse went down on for a drag operation.
		 */
		private int dragOffsetY;

		/**
		 * Width of the window when the drag started.
		 */
		private int dragWidth;

		/**
		 * Height of the window when the drag started.
		 */
		private int dragHeight;

		public void mousePressed(MouseEvent ev) {
			JRootPane rootPane = getRootPane();

			if(rootPane.getWindowDecorationStyle() == JRootPane.NONE) {
				return;
			}

			Point dragWindowOffset = ev.getPoint();
			Window w = (Window)ev.getSource();
			Point convertedDragWindowOffset = SwingUtilities.convertPoint(w,
				dragWindowOffset, getTitlePane());

			Frame f = null;
			Dialog d = null;

			if(w instanceof Frame) {
				f = (Frame)w;
			}
			else if(w instanceof Dialog) {
				d = (Dialog)w;
			}

			int frameState = (f != null ? f.getExtendedState() : 0);

			if(getTitlePane() != null
				&& getTitlePane().contains(convertedDragWindowOffset))
			{
				if(ev.getClickCount() == 2) {
					if(f != null && f.isResizable()) {
						if((frameState & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ
							|| (frameState & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT)
						{
							f.setExtendedState(frameState
								& ~Frame.MAXIMIZED_BOTH);
						}
						else {
							f.setExtendedState(frameState
								| Frame.MAXIMIZED_BOTH);
						}
						return;
					}
				}
				
				if(((f != null && ((frameState & Frame.MAXIMIZED_HORIZ) != Frame.MAXIMIZED_HORIZ &&
					(frameState & Frame.MAXIMIZED_VERT) != Frame.MAXIMIZED_VERT)) || (d != null))
					&& dragWindowOffset.y >= BORDER_DRAG_THICKNESS
					&& dragWindowOffset.x >= BORDER_DRAG_THICKNESS
					&& dragWindowOffset.x < w.getWidth() - BORDER_DRAG_THICKNESS)
				{
					isMovingWindow = true;
					dragOffsetX = dragWindowOffset.x;
					dragOffsetY = dragWindowOffset.y;
					return;
				}
			}
			
			if((f != null && f.isResizable() &&
				((frameState & Frame.MAXIMIZED_HORIZ) != Frame.MAXIMIZED_HORIZ &&
				(frameState & Frame.MAXIMIZED_VERT) != Frame.MAXIMIZED_VERT)) ||
				(d != null && d.isResizable()))
			{
				dragOffsetX = dragWindowOffset.x;
				dragOffsetY = dragWindowOffset.y;
				dragWidth = w.getWidth();
				dragHeight = w.getHeight();
				int corner = calculateCorner(
					w, dragWindowOffset.x, dragWindowOffset.y);
				dragCursor = getCursor(corner);
			}
		}

		public void mouseReleased(MouseEvent ev) {
			if(dragCursor != 0 && window != null && !window.isValid()) {
				// Some Window systems validate as you resize, others won't,
				// thus the check for validity before repainting.
				window.validate();
				getRootPane().repaint();
			}
			
			isMovingWindow = false;
			dragCursor = 0;
			Window w = (Window)ev.getSource();
			
			if(w.getCursor() != lastCursor) {
				w.setCursor(lastCursor);
			}
		}

		public void mouseMoved(MouseEvent ev) {
			JRootPane root = getRootPane();

			if(root.getWindowDecorationStyle() == JRootPane.NONE) {
				return;
			}

			Window w = (Window)ev.getSource();

			Frame f = null;
			Dialog d = null;

			if(w instanceof Frame) {
				f = (Frame)w;
			}
			else if(w instanceof Dialog) {
				d = (Dialog)w;
			}

			// Update the cursor
			int cursor = getCursor(calculateCorner(w, ev.getX(), ev.getY()));

			if(cursor != 0 &&
				((f != null && (f.isResizable() &&
				(f.getExtendedState() & Frame.MAXIMIZED_VERT) != Frame.MAXIMIZED_VERT &&
				(f.getExtendedState() & Frame.MAXIMIZED_HORIZ) != Frame.MAXIMIZED_HORIZ)) ||
				(d != null && d.isResizable())))
			{
				w.setCursor(Cursor.getPredefinedCursor(cursor));
			}
			else {
				w.setCursor(lastCursor);
			}
		}

		private void adjust(Rectangle bounds, Dimension min, int deltaX,
			int deltaY, int deltaWidth, int deltaHeight)
		{
			bounds.x += deltaX;
			bounds.y += deltaY;
			bounds.width += deltaWidth;
			bounds.height += deltaHeight;
			
			if(min != null) {
				if(bounds.width < min.width) {
					int correction = min.width - bounds.width;
					
					if(deltaX != 0) {
						bounds.x -= correction;
					}
					
					bounds.width = min.width;
				}
				
				if(bounds.height < min.height) {
					int correction = min.height - bounds.height;
					
					if(deltaY != 0) {
						bounds.y -= correction;
					}
					
					bounds.height = min.height;
				}
			}
		}

		public void mouseDragged(MouseEvent ev) {
			Window w = (Window)ev.getSource();
			Point pt = ev.getPoint();

			if(isMovingWindow) {
				Point windowPt = w.getLocationOnScreen();

				windowPt.x += pt.x - dragOffsetX;
				windowPt.y += pt.y - dragOffsetY;
				w.setLocation(windowPt);
			}
			else if(dragCursor != 0) {
				Rectangle r = w.getBounds();
				Rectangle startBounds = new Rectangle(r);
				Dimension min = w.getMinimumSize();

				switch(dragCursor) {
					case Cursor.E_RESIZE_CURSOR:
						adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX)
							- r.width, 0);
						break;
					case Cursor.S_RESIZE_CURSOR:
						adjust(r, min, 0, 0, 0, pt.y
							+ (dragHeight - dragOffsetY) - r.height);
						break;
					case Cursor.N_RESIZE_CURSOR:
						adjust(r, min, 0, pt.y - dragOffsetY, 0,
							-(pt.y - dragOffsetY));
						break;
					case Cursor.W_RESIZE_CURSOR:
						adjust(r, min, pt.x - dragOffsetX, 0,
							-(pt.x - dragOffsetX), 0);
						break;
					case Cursor.NE_RESIZE_CURSOR:
						adjust(r, min, 0, pt.y - dragOffsetY, pt.x
							+ (dragWidth - dragOffsetX) - r.width,
							-(pt.y - dragOffsetY));
						break;
					case Cursor.SE_RESIZE_CURSOR:
						adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX)
							- r.width, pt.y + (dragHeight - dragOffsetY)
							- r.height);
						break;
					case Cursor.NW_RESIZE_CURSOR:
						adjust(r, min, pt.x - dragOffsetX, pt.y - dragOffsetY,
							-(pt.x - dragOffsetX), -(pt.y - dragOffsetY));
						break;
					case Cursor.SW_RESIZE_CURSOR:
						adjust(r, min, pt.x - dragOffsetX, 0,
							-(pt.x - dragOffsetX), pt.y
								+ (dragHeight - dragOffsetY) - r.height);
						break;
					default:
						break;
				}
				
				if(!r.equals(startBounds)) {
					w.setBounds(r);

					// Defer repaint/validate on mouseReleased unless dynamic
					// layout is active.
					// [not active on my system... (Win 2000 Server)]
					if(Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
						w.validate();
						getRootPane().repaint();
					}
				}
			}
		}

		public void mouseEntered(MouseEvent ev) {
			Window w = (Window)ev.getSource();
			lastCursor = w.getCursor();

			mouseMoved(ev);
		}

		public void mouseExited(MouseEvent ev) {
			Window w = (Window)ev.getSource();
			w.setCursor(lastCursor);
		}

		public void mouseClicked(MouseEvent ev) {}

		/**
		 * Returns the corner that contains the point <code>x</code>,
		 * <code>y</code>, or -1 if the position doesn't match a corner.
		 */
		private int calculateCorner(Component c, int x, int y) {
			int xPosition = calculatePosition(x, c.getWidth());
			int yPosition = calculatePosition(y, c.getHeight());

			if(xPosition == -1 || yPosition == -1) {
				return -1;
			}
			
			return yPosition * 5 + xPosition;
		}

		/**
		 * Returns the Cursor to render for the specified corner. This returns 0
		 * if the corner doesn't map to a valid Cursor
		 */
		private int getCursor(int corner) {
			if(corner == -1) {
				return 0;
			}
			
			return cursorMapping[corner];
		}

		/**
		 * Returns an integer indicating the position of <code>spot</code> in
		 * <code>width</code>. The return value will be: 0 if <
		 * BORDER_DRAG_THICKNESS 1 if < CORNER_DRAG_WIDTH 2 if >=
		 * CORNER_DRAG_WIDTH && < width - BORDER_DRAG_THICKNESS 3 if >= width -
		 * CORNER_DRAG_WIDTH 4 if >= width - BORDER_DRAG_THICKNESS 5 otherwise
		 */
		private int calculatePosition(int spot, int width) {
			if(spot < BORDER_DRAG_THICKNESS) {
				return 0;
			}
			if(spot < CORNER_DRAG_WIDTH) {
				return 1;
			}
			if(spot >= (width - BORDER_DRAG_THICKNESS)) {
				return 4;
			}
			if(spot >= (width - CORNER_DRAG_WIDTH)) {
				return 3;
			}
			return 2;
		}
	}

	/*
	 * Is a sun.swing.UIAction in BasicPopupMenuUI, but sun.swing.UIAction is
	 * new in JDK 1.5
	 */
	private class MenuActions extends AbstractAction {

		private String name;

		MenuActions(String name) {
			super(name);

			this.name = name;
		}

		public void actionPerformed(ActionEvent e) {
			if(CANCEL.equals(name)) {
				cancel();
			}
			else if(SELECT_PARENT.equals(name)) {
				selectParentChild(PARENT);
			}
			else if(SELECT_CHILD.equals(name)) {
				selectParentChild(CHILD);
			}
			else if(RETURN.equals(name)) {
                doReturn();
            }
		}

		/**
		 * Called if user pressed ESCAPE. In contrast to Java LAF,
		 * we select a top menu as its popup is closed.
		 *
		 */
		private void cancel() {
			// 4234793: This action should call JPopupMenu.firePopupMenuCanceled
			// but it's
			// a protected method. The real solution could be to make
			// firePopupMenuCanceled public and call it directly.
			JPopupMenu lastPopup = (JPopupMenu)getLastPopup();

			if(lastPopup != null) {
				lastPopup.putClientProperty("JPopupMenu.firePopupMenuCanceled",
					Boolean.TRUE);
			}

			MenuElement path[] = MenuSelectionManager.defaultManager()
				.getSelectedPath();
			// System.out.println("path.length=" + path.length);

			// if(path.length > 4) { /* PENDING(arnaud) Change this to 2 when a
			// mouse grabber is available for MenuBar */
			if(path.length > 2) {
				int newSize = Math.max(2, path.length - 2);
				MenuElement newPath[] = new MenuElement[newSize];
				System.arraycopy(path, 0, newPath, 0, newSize);
				MenuSelectionManager.defaultManager().setSelectedPath(newPath);

				if(newSize == 2 && !topMenuToClose) {
					topMenuToClose = true;
					addEscapeMenuHandlers();
				}
			}
			else {
				MenuSelectionManager.defaultManager().clearSelectedPath();

				if(topMenuToClose) {
					topMenuToClose = false;
					removeEscapeMenuHandlers();
				}
			}
		}
		
		/**
		 * Called if user pressed RETURN or SPACE. In contrast
		 * to Java LAF, we select the first enabled entry of newly
		 * opened popups.
		 *
		 */
		private void doReturn() {
			KeyboardFocusManager fmgr = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
			Component focusOwner = fmgr.getFocusOwner();
			
			if(focusOwner != null && !(focusOwner instanceof JRootPane)) {
				return;
			}

			MenuSelectionManager msm = MenuSelectionManager.defaultManager();
			MenuElement path[] = msm.getSelectedPath();
			MenuElement lastElement;
			
			if(path.length > 0) {
				lastElement = path[path.length - 1];
				
				if(lastElement instanceof JMenu) {
					JPopupMenu popup = ((JMenu)lastElement).getPopupMenu();
					MenuElement nextItem = findEnabledChild(
						popup.getSubElements(), -1, true);
					
					if(nextItem != null) {
						MenuElement newPath[] = new MenuElement[path.length + 2];
						System.arraycopy(path, 0, newPath, 0, path.length);
						newPath[path.length] = popup;
						newPath[path.length + 1] = nextItem;
						msm.setSelectedPath(newPath);
					}
					else {
						MenuElement newPath[] = new MenuElement[path.length + 1];
						System.arraycopy(path, 0, newPath, 0, path.length);
						newPath[path.length] = popup;
						msm.setSelectedPath(newPath);
					}
				}
				else if(lastElement instanceof JMenuItem) {
					JMenuItem mi = (JMenuItem)lastElement;

					if(mi.getUI() instanceof TinyMenuItemUI) {
						((TinyMenuItemUI)mi.getUI()).doClick(msm);
					}
					else {
						msm.clearSelectedPath();
						mi.doClick(0);
					}
				}
			}
		}

		/**
		 * Called if user pressed LEFT resp. RIGHT. In contrast
		 * to Java LAF, we select the first enabled entry of newly
		 * opened popups.
		 * @param direction
		 */
		private void selectParentChild(boolean direction) {
			MenuSelectionManager msm = MenuSelectionManager.defaultManager();
			MenuElement path[] = msm.getSelectedPath();
			int len = path.length;

			if(direction == PARENT) {
				// selecting parent	(LEFT)
				int popupIndex = len - 1;

				if(len > 2 &&
					// check if we have an open submenu. A submenu item may or
					// may not be selected, so submenu popup can be either the
					// last or next to the last item.
					(path[popupIndex] instanceof JPopupMenu || path[--popupIndex] instanceof JPopupMenu)
					&& !((JMenu)path[popupIndex - 1]).isTopLevelMenu())
				{
					// we have a submenu, just close it
					MenuElement newPath[] = new MenuElement[popupIndex];
					System.arraycopy(path, 0, newPath, 0, popupIndex);
					msm.setSelectedPath(newPath);
					return;
				}
			}
			else {
				// selecting child (RIGHT)
				if(len > 0 && path[len - 1] instanceof JMenu
					&& !((JMenu)path[len - 1]).isTopLevelMenu())
				{
					// we have a submenu, open it
					JMenu menu = (JMenu)path[len - 1];
					JPopupMenu popup = menu.getPopupMenu();
					MenuElement[] subs = popup.getSubElements();
					MenuElement item = findEnabledChild(subs, -1, true);
					MenuElement[] newPath;

					if(item == null) {
						newPath = new MenuElement[len + 1];
					}
					else {
						newPath = new MenuElement[len + 2];
						newPath[len + 1] = item;
					}
					
					System.arraycopy(path, 0, newPath, 0, len);
					newPath[len] = popup;
					msm.setSelectedPath(newPath);
					return;
				}
			}

			// check if we have a toplevel menu selected.
			// If this is the case, we select another toplevel menu
			if(len > 1 && path[0] instanceof JMenuBar) {
				MenuElement currentMenu = path[1];
				MenuElement nextMenu = findEnabledChild(path[0]
					.getSubElements(), currentMenu, direction);

				if(nextMenu != null && nextMenu != currentMenu) {
					MenuElement newSelection[] = null;
					if(len == 2) {
						// menu is selected but its popup not shown
						newSelection = new MenuElement[2];
						newSelection[0] = path[0];
						newSelection[1] = nextMenu;
					}
					else {
						// menu is selected and its popup is shown
						JPopupMenu popup = ((JMenu)nextMenu).getPopupMenu();
						MenuElement nextItem = findEnabledChild(
							popup.getSubElements(), -1, true);
						
						if(nextItem != null) {
							newSelection = new MenuElement[4];
							newSelection[0] = path[0];
							newSelection[1] = nextMenu;
							newSelection[2] = popup;
							newSelection[3] = nextItem;
						}
						else {
							// popup has no enabled child
							newSelection = new MenuElement[3];
							newSelection[0] = path[0];
							newSelection[1] = nextMenu;
							newSelection[2] = popup;
						}
					}
					msm.setSelectedPath(newSelection);
				}
			}
		}

		private void selectItem(boolean direction) {
			MenuSelectionManager msm = MenuSelectionManager.defaultManager();
			MenuElement path[] = msm.getSelectedPath();
			
			if(path.length < 2) return;

			int len = path.length;

			if(path[0] instanceof JMenuBar && path[1] instanceof JMenu && len == 2) {
				// a toplevel menu is selected, but its popup not shown.
				// Show the popup and select the first item
				JPopupMenu popup = ((JMenu)path[1]).getPopupMenu();
				MenuElement next = findEnabledChild(popup.getSubElements(), -1,
					FORWARD);
				MenuElement[] newPath;

				if(next != null) {
					// an enabled item found -- include it in newPath
					newPath = new MenuElement[4];
					newPath[3] = next;
				}
				else {
					// menu has no enabled items -- still must show the popup
					newPath = new MenuElement[3];
				}
				
				System.arraycopy(path, 0, newPath, 0, 2);
				newPath[2] = popup;
				msm.setSelectedPath(newPath);

			}
			else if(path[len - 1] instanceof JPopupMenu && path[len - 2] instanceof JMenu) {
				// a menu (not necessarily toplevel) is open and its popup
				// shown. Select the appropriate menu item
				JMenu menu = (JMenu)path[len - 2];
				JPopupMenu popup = menu.getPopupMenu();
				MenuElement next = findEnabledChild(popup.getSubElements(), -1,
					direction);

				if(next != null) {
					MenuElement[] newPath = new MenuElement[len + 1];
					System.arraycopy(path, 0, newPath, 0, len);
					newPath[len] = next;
					msm.setSelectedPath(newPath);
				}
				else {
					// all items in the popup are disabled.
					// We're going to find the parent popup menu and select
					// its next item. If there's no parent popup menu (i.e.
					// current menu is toplevel), do nothing
					if(len > 2 && path[len - 3] instanceof JPopupMenu) {
						popup = ((JPopupMenu)path[len - 3]);
						next = findEnabledChild(popup.getSubElements(), menu,
							direction);

						if(next != null && next != menu) {
							MenuElement[] newPath = new MenuElement[len - 1];
							System.arraycopy(path, 0, newPath, 0, len - 2);
							newPath[len - 2] = next;
							msm.setSelectedPath(newPath);
						}
					}
				}

			}
			else {
				// just select the next item, no path expansion needed
				MenuElement subs[] = path[len - 2].getSubElements();
				MenuElement nextChild = findEnabledChild(subs, path[len - 1],
					direction);
				if(nextChild == null) {
					nextChild = findEnabledChild(subs, -1, direction);
				}
				if(nextChild != null) {
					path[len - 1] = nextChild;
					msm.setSelectedPath(path);
				}
			}
		}

		private JPopupMenu getLastPopup() {
			MenuSelectionManager msm = MenuSelectionManager.defaultManager();
			MenuElement[] p = msm.getSelectedPath();
			JPopupMenu popup = null;

			for(int i = p.length - 1; popup == null && i >= 0; i--) {
				if(p[i] instanceof JPopupMenu) popup = (JPopupMenu)p[i];
			}

			return popup;
		}

		private MenuElement findEnabledChild(MenuElement e[], int fromIndex,
			boolean forward)
		{
			MenuElement result = null;
			if(forward) {
				result = nextEnabledChild(e, fromIndex + 1, e.length - 1);
				if(result == null)
					result = nextEnabledChild(e, 0, fromIndex - 1);
			}
			else {
				result = previousEnabledChild(e, fromIndex - 1, 0);
				if(result == null)
					result = previousEnabledChild(e, e.length - 1,
						fromIndex + 1);
			}
			return result;
		}

		private MenuElement nextEnabledChild(MenuElement e[], int fromIndex,
			int toIndex) {
			for(int i = fromIndex; i <= toIndex; i++) {
				if(e[i] != null) {
					Component comp = e[i].getComponent();
					if(comp != null
						&& (comp.isEnabled() || UIManager
							.getBoolean("MenuItem.disabledAreNavigable"))
						&& comp.isVisible()) {
						return e[i];
					}
				}
			}
			return null;
		}

		private MenuElement previousEnabledChild(MenuElement e[],
			int fromIndex, int toIndex) {
			for(int i = fromIndex; i >= toIndex; i--) {
				if(e[i] != null) {
					Component comp = e[i].getComponent();
					if(comp != null
						&& (comp.isEnabled() || UIManager
							.getBoolean("MenuItem.disabledAreNavigable"))
						&& comp.isVisible()) {
						return e[i];
					}
				}
			}
			return null;
		}

		private MenuElement findEnabledChild(MenuElement e[], MenuElement elem,
			boolean forward)
		{
			for(int i = 0; i < e.length; i++) {
				if(e[i] == elem) {
					return findEnabledChild(e, i, forward);
				}
			}
			return null;
		}
	}
}
