package com.digitprop.tonic;


import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;


/**	UI delegate for JScrollPanes.
 * 
 * 	@author	Markus Fischer
 *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307  USA
 * 
 * You can contact the author at:
 *    Markus Fischer
 *    www.digitprop.com
 *    info@digitprop.com
 * ------------------------------------------------------------------------
 */
public class ScrollPaneUI  extends javax.swing.plaf.ScrollPaneUI implements ScrollPaneConstants
{
	/**	The underlying scroll pane */
   protected JScrollPane scrollpane;
   
   protected ChangeListener vsbChangeListener;
   
   protected ChangeListener hsbChangeListener;
   
   protected ChangeListener viewportChangeListener;
   
   protected PropertyChangeListener spPropertyChangeListener;
   
   private MouseWheelListener mouseScrollListener;

   /**	PropertyChangeListener installed on the vertical scrollbar. */
   private PropertyChangeListener vsbPropertyChangeListener;

	/**	PropertyChangeListener installed on the horizontal scrollbar. */
   private PropertyChangeListener hsbPropertyChangeListener;

	/**	The default implementation of createHSBPropertyChangeListener and
	 * 	createVSBPropertyChangeListener share the PropertyChangeListener, which
	 * 	is this ivar.
	 */
	private PropertyChangeListener sbPropertyChangeListener;

    /**	State flag that shows whether setValue() was called from a user program
     * 	before the value of "extent" was set in right-to-left component
     * 	orientation.
     */
	private boolean setValueCalled = false;

   
   /**	Creates and returns the UI delegate for the specified component */
	public static ComponentUI createUI(JComponent component) 
   {
		return new ScrollPaneUI();
	}


	/**	Paints the specified component */
	public void paint(Graphics g, JComponent c) 
	{
		// Overridden to prevent superclass from painting anything 
	}


	/**	Returns the preferred size of the specified component. In this case,
	 * 	this will be null to indicate to the LayoutManager that it must
	 * 	compute this value.
	 */
	public Dimension getPreferredSize(JComponent c) 
	{
		return null;
	}


	/**	Returns the minimum size of the specified component */
	public Dimension getMinimumSize(JComponent c) 
	{
		return getPreferredSize(c);
	}


	/**	Returns the maximum size of the specified component */
	public Dimension getMaximumSize(JComponent c) 
	{
		return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
	}


