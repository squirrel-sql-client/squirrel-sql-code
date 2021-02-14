package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2004 Colin Bell
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

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Common GUI utilities accessed via static methods.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class GUIUtils
{
	private static final ILogger s_log = LoggerController.createLogger(GUIUtils.class);

   /**
    * Centers <CODE>wind</CODE> within its parent. If it has no parent then
    * center within the screen. If centering would cause the title bar to go
    * above the parent (I.E. cannot see the titlebar and so cannot move the
    * window) then move the window down.
    *
    * @param	 wind	The Window to be centered.
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
         center(wind, new Rectangle(parent.getLocationOnScreen(), parent.getSize()));
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
	 * @param	frame	The internal frame to be centered.
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
	 * @param	wind	The Window to be centered.
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

	public static void moveToFront(final JInternalFrame fr)
	{
		if (fr != null)
		{
			processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					fr.moveToFront();
					fr.setVisible(true);
					try
					{
						fr.setSelected(true);
                  if(fr.isIcon())
                  {
                     fr.setIcon(false);
                  }
                  fr.setSelected(true);
					}
					catch (PropertyVetoException ex)
					{
						s_log.error("Error bringing internal frame to the front", ex);
					}
               fr.requestFocus();
				}
			});
		}
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


	public static Window getOwningWindow(Component comp)
	{
		if(comp instanceof Window)
		{
			return (Window) comp;
		}

		Dialog owningDialog = _getOwningDialog(comp);

		if(null != owningDialog)
		{
			return owningDialog;
		}
		else
		{
			return getOwningFrame(comp);
		}
	}

	/**
	 * Form outside use {@link #getOwningWindow(Component)}
	 */
	private static Dialog _getOwningDialog(Component comp)
	{
		if (comp == null)
		{
			return null;
		}

		if (comp instanceof Dialog)
		{
			return (Dialog) comp;
		}
		return _getOwningDialog(SwingUtilities.windowForComponent(comp));
	}


	/**
	 * Return <TT>true</TT> if <TT>frame</TT> is a tool window. I.E. is the
	 * <TT>JInternalFrame.isPalette</TT> set to <TT>Boolean.TRUE</TT>?
	 *
	 * @param	frame	The <TT>JInternalFrame</TT> to be checked.
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
	 * @param	btns	Array of buttons to eb resized.
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

   public static boolean isWithinParent(Component wind)
	{
		if (wind == null)
		{
			throw new IllegalArgumentException("Null Component passed");
		}

		Rectangle windowBounds = wind.getBounds();
		Component parent = wind.getParent();
		Rectangle parentRect = null;
		if (parent != null)
		{
			parentRect = new Rectangle(parent.getSize());
		}
		else
		{
			//parentRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			parentRect = getScreenBoundsFor(windowBounds);
		}
		
		//if (windowBounds.x > (parentRect.width - 20)
//			|| windowBounds.y > (parentRect.height - 20)
			//|| (windowBounds.x + windowBounds.width) < 20
			//|| (windowBounds.y + windowBounds.height) < 20)
		//{
			//return false;
		//}
		if (windowBounds.x < (parentRect.x - 20)
				|| windowBounds.y < (parentRect.y - 20))
		{
			return false;
		}
		return true;
	}

	public static Rectangle getScreenBoundsFor(Rectangle rc)
	{
        final GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        final List<GraphicsConfiguration> configs = 
            new ArrayList<GraphicsConfiguration>();

        for (int i = 0; i < gds.length; i++)
        {
            GraphicsConfiguration gc = gds[i].getDefaultConfiguration();
            if (rc.intersects(gc.getBounds()))
            {
            	configs.add(gc);
            }
        }
        
        GraphicsConfiguration selected = null;
        if (configs.size() > 0)
        {
            for (Iterator<GraphicsConfiguration> it = configs.iterator(); it.hasNext();)
            {
            	GraphicsConfiguration gcc = it.next();
                if (selected == null)
                    selected = gcc;
                else
                {
                    if (gcc.getBounds().contains(rc.x + 20, rc.y + 20))
                    {
                    	selected = gcc;
                    	break;
                    }
                }
            }
        }
        else
        {
            selected = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }

        int x = selected.getBounds().x;
        int y = selected.getBounds().y;
        int w = selected.getBounds().width;
        int h = selected.getBounds().height;
        
        return new Rectangle(x,y,w,h); 
	}
	
	public static void processOnSwingEventThread(Runnable todo)
	{
		processOnSwingEventThread(todo, false);
	}

	public static void processOnSwingEventThread(Runnable todo, boolean wait)
	{
		if (todo == null)
		{
			throw new IllegalArgumentException("Runnable == null");
		}

		if (SwingUtilities.isEventDispatchThread())
		{
			todo.run();
			return;
		}

		if (wait)
		{
			try
			{
				SwingUtilities.invokeAndWait(todo);
			}
			catch (InvocationTargetException | InterruptedException ex)
			{
				throw Utilities.wrapRuntime(ex);
			}
		}
		else
		{
			SwingUtilities.invokeLater(todo);
		}
	}

	/**
	 * Centers <CODE>wind</CODE> within the passed rectangle.
	 *
	 * @param	wind	The Window to be centered.
	 * @param	rect	The rectangle (in screen coords) to center
	 *					<CODE>wind</CODE> within.
	 *
	 * @throws	IllegalArgumentException
	 *			If <TT>Window</TT> or <TT>Rectangle</TT> is <TT>null</TT>.
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

	public static Point getScreenLocationFor(Component component)
   {
      Component comp = component;

      Point ret = new Point(0,0);

      for(;;)
      {
         Point buf;
         if(comp instanceof Window)
         {
            buf = comp.getLocationOnScreen();
            ret.translate(buf.x, buf.y);
            return ret;
         }
         else
         {
            buf = comp.getLocation();
            ret.translate(buf.x, buf.y);
            comp = comp.getParent();
         }

      }
   }

   public static void enableCloseByEscape(final JDialog dialog)
   {
		enableCloseByEscape(dialog, null);
	}

   public static void enableCloseByEscape(final JDialog dialog, final CloseByEscapeListener closeByEscapeListener)
   {
      AbstractAction closeAction = new AbstractAction()
      {

         public void actionPerformed(ActionEvent actionEvent)
         {
         	if(null != closeByEscapeListener)
				{
					closeByEscapeListener.willCloseByEscape(dialog);
				}

            dialog.setVisible(false);
            dialog.dispose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      dialog.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      dialog.getRootPane().getActionMap().put("CloseAction", closeAction);
   }

	public static void enableCloseByEscape(DialogWidget dialogWidget)
	{
		enableCloseByEscape(dialogWidget, null);
	}

	public static void enableCloseByEscape(DialogWidget dialogWidget, final CloseByEscapeForDialogWidgetListener closeByEscapeListener)
	{
		AbstractAction closeAction = new AbstractAction()
		{

			public void actionPerformed(ActionEvent actionEvent)
			{
				if(null != closeByEscapeListener)
				{
					closeByEscapeListener.willCloseByEscape(dialogWidget);
				}

				dialogWidget.setVisible(false);
				dialogWidget.dispose();
			}
		};

		KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		dialogWidget.getDelegate().getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
		dialogWidget.getDelegate().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
		dialogWidget.getDelegate().getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
		dialogWidget.getDelegate().getRootPane().getActionMap().put("CloseAction", closeAction);
	}


	public static DefaultMutableTreeNode createFolderNode(Object userObject)
   {
      DefaultMutableTreeNode newFolder  = new DefaultMutableTreeNode(userObject)
      {
         public boolean isLeaf()
         {
            return false;
         }
      };
      return newFolder;
   }

	public static void forceFocus(final JComponent comp)
	{
		forceFocus(comp, null);
	}

	public static void forceFocus(final JComponent comp, Runnable callWhenFocused)
	{
		final Timer[] timerRef = new Timer[1];

		timerRef[0] = new Timer(100, new ActionListener()
		{
			private int maxCount = 0;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (comp.hasFocus() || maxCount > 15)
				{
					timerRef[0].stop();

					if(null != callWhenFocused)
					{
						callWhenFocused.run();
					}
					return;
				}
				comp.requestFocusInWindow();
				comp.requestFocus();
				++maxCount;

			}
		});

		timerRef[0].setRepeats(true);

		timerRef[0].start();

		comp.requestFocusInWindow();
		comp.requestFocus();
	}


	public static void forceProperty(PropertyCheck propertyCheck)
	{
		forceProperty(propertyCheck, null);
	}
	public static void forceProperty(PropertyCheck propertyCheck, Runnable callOnSuccess)
	{
		final Timer[] timerRef = new Timer[1];

		timerRef[0] = new Timer(100, new ActionListener()
		{
			private int maxCount = 0;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (propertyCheck.checkAndSetProperty() || maxCount > 15)
				{
					timerRef[0].stop();

					if(null != callOnSuccess)
					{
						callOnSuccess.run();
					}

					return;
				}
				++maxCount;

			}
		});

		timerRef[0].setRepeats(true);

		timerRef[0].start();

		propertyCheck.checkAndSetProperty();
	}

	public static void forceWidth(JComponent comp, int width)
	{
		forceProperty(() -> checkAndForceSize(comp, width));
	}

	private static boolean checkAndForceSize(JComponent comp, int width)
	{
		setPreferredWidth(comp, width);
		setMinimumWidth(comp, width);

		return comp.getSize().width == width;
	}


	public static void forceScrollToBegin(JScrollPane scrollPane)
	{
		final Timer[] timerRef = new Timer[1];

		timerRef[0] = new Timer(100, new ActionListener()
		{
			private int maxCount = 0;

			@Override
			public void actionPerformed(ActionEvent e)
			{
            int hValue = scrollPane.getHorizontalScrollBar().getValue();
            int vValue = scrollPane.getVerticalScrollBar().getValue();
            if ( (0 == hValue && 0 == vValue)  || maxCount > 15)
				{
					timerRef[0].stop();
					return;
				}
            scrollPane.scrollRectToVisible(new Rectangle(0,0, 1,1));
            scrollPane.getHorizontalScrollBar().setValue(0);
            scrollPane.getVerticalScrollBar().setValue(0);
            ++maxCount;

            //System.out.println("GUIUtils.actionPerformed v = " + vValue + " h = " + hValue);

			}
		});

		timerRef[0].setRepeats(true);

		timerRef[0].start();

      scrollPane.scrollRectToVisible(new Rectangle(0,0, 1,1));
      scrollPane.getHorizontalScrollBar().setValue(0);
      scrollPane.getVerticalScrollBar().setValue(0);
	}

	public static int getMinHeightOfAllScreens()
	{
		//return Toolkit.getDefaultToolkit().getScreenSize().height;

		//Rectangle virtualBounds = new Rectangle();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		int minHight = Integer.MAX_VALUE;
		for (int j = 0; j < gs.length; j++)
		{
			GraphicsDevice gd = gs[j];
			GraphicsConfiguration[] gc = gd.getConfigurations();

			for (int i = 0; i < gc.length; i++)
			{
				//virtualBounds = virtualBounds.union(gc[i].getBounds());
				minHight = Math.min(gc[i].getBounds().height, minHight);
			}
		}

		return minHight;
	}


	/**
	 * If the rectInScreenCoordinates are not inside one of the screens the x and y coordinates are shifted
	 * such that it fits in the screen and that takes the smallest shift possible.
	 *
	 * Note: If rectInScreenCoordinates is larger than any of the screens the method won't work / has to be fixed/extended.
	 *
	 */
	public static Rectangle ensureBoundsOnOneScreen(Rectangle rectInScreenCoordinates)
	{
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
		Rectangle screenBounds = new Rectangle();

		int minDx = 0;
		int minDy = 0;

		for (int j = 0; j < graphicsDevices.length; j++)
		{

			GraphicsDevice graphicsDevice = graphicsDevices[j];
			screenBounds.setRect(graphicsDevice.getDefaultConfiguration().getBounds());

			screenBounds.setRect(screenBounds.x, screenBounds.y, screenBounds.width, screenBounds.height);

			if (screenBounds.contains(rectInScreenCoordinates))
			{
				return rectInScreenCoordinates;
			}

//			int dx = Math.abs(screenBounds.x + screenBounds.width - (rectInScreenCoordinates.x + rectInScreenCoordinates.width));
//			int dy = Math.abs(screenBounds.y + screenBounds.height - (rectInScreenCoordinates.y + rectInScreenCoordinates.height));
			int dx = screenBounds.x + screenBounds.width - (rectInScreenCoordinates.x + rectInScreenCoordinates.width);
			int dy = screenBounds.y + screenBounds.height - (rectInScreenCoordinates.y + rectInScreenCoordinates.height);

			if(0 == minDx && 0 == minDy)
			{
				minDx = dx;
				minDy = dy;
			}
			else if(minDx*minDx + minDy*minDy > dx*dx + dy*dy)
			{
				minDx = dx;
				minDy = dy;
			}
		}

//		rectInScreenCoordinates.x = Math.max(0, rectInScreenCoordinates.x - minDx);
//		rectInScreenCoordinates.y = Math.max(0, rectInScreenCoordinates.y - minDy);

		if (minDx < 0)
		{
			rectInScreenCoordinates.x = Math.max(0, rectInScreenCoordinates.x + minDx);
		}

		if (minDy < 0)
		{
			rectInScreenCoordinates.y = Math.max(0, rectInScreenCoordinates.y + minDy);
		}

		return rectInScreenCoordinates;

	}

	/**
	 * @return Just for convenience returns the btn parameter
	 */
   public static <T extends AbstractButton> T styleAsToolbarButton(T btn)
   {
   	return styleAsToolbarButton(btn, false);
	}

	/**
	 * @return Just for convenience returns the btn parameter
	 */
   public static <T extends AbstractButton> T styleAsToolbarButton(T btn, boolean focusable)
   {
   	return styleAsToolbarButton(btn, focusable, true);
	}
	/**
	 * @return Just for convenience returns the btn parameter
	 */
   public static <T extends AbstractButton> T styleAsToolbarButton(T btn, boolean focusable, boolean bordered)
   {
		setButtonContentAreaFilledRespectSelectedToggle(btn, false);

		if (bordered)
		{
			btn.setBorder(BorderFactory.createEtchedBorder());
		}
		else
		{
			btn.setBorder(BorderFactory.createEmptyBorder());
		}

		btn.setFocusable(focusable);

      btn.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent e)
         {
				setButtonContentAreaFilledRespectSelectedToggle(btn, true);
         }

         @Override
         public void mouseExited(MouseEvent e)
         {
				setButtonContentAreaFilledRespectSelectedToggle(btn, false);
         }
      });

      if (btn instanceof JToggleButton)
      {
         btn.addChangeListener(e -> btn.setContentAreaFilled(btn.isSelected() || isMouseOver(btn)));
      }
      return btn;
   }

   private static boolean isMouseOver(Component component)
   {
      if (!component.isShowing() || GraphicsEnvironment.isHeadless())
      {
         return false;
      }

      GraphicsConfiguration gconf = component.getGraphicsConfiguration();
      PointerInfo mousePointer = MouseInfo.getPointerInfo();
      if (gconf.getDevice() != mousePointer.getDevice())
      {
         return false;
      }

      Point location = component.getLocationOnScreen();
      Rectangle bounds = new Rectangle(location.x, location.y, component.getWidth(), component.getHeight());
      return bounds.contains(mousePointer.getLocation());
   }

	private static void setButtonContentAreaFilledRespectSelectedToggle(AbstractButton btn, boolean b)
	{
		if (btn instanceof JToggleButton && btn.isSelected())
		{
			btn.setContentAreaFilled(true);
			return;
		}


		btn.setContentAreaFilled(b);
	}

	public static void listenToMouseWheelClickOnTab(JTabbedPane tabbedPane, MouseWheelClickOnTabListener mouseWheelClickOnTabListener)
	{
		tabbedPane.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				handleMiddleMouseClick(e, tabbedPane, mouseWheelClickOnTabListener);
			}
		});

	}

	private static void handleMiddleMouseClick(MouseEvent e, JTabbedPane tabbedPane, MouseWheelClickOnTabListener mouseWheelClickOnTabListener)
	{
		if(SwingUtilities.isMiddleMouseButton (e))
		{
			int tabIndex = tabbedPane.getUI().tabForCoordinate(tabbedPane, e.getX(), e.getY());
			if (-1 != tabIndex)
			{
				mouseWheelClickOnTabListener.mouseWheeleClickedOnTabComponent(tabIndex, tabbedPane.getTabComponentAt(tabIndex));
			}
		}

	}

	public static Dimension initLocation(Window window, int defaultWidth, int defaultHeight)
	{
		final String identifier = window.getClass().getName();
		return initLocation(window, defaultWidth, defaultHeight, identifier);
	}

	public static Dimension initLocation(Window window, int defaultWidth, int defaultHeight, String identifier)
	{
		String widthPropKey = identifier + ".WIDTH";
		String heightPropKey = identifier + ".HEIGHT";

		Dimension size = new Dimension(Props.getInt(widthPropKey, defaultWidth), Props.getInt(heightPropKey, defaultHeight));
		window.setSize(size);

		GUIUtils.centerWithinParent(window);

		window.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				if (0 < window.getWidth() && 0 < window.getHeight())
				{
					Props.putInt(widthPropKey, window.getWidth());
					Props.putInt(heightPropKey, window.getHeight());
				}
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				if (0 < window.getWidth() && 0 < window.getHeight())
				{
					Props.putInt(widthPropKey, window.getWidth());
					Props.putInt(heightPropKey, window.getHeight());
				}
			}
		});

		return size;
	}

	public static JTextField styleTextFieldToCopyableLabel(JTextField textField)
   {
      textField.setEditable(false);
      textField.setBackground(new JPanel().getBackground());
      textField.setBorder(null);
      return textField;
   }

	public static JComponent setPreferredWidth(JComponent comp, int width)
	{
		comp.setPreferredSize(new Dimension(width, comp.getPreferredSize().height));
		return comp;
	}

	public static JComponent setPreferredHeight(JComponent comp, int height)
	{
		comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, height));
		return comp;
	}

	public static JComponent setMaximumWidth(JComponent comp, int width)
	{
		comp.setMaximumSize(new Dimension(width, comp.getMaximumSize().height));
		return comp;
	}

	public static JComponent setMaximumHeight(JComponent comp, int height)
	{
		comp.setMaximumSize(new Dimension(comp.getMaximumSize().width, height));
		return comp;
	}

	public static JComponent setMinimumWidth(JComponent comp, int width)
	{
		comp.setMinimumSize(new Dimension(width, comp.getMinimumSize().height));
		return comp;
	}

	public static JComponent setMinimumHeight(JComponent comp, int height)
	{
		comp.setMinimumSize(new Dimension(comp.getMinimumSize().width, height));
		return comp;
	}

   public static void inheritBackground(Component comp)
   {
      if (comp instanceof JComponent)
         ((JComponent) comp).setOpaque(false);

      Color original = comp.isBackgroundSet() ? comp.getBackground() : null;
      Runnable updateBackground = () ->
      {
         Component parent = comp.getParent();
         comp.setBackground(parent != null && parent.isBackgroundSet()
                            ? parent.getBackground()
                            : original);
      };

      PropertyChangeListener backgroundListener = evt -> updateBackground.run();

      comp.addHierarchyListener(evt ->
      {
         if ((evt.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) == 0)
            return;

         if (evt.getChanged() != comp)
            return;

         Container previous = evt.getChangedParent();
         Container current = comp.getParent();

         if (previous != current && previous != null)
         {
            previous.removePropertyChangeListener("background", backgroundListener);
         }

         if (current != null)
         {
            current.addPropertyChangeListener("background", backgroundListener);
            updateBackground.run();
         }
      });

      if (comp.getParent() != null)
      {
         comp.getParent().addPropertyChangeListener(backgroundListener);
         updateBackground.run();
      }
   }

   public static JPanel createVerticalSeparatorPanel()
   {
      JPanel separator = new JPanel();
      separator.setPreferredSize(new Dimension(4, separator.getPreferredSize().height));
      separator.setBorder(BorderFactory.createEtchedBorder());
      return separator;
   }

   public static JPanel createHorizontalSeparatorPanel()
   {
      JPanel separator = new JPanel();
      separator.setPreferredSize(new Dimension(separator.getPreferredSize().width, 4));
      separator.setBorder(BorderFactory.createEtchedBorder());
      return separator;
   }
}
