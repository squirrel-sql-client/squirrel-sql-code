/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyTabbedPaneUI
 * 
 * @version 1.3.04
 * @author Hans Bickel
 */
public class TinyTabbedPaneUI extends BasicTabbedPaneUI {

	/**
	 * Creates the UI delegate for the given component.
	 *
	 * @param c The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new TinyTabbedPaneUI();
	}

	int rollover = -1;

	protected void installListeners() {
		super.installListeners();
		
		tabPane.addMouseMotionListener(
			(MouseMotionListener)mouseListener);
	}
	
	protected MouseListener createMouseListener() {
		return new TinyMouseHandler();
	}

	protected void installDefaults() {
		super.installDefaults();
	}
	
	private boolean scrollableTabLayoutEnabled() {
        return (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT);
    }

	private void checkRollOver(int tabIndex) {
		if(rollover >= tabPane.getTabCount()) {
			rollover = -1;
		}
		
		if(tabIndex == rollover) return;
	
		if(rollover != -1) { // Update old rollover
			tabPane.repaint(getTabBounds(tabPane, rollover));
			
			if(tabIndex == -1) rollover = -1;
		}
	
		if(tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
			// Paint new rollover
			rollover = tabIndex;
			tabPane.repaint(getTabBounds(tabPane, tabIndex));
		}
	}

	/**
	 * Overridden so we can enable/disable tab rotating using
	 * TinyTabbedPaneLayout.
     * Invoked by <code>installUI</code> to create
     * a layout manager object to manage
     * the <code>JTabbedPane</code>.
     *
     * @return a layout manager object
     *
     * @see TabbedPaneLayout
     * @see javax.swing.JTabbedPane#getTabLayoutPolicy
     */
    protected LayoutManager createLayoutManager() {
		if(tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
		    return super.createLayoutManager();
		} else { /* WRAP_TAB_LAYOUT */
	        return new TinyTabbedPaneLayout();
		}
    }
    
    private int getTabAtLocation(int x, int y) {
		if(TinyUtils.is1dot4()) {
			ensureCurrentLayout();

			int tabCount = tabPane.getTabCount();
			
			for(int i = 0; i < tabCount; i++) {
				if(rects[i].contains(x, y)) {
					return i;
				}
			}
			
			return -1;
		}
		else {	// JRE 1.5 or higher
			return tabForCoordinate(tabPane, x, y);
		}
	}

	private void ensureCurrentLayout() {
		if(!tabPane.isValid()) {
			tabPane.validate();
		}
		
		/* If tabPane doesn't have a peer yet, the validate() call will
		 * silently fail.  We handle that by forcing a layout if tabPane
		 * is still invalid.  See bug 4237677.
		 */
		if(!tabPane.isValid()) {
			TabbedPaneLayout layout = (TabbedPaneLayout)tabPane.getLayout();
			layout.calculateLayoutInfo();
		}
	}

	public class TinyMouseHandler implements MouseListener, MouseMotionListener {

		public void mousePressed(MouseEvent e) {
			if(!tabPane.isEnabled()) return;

			// 1.3.04 code - see getTabAtLocation(int, int) for
			// JRE 1.5 fix
			int tabIndex = getTabAtLocation(e.getX(), e.getY());

            if(tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
                if(tabIndex != tabPane.getSelectedIndex()) {
                    // Clicking on unselected tab, change selection, do NOT
                    // request focus.
                    // This will trigger the focusIndex to change by way
                    // of stateChanged.
                    tabPane.setSelectedIndex(tabIndex);
                }
                else if(tabPane.isRequestFocusEnabled()) {
                    // Clicking on selected tab, try and give the tabbedpane
                    // focus.  Repaint will occur in focusGained.
                    tabPane.requestFocus();
                }
            }
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {
			if(rollover >= tabPane.getTabCount()) {
				rollover = -1;
			}

			if(rollover != -1) {
				tabPane.repaint(getTabBounds(tabPane, rollover));
				rollover = -1;
			}
		}

		public void mouseClicked(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}

		public void mouseDragged(MouseEvent e) {}

		public void mouseMoved(MouseEvent e) {
			if(tabPane == null) return;
			if(!tabPane.isEnabled()) return;
			
			// Note: When running JRE v1.4 there's no way to do
			// tab rollovers with SCROLL_TAB_LAYOUT
			if(TinyUtils.is1dot4() && scrollableTabLayoutEnabled()) return;

			checkRollOver(getTabAtLocation(e.getX(), e.getY()));
		}
	}

