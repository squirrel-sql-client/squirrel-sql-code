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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.SortAliasesAction;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This window shows all the database aliases defined in the system.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasesListInternalFrame extends BaseListInternalFrame
{
	private static final long serialVersionUID = 1L;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AliasesListInternalFrame.class);

	/** Application API. */
	private IApplication _app;

	/** User Interface facory. */
	private UserInterfaceFactory _uiFactory;

	/**
	 * ctor.
	 */
	public AliasesListInternalFrame(IApplication app, AliasesList list)
	{
		super(new UserInterfaceFactory(app, list));
		_app = app;
		_uiFactory = (UserInterfaceFactory)getUserInterfaceFactory();

		// Enable/disable actions depending on whether an item is selected in
		// the list.
		_uiFactory.enableDisableActions();

      addVetoableChangeListener(new VetoableChangeListener()
      {
         public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException
         {
            if(IS_CLOSED_PROPERTY.equals(evt.getPropertyName()) && Boolean.TRUE.equals(evt.getNewValue()))
            {
               nowVisible(true);
                // i18n[AliasesListInternalFrame.error.ctrlF4key=Probably closed by the ctrl F4 key. See BasicDesktopPaneUi.CloseAction]
               throw new PropertyVetoException(s_stringMgr.getString("AliasesListInternalFrame.error.ctrlF4key"), evt);
            }
         }
      });

      addInternalFrameListener(new InternalFrameAdapter()
      {
         public void internalFrameClosing(InternalFrameEvent e)
         {
            nowVisible(false);
         }
      });


      _app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				final String propName = evt != null ? evt.getPropertyName() : null;
				if (propName == null
					|| propName.equals(SquirrelPreferences.IPropertyNames.SHOW_ALIASES_TOOL_BAR))
				{
					boolean show = _app.getSquirrelPreferences().getShowAliasesToolBar();
					if (show)
					{
						_uiFactory.createToolBar();
					}
					else
					{
						_uiFactory._tb = null;
					}
					setToolBar(_uiFactory.getToolBar());
				}
			}
		});


      addFocusListener(new FocusAdapter()
      {
         public void focusGained(FocusEvent e)
         {
            _uiFactory._aliasesList.requestFocus();
         }

      });
	}


   /**
	 * Retrieve the index of the currently selected alias.
	 *
	 * @return	index of currently selected alias.
	 */
	public int getSelectedIndex()
	{
		return _uiFactory._aliasesList.getSelectedIndex();
	}

	public IAliasesList getAliasesList()
	{
		return _uiFactory._aliasesList;
	}

   public void nowVisible(boolean b)
   {
      _app.getMainFrame().setEnabledAliasesMenu(b);
   }

   private static final class UserInterfaceFactory
		implements BaseListInternalFrame.IUserInterfaceFactory
	{
		private IApplication _app;
		private final AliasesList _aliasesList;
		private ToolBar _tb;
		private BasePopupMenu _pm = new BasePopupMenu();

		UserInterfaceFactory(IApplication app, AliasesList list)
				throws IllegalArgumentException
		{
			super();
			if (app == null)
			{
				throw new IllegalArgumentException("IApplication == null");
			}
			if (list == null)
			{
				throw new IllegalArgumentException("AliasesList == null");
			}

			_app = app;
			_aliasesList = list;

			if (_app.getSquirrelPreferences().getShowAliasesToolBar())
			{
				createToolBar();
			}

			final ActionCollection actions = _app.getActionCollection();
			_pm.add(actions.get(ConnectToAliasAction.class));
			_pm.addSeparator();
			_pm.add(actions.get(CreateAliasAction.class));
			_pm.addSeparator();
			_pm.add(actions.get(ModifyAliasAction.class));
			_pm.add(actions.get(CopyAliasAction.class));
			_pm.add(actions.get(SortAliasesAction.class));
			_pm.add(actions.get(AliasPropertiesAction.class));
			_pm.addSeparator();
			_pm.add(actions.get(DeleteAliasAction.class));
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
			return _aliasesList;
		}

		public String getWindowTitle()
		{
			return s_stringMgr.getString("AliasesListInternalFrame.windowtitle");
		}

		public ICommand getDoubleClickCommand()
		{
			ICommand cmd = null;
			SQLAlias alias = _aliasesList.getSelectedAlias();
			if (alias != null)
			{
				cmd = new ConnectToAliasCommand(_app, alias);
			}
			return cmd;
		}

		/**
		 * Enable/disable actions depending on whether an item is selected in list.
		 */
		public void enableDisableActions()
		{
			boolean enable = false;
			try
			{
				enable = _aliasesList.getSelectedAlias() != null;
			}
			catch (Exception ignore)
			{
				// Getting an error in the JDK.
				// Exception occurred during event dispatching:
				// java.lang.ArrayIndexOutOfBoundsException: 0 >= 0
				// at java.util.Vector.elementAt(Vector.java:417)
				// at javax.swing.DefaultListModel.getElementAt(DefaultListModel.java:70)
				// at javax.swing.JList.getSelectedValue(JList.java:1397)
				// at net.sourceforge.squirrel_sql.mainframe.AliasesList.getSelectedAlias(AliasesList.java:77)
			}

			final ActionCollection actions = _app.getActionCollection();
			actions.get(ConnectToAliasAction.class).setEnabled(enable);
			actions.get(CopyAliasAction.class).setEnabled(enable);
			actions.get(DeleteAliasAction.class).setEnabled(enable);
			actions.get(ModifyAliasAction.class).setEnabled(enable);
		}

		private void createToolBar()
		{
			_tb = new ToolBar();
			_tb.setUseRolloverButtons(true);
			_tb.setFloatable(false);

			final JLabel lbl = new JLabel(getWindowTitle(), SwingConstants.CENTER);
			lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			_tb.add(lbl, 0);

			final ActionCollection actions = _app.getActionCollection();
			_tb.add(actions.get(ConnectToAliasAction.class));
			_tb.addSeparator();
			_tb.add(actions.get(CreateAliasAction.class));
			_tb.add(actions.get(ModifyAliasAction.class));
			_tb.add(actions.get(CopyAliasAction.class));
			_tb.add(actions.get(DeleteAliasAction.class));
         _tb.add(actions.get(AliasPropertiesAction.class));
			_tb.addSeparator();
			_tb.add(actions.get(SortAliasesAction.class));
		}

		public SquirrelPreferences getPreferences()
		{
			return _app.getSquirrelPreferences();
		}
	}
   
   
}
