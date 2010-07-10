package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

public class GUIUtils {
    /**
     * Centers <CODE>wind</CODE> within its parent. If it has no parent then
     * center within desktop.
     *
     * @param   wind    The Window to be centered.
     *
     * @throws IllegalArgumentException     If <TT>wind</TT> is <TT>null</TT>.
     */
    public static void centerWithinParent(Window wind)
            throws IllegalArgumentException {
        if (wind == null) {
            throw new IllegalArgumentException("null Window passed");
        }
        Container parent = wind.getParent();
        if (parent != null && parent.isVisible()) {
            center(wind, new Rectangle(parent.getLocationOnScreen(),
                                            parent.getSize()));
        } else {
            centerWithinDesktop(wind);
        }
    }

    /**
     * Centers <CODE>wind</CODE> within desktop.
     *
     * @param   wind    The Window to be centered.
     *
     * @throws IllegalArgumentException     If <TT>wind</TT> is <TT>null</TT>.
     */
    public static void centerWithinDesktop(Window wind)
            throws IllegalArgumentException {
        if (wind == null) {
            throw new IllegalArgumentException("null Window passed");
        }

        Rectangle rcDesk = new Rectangle(
                        Toolkit.getDefaultToolkit().getScreenSize() );
        Dimension windSize  = wind.getSize();
        Dimension parentSize= new Dimension(rcDesk.width, rcDesk.height);
        if (windSize.height > parentSize.height) {
            windSize.height = parentSize.height;
        }
        if (windSize.width > parentSize.width) {
            windSize.width = parentSize.width;
        }
        center(wind, rcDesk);
    }

    /**
     * Return the owning <CODE>Frame</CODE> for the passed component
     * of <CODE>null</CODE> if it doesn't have one.
     *
     * @throws IllegalArgumentException     If <TT>wind</TT> is <TT>null</TT>.
     */
    public static Frame getOwningFrame(Component comp)
            throws IllegalArgumentException {
        if (comp == null) {
            throw new IllegalArgumentException("null Component passed");
        }

        if (comp instanceof Frame) {
            return (Frame)comp;
        }
        return getOwningFrame(SwingUtilities.windowForComponent(comp));
    }

    /**
     * Return <TT>true</TT> if <TT>frame</TT> is a tool window. I.E. is the
     * <TT>JInternalFrame.isPalette</TT> set to <TT>Boolean.TRUE</TT>?
     *
     * @param   frame   The <TT>JInternalFrame</TT> to be checked.
     *
     * @throws IllegalArgumentException     If <TT>frame</TT> is <TT>null</TT>.
     */
    public static boolean isToolWindow(JInternalFrame frame)
            throws IllegalArgumentException {
        if (frame == null) {
            throw new IllegalArgumentException("null JInternalFrame passed");
        }

        Object obj = frame.getClientProperty("JInternalFrame.isPalette");
        return obj != null && obj == Boolean.TRUE;
    }

    /**
     * Make the passed internal frame a Tool Window.
     */
    public static void makeToolWindow(JInternalFrame frame, boolean isToolWindow)
            throws IllegalArgumentException {
        if (frame == null) {
            throw new IllegalArgumentException("null JInternalFrame passed");
        }

        frame.putClientProperty("JInternalFrame.isPalette", isToolWindow ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Change the sizes of all the passed buttons to be the size of the
     * largest one.
     *
     * @param   btns   Array of buttons to eb resized.
     *
     * @throws IllegalArgumentException     If <TT>btns</TT> is <TT>null</TT>.
     */
    public static void setJButtonSizesTheSame(JButton[] btns)
            throws IllegalArgumentException {
        if (btns == null) {
            throw new IllegalArgumentException("null JButton[] passed");
        }

        // Get the largest width and height
        Dimension maxSize = new Dimension(0, 0);
        for (int i = 0; i < btns.length; ++i) {
            JButton btn = btns[i];
            FontMetrics fm = btn.getFontMetrics(btn.getFont());
            String text = btn.getText();
            Rectangle2D bounds = fm.getStringBounds(btn.getText(), btn.getGraphics());
            int boundsHeight = (int)bounds.getHeight();
            int boundsWidth = (int)bounds.getWidth();
            maxSize.width = boundsWidth > maxSize.width ? boundsWidth : maxSize.width;
            maxSize.height = boundsHeight > maxSize.height ? boundsHeight : maxSize.height;
        }

        Insets insets = btns[0].getInsets();
        maxSize.width += insets.left + insets.right;
        maxSize.height += insets.top + insets.bottom;

        for (int i = 0; i < btns.length; ++i) {
            JButton btn = btns[i];
            btn.setPreferredSize(maxSize);
        }
    }

    /**
     * Return an array containing all <TT>JInternalFrame</TT> objects
     * that were passed in <TT>frames</TT> that are not tool windows.
     *
     * @param   frames      <TT>JInternalFrame</TT> objects to be checked.
     */
    public static JInternalFrame[] getOpenNonToolWindows(JInternalFrame[] frames)
            throws IllegalArgumentException {
        if (frames == null) {
            throw new IllegalArgumentException("null JInternalFrame[] passed");
        }
        List framesList = new ArrayList();
        for (int i = 0; i < frames.length; ++i) {
            JInternalFrame fr = frames[i];
            if (!isToolWindow(fr) && !fr.isClosed()) {
                framesList.add(frames[i]);
            }
        }
        return (JInternalFrame[])framesList.toArray(new JInternalFrame[framesList.size()]);
    }

    public static boolean isWithinParent(Component wind) throws IllegalArgumentException {
        if (wind == null) {
            throw new IllegalArgumentException("Null Component passed");
        }

        Component parent = wind.getParent();
        Rectangle parentRect = null;
        if (parent != null) {
            parentRect = new Rectangle(parent.getSize());
        } else {
            parentRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        }
        Rectangle windowBounds = wind.getBounds();
        if (windowBounds.x > (parentRect.width - 20)
                || windowBounds.y > (parentRect.height - 20)
                || (windowBounds.x + windowBounds.width) < 20
                || (windowBounds.y + windowBounds.height) < 20) {
            return false;
        }
        return true;
    }

    /**
     * Centers <CODE>wind</CODE> within the passed rectangle.
     *
     * @param   wind    The Window to be centered.
     * @param   rect    The rectangle (in screen coords) to center
     *                  <CODE>wind</CODE> within.
     *
     * @throws IllegalArgumentException
     *      If <TT>Window</TT> or <TT>Rectangle</TT> is <TT>null</TT>.
     */
    private static void center(Window wind, Rectangle rect)
            throws IllegalArgumentException {
        if (wind == null || rect == null) {
            throw new IllegalArgumentException("null Window or Rectangle passed");
        }

        Dimension windSize  = wind.getSize();
        Dimension parentSize= new Dimension(rect.width, rect.height);
        int x = ((parentSize.width - windSize.width) / 2) + rect.x;
        int y = ((parentSize.height - windSize.height) / 2) + rect.y;
        wind.setLocation(x, y);
    }
}
