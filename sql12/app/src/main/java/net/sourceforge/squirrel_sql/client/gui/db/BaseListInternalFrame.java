package net.sourceforge.squirrel_sql.client.gui.db;
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DockWidget;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

abstract class BaseListInternalFrame extends DockWidget
{
	protected interface IUserInterfaceFactory
	{
		ToolBar getToolBar();
		BasePopupMenu getPopupMenu();
		IBaseList getList();
		String getWindowTitle();
		ICommand getDoubleClickCommand(MouseEvent evt);
		SquirrelPreferences getPreferences();
	}

	/** Logger for this class. */
	private static ILogger s_log =
			LoggerController.createLogger(BaseListInternalFrame.class);

	private IUserInterfaceFactory _uiFactory;

	/** Popup menu for the list. */
	private BasePopupMenu _popupMenu;

	/** Toolbar for window. */
	private ToolBar _toolBar;

	private boolean _hasBeenBuilt;

	private boolean _hasBeenSized = false;

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(BaseListInternalFrame.class);
    
	public BaseListInternalFrame(IUserInterfaceFactory uiFactory, IApplication app)
	{
		super(uiFactory.getWindowTitle(), true, true, app);
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
	 * @param	evt	 The mouse event being processed.
	 */
	private void onMousePress(MouseEvent evt)
	{
		if (evt.isPopupTrigger())
		{
			
			// If the user wants to select for Right mouse clicks then change the selection before popup appears
         if (_uiFactory.getPreferences().getSelectOnRightMouseClick())
         {
            _uiFactory.getList().selectListEntryAtPoint(evt.getPoint());
         }

         if (_popupMenu == null)
			{
				_popupMenu = _uiFactory.getPopupMenu();
			}
			_popupMenu.show(evt);
		}
	}

   private void onMouseClicked(MouseEvent evt)
   {
      if (evt.getClickCount() == 2)
      {
         ICommand cmd = _uiFactory.getDoubleClickCommand(evt);
         if (cmd != null)
         {
            try
            {
               cmd.execute();
            }
            catch (BaseException ex)
            {
                      // i18n[BaseListInternalFrame.error.execdoubleclick=Error occured executing doubleclick event]
               s_log.error(s_stringMgr.getString("BaseListInternalFrame.error.execdoubleclick"), ex);
            }
         }
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
		makeToolWindow(true);

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// Pane to add window content to.
		final Container content = getContentPane();
		content.setLayout(new BorderLayout());

		String winTitle = _uiFactory.getWindowTitle();
		if (winTitle != null)
		{
			setTitle(winTitle);
		}

		// Put toolbar at top of window.
		setToolBar(_uiFactory.getToolBar());

		// The main list for window.
		final IBaseList list = _uiFactory.getList();


		// List in the centre of the window.
		content.add(list.getComponent(), BorderLayout.CENTER);

		// Add mouse listener for displaying popup menu.
		list.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				onMousePress(evt);
			}
			public void mouseReleased(MouseEvent evt)
			{
				onMousePress(evt);
			}

         public void mouseClicked(MouseEvent evt)
         {
            onMouseClicked(evt);
         }

		});


//		// When window opened ensure it is wide enough to display the toolbar.
//		// There is a bug in JDK1.2 where internalFrameOpened() doesn't get
//		// called so we've used a workaround. The workaround doesn't work in
//		// JDK1.3.
//		addInternalFrameListener(new InternalFrameAdapter()
//		{
//			//private boolean _hasBeenActivated = false;
//			//public void internalFrameActivated(InternalFrameEvent evt)
//			//{
//				//if (!_hasBeenActivated)
//				//{
//				////	_hasBeenActivated = true;
//				//	privateResize();
//				//}
//				//list.requestFocus();
//			//}
//
//			public void internalFrameOpened(InternalFrameEvent evt)
//			{
//				privateResize();
//			}
//
//		});
//
//		validate();

	}
}