	/**
	 * Paints the backround of a given tab.
	 *
	 * @param g The graphics context.
	 * @param tabPlacement The placement of the tab to paint.
	 * @param tabIndex The index of the tab to paint.
	 * @param x The x coordinate of the top left corner.
	 * @param y The y coordinate of the top left corner.
	 * @param w The width.
	 * @param h The height.
	 * @param isSelected True if the tab to paint is selected otherwise false.
	 */
	protected void paintTabBackground(Graphics g, int tabPlacement,
		int tabIndex, int x, int y, int w, int h, boolean isSelected)
	{
		boolean isEnabled = (tabPane.isEnabled() & tabPane.isEnabledAt(tabIndex));
		
		if(isSelected && !Theme.ignoreSelectedBg.getValue()) {
			if(isEnabled) {
				g.setColor(Theme.tabSelectedColor.getColor());
			}
			else {
				g.setColor(Theme.tabDisabledSelectedColor.getColor());
			}
		}
		else {
			if(isEnabled) {
				// because (Tiny)JTabbedPane now has a defined
				// background color, this should work
				g.setColor(tabPane.getBackgroundAt(tabIndex));
			}
			else {
				g.setColor(Theme.tabDisabledColor.getColor());
			}
		}

		switch (tabPlacement) {
			case LEFT :
				g.fillRect(x + 1, y + 1, w - 1, h - 3);
				break;
			case RIGHT :
				x -= 2;
				g.fillRect(x, y + 1, w - 1, h - 3);
				break;
			case BOTTOM :
				y -= 2;
				g.fillRect(x + 1, y, w - 3, h - 1);
				break;
			case TOP :
			default :
				g.fillRect(x + 1, y + 1, w - 3, h - 1);
		}
	}

	/**
	 * Paints the border of a given tab.
	 *
	 * @param g The graphics context.
	 * @param tabPlacement The placement of the tab to paint.
	 * @param selectedIndex The index of the selected tab.
	 */
	protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
	}

	/**
	 * @see javax.swing.plaf.basic.BasicTabbedPaneUI#paintFocusIndicator(Graphics, int, Rectangle[], int, Rectangle, Rectangle, boolean)
	 */
	protected void paintFocusIndicator(Graphics g, int tabPlacement,
		Rectangle[] rects, int tabIndex, Rectangle iconRect,
		Rectangle textRect, boolean isSelected)
	{
		if(!Theme.tabFocus.getValue()) return;
		
		Rectangle tabRect = rects[tabIndex];
		
        if(tabPane.hasFocus() && isSelected) {
            int x, y, w, h;
            g.setColor(Theme.tabFontColor.getColor());
            
            switch(tabPlacement) {
              case LEFT:
                  x = tabRect.x + 3;
                  y = tabRect.y + 3;
                  w = tabRect.width - 5;
                  h = tabRect.height - 7;
                  break;
              case RIGHT:
                  x = tabRect.x;
                  y = tabRect.y + 3;
                  w = tabRect.width - 5;
                  h = tabRect.height - 7;
                  break;
              case BOTTOM:
                  x = tabRect.x + 3;
                  y = tabRect.y;
                  w = tabRect.width - 7;
                  h = tabRect.height - 5;
                  break;
              case TOP:
              default:
                  x = tabRect.x + 3;
                  y = tabRect.y + 3;
                  w = tabRect.width - 7;
                  h = tabRect.height - 5;
            }
            
            BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
        }
	}

	/**
	 * Draws the border around each tab.
	 *
	 * @param g The graphics context.
	 * @param tabPlacement The placement of the tabs.
	 * @param tabIndex The index of the tab to paint.
	 * @param x The x coordinate of the top left corner.
	 * @param y The y coordinate of the top left corner.
	 * @param w The width.
	 * @param h The height.
	 * @param isSelected True if the tab to paint is selected otherwise false.
	 */
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		boolean isEnabled = (tabPane.isEnabledAt(tabIndex));
		if(!tabPane.isEnabled()) isEnabled = false;
		
		boolean isRollover = (rollover == tabIndex);