    protected void installDefaults(JScrollPane scrollpane) 
    {
	LookAndFeel.installBorder(scrollpane, "ScrollPane.border");
	LookAndFeel.installColorsAndFont(scrollpane, 
	    "ScrollPane.background", 
	    "ScrollPane.foreground", 
            "ScrollPane.font");

        Border vpBorder = scrollpane.getViewportBorder();
        if ((vpBorder == null) ||( vpBorder instanceof UIResource)) {
	    vpBorder = UIManager.getBorder("ScrollPane.viewportBorder");
	    scrollpane.setViewportBorder(vpBorder);
        }
        
        scrollpane.setLayout(new TonicScrollPaneLayout());
        scrollpane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new UpperRightCorner());
    }


    protected void installListeners(JScrollPane c) 
    {
	vsbChangeListener = createVSBChangeListener();
        vsbPropertyChangeListener = createVSBPropertyChangeListener();
	hsbChangeListener = createHSBChangeListener();
        hsbPropertyChangeListener = createHSBPropertyChangeListener();
	viewportChangeListener = createViewportChangeListener();
	spPropertyChangeListener = createPropertyChangeListener();

	JViewport viewport = scrollpane.getViewport();
	JScrollBar vsb = scrollpane.getVerticalScrollBar();
	JScrollBar hsb = scrollpane.getHorizontalScrollBar();

	if (viewport != null) {
	    viewport.addChangeListener(viewportChangeListener);
	}
	if (vsb != null) {
	    vsb.getModel().addChangeListener(vsbChangeListener);
            vsb.addPropertyChangeListener(vsbPropertyChangeListener);
	}
	if (hsb != null) {
	    hsb.getModel().addChangeListener(hsbChangeListener);
            hsb.addPropertyChangeListener(hsbPropertyChangeListener);
	}

	scrollpane.addPropertyChangeListener(spPropertyChangeListener);

    mouseScrollListener = createMouseWheelListener();
    scrollpane.addMouseWheelListener(mouseScrollListener);

    }

    protected void installKeyboardActions(JScrollPane c) {
	InputMap inputMap = getInputMap(JComponent.
				  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	SwingUtilities.replaceUIInputMap(c, JComponent.
			       WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
	ActionMap actionMap = getActionMap();

	SwingUtilities.replaceUIActionMap(c, actionMap);
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
	    InputMap keyMap = (InputMap)UIManager.get("ScrollPane.ancestorInputMap");
	    InputMap rtlKeyMap;

	    if (scrollpane.getComponentOrientation().isLeftToRight() ||
		((rtlKeyMap = (InputMap)UIManager.get("ScrollPane.ancestorInputMap.RightToLeft")) == null)) {
		return keyMap;
	    } else {
		rtlKeyMap.setParent(keyMap);
		return rtlKeyMap;
	    }
	}
	return null;
    }

    ActionMap getActionMap() {
	ActionMap map = (ActionMap)UIManager.get("ScrollPane.actionMap");

	if (map == null) {
	    map = createActionMap();
	    if (map != null) {
		UIManager.getLookAndFeelDefaults().put("ScrollPane.actionMap",
                                                       map);
	    }
	}
	return map;
    }

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();
	map.put("scrollUp", new ScrollAction("scrollUp", SwingConstants.
						 VERTICAL, -1, true));
	map.put("scrollDown", new ScrollAction("scrollDown",
				     SwingConstants.VERTICAL, 1, true));
	map.put("scrollHome", new ScrollHomeAction("ScrollHome"));
	map.put("scrollEnd", new ScrollEndAction("ScrollEnd"));
	map.put("unitScrollUp", new ScrollAction
	       ("UnitScrollUp", SwingConstants.VERTICAL, -1,false));
	map.put("unitScrollDown", new ScrollAction
	       ("UnitScrollDown", SwingConstants.VERTICAL, 1, false));

	if (scrollpane.getComponentOrientation().isLeftToRight()) {
	    map.put("scrollLeft", new ScrollAction("scrollLeft",
				  SwingConstants.HORIZONTAL, -1, true));
	    map.put("scrollRight", new ScrollAction("ScrollRight",
					SwingConstants.HORIZONTAL, 1, true));
	    map.put("unitScrollRight", new ScrollAction
	       ("UnitScrollRight", SwingConstants.HORIZONTAL, 1, false));
	    map.put("unitScrollLeft", new ScrollAction
	       ("UnitScrollLeft", SwingConstants.HORIZONTAL, -1, false));
	} else {
	    map.put("scrollLeft", new ScrollAction("scrollLeft",
				  SwingConstants.HORIZONTAL, 1, true));
	    map.put("scrollRight", new ScrollAction("ScrollRight",
					SwingConstants.HORIZONTAL, -1, true));
	    map.put("unitScrollRight", new ScrollAction
	       ("UnitScrollRight", SwingConstants.HORIZONTAL, -1, false));
	    map.put("unitScrollLeft", new ScrollAction
	       ("UnitScrollLeft", SwingConstants.HORIZONTAL, 1, false));
	}
	return map;
    }


	/**	Installs the UI settings for the specified component */
    public void installUI(JComponent component) 
	{
		scrollpane= (JScrollPane) component;
		installDefaults(scrollpane);
		installListeners(scrollpane);
		installKeyboardActions(scrollpane);
	}


    protected void uninstallDefaults(JScrollPane c) {
	LookAndFeel.uninstallBorder(scrollpane);

        if (scrollpane.getViewportBorder() instanceof UIResource) {
            scrollpane.setViewportBorder(null);
        }
    }


    protected void uninstallListeners(JComponent c) {
	JViewport viewport = scrollpane.getViewport();
	JScrollBar vsb = scrollpane.getVerticalScrollBar();
	JScrollBar hsb = scrollpane.getHorizontalScrollBar();

	if (viewport != null) {
	    viewport.removeChangeListener(viewportChangeListener);
	}
	if (vsb != null) {
	    vsb.getModel().removeChangeListener(vsbChangeListener);
            vsb.removePropertyChangeListener(vsbPropertyChangeListener);
	}
	if (hsb != null) {
	    hsb.getModel().removeChangeListener(hsbChangeListener);
            hsb.removePropertyChangeListener(hsbPropertyChangeListener);
	}

	scrollpane.removePropertyChangeListener(spPropertyChangeListener);

    if (mouseScrollListener != null) {
        scrollpane.removeMouseWheelListener(mouseScrollListener);
    }

	vsbChangeListener = null;
	hsbChangeListener = null;
	viewportChangeListener = null;
	spPropertyChangeListener = null;
        mouseScrollListener = null;
    }


    protected void uninstallKeyboardActions(JScrollPane c) {
	SwingUtilities.replaceUIActionMap(c, null);
	SwingUtilities.replaceUIInputMap(c, JComponent.
			   WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
    }


	/**	Uninstalls the UI settings from the specified component */
	public void uninstallUI(JComponent c) 
	{
		uninstallDefaults(scrollpane);
		uninstallListeners(scrollpane);
		uninstallKeyboardActions(scrollpane);
		scrollpane= null;
	}


    protected void syncScrollPaneWithViewport()
    {
	JViewport viewport = scrollpane.getViewport();
	JScrollBar vsb = scrollpane.getVerticalScrollBar();
	JScrollBar hsb = scrollpane.getHorizontalScrollBar();
	JViewport rowHead = scrollpane.getRowHeader();
	JViewport colHead = scrollpane.getColumnHeader();
	boolean ltr = scrollpane.getComponentOrientation().isLeftToRight();

	if (viewport != null) {
	    Dimension extentSize = viewport.getExtentSize();
	    Dimension viewSize = viewport.getViewSize();
	    Point viewPosition = viewport.getViewPosition();

	    if (vsb != null) {
		int extent = extentSize.height;
		int max = viewSize.height;
		int value = Math.max(0, Math.min(viewPosition.y, max - extent));
		vsb.setValues(value, extent, 0, max);
	    }

	    if (hsb != null) {
		int extent = extentSize.width;
		int max = viewSize.width;
		int value;

		if (ltr) {
		    value = Math.max(0, Math.min(viewPosition.x, max - extent));
		} else {
		    int currentValue = hsb.getValue();

		    /* Use a particular formula to calculate "value"
		     * until effective x coordinate is calculated.
		     */
		    if (setValueCalled && ((max - currentValue) == viewPosition.x)) {
			value = Math.max(0, Math.min(max - extent, currentValue));
			/* After "extent" is set, turn setValueCalled flag off.
			 */
			if (extent != 0) {
			    setValueCalled = false;
			}
		    } else {
			if (extent > max) {
			    viewPosition.x = max - extent;
			    viewport.setViewPosition(viewPosition);
			    value = 0;
			} else {
			   /* The following line can't handle a small value of
			    * viewPosition.x like Integer.MIN_VALUE correctly
			    * because (max - extent - viewPositoiin.x) causes
			    * an overflow. As a result, value becomes zero.
			    * (e.g. setViewPosition(Integer.MAX_VALUE, ...)
			    *       in a user program causes a overflow.
			    *       Its expected value is (max - extent).)
			    * However, this seems a trivial bug and adding a
			    * fix makes this often-called method slow, so I'll
			    * leave it until someone claims.
			    */
			    value = Math.max(0, Math.min(max - extent, max - extent - viewPosition.x));
			}
		    }
		}
		hsb.setValues(value, extent, 0, max);
	    }

	    if (rowHead != null) {
		Point p = rowHead.getViewPosition();
		p.y = viewport.getViewPosition().y;
                p.x = 0;
		rowHead.setViewPosition(p);
	    }

	    if (colHead != null) {
		Point p = colHead.getViewPosition();
		if (ltr) {
		    p.x = viewport.getViewPosition().x;
		} else {
		    p.x = Math.max(0, viewport.getViewPosition().x);
		}
                p.y = 0;
		colHead.setViewPosition(p);
	    }
	}
    }


	/**	Listener for Viewport events */
	public class ViewportChangeHandler implements ChangeListener
	{
		public void stateChanged(ChangeEvent e) 
		{
			syncScrollPaneWithViewport();
		}
	}


    protected ChangeListener createViewportChangeListener() {
	return new ViewportChangeHandler();
    }


    /**
     * Horizontal scrollbar listener.
     */
    public class HSBChangeListener implements ChangeListener
    {
	public void stateChanged(ChangeEvent e)
	{
	    JViewport viewport = scrollpane.getViewport();
	    if (viewport != null) {
		BoundedRangeModel model = (BoundedRangeModel)(e.getSource());
		Point p = viewport.getViewPosition();
		int value = model.getValue();
		if (scrollpane.getComponentOrientation().isLeftToRight()) {
		    p.x = value;
		} else {
		    int max = viewport.getViewSize().width;
		    int extent = viewport.getExtentSize().width;
		    int oldX = p.x;

		    /* Set new X coordinate based on "value".
		     */
		    p.x = max - extent - value;

		    /* If setValue() was called before "extent" was fixed,
		     * turn setValueCalled flag on.
		     */
		    if ((extent == 0) && (value != 0) && (oldX == max)) {
			setValueCalled = true;
		    } else {
			/* When a pane without a horizontal scroll bar was
			 * reduced and the bar appeared, the viewport should
			 * show the right side of the view.
			 */
			if ((extent != 0) && (oldX < 0) && (p.x == 0)) {
			    p.x += value;
			}
		    }
		}
		viewport.setViewPosition(p);
	    }
	}
    }

    /**
     * Returns a <code>PropertyChangeListener</code> that will be installed
     * on the horizontal <code>JScrollBar</code>.
     */
    private PropertyChangeListener createHSBPropertyChangeListener() {
        return getSBPropertyChangeListener();
    }

    /**
     * Returns a shared <code>PropertyChangeListener</code> that will update
     * the listeners installed on the scrollbars as the model changes.
     */
    private PropertyChangeListener getSBPropertyChangeListener() {
        if (sbPropertyChangeListener == null) {
            sbPropertyChangeListener = new ScrollBarPropertyChangeHandler();
        }
        return sbPropertyChangeListener;
    }

    protected ChangeListener createHSBChangeListener() {
	return new HSBChangeListener();
    }


    /**
     * Vertical scrollbar listener.
     */
    public class VSBChangeListener implements ChangeListener
    {
	public void stateChanged(ChangeEvent e)
	{
	    JViewport viewport = scrollpane.getViewport();
	    if (viewport != null) {
		BoundedRangeModel model = (BoundedRangeModel)(e.getSource());
		Point p = viewport.getViewPosition();
		p.y = model.getValue();
		viewport.setViewPosition(p);
	    }
	}
    }


    /**
     * PropertyChangeListener for the ScrollBars.
     */
    private class ScrollBarPropertyChangeHandler implements
                               PropertyChangeListener {
        // Listens for changes in the model property and reinstalls the
        // horizontal/vertical PropertyChangeListeners.
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            Object source = e.getSource();

            if ("model".equals(propertyName)) {
                JScrollBar sb = scrollpane.getVerticalScrollBar();
                BoundedRangeModel oldModel = (BoundedRangeModel)e.
                                     getOldValue();
                ChangeListener cl = null;

                if (source == sb) {
                    cl = vsbChangeListener;
                }
                else if (source == scrollpane.getHorizontalScrollBar()) {
                    sb = scrollpane.getHorizontalScrollBar();
                    cl = hsbChangeListener;
                }
                if (cl != null) {
                    if (oldModel != null) {
                        oldModel.removeChangeListener(cl);
                    }
                    if (sb.getModel() != null) {
                        sb.getModel().addChangeListener(cl);
                    }
                }
            }
            else if ("componentOrientation".equals(propertyName)) {
                if (source == scrollpane.getHorizontalScrollBar()) {
		    JScrollBar hsb = scrollpane.getHorizontalScrollBar();
 		    JViewport viewport = scrollpane.getViewport();
                    Point p = viewport.getViewPosition();
                    if (scrollpane.getComponentOrientation().isLeftToRight()) {
                        p.x = hsb.getValue();
                    } else {
                        p.x = viewport.getViewSize().width - viewport.getExtentSize().width - hsb.getValue();
                    }
                    viewport.setViewPosition(p);
                }
            }
        }
    }

    /**
     * Returns a <code>PropertyChangeListener</code> that will be installed
     * on the vertical <code>JScrollBar</code>.
     */
    private PropertyChangeListener createVSBPropertyChangeListener() {
        return getSBPropertyChangeListener();
    }

    protected ChangeListener createVSBChangeListener() {
	return new VSBChangeListener();
    }

    /**
     * MouseWheelHandler is an inner class which implements the 
     * MouseWheelListener interface.  MouseWheelHandler responds to 
     * MouseWheelEvents by scrolling the JScrollPane appropriately.
     * If the scroll pane's
     * <code>isWheelScrollingEnabled</code>
     * method returns false, no scrolling occurs.
     * 
     * @see javax.swing.JScrollPane#isWheelScrollingEnabled
     * @see #createMouseWheelListener
     * @see java.awt.event.MouseWheelListener
     * @see java.awt.event.MouseWheelEvent
     * @since 1.4
     */
    protected class MouseWheelHandler implements MouseWheelListener {
        /**
         * Called when the mouse wheel is rotated while over a
         * JScrollPane.
         *
         * @param e     MouseWheelEvent to be handled
         * @since 1.4
         */
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (scrollpane.isWheelScrollingEnabled() &&
                e.getScrollAmount() != 0) {
                JScrollBar toScroll = scrollpane.getVerticalScrollBar();
                int direction = 0;
                // find which scrollbar to scroll, or return if none
                if (toScroll == null || !toScroll.isVisible()) { 
                    toScroll = scrollpane.getHorizontalScrollBar();
                    if (toScroll == null || !toScroll.isVisible()) { 
                        return;
                    }
                }
                direction = e.getWheelRotation() < 0 ? -1 : 1;
                
                if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                     ScrollBarUI.scrollTonicByUnits(toScroll, direction,
                                                         e.getScrollAmount());
                }
                else if (e.getScrollType() ==
                        MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
                    ScrollBarUI.scrollTonicByBlock(toScroll, direction);
                }
            }
        }
    }

    /**
     * Creates an instance of MouseWheelListener, which is added to the
     * JScrollPane by installUI().  The returned MouseWheelListener is used
     * to handle mouse wheel-driven scrolling.
     *
     * @return      MouseWheelListener which implements wheel-driven scrolling
     * @see #installUI
     * @see MouseWheelHandler
     * @since 1.4
     */
    protected MouseWheelListener createMouseWheelListener() {
        return new MouseWheelHandler();
    }

    protected void updateScrollBarDisplayPolicy(PropertyChangeEvent e) {
	scrollpane.revalidate();
	scrollpane.repaint();
    }


    protected void updateViewport(PropertyChangeEvent e) 
    {
	JViewport oldViewport = (JViewport)(e.getOldValue());
	JViewport newViewport = (JViewport)(e.getNewValue());

	if (oldViewport != null) {
	    oldViewport.removeChangeListener(viewportChangeListener);
	}
	
	if (newViewport != null) {
	    Point p = newViewport.getViewPosition();
	    if (scrollpane.getComponentOrientation().isLeftToRight()) {
		p.x = Math.max(p.x, 0);
	    } else {
		int max = newViewport.getViewSize().width;
		int extent = newViewport.getExtentSize().width;
		if (extent > max) {
		    p.x = max - extent;
		} else {
		    p.x = Math.max(0, Math.min(max - extent, p.x));
		}
	    }
	    p.y = Math.max(p.y, 0);
	    newViewport.setViewPosition(p);
	    newViewport.addChangeListener(viewportChangeListener);
	}
    }


    protected void updateRowHeader(PropertyChangeEvent e) 
    {
	JViewport newRowHead = (JViewport)(e.getNewValue());
	if (newRowHead != null) {
	    JViewport viewport = scrollpane.getViewport();
	    Point p = newRowHead.getViewPosition();
	    p.y = (viewport != null) ? viewport.getViewPosition().y : 0;
	    newRowHead.setViewPosition(p);
	}
    }


    protected void updateColumnHeader(PropertyChangeEvent e) 
    {
	JViewport newColHead = (JViewport)(e.getNewValue());
	if (newColHead != null) {
	    JViewport viewport = scrollpane.getViewport();
	    Point p = newColHead.getViewPosition();
	    if (viewport == null) {
		p.x = 0;
	    } else {
		if (scrollpane.getComponentOrientation().isLeftToRight()) {
		    p.x = viewport.getViewPosition().x;
		} else {
		    p.x = Math.max(0, viewport.getViewPosition().x);
		}
	    }
	    newColHead.setViewPosition(p);
	    scrollpane.add(newColHead, COLUMN_HEADER);
	}
    }

    private void updateHorizontalScrollBar(PropertyChangeEvent pce) {
	updateScrollBar(pce, hsbChangeListener, hsbPropertyChangeListener);
    }

    private void updateVerticalScrollBar(PropertyChangeEvent pce) {
	updateScrollBar(pce, vsbChangeListener, vsbPropertyChangeListener);
    }

    private void updateScrollBar(PropertyChangeEvent pce, ChangeListener cl,
                                 PropertyChangeListener pcl) {
        JScrollBar sb = (JScrollBar)pce.getOldValue();
        if (sb != null) {
            if (cl != null) {
                sb.getModel().removeChangeListener(cl);
            }
            if (pcl != null) {
                sb.removePropertyChangeListener(pcl);
            }
        }
        sb = (JScrollBar)pce.getNewValue();
        if (sb != null) {
            if (cl != null) {
                sb.getModel().addChangeListener(cl);
            }
            if (pcl != null) {
                sb.addPropertyChangeListener(pcl);
            }
	}
    }

    public class PropertyChangeHandler implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String propertyName = e.getPropertyName();

	    if (propertyName.equals("verticalScrollBarDisplayPolicy")) {
		updateScrollBarDisplayPolicy(e);
	    }
	    else if (propertyName.equals("horizontalScrollBarDisplayPolicy")) {
		updateScrollBarDisplayPolicy(e);
	    }
	    else if (propertyName.equals("viewport")) {
		updateViewport(e);
	    }
	    else if (propertyName.equals("rowHeader")) {
		updateRowHeader(e);
	    }
	    else if (propertyName.equals("columnHeader")) {
		updateColumnHeader(e);
	    }
	    else if (propertyName.equals("verticalScrollBar")) {
		updateVerticalScrollBar(e);
	    }
	    else if (propertyName.equals("horizontalScrollBar")) {
		updateHorizontalScrollBar(e);
	    }
	    else if (propertyName.equals("componentOrientation")) {
		scrollpane.revalidate();
		scrollpane.repaint();

		InputMap inputMap = getInputMap(JComponent.
					WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		SwingUtilities.replaceUIInputMap(scrollpane, JComponent.
				WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);

		UIManager.getLookAndFeelDefaults().put("ScrollPane.actionMap",
                                                       null);
		ActionMap actionMap = getActionMap();
		SwingUtilities.replaceUIActionMap(scrollpane, actionMap);
	    }
	}
    }



    /**
     * Creates an instance of PropertyChangeListener that's added to 
     * the JScrollPane by installUI().  Subclasses can override this method
     * to return a custom PropertyChangeListener, e.g.
     * <pre>
     * class MyScrollPaneUI extends BasicScrollPaneUI {
     *    protected PropertyChangeListener <b>createPropertyChangeListener</b>() {
     *        return new MyPropertyChangeListener();
     *    }
     *    public class MyPropertyChangeListener extends PropertyChangeListener {
     *        public void propertyChange(PropertyChangeEvent e) {
     *            if (e.getPropertyName().equals("viewport")) {
     *                // do some extra work when the viewport changes
     *            }
     *            super.propertyChange(e);
     *        }
     *    }
     * }
     * </pre>
     * 
     * @see java.beans.PropertyChangeListener
     * @see #installUI
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }


    /**
     * Action to scroll left/right/up/down.
     */
    private static class ScrollAction extends AbstractAction {
	/** Direction to scroll. */
	protected int orientation;
	/** 1 indicates scroll down, -1 up. */
	protected int direction;
	/** True indicates a block scroll, otherwise a unit scroll. */
	private boolean block;

	protected ScrollAction(String name, int orientation, int direction,
			       boolean block) {
	    super(name);
	    this.orientation = orientation;
	    this.direction = direction;
	    this.block = block;
	}

	public void actionPerformed(ActionEvent e) {
	    JScrollPane scrollpane = (JScrollPane)e.getSource();
	    JViewport vp = scrollpane.getViewport();
	    Component view;
	    if (vp != null && (view = vp.getView()) != null) {
		Rectangle visRect = vp.getViewRect();
		Dimension vSize = view.getSize();
		int amount;

		if (view instanceof Scrollable) {
		    if (block) {
			amount = ((Scrollable)view).getScrollableBlockIncrement
			         (visRect, orientation, direction);
		    }
		    else {
			amount = ((Scrollable)view).getScrollableUnitIncrement
			         (visRect, orientation, direction);
		    }
		}
		else {
		    if (block) {
			if (orientation == SwingConstants.VERTICAL) {
			    amount = visRect.height;
			}
			else {
			    amount = visRect.width;
			}
		    }
		    else {
			amount = 10;
		    }
		}
		if (orientation == SwingConstants.VERTICAL) {
		    visRect.y += (amount * direction);
		    if ((visRect.y + visRect.height) > vSize.height) {
			visRect.y = Math.max(0, vSize.height - visRect.height);
		    }
		    else if (visRect.y < 0) {
			visRect.y = 0;
		    }
		}
		else {
		    if (scrollpane.getComponentOrientation().isLeftToRight()) {
			visRect.x += (amount * direction);
			if ((visRect.x + visRect.width) > vSize.width) {
			    visRect.x = Math.max(0, vSize.width - visRect.width);
			} else if (visRect.x < 0) {
			    visRect.x = 0;
			}
		    } else {
			visRect.x -= (amount * direction);
                        if (visRect.width > vSize.width) {
                            visRect.x = vSize.width - visRect.width;
                        } else {
                            visRect.x = Math.max(0, Math.min(vSize.width - visRect.width, visRect.x));
			}
		    }
		}
		vp.setViewPosition(visRect.getLocation());
	    }
	}
    }


    /**
     * Action to scroll to x,y location of 0,0.
     */
    private static class ScrollHomeAction extends AbstractAction {
	protected ScrollHomeAction(String name) {
	    super(name);
	}

	public void actionPerformed(ActionEvent e) {
	    JScrollPane scrollpane = (JScrollPane)e.getSource();
	    JViewport vp = scrollpane.getViewport();
	    Component view;
	    if (vp != null && (view = vp.getView()) != null) {
		if (scrollpane.getComponentOrientation().isLeftToRight()) {
		    vp.setViewPosition(new Point(0, 0));
		} else {
		    Rectangle visRect = vp.getViewRect();
		    Rectangle bounds = view.getBounds();
		    vp.setViewPosition(new Point(bounds.width - visRect.width, 0));
		}
	    }
	}
    }


    /**
     * Action to scroll to last visible location.
     */
    private static class ScrollEndAction extends AbstractAction {
	protected ScrollEndAction(String name) {
	    super(name);
	}

	public void actionPerformed(ActionEvent e) {
	    JScrollPane scrollpane = (JScrollPane)e.getSource();
	    JViewport vp = scrollpane.getViewport();
	    Component view;
	    if (vp != null && (view = vp.getView()) != null) {
		Rectangle visRect = vp.getViewRect();
		Rectangle bounds = view.getBounds();
		if (scrollpane.getComponentOrientation().isLeftToRight()) {
		    vp.setViewPosition(new Point(bounds.width - visRect.width,
					     bounds.height - visRect.height));
		} else {
		    vp.setViewPosition(new Point(0, 
					     bounds.height - visRect.height));
		}
	    }
	}
    }
    
    
    class UpperRightCorner extends JComponent
    {
    	public void paint(Graphics g)
    	{
    		g.setColor(UIManager.getColor("Button.borderColor"));
    		g.drawLine(0, 0, 0, getHeight());
    	}
    }
}

