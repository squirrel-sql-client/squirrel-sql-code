package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.gui.ToolBar;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
/**
 * This toolbar will navigate through a <TT>HtmlViewerPanel</TT>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class HtmlViewerPanelToolBar extends ToolBar
{
	/** Application API. */
	private final IApplication _app;

	/** Panel that this toolbar is responsible for. */
	private final HtmlViewerPanel _pnl;

	/**
	 * Ctor.
	 *
	 * @param	app		Applciation API.
	 * @param	pnl		Panel that this toolbar will navigate.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IApplciation</TT> or
	 *			<TT>HtmlViewerPanel</TT> passed.
	 */
	public HtmlViewerPanelToolBar(IApplication app, HtmlViewerPanel pnl)
	{
		super();

		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (pnl == null)
		{
			throw new IllegalArgumentException("HtmlViewerPanel == null");
		}

		_app = app;
		_pnl = pnl;

		setUseRolloverButtons(true);
		setFloatable(false);
		add(new HomeAction(_app));
		add(new BackAction(_app));
		add(new ForwardAction(_app));
		add(new RefreshAction(_app));
	}

	private final class BackAction extends SquirrelAction
	{
		public BackAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.goBack();
		}
	}

	private final class ForwardAction extends SquirrelAction
	{
		public ForwardAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.goForward();
		}
	}

	private final class RefreshAction extends SquirrelAction
	{
		public RefreshAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.refreshPage();
		}
	}

	private final class HomeAction extends SquirrelAction
	{
		public HomeAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.goHome();
		}
	}
}
