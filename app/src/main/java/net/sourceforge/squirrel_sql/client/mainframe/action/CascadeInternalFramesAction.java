package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.mainframe.IHasJDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.mainframe.WidgetUtils;
import net.sourceforge.squirrel_sql.fw.gui.CascadeInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This class will cascade all internal frames owned by a
 * <CODE>JDesktopPane</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CascadeInternalFramesAction
	extends BaseAction
	implements IHasJDesktopPane
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CascadeInternalFramesAction.class);

	/**
	 * The <CODE>JDesktopPane</CODE> that owns the internal frames to be
	 * cascaded.
	 */
	private IDesktopContainer _desktop;
   private IApplication _app;

   /**
	 * Default constructor.
    * @param app
    */
	public CascadeInternalFramesAction(IApplication app)
	{
      _app = app;
   }

   /**
	 * Set the <CODE>JDesktopPane</CODE> that owns the internal frames to be
	 * tiled.
	 *
	 * @param	desktop		the <CODE>JDesktopPane</CODE> that owns the
	 *						internal frames to be tiled.
	 */
	public void setDesktopContainer(IDesktopContainer value)
	{
		_desktop = value;
	}

	/**
	 * Cascade the internal frames.
	 *
	 * @param	evt	 Specifies the event being proceessed.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (_desktop != null && _app.getDesktopStyle().isInternalFrameStyle())
		{
			Dimension cs = null; // Size to set child windows to.
			CascadeInternalFramePositioner pos =
				new CascadeInternalFramePositioner();
			IWidget[] widgets =
				WidgetUtils.getOpenNonToolWindows(_desktop.getAllWidgets());
			for (int i = widgets.length - 1; i >= 0; --i)
			{
				JInternalFrame child = widgets[i].getInternalFrame();

				if (cs == null && child.getParent() != null)
				{
					cs = child.getParent().getSize();
					// Cast to int required as Dimension::setSize(double,double)
					// doesn't appear to do anything in JDK1.2.2.
					cs.setSize(
						(int) (cs.width * 0.8d),
						(int) (cs.height * 0.8d));
				}
				if (cs != null)
				{
					child.setSize(cs);
					pos.positionInternalFrame(child);
				}
			}
		}
	}
}
