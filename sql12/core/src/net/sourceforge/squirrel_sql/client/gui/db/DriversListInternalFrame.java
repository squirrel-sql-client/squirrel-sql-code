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
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.InstallDefaultDriversAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowDriverWebsiteAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowLoadedDriversOnlyAction;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
/**
 * This windows displays a list of JDBC drivers and allows the user
 * to maintain their details, add new ones etc.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriversListInternalFrame extends BaseListInternalFrame
{
	private static final long serialVersionUID = 1L;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(BaseListInternalFrame.class);

	/** Application API. */
	private IApplication _app;

	/** User Interface facory. */
	private UserInterfaceFactory _uiFactory;

	/**
	 * Default ctor.
	 */
	public DriversListInternalFrame(IApplication app, DriversList list)
	{
		super(new UserInterfaceFactory(app, list), app);
		_app = app;
		_uiFactory = (UserInterfaceFactory)getUserInterfaceFactory();
		_uiFactory.setDriversListInternalFrame(this);


      addVetoableChangeListener(new VetoableChangeListener()
      {
         public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException
         {
            if(JInternalFrame.IS_CLOSED_PROPERTY.equals(evt.getPropertyName()) && Boolean.TRUE.equals(evt.getNewValue()))
            {
               // i18n[DriversListInternalFrame.error.ctrlF4key=Probably closed by the ctrl F4 key. See BasicDesktopPaneUi.CloseAction]
               throw new PropertyVetoException(s_stringMgr.getString("DriversListInternalFrame.error.ctrlF4key"), evt);
            }
         }
      });

      addWidgetListener(new WidgetAdapter()
      {
         @Override
         public void widgetOpened(WidgetEvent evt)
         {
            nowVisible(true);
         }
         
         @Override
         public void widgetClosing(WidgetEvent evt)
         {
            nowVisible(false);
         }

         @Override
         public void widgetClosed(WidgetEvent evt)
         {
            nowVisible(false);
         }
      });



      _app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				final String propName = evt != null ? evt.getPropertyName() : null;
				_uiFactory.propertiesChanged(propName);
			}
		});
	}


   public void nowVisible(final boolean b)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _app.getMainFrame().setEnabledDriversMenu(b);
            _uiFactory._driversList.requestFocus();
         }
      });
   }


   private final static class UserInterfaceFactory
		implements BaseListInternalFrame.IUserInterfaceFactory
	{
		private IApplication _app;
		private DriversList _driversList;
		private ToolBar _tb;
		private BasePopupMenu _pm = new BasePopupMenu();
		private DriversListInternalFrame _tw;

		UserInterfaceFactory(IApplication app, DriversList list)
		{
			super();
			if (app == null)
			{
				throw new IllegalArgumentException("IApplication == null");
			}
			if (list == null)
			{
				throw new IllegalArgumentException("DriversList == null");
			}

			_app = app;
//			_driversList = new DriversList(app);
			_driversList = list;

			final ActionCollection actions = app.getActionCollection();
			_pm.add(actions.get(CreateDriverAction.class));
			_pm.addSeparator();
			_pm.add(actions.get(ModifyDriverAction.class));
			_pm.add(actions.get(CopyDriverAction.class));
            _pm.add(actions.get(ShowDriverWebsiteAction.class));
            _pm.addSeparator();
			_pm.add(actions.get(DeleteDriverAction.class));
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

		public IBaseList getList()
		{
			return _driversList;
		}

		public String getWindowTitle()
		{
			return s_stringMgr.getString("DriversListInternalFrame.windowtitle");
		}

		public ICommand getDoubleClickCommand(MouseEvent evt)
		{
			ICommand cmd = null;
			ISQLDriver driver = _driversList.getSelectedDriver();
			if (driver != null)
			{
				cmd = new ModifyDriverCommand(_app, driver);
			}
			return cmd;
		}


		void setDriversListInternalFrame(DriversListInternalFrame tw)
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

		private void createToolBar()
		{
			_tb = new ToolBar();
			_tb.setUseRolloverButtons(true);
			_tb.setFloatable(false);

         if (_app.getDesktopStyle().isInternalFrameStyle())
         {
            final JLabel lbl = new JLabel(getWindowTitle(), SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            _tb.add(lbl, 0);
         }

         final ActionCollection actions = _app.getActionCollection();
			_tb.add(actions.get(CreateDriverAction.class));
			_tb.add(actions.get(ModifyDriverAction.class));
			_tb.add(actions.get(CopyDriverAction.class));
         _tb.add(actions.get(ShowDriverWebsiteAction.class));
			_tb.add(actions.get(DeleteDriverAction.class));
			_tb.addSeparator();
			_tb.add(actions.get(InstallDefaultDriversAction.class));
			_tb.addSeparator();
//			_tb.add(actions.get(ShowLoadedDriversOnlyAction.class));

			final Action act = actions.get(ShowLoadedDriversOnlyAction.class);
			final JToggleButton btn = new JToggleButton(act);
			final boolean show = _app.getSquirrelPreferences().getShowLoadedDriversOnly();
			btn.setSelected(show);
			btn.setText(null);
			_tb.add(btn);
		}

		public SquirrelPreferences getPreferences()
		{
			return _app.getSquirrelPreferences();
		}
	}
}
