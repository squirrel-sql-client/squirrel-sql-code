package com.digitprop.tonic;


import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;


/**	UI delegate for JMenuBars.
 * 
 * 	@author	Markus Fischer
  *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307  USA
 * 
 * You can contact the author at:
 *    Markus Fischer
 *    www.digitprop.com
 *    info@digitprop.com
 * ------------------------------------------------------------------------
 */
public class MenuBarUI extends BasicMenuBarUI
{
	/**	The underlying menu bar */
	protected JMenuBar menuBar= null;

	/**	The container listener registered with the menu bar */
	protected ContainerListener containerListener;

	/**	The change listener registered with the menu bar */
	protected ChangeListener changeListener;

	/**	The property change listener registered with the menu bar */
	private PropertyChangeListener propertyChangeListener;


	/**	Creates and returns the UI delegate for the specified component */
	public static ComponentUI createUI(JComponent x)
	{
		return new MenuBarUI();
	}


	/**	Installs the UI delegate for the specified component */
	public void installUI(JComponent c)
	{
		menuBar= (JMenuBar) c;

		installDefaults();
		installListeners();
		installKeyboardActions();
	}


	/**	Installs the default settings for the associated menu bar */
	protected void installDefaults()
	{
		if (menuBar.getLayout() == null
			|| menuBar.getLayout() instanceof UIResource)
		{
			if (TonicUtils.isLeftToRight(menuBar))
			{
				menuBar.setLayout(new DefaultMenuLayout(menuBar, BoxLayout.X_AXIS));
			}
			else
			{
				menuBar.setLayout(new RightToLeftMenuLayout());
			}
		}
		
		menuBar.setOpaque(true);
		LookAndFeel.installBorder(menuBar, "MenuBar.border");
		LookAndFeel.installColorsAndFont(menuBar, "MenuBar.background", "MenuBar.foreground", "MenuBar.font");
	}


	/**	Installs listeners for the associated menu bar */
	protected void installListeners()
	{
		containerListener= createContainerListener();
		changeListener= createChangeListener();
		propertyChangeListener= createPropertyChangeListener();

		for (int i= 0; i < menuBar.getMenuCount(); i++)
		{
			JMenu menu= menuBar.getMenu(i);
			if (menu != null)
				menu.getModel().addChangeListener(changeListener);
		}
		menuBar.addContainerListener(containerListener);
		menuBar.addPropertyChangeListener(propertyChangeListener);
	}


	/**	Installs the keyboard actions for the associated menu bar */
	protected void installKeyboardActions()
	{
		InputMap inputMap= getMyInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		SwingUtilities.replaceUIInputMap(
			menuBar,
			JComponent.WHEN_IN_FOCUSED_WINDOW,
			inputMap);
		ActionMap actionMap= getMyActionMap();

		SwingUtilities.replaceUIActionMap(menuBar, actionMap);
	}