		drawXpTabBorder(g, tabPlacement, x, y, w, h, isSelected, isEnabled, isRollover);
	}

	private void drawXpTabBorder(Graphics g, int tabPlacement, int x, int y, int w, int h,
		boolean isSelected, boolean isEnabled, boolean isRollover)
	{
		if(!isEnabled) {
			DrawRoutines.drawXpTabBorder(g,
				Theme.tabDisabledBorderColor.getColor(), x, y, w, h, tabPlacement);
		}
		else if(isSelected) {
			DrawRoutines.drawSelectedXpTabBorder(g,
				Theme.tabBorderColor.getColor(), x, y, w, h, tabPlacement);
		}
		else if(isRollover && Theme.tabRollover.getValue()) {
			DrawRoutines.drawSelectedXpTabBorder(g,
				Theme.tabBorderColor.getColor(), x, y, w, h, tabPlacement);
		}
		else {
			DrawRoutines.drawXpTabBorder(g,
				Theme.tabBorderColor.getColor(), x, y, w, h, tabPlacement);
		}
	}

	/**
	 * Paint the border and then call paint().
	 */
	public void update(Graphics g, JComponent c) {
		Insets insets = tabPane.getInsets();
		int x = insets.left;
		int y = insets.top;
		int w = tabPane.getWidth() - insets.right - insets.left;
		int h = tabPane.getHeight() - insets.top - insets.bottom;

		if(c.isOpaque()) {
			g.setColor(Theme.backColor.getColor());
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
		}

		int tabPlacement = tabPane.getTabPlacement();
		switch (tabPlacement) {
			case LEFT :
				x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
				w -= (x - insets.left);
				break;
			case RIGHT :
				w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
				break;
			case BOTTOM :
				h -= calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
				break;
			case TOP :
			default :
				y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
				h -= (y - insets.top);
		}

		drawXpContentBorder(g, x, y, w, h);

		super.paint(g, c);
	}

	private void drawXpContentBorder(Graphics g, int x, int y, int w, int h) {
		if(tabPane.isEnabled()) {
			g.setColor(Theme.tabPaneBorderColor.getColor());
		}
		else {
			g.setColor(Theme.tabPaneDisabledBorderColor.getColor());
		}
		
		g.drawRect(x, y, w - 3, h - 3);

		// Shadow
		g.setColor(ColorRoutines.darken(Theme.backColor.getColor(), 15));
		g.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 2); // right
		g.drawLine(x + 1, y + h - 2, x + w - 3, y + h - 2); // bottom
	}

	protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected) {
		Rectangle tabRect = rects[tabIndex];
		int nudge = 0;
		
		switch (tabPlacement) {
			case LEFT :
				nudge = isSelected ? -1 : 1;
				break;
			case RIGHT :
				nudge = isSelected ? 1 : -1;
				break;
			case BOTTOM :
			case TOP :
			default :
				nudge = 0;
		}
		
		return nudge;
	}

	protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
		Rectangle tabRect = rects[tabIndex];
		int nudge = 0;
		
		switch (tabPlacement) {
			case BOTTOM :
				nudge = isSelected ? 1 : -1;
				break;
			case LEFT :
			case RIGHT :
				nudge = tabRect.height % 2;
				break;
			case TOP :
			default :
				nudge = isSelected ? -1 : 1;
		}
		
		return nudge;
	}

	protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
		int tabIndex, String title, Rectangle textRect, boolean isSelected)
	{
		g.setFont(font);
	
		View v = getTextViewForTab(tabIndex);
		
		if(v != null) {
			// html
			v.paint(g, textRect);
		}
		else {
			// plain text
			int mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
	
			if(tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
				g.setColor(tabPane.getForegroundAt(tabIndex));
			}
			else { // tab disabled
				g.setColor(Theme.tabDisabledTextColor.getColor());
			}
	
			// Note: Using BasicGraphicsUtils.drawStringUnderlineCharAt(...)
			// prevented text antialiasing with JRE 1.6.0_10
			TinyUtils.drawStringUnderlineCharAt(tabPane, g, title,
				mnemIndex, textRect.x, textRect.y + metrics.getAscent());
		}
	}
	
	protected class TinyTabbedPaneLayout extends TabbedPaneLayout {
		
		protected void rotateTabRuns(int tabPlacement, int selectedRun) {
			if(!Theme.fixedTabs.getValue()) {
				super.rotateTabRuns(tabPlacement, selectedRun);
			}
		}
	}
}