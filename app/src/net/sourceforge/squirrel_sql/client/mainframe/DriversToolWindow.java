package net.sourceforge.squirrel_sql.client.mainframe;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.InstallDefaultDriversAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowLoadedDriversOnlyAction;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
/**
 * This windows displays a list of JDBC drivers and allows the user
 * to maintain their details, add new ones etc.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriversToolWindow extends BaseToolWindow
{
	/** Application API. */
	private IApplication _app;

	/** User Interface facory. */
	private UserInterfaceFactory _uiFactory;

	/**
	 * Default ctor.
	 */
	public DriversToolWindow(IApplication app)
	{
		super(app, new UserInterfaceFactory(app));
		_app = app;
		_uiFactory = (UserInterfaceFactory)getUserInterfaceFactory();
		_uiFactory.setDriversToolWindow(this);

		// Enable/disable actions depending on whether an item is selected in
		// the list.
		_uiFactory.enableDisableActions();

		_app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				final String propName = evt != null ? evt.getPropertyName() : null;
				_uiFactory.propertiesChanged(propName);
			}
		});
	}

	/**
	 * Retrieve the index of the currently selected driver.
	 *
	 * @return	index of currently selected driver.
	 */
	public int getSelectedIndex()
	{
		return _uiFactory._driversList.getSelectedIndex();
	}

	private final static class UserInterfaceFactory
		implements BaseToolWindow.IUserInterfaceFactory
	{
		private IApplication _app;
		private DriversList _driversList;
		private ToolBar _tb;
		private BasePopupMenu _pm = new BasePopupMenu();
		private DriversToolWindow _tw;
		private CopyDriverAction _copyDriverAction;
		private CreateDriverAction _createDriverAction;
		private DeleteDriverAction _deleteDriverAction;
		private ModifyDriverAction _modifyDriverAction;

		UserInterfaceFactory(IApplication app)
		{
			super();
			_app = app;
			_driversList = new DriversList(app);

			preloadActions(app);

			_pm.add(_createDriverAction);
			_pm.addSeparator();
			_pm.add(_modifyDriverAction);
			_pm.add(_copyDriverAction);
			_pm.addSeparator();
			_pm.add(_deleteDriverAction);
			_pm.addSeparator();
		}

		public ToolBar getToolBar()
		{
			return _tb;
		}

		public BasePopupMenu getPopupMenu()
		{
			return _pm;
		}

		public JList getList()
		{
			return _driversList;
		}

		public String getWindowTitle()
		{
			return "Drivers"; // i18n
		}

		public ICommand getDoubleClickCommand()
		{
			ICommand cmd = null;
			ISQLDriver driver = _driversList.getSelectedDriver();
			if (driver != null)
			{
				cmd = new ModifyDriverCommand(driver);
			}
			return cmd;
		}

		/**
		 * Enable/disable actions depending on whether an item is selected
		 * in list.
		 */
		public void enableDisableActions()
		{
			boolean enable = false;
			try
			{
				enable = _driversList.getSelectedDriver() != null;
			}
			catch (Exception ignore)
			{
				// Getting an error in the JDK.
				// Exception occurred during event dispatching:
				// java.lang.ArrayIndexOutOfBoundsException: 0 >= 0
				// at java.util.Vector.elementAt(Vector.java:417)
				// at javax.swing.DefaultListModel.getElementAt(DefaultListModel.java:70)
				// at javax.swing.JList.getSelectedValue(JList.java:1397)
				// at net.sourceforge.squirrel_sql.client.mainframe.DriversList.getSelectedDriver(DriversList.java:77)
			}
			_copyDriverAction.setEnabled(enable);
			_deleteDriverAction.setEnabled(enable);
			_modifyDriverAction.setEnabled(enable);
		}

		void setDriversToolWindow(DriversToolWindow tw)
		{
			_tw = tw;
			propertiesChanged(null);
		}

		public void propertiesChanged(String propName)
		{
			if (propName == null ||
				propName.equals(SquirrelPreferences.IPropertyNames.SHOW_DRIVERS_TOOL_BAR))
			{
				boolean show = _app.getSquirrelPreferences().getShowDriversToolBar();
				if (show)
				{
					createToolBar();
				}
				else
				{
					_tb = null;
				}
				_tw.setToolBar(getToolBar());
			}
		}

		private void preloadActions(IApplication app)
		{
			ActionCollection actions = app.getActionCollection();
			actions.add(_modifyDriverAction = new ModifyDriverAction(_app, _driversList));
			actions.add(_deleteDriverAction = new DeleteDriverAction(_app, _driversList));
			actions.add(_copyDriverAction = new CopyDriverAction(_app, _driversList));
			actions.add(_createDriverAction = new CreateDriverAction(_app));
		}

		private void createToolBar()
		{
			final ActionCollection actions = _app.getActionCollection();

			_tb = new ToolBar();
			_tb.setBorder(BorderFactory.createEtchedBorder());
			_tb.setUseRolloverButtons(true);
			_tb.setFloatable(false);

			final JLabel lbl = new JLabel(getWindowTitle(), SwingConstants.LEFT);
			lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			_tb.add(lbl, 0);
			_tb.add(new ToolBar.Separator(), 1);

			_tb.add(_createDriverAction);
			_tb.add(_modifyDriverAction);
			_tb.add(_copyDriverAction);
			_tb.add(_deleteDriverAction);
			_tb.add(new ToolBar.Separator(), 1);
			_tb.add(actions.get(InstallDefaultDriversAction.class));
			_tb.add(new ToolBar.Separator(), 1);
//			_tb.add(actions.get(ShowLoadedDriversOnlyAction.class));

			Action act = (Action)actions.get(ShowLoadedDriversOnlyAction.class);
			JToggleButton btn = new JToggleButton(act);
			boolean show = _app.getSquirrelPreferences().getShowLoadedDriversOnly();
			btn.setSelected(show);
			btn.setText(null);
			_tb.add(btn);
		}
	}
}
