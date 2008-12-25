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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.MaximizeInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.client.gui.mainframe.IHasJDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.mainframe.WidgetUtils;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeInternalFramesAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * This class will cascade all internal frames owned by a
 * <CODE>JDesktopPane</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MaximizeInternalFramesAction
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
	public MaximizeInternalFramesAction(IApplication app)
	{
      super(s_stringMgr.getString("MaximizeInternalFramesAction.title"));
      _app = app;
   }

   /**
	 * Set the <CODE>JDesktopPane</CODE> that owns the internal frames to be
	 * maximized.
	 *
	 * @param   desktop	 the <CODE>JDesktopPane</CODE> that owns the
	 *					  internal frames to be maximized.
	 */
	public void setDesktopContainer(IDesktopContainer value)
	{
		_desktop = value;
	}

	/**
	 * Maximize the internal frames.
	 *
	 * @param   evt	 Specifies the event being proceessed.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (_desktop != null  && _app.getDesktopStyle().isInternalFrameStyle())
		{
			MaximizeInternalFramePositioner pos =
				new MaximizeInternalFramePositioner();
			IWidget[] widgets = WidgetUtils.getOpenNonToolWindows(_desktop.getAllWidgets());
			for (int i = widgets.length - 1; i >= 0; --i)
			{
				pos.positionInternalFrame(widgets[i].getInternalFrame());
			}
		}
	}
}
