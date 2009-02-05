package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;
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
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.mainframe.SquirrelDesktopManager;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.gui.CascadeInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;

import javax.swing.*;

//
public class ScrollableDesktopPane extends JDesktopPane implements IDesktopContainer
{

	private static final long serialVersionUID = 1L;

	/** Logger for this class. */
   private transient final ILogger s_log = LoggerController.createLogger(ScrollableDesktopPane.class);


	private transient MyComponentListener _listener = new MyComponentListener();

   private transient final IInternalFramePositioner _internalFramePositioner = new CascadeInternalFramePositioner();
   private transient IApplication _app;


   /**
	 * Default ctor.
    * @param app
    */
	public ScrollableDesktopPane(IApplication app)
	{
		super();
      _app = app;
   }


	protected void paintComponent(Graphics g)
	{
		setPreferredSize(getRequiredSize());
		super.paintComponent(g);
	}

	public void remove(Component comp)
	{
		if (comp != null)
		{
			comp.removeComponentListener(_listener);
         super.remove(comp);
		}
		revalidate();
		repaint();
	}

	protected void addImpl(Component comp, Object constraints, int index)
	{
		if (comp != null)
		{
			comp.addComponentListener(_listener);
			revalidate();
		}
		super.addImpl(comp, constraints, index);
	}

	/**
	 * Calculate the required size of this desktop pane so that
	 * all visible intenal frames will be fully shown.
	 *
	 * @return <TT>Dimension</TT> required size.
	 */
	public Dimension getRequiredSize()
	{
		JInternalFrame[] frames = getAllFrames();
		int maxX = 0;
		int maxY = 0;
		for (int i = 0; i < frames.length; ++i)
		{
			if (frames[i].isVisible())
			{
				JInternalFrame frame = frames[i];
				int x = frame.getX() + frame.getWidth();
				if (x > maxX)
				{
					maxX = x;
				}
				int y = frame.getY() + frame.getHeight();
				if (y > maxY)
				{
					maxY = y;
				}
			}
		}
		return new Dimension(maxX, maxY);
	}



   public IWidget[] getAllWidgets()
   {
      JInternalFrame[] jInternalFrames = getAllFrames();
      IWidget[] ret = new IWidget[jInternalFrames.length];
      for (int i = 0; i < jInternalFrames.length; i++)
      {
         ret[i] = ((InternalFrameDelegate)jInternalFrames[i]).getWidget();
      }
      return ret;
   }

   public IWidget getSelectedWidget()
   {
      InternalFrameDelegate d = (InternalFrameDelegate) getSelectedFrame();
      return d.getWidget();
   }


   public JComponent getComponent()
   {
      return this;
   }

   public void addWidget(DialogWidget widget)
   {
      if (null != widget)
      {
         JInternalFrame delegate = (JInternalFrame) widget.getDelegate();
         addInternalFrame(delegate);
      }
   }

   public void addWidget(DockWidget widget)
   {
      JInternalFrame delegate = (JInternalFrame) widget.getDelegate();
      addInternalFrame(delegate);
   }

   public void addWidget(TabWidget widget)
   {
      JInternalFrame delegate = (JInternalFrame) widget.getDelegate();
      addInternalFrame(delegate);
   }

   private void addInternalFrame(JInternalFrame delegate)
   {
      beforeAdd(delegate);
      super.add(delegate);
      afterAdd(delegate);
   }


   private void afterAdd(JInternalFrame child)
   {
      if (!GUIUtils.isToolWindow(child))
      {
         _internalFramePositioner.positionInternalFrame(child);
      }
//		JInternalFrame[] frames = GUIUtils.getOpenNonToolWindows(getDesktopPane().getAllFrames());
//		_app.getActionCollection().internalFrameOpenedOrClosed(frames.length);

      // Size non-tool child window.
      if (!GUIUtils.isToolWindow(child))
      {
         if (child.isMaximizable() && _app.getSquirrelPreferences().getMaximizeSessionSheetOnOpen())
         {
            try
            {
               child.setMaximum(true);
            }
            catch (PropertyVetoException ex)
            {
               s_log.error("Unable to maximize window", ex);
            }
         }
      }
   }

   private void beforeAdd(JInternalFrame child)
   {
      if (!GUIUtils.isToolWindow(child))
      {
         Dimension cs = getSize();
         // Cast to int required as Dimension::setSize(double,double)
         // doesn't appear to do anything in JDK1.2.2.
         cs.setSize((int) (cs.width * 0.8d), (int) (cs.height * 0.8d));
         child.setSize(cs);
      }
   }




   public void putClientProperty(String key, String value)
   {
      super.putClientProperty(key, value);
   }

   public void setDesktopManager(SquirrelDesktopManager squirrelDesktopManager)
   {
      super.setDesktopManager(new DesktopManagerWrapper(squirrelDesktopManager));
   }

   private final class MyComponentListener implements ComponentListener
	{
		public void componentHidden(ComponentEvent evt)
		{
			ScrollableDesktopPane.this.revalidate();
		}

		public void componentMoved(ComponentEvent evt)
		{
			ScrollableDesktopPane.this.revalidate();
		}

		public void componentResized(ComponentEvent evt)
		{
			ScrollableDesktopPane.this.revalidate();
		}

		public void componentShown(ComponentEvent evt)
		{
			ScrollableDesktopPane.this.revalidate();
		}
	}
}
