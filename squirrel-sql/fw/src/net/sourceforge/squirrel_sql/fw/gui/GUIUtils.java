package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
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

/**
 * Common GUI utilities accessed via static methods.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class GUIUtils
{
	/**
	 * Centers <CODE>wind</CODE> within its parent. If it has no parent then
	 * center within the screen. If centering would cause the title bar to go
	 * above the parent (I.E. cannot see the titlebar and so cannot move the
	 * window) then move the window down.
	 *
	 * @param   wind	The Window to be centered.
	 *
	 * @throws IllegalArgumentException	 If <TT>wind</TT> is <TT>null</TT>.
	 */
	public static void centerWithinParent(Window wind)
	{
		if (wind == null)
		{
			throw new IllegalArgumentException("null Window passed");
		}
		final Container parent = wind.getParent();
		if (parent != null && parent.isVisible())
		{
			center(wind, new Rectangle(parent.getLocationOnScreen(),
					parent.getSize()));
		}
		else
		{
			centerWithinScreen(wind);
		}
	}

	/**
	 * Centers passed internal frame within its desktop area. If centering
     * would cause the title bar to go off the top of the screen then move the
	 * window down.
	 *
	 * @param   frame	The internal frame to be centered.
	 *
	 * @throws IllegalArgumentException	 If <TT>frame</TT> is <TT>null</TT>.
	 */
	public static void centerWithinDesktop(JInternalFrame frame)
	{
		if (frame == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}
		final Container parent = frame.getDesktopPane();
		if (parent != null && parent.isVisible())
		{
			center(frame, new Rectangle(new Point(0, 0), parent.getSize()));
		}
	}

	/**
	 * Centers <CODE>wind</CODE> within the screen. If centering would cause the
	 * title bar to go off the top of the screen then move the window down.
	 *
	 * @param   wind	The Window to be centered.
	 *
	 * @throws IllegalArgumentException	 If <TT>wind</TT> is <TT>null</TT>.
	 */
	public static void centerWithinScreen(Window wind)
	{
		if (wind == null)
		{
			throw new IllegalArgumentException("null Window passed");
		}
		final Toolkit toolKit = Toolkit.getDefaultToolkit();
		final Rectangle rcScreen = new Rectangle(toolKit.getScreenSize());
		final Dimension windSize = wind.getSize();
		final Dimension parentSize = new Dimension(rcScreen.width, rcScreen.height);
		if (windSize.height > parentSize.height)
		{
			windSize.height = parentSize.height;
		}
		if (windSize.width > parentSize.width)
		{
			windSize.width = parentSize.width;
		}
		center(wind, rcScreen);
	}

	/**
	 * Return the owning <CODE>Frame</CODE> for the passed component
	 * of <CODE>null</CODE> if it doesn't have one.
	 *
	 * @throws IllegalArgumentException	 If <TT>wind</TT> is <TT>null</TT>.
	 */
	public static Frame getOwningFrame(Component comp)
	{
		if (comp == null)
		{
			throw new IllegalArgumentException("null Component passed");
		}

		if (comp instanceof Frame)
		{
			return (Frame) comp;
		}
		return getOwningFrame(SwingUtilities.windowForComponent(comp));
	}

	/**
	 * Return <TT>true</TT> if <TT>frame</TT> is a tool window. I.E. is the
	 * <TT>JInternalFrame.isPalette</TT> set to <TT>Boolean.TRUE</TT>?
	 *
	 * @param   frame   The <TT>JInternalFrame</TT> to be checked.
	 *
	 * @throws IllegalArgumentException	 If <TT>frame</TT> is <TT>null</TT>.
	 */
	public static boolean isToolWindow(JInternalFrame frame)
	{
		if (frame == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}

		final Object obj = frame.getClientProperty("JInternalFrame.isPalette");
		return obj != null && obj == Boolean.TRUE;
	}

	/**
	 * Make the passed internal frame a Tool Window.
	 */
	public static void makeToolWindow(JInternalFrame frame, boolean isToolWindow)
	{
		if (frame == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}
		frame.putClientProperty("JInternalFrame.isPalette",
								isToolWindow ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Change the sizes of all the passed buttons to be the size of the
	 * largest one.
	 *
	 * @param   btns   Array of buttons to eb resized.
	 *
	 * @throws IllegalArgumentException	 If <TT>btns</TT> is <TT>null</TT>.
	 */
	public static void setJButtonSizesTheSame(JButton[] btns)
	{
		if (btns == null)
		{
			throw new IllegalArgumentException("null JButton[] passed");
		}

		// Get the largest width and height
		final Dimension maxSize = new Dimension(0, 0);
		for (int i = 0; i < btns.length; ++i)
		{
			final JButton btn = btns[i];
			final FontMetrics fm = btn.getFontMetrics(btn.getFont());
			Rectangle2D bounds = fm.getStringBounds(btn.getText(), btn.getGraphics());
			int boundsHeight = (int) bounds.getHeight();
			int boundsWidth = (int) bounds.getWidth();
			maxSize.width = boundsWidth > maxSize.width ? boundsWidth : maxSize.width;
			maxSize.height = boundsHeight > maxSize.height ? boundsHeight : maxSize.height;
		}

		Insets insets = btns[0].getInsets();
		maxSize.width += insets.left + insets.right;
		maxSize.height += insets.top + insets.bottom;

		for (int i = 0; i < btns.length; ++i)
		{
			JButton btn = btns[i];
			btn.setPreferredSize(maxSize);
		}
	}

	/**
	 * Return an array containing all <TT>JInternalFrame</TT> objects
	 * that were passed in <TT>frames</TT> that are tool windows.
	 *
	 * @param   frames	  <TT>JInternalFrame</TT> objects to be checked.
	 */
	public static JInternalFrame[] getOpenToolWindows(JInternalFrame[] frames)
	{
		if (frames == null)
		{
			throw new IllegalArgumentException("null JInternalFrame[] passed");
		}
		List framesList = new ArrayList();
		for (int i = 0; i < frames.length; ++i)
		{
			JInternalFrame fr = frames[i];
			if (isToolWindow(fr) && !fr.isClosed())
			{
				framesList.add(frames[i]);
			}
		}
		return (JInternalFrame[]) framesList.toArray(new JInternalFrame[framesList.size()]);
	}

	/**
	 * Return an array containing all <TT>JInternalFrame</TT> objects
	 * that were passed in <TT>frames</TT> that are <EM>not</EM> tool windows.
	 *
	 * @param   frames	  <TT>JInternalFrame</TT> objects to be checked.
	 */
	public static JInternalFrame[] getOpenNonToolWindows(JInternalFrame[] frames)
	{
		if (frames == null)
		{
			throw new IllegalArgumentException("null JInternalFrame[] passed");
		}
		List framesList = new ArrayList();
		for (int i = 0; i < frames.length; ++i)
		{
			JInternalFrame fr = frames[i];
			if (!isToolWindow(fr) && !fr.isClosed())
			{
				framesList.add(frames[i]);
			}
		}
		return (JInternalFrame[]) framesList.toArray(new JInternalFrame[framesList.size()]);
	}

	public static boolean isWithinParent(Component wind)
	{
		if (wind == null)
		{
			throw new IllegalArgumentException("Null Component passed");
		}

		Component parent = wind.getParent();
		Rectangle parentRect = null;
		if (parent != null)
		{
			parentRect = new Rectangle(parent.getSize());
		}
		else
		{
			parentRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		}
		Rectangle windowBounds = wind.getBounds();
		if (windowBounds.x > (parentRect.width - 20)
			|| windowBounds.y > (parentRect.height - 20)
			|| (windowBounds.x + windowBounds.width) < 20
			|| (windowBounds.y + windowBounds.height) < 20)
		{
			return false;
		}
		return true;
	}

	// From a comp.lang.java.programmer item by James White.
	//	public static void showModally(JInternalFrame frame) {
	//		frame.setVisible(true);
	//
	//		// Since all input will be blocked until this dialog is dismissed,
	//		// make sure its parent containers are visible first (this component
	//		// is tested below).  This is necessary for JApplets, because
	//		// because an applet normally isn't made visible until after its
	//		// start() method returns -- if this method is called from start(),
	//		// the applet will appear to hang while an invisible modal frame
	//		// waits for input.
	//		if (!frame.isShowing()) {
	//			Container parent = frame.getParent();
	//			while (parent != null) {
	//				if (!parent.isVisible()) {
	//					parent.setVisible(true);
	//				}
	//				parent = parent.getParent();
	//			}
	//		}
	//
	//		try {
	//			if (SwingUtilities.isEventDispatchThread()) {
	//				// System.err.println("Modal on dispatch thread.");
	//				EventQueue theQueue =
	//				frame.getToolkit().getSystemEventQueue();
	//				while (frame.isShowing()) {
	//					// This is essentially the body of EventDispatchThread
	//					AWTEvent event = theQueue.getNextEvent();
	//					Object src = event.getSource();
	//					// can't call theQueue.dispatchEvent, so I pasted it's body here
	//					/*if (event instanceof ActiveEvent) {
	//						((ActiveEvent) event).dispatch();
	//					} else */
	//					if (src instanceof Component) {
	//						((Component) src).dispatchEvent(event);
	//					} else if (src instanceof MenuComponent) {
	//						((MenuComponent) src).dispatchEvent(event);
	//					} else {
	////						System.err.println("unable to dispatch event: " + event);
	//					}
	//				}
	//			} else {
	//				// System.err.println("Modal on seperate thread.");
	//				while (frame.isShowing()) {
	//					frame.wait();
	//				}
	//			}
	//		} catch(InterruptedException e) {
	//		}
	//		// System.err.println("Modal finished.");
	//	}

	/**
	 * Centers <CODE>wind</CODE> within the passed rectangle.
	 *
	 * @param   wind	The Window to be centered.
	 * @param   rect	The rectangle (in screen coords) to center
	 *					<CODE>wind</CODE> within.
	 *
	 * @throws IllegalArgumentException
	 *	  If <TT>Window</TT> or <TT>Rectangle</TT> is <TT>null</TT>.
	 */
	private static void center(Component wind, Rectangle rect)
	{
		if (wind == null || rect == null)
		{
			throw new IllegalArgumentException("null Window or Rectangle passed");
		}
		Dimension windSize = wind.getSize();
		int x = ((rect.width - windSize.width) / 2) + rect.x;
		int y = ((rect.height - windSize.height) / 2) + rect.y;
		if (y < rect.y)
		{
			y = rect.y;
		}
		wind.setLocation(x, y);
	}
}