	/**	Returns the input map for the specified condition, for the 
	 * 	associated menu bar. 
	 * 
	 * 	@param	condition		This must be one of the constants
	 * 									defined in JComponent
	 */
	public InputMap getMyInputMap(int condition)
	{
		if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW)
		{
			Object[] bindings= (Object[]) UIManager.get("MenuBar.windowBindings");
			if (bindings != null)
			{
				return LookAndFeel.makeComponentInputMap(menuBar, bindings);
			}
		}
		return null;
	}


	/**	Returns the action map for the menu bar */
	ActionMap getMyActionMap()
	{
		ActionMap map= (ActionMap) UIManager.get("MenuBar.actionMap");

		if (map == null)
		{
			map= createMyActionMap();
			if (map != null)
			{
				UIManager.getLookAndFeelDefaults().put("MenuBar.actionMap", map);
			}
		}
		return map;
	}


	/**	Creates an action map for the associated menu bar */
	ActionMap createMyActionMap()
	{
		ActionMap map= new ActionMapUIResource();
		map.put("takeFocus", new TakeFocus());
		return map;
	}


	/**	Uninstalls the UI delegate for the specified component */
	public void uninstallUI(JComponent c)
	{
		uninstallDefaults();
		uninstallListeners();
		uninstallKeyboardActions();

		menuBar= null;
	}


	/**	Uninstalls the defaults for the associated menu bar */
	protected void uninstallDefaults()
	{
		if (menuBar != null)
		{
			LookAndFeel.uninstallBorder(menuBar);
		}
	}


	/**	Uninstalls any registered listeners for the associated menu bar */
	protected void uninstallListeners()
	{
		menuBar.removeContainerListener(containerListener);
		menuBar.removePropertyChangeListener(propertyChangeListener);

		for (int i= 0; i < menuBar.getMenuCount(); i++)
		{
			JMenu menu= menuBar.getMenu(i);
			if (menu != null)
				menu.getModel().removeChangeListener(changeListener);
		}

		containerListener= null;
		changeListener= null;
		propertyChangeListener= null;
	}


	/**	Uninstalls any keyboard actions for the associated menu bar */
	protected void uninstallKeyboardActions()
	{
		SwingUtilities.replaceUIInputMap(
			menuBar,
			JComponent.WHEN_IN_FOCUSED_WINDOW,
			null);
		SwingUtilities.replaceUIActionMap(menuBar, null);
	}


	/**	Creates and returns a container listener for the associated menu bar */
	protected ContainerListener createContainerListener()
	{
		return new ContainerHandler();
	}


	/**	Creates and returns a change listener for the associated menu bar */
	protected ChangeListener createChangeListener()
	{
		return new ChangeHandler();
	}


	/**	Creates and returns a property listener for the associated menu bar */
	private PropertyChangeListener createPropertyChangeListener()
	{
		return new PropertyChangeHandler();
	}


	/**	The change listener for associated menu bars */
	private class ChangeHandler implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			int i, c;
			for (i= 0, c= menuBar.getMenuCount(); i < c; i++)
			{
				JMenu menu= menuBar.getMenu(i);
				if (menu != null && menu.isSelected())
				{
					menuBar.getSelectionModel().setSelectedIndex(i);
					break;
				}
			}
		}
	}


	/***	This PropertyChangeListener is used to adjust the default layout
	 * 	manger when the menuBar is given a right-to-left ComponentOrientation.
	 * 	This is a hack to work around the fact that the DefaultMenuLayout
	 * 	(BoxLayout) isn't aware of ComponentOrientation.  When BoxLayout is
	 * 	made aware of ComponentOrientation, this listener will no longer be
	 * 	necessary.
	 */
	private class PropertyChangeHandler implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String name= e.getPropertyName();
			if (name.equals("componentOrientation")
				&& (menuBar.getLayout() instanceof UIResource))
			{
				if (TonicUtils.isLeftToRight(menuBar))
				{
					menuBar.setLayout(
						new DefaultMenuLayout(menuBar, BoxLayout.X_AXIS));
				}
				else
				{
					menuBar.setLayout(new RightToLeftMenuLayout());
				}
			}
		}
	}


	/**	Returns the preferred size for the specified component */
	public Dimension getPreferredSize(JComponent c)
	{
		return null;
	}


	/**	Returns the minimum size for the specified component */
	public Dimension getMinimumSize(JComponent c)
	{
		return null;
	}


	/**	Returns the maximum size for the specified component */
	public Dimension getMaximumSize(JComponent c)
	{
		return null;
	}


	/**	A container listener for associated menu bars */
	private class ContainerHandler implements ContainerListener
	{
		public void componentAdded(ContainerEvent e)
		{
			Component c= e.getChild();
			if (c instanceof JMenu)
				 ((JMenu) c).getModel().addChangeListener(changeListener);
		}
		public void componentRemoved(ContainerEvent e)
		{
			Component c= e.getChild();
			if (c instanceof JMenu)
				 ((JMenu) c).getModel().removeChangeListener(changeListener);
		}
	}


	private static class TakeFocus extends AbstractAction
	{
		TakeFocus()
		{
		}

		public void actionPerformed(ActionEvent e)
		{
			JMenuBar menuBar= (JMenuBar) e.getSource();
			MenuSelectionManager defaultManager=
				MenuSelectionManager.defaultManager();
			MenuElement me[];
			MenuElement subElements[];
			JMenu menu= menuBar.getMenu(0);
			if (menu != null)
			{
				me= new MenuElement[3];
				me[0]= (MenuElement) menuBar;
				me[1]= (MenuElement) menu;
				me[2]= (MenuElement) menu.getPopupMenu();
				defaultManager.setSelectedPath(me);
			}
		}
	}


	private static class RightToLeftMenuLayout
		extends FlowLayout
		implements UIResource
	{
		private RightToLeftMenuLayout()
		{
			super(3 /*FlowLayout.LEADING*/
			, 0, 0);
		}
	}
}
