package net.sourceforge.squirrel_sql.client.gui.mainframe;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DockWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.*;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPane;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.MessagePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IMainFrame;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;


@SuppressWarnings("serial")
public class MainFrame extends JFrame implements IMainFrame //BaseMDIParentFrame
{
   public interface IMenuIDs extends MainFrameMenuBar.IMenuIDs
	{
		// Empty body.
	}

	/** Logger for this class. */
	private final ILogger s_log = LoggerController.createLogger(MainFrame.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(MainFrame.class);    
    
	/** Application API. */
	private final IApplication _app;

//	private AliasesListInternalFrame _aliasesListWindow;
//	private DriversListInternalFrame _driversListWindow;

	/** Toolbar at top of window. */
	private MainFrameToolBar _toolBar;


	/** Status bar at bottom of window. */
	private MainFrameStatusBar _statusBar;

	/** Message panel at bottom of window. */
	// JASON: Should be part of status bar?
	private MessagePanel _msgPnl;

	/** If <TT>true</TT> then status bar is visible. */
	private boolean _statusBarVisible = false;

	private IDesktopContainer _desktop;

   private JSplitPane _splitPn;

   private SplitPnResizeHandler _splitPnResizeHandler;

   /**
	 * Ctor.
	 *
	 * @param	app	 Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT> <TT>IApplication</TT>
	 *			passed.
	 */
	public MainFrame(IApplication app)
	{
		super(Version.getVersion());
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_desktop = DesktopContainerFactory.createDesktopContainer(_app);
		createUserInterface();
		preferencesHaveChanged(null); // Initial load of prefs.
		_app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				synchronized (MainFrame.this)
				{
					preferencesHaveChanged(evt);
				}
			}
		});

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				IDesktopContainer comp = getDesktopContainer();
				comp.setPreferredSize(comp.getRequiredSize());
				comp.revalidate();

            if(_app.getDesktopStyle().isDockTabStyle())
            {
               _app.getMultipleWindowsHandler().registerMainFrame(_app, (DockTabDesktopPane)_desktop);
            }
			}
		});
		
	}
	// ...
	public void dispose()
	{
      boolean shouldDispose = true;
      if (!_app.shutdown(true))
      {
         String msg = s_stringMgr.getString("MainFrame.errorOnClose");
         shouldDispose = Dialogs.showYesNo(_app.getMainFrame(), msg);
      }
      if (shouldDispose)
      {
         closeAllToolWindows();
         super.dispose();
         System.exit(0);
      }
   }

	public void pack()
	{
		// Don't call super. Packing this frame causes problems.
	}

	public IApplication getApplication()
	{
		return _app;
	}

	public IDesktopContainer getDesktopContainer()
	{
		return _desktop;
	}

   public void addWidget(DialogWidget widget)
   {
      _desktop.addWidget(widget);
   }

   public void addWidget(DockWidget widget)
   {
      _desktop.addWidget(widget);
   }

   public void addWidget(TabWidget widget)
   {
      _desktop.addWidget(widget);
   }



   public JMenu getSessionMenu()
	{
		return ((MainFrameMenuBar) getJMenuBar()).getSessionMenu();
	}

	public void addToMenu(int menuId, JMenu menu)
	{
		if (menu == null)
		{
			throw new IllegalArgumentException("Null JMenu passed");
		}
		((MainFrameMenuBar)getJMenuBar()).addToMenu(menuId, menu);
	}

	public void addToMenu(int menuId, Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Null BaseAction passed");
		}
		((MainFrameMenuBar)getJMenuBar()).addToMenu(menuId, action);
	}

	/**
	 * Add component to the status bar.
	 *
	 * @param	comp	Component to add.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>JComponent</TT> passed.
	 */
	public void addToStatusBar(JComponent comp)
	{
		if (comp == null)
		{
			throw new IllegalArgumentException("JComponent == null");
		}
		_statusBar.addJComponent(comp);
	}

	/**
	 * Remove component to the main frames status bar.
	 *
	 * @param	comp	Component to remove.
	 */
	public void removeFromStatusBar(JComponent comp)
	{
		if (comp == null)
		{
			throw new IllegalArgumentException("JComponent == null");
		}
		_statusBar.remove(comp);
	}

   public MessagePanel getMessagePanel()
	{
		return _msgPnl;
	}

	private void preferencesHaveChanged(PropertyChangeEvent evt)
	{
		String propName = evt != null ? evt.getPropertyName() : null;

		final SquirrelPreferences prefs = _app.getSquirrelPreferences();

		if (propName == null
			|| propName.equals(
				SquirrelPreferences.IPropertyNames.SHOW_CONTENTS_WHEN_DRAGGING))
		{
			if (prefs.getShowContentsWhenDragging())
			{
				getDesktopContainer().putClientProperty("JDesktopPane.dragMode", null);
			}
			else
			{
				getDesktopContainer().putClientProperty("JDesktopPane.dragMode", "outline");
			}
		}

		if (propName == null
			|| propName.equals(SquirrelPreferences.IPropertyNames.SHOW_MAIN_STATUS_BAR))
		{
			final boolean show = prefs.getShowMainStatusBar();
			if (!show && _statusBarVisible)
			{
				getContentPane().remove(_statusBar);
				_statusBarVisible = false;
			}
			else if (show && !_statusBarVisible)
			{
				getContentPane().add(_statusBar, BorderLayout.SOUTH);
				_statusBarVisible = true;
			}
		}
		if (propName == null
			|| propName.equals(SquirrelPreferences.IPropertyNames.SHOW_MAIN_TOOL_BAR))
		{
			final boolean show = prefs.getShowMainToolBar();
			if (!show && _toolBar != null)
			{
				getContentPane().remove(_toolBar);
				_toolBar = null;
			}
			else if (show && _toolBar == null)
			{
				_toolBar = new MainFrameToolBar(_app);
				getContentPane().add(_toolBar, BorderLayout.NORTH);
			}
		}

	}

	private void closeAllToolWindows()
	{
		IWidget[] frames =
			WidgetUtils.getOpenToolWindows(getDesktopContainer().getAllWidgets());
		for (int i = 0; i < frames.length; ++i)
		{
			frames[i].dispose();
		}
	}

	private void createUserInterface()
	{
		setVisible(false);
		setDefaultCloseOperation(MainFrame.DO_NOTHING_ON_CLOSE);

		final SquirrelResources rsrc = _app.getResources();

		getDesktopContainer().setDesktopManager(new SquirrelDesktopManager(_app));

		final Container content = getContentPane();

		content.setLayout(new BorderLayout());
		final JScrollPane sp = new JScrollPane(getDesktopContainer().getComponent());
		sp.setBorder(BorderFactory.createEmptyBorder());

		_msgPnl = new MessagePanel()
      {
         public void setSize(int width, int height)
         {
            super.setSize(width, height);
            if(0 < width && 0 < height)
            {
               // The call here is the result of a desperate fight
               // to find a place where the components in the split
               // had not height = 0. If someone knows a better way
               // please tell me I'll apreciate any advice.
               // gerdwagner@users.sourceforge.net
               _splitPnResizeHandler.resizeSplitOnStartup();
            }
         }
      };
      _msgPnl.setName(MessagePanel.class.toString());
		_msgPnl.setEditable(false);


		_splitPn = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		_splitPn.add(sp);
		_splitPn.add(new JScrollPane(_msgPnl));

      _splitPn.setResizeWeight(1);
      _splitPn.setOneTouchExpandable(true);
      _splitPnResizeHandler = new SplitPnResizeHandler(_splitPn, _msgPnl);


      content.add(_splitPn, BorderLayout.CENTER);

		_statusBar = new MainFrameStatusBar(_app);
		final Font fn = _app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);

		setJMenuBar(new MainFrameMenuBar(_app, getDesktopContainer(), _app.getActionCollection()));

		setupFromPreferences();

		final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.APPLICATION_ICON);
		if (icon != null)
		{
			setIconImage(icon.getImage());
		}
		else
		{
			s_log.error("Missing icon for mainframe");
		}

		// On Win 2000 & XP mnemonics are normally hidden. To make them
		// visible you press the alt key. Under the Windows L&F pressing
		// alt may not work. This code is a workaround. See bug report
		// 4736093 for more information.
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ALT, Event.ALT_MASK, false),
				"repaint");

		validate();

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				dispose();
			}
		});
	}

	private void setupFromPreferences()
	{
		final SquirrelPreferences prefs = _app.getSquirrelPreferences();
		MainFrameWindowState ws = prefs.getMainFrameWindowState();

		// Position window to where it was when last closed. If this is not
		// on the screen, move it back on to the screen.
		setBounds(ws.getBounds().createRectangle());
		if (!GUIUtils.isWithinParent(this))
		{
			setLocation(new Point(10, 10));
		}
		setExtendedState(ws.getFrameExtendedState());
	}

   public JMenu getWindowsMenu()
	{
		return ((MainFrameMenuBar)getJMenuBar()).getWindowsMenu();
	}

   public void setEnabledAliasesMenu(boolean b)
   {
      MainFrameMenuBar mainFrameMenuBar = (MainFrameMenuBar) getJMenuBar();
      mainFrameMenuBar.setEnabledAliasesMenu(b);
   }

   public void setEnabledDriversMenu(boolean b)
   {
      MainFrameMenuBar mainFrameMenuBar = (MainFrameMenuBar) getJMenuBar();
      mainFrameMenuBar.setEnabledDriversMenu(b);
   }




   public void addToToolBar(Action act)
   {
      _toolBar.add(act);
   }
}
