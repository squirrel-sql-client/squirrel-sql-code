package net.sourceforge.squirrel_sql.client.mainframe;
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
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;

abstract class BaseToolWindow extends BaseSheet
{

	protected interface IUserInterfaceFactory
	{
		ToolBar getToolBar();
		BasePopupMenu getPopupMenu();
		JList getList();
		String getWindowTitle();
		ICommand getDoubleClickCommand();
		void enableDisableActions();
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(BaseToolWindow.class);

	/** Application API. */
	private IApplication _app;

	private IUserInterfaceFactory _uiFactory;

	/** Popup menu for the list. */
	private BasePopupMenu _popupMenu;

	/** Toolbar for window. */
	private ToolBar _toolBar;

	private boolean _hasBeenBuilt;

	private boolean _hasBeenSized = false;

	public BaseToolWindow(IApplication app, IUserInterfaceFactory uiFactory)
	{
		super("", true, true);
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (uiFactory == null)
		{
			throw new IllegalArgumentException("Null IUserInterfaceFactory passed");
		}
		_app = app;
		_uiFactory = uiFactory;

		createUserInterface();
	}

	public void updateUI()
	{
		super.updateUI();
		if (_hasBeenBuilt)
		{
			_hasBeenSized = false;
			privateResize();
		}
	}

	protected IUserInterfaceFactory getUserInterfaceFactory()
	{
		return _uiFactory;
	}

	protected void setToolBar(ToolBar tb)
	{
		final Container content = getContentPane();
		if (_toolBar != null)
		{
			content.remove(_toolBar);
		}
		if (tb != null)
		{
			content.add(tb, BorderLayout.NORTH);
		}
		_toolBar = tb;
	}

	/**
	 * Process a mouse press event in this list. If this event is a trigger
	 * for a popup menu then display the popup menu.
	 *
	 * @param   evt	 The mouse event being processed.
	 */
	private void mousePress(MouseEvent evt)
	{
		if (evt.isPopupTrigger())
		{
			if (_popupMenu == null)
			{
				_popupMenu = _uiFactory.getPopupMenu();
			}
			_popupMenu.show(evt);
		}
	}

	private void privateResize()
	{
		if (!_hasBeenSized)
		{
			if (_toolBar != null)
			{
				_hasBeenSized = true;
				Dimension windowSize = getSize();
				int rqdWidth = _toolBar.getPreferredSize().width + 15;
				if (rqdWidth > windowSize.width)
				{
					windowSize.width = rqdWidth;
					setSize(windowSize);
				}
			}
		}
	}

	private void createUserInterface()
	{
		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Pane to add window content to.
		final Container content = getContentPane();
		content.setLayout(new BorderLayout());

		String title = _uiFactory.getWindowTitle();
		if (title != null)
		{
			setTitle(title);
		}

		// Put toolbar at top of window.
		setToolBar(_uiFactory.getToolBar());
//		if (_toolBar != null)
//		{
//			String title = _uiFactory.getWindowTitle();
//			if (title != null)
//			{
//				final JLabel lbl = new JLabel(title, SwingConstants.CENTER);
//				lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
//				_toolBar.add(lbl, 0);
//				_toolBar.add(new JToolBar.Separator(), 1);
//				setTitle(title);
//			}
//			content.add(_toolBar, BorderLayout.NORTH);
//		}

		// The main list for window.
		final JList list = _uiFactory.getList();

		// Allow list to scroll.
		final JScrollPane sp = new JScrollPane();
		sp.setViewportView(list);
		sp.setPreferredSize(new Dimension(100, 100));

		// List in the centre of the window.
		content.add(sp, BorderLayout.CENTER);

		// Add mouse listener for displaying popup menu.
		list.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				mousePress(evt);
			}
			public void mouseReleased(MouseEvent evt)
			{
				mousePress(evt);
			}
		});

		// Add a listener to handle doubleclick events in the list.
		list.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					ICommand cmd = _uiFactory.getDoubleClickCommand();
					if (cmd != null)
					{
						try
						{
							cmd.execute();
						}
						catch (BaseException ex)
						{
							s_log.error("Error occured executing doubleclick event", ex);
						}
					}
				}
			}
		});

		// Add listener to listen for items added/removed from list.
		list.getModel().addListDataListener(new ListDataListener()
		{
			public void intervalAdded(ListDataEvent evt)
			{
				list.setSelectedIndex(evt.getIndex0()); // select the one just added.
				_uiFactory.enableDisableActions();
			}
			public void intervalRemoved(ListDataEvent evt)
			{
				int nextIdx = evt.getIndex0();
				int lastIdx = list.getModel().getSize() - 1;
				if (nextIdx > lastIdx)
				{
					nextIdx = lastIdx;
				}
				list.setSelectedIndex(nextIdx);
				_uiFactory.enableDisableActions();
			}
			public void contentsChanged(ListDataEvent evt)
			{
			}
		});

		// When this window is activated give focus to the list box.
		// When window opened ensure it is wide enough to display the toolbar.
		// There is a bug in JDK1.2 where internalFrameOpened() doesn't get
		// called so we've used a workaround. The workaround doesn't work in
		// JDK1.3.
		addInternalFrameListener(new InternalFrameAdapter()
		{
			private boolean _hasBeenActivated = false;
			public void internalFrameActivated(InternalFrameEvent evt)
			{
				if (!_hasBeenActivated)
				{
					_hasBeenActivated = true;
					privateResize();
				}
				list.requestFocus();
			}

			public void internalFrameOpened(InternalFrameEvent evt)
			{
				privateResize();
			}

		});

		validate();

	}
}