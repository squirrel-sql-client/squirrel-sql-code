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

import net.sourceforge.squirrel_sql.client.ApplicationListener;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.mainframe.action.*;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.prefs.Preferences;

/**
 * This window shows all the database aliases defined in the system.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasesListInternalFrame extends BaseListInternalFrame
{

   private static final String PREF_KEY_VIEW_ALIASES_AS_TREE = "Squirrel.viewAliasesAsTree";

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
	public AliasesListInternalFrame(IApplication app, IAliasesList list)
	{
		super(new UserInterfaceFactory(app, list), app);
		_app = app;
		_uiFactory = (UserInterfaceFactory)getUserInterfaceFactory();


      addVetoableChangeListener(new VetoableChangeListener()
      {
         public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException
         {
            if(JInternalFrame.IS_CLOSED_PROPERTY.equals(evt.getPropertyName()) && Boolean.TRUE.equals(evt.getNewValue()))
            {
               nowVisible(true);
                // i18n[AliasesListInternalFrame.error.ctrlF4key=Probably closed by the ctrl F4 key. See BasicDesktopPaneUi.CloseAction]
               throw new PropertyVetoException(s_stringMgr.getString("AliasesListInternalFrame.error.ctrlF4key"), evt);
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

   public IAliasesList getAliasesList()
	{
		return _uiFactory._aliasesList;
	}

   public void nowVisible(final boolean b)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _app.getMainFrame().setEnabledAliasesMenu(b);
            _uiFactory._aliasesList.requestFocus();
         }
      });
   }

   public void enableDisableActions()
   {
      _uiFactory.enableDisableActions();
   }

   public boolean isEmpty()
   {
      return _uiFactory._aliasesList.isEmpty();   
   }

   private static final class UserInterfaceFactory
		implements BaseListInternalFrame.IUserInterfaceFactory
	{
		private IApplication _app;
		private final IAliasesList _aliasesList;
		private ToolBar _tb;
		private BasePopupMenu _pm = new BasePopupMenu();

		UserInterfaceFactory(IApplication app, IAliasesList list)
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
			_pm.add(actions.get(ModifyAliasAction.class));
			_pm.add(actions.get(CopyAliasAction.class));
			_pm.add(actions.get(DeleteAliasAction.class));
			_pm.addSeparator();
         _pm.add(actions.get(AliasPropertiesAction.class));
         _pm.addSeparator();
         _pm.add(actions.get(SortAliasesAction.class));
         _pm.addSeparator();
         addToMenuAsCheckBoxMenuItem(_app.getResources(), actions.get(ToggleTreeViewAction.class), _pm);
         _pm.add(actions.get(NewAliasFolderAction.class));
         _pm.add(actions.get(CopyToPasteAliasFolderAction.class));
         _pm.add(actions.get(CutAliasFolderAction.class));
         _pm.add(actions.get(PasteAliasFolderAction.class));
         _pm.add(actions.get(CollapseAllAliasFolderAction.class));
         _pm.add(actions.get(ExpandAllAliasFolderAction.class));

         app.addApplicationListener(new ApplicationListener()
         {
            public void saveApplicationState()
            {
               onSaveApplicationState();
            }
         });

         SwingUtilities.invokeLater(
            new Runnable()
            {
               public void run()
               {
                  ToggleTreeViewAction actViewAsTree = (ToggleTreeViewAction) actions.get(ToggleTreeViewAction.class);
                  actViewAsTree.getToggleComponentHolder().setSelected(Preferences.userRoot().getBoolean(PREF_KEY_VIEW_ALIASES_AS_TREE, false));
                  actViewAsTree.actionPerformed(new ActionEvent(this, 1, "actionPerformed"));
                  enableDisableActions();
               }
            });
      }

      private void onSaveApplicationState()
      {
         IToggleAction actViewAsTree = (IToggleAction) _app.getActionCollection().get(ToggleTreeViewAction.class);
         Preferences.userRoot().putBoolean(PREF_KEY_VIEW_ALIASES_AS_TREE, actViewAsTree.getToggleComponentHolder().isSelected());
      }
      

      private JCheckBoxMenuItem addToMenuAsCheckBoxMenuItem(Resources rsrc, Action action, JPopupMenu menu)
      {
         JCheckBoxMenuItem mnu = rsrc.addToMenuAsCheckBoxMenuItem(action, menu);
         if(action instanceof IToggleAction)
         {
            ((IToggleAction)action).getToggleComponentHolder().addToggleableComponent(mnu);
         }
         return mnu;
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
			return _aliasesList;
		}

		public String getWindowTitle()
		{
			return s_stringMgr.getString("AliasesListInternalFrame.windowtitle");
		}

		public ICommand getDoubleClickCommand(MouseEvent evt)
		{
			ICommand cmd = null;
			SQLAlias alias = _aliasesList.getSelectedAlias(evt);
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
			final ActionCollection actions = _app.getActionCollection();

         ToggleTreeViewAction actViewAsTree = (ToggleTreeViewAction) actions.get(ToggleTreeViewAction.class);


         boolean viewAsTree = actViewAsTree.getToggleComponentHolder().isSelected();

         actions.get(NewAliasFolderAction.class).setEnabled(viewAsTree);
         actions.get(CopyToPasteAliasFolderAction.class).setEnabled(viewAsTree);
         actions.get(CutAliasFolderAction.class).setEnabled(viewAsTree);
         actions.get(PasteAliasFolderAction.class).setEnabled(viewAsTree);
         actions.get(CollapseAllAliasFolderAction.class).setEnabled(viewAsTree);
         actions.get(ExpandAllAliasFolderAction.class).setEnabled(viewAsTree);
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
			_tb.add(actions.get(ConnectToAliasAction.class));
			_tb.addSeparator();
			_tb.add(actions.get(CreateAliasAction.class));
			_tb.add(actions.get(ModifyAliasAction.class));
			_tb.add(actions.get(CopyAliasAction.class));
			_tb.add(actions.get(DeleteAliasAction.class));
         _tb.addSeparator();
         _tb.add(actions.get(AliasPropertiesAction.class));
			_tb.addSeparator();
			_tb.add(actions.get(SortAliasesAction.class));
         _tb.addSeparator();
         _tb.addToggleAction((IToggleAction)actions.get(ToggleTreeViewAction.class));
         _tb.add(actions.get(NewAliasFolderAction.class));
         _tb.add(actions.get(CopyToPasteAliasFolderAction.class));
         _tb.add(actions.get(CutAliasFolderAction.class));
         _tb.add(actions.get(PasteAliasFolderAction.class));
         _tb.add(actions.get(CollapseAllAliasFolderAction.class));
         _tb.add(actions.get(ExpandAllAliasFolderAction.class));
		}

		public SquirrelPreferences getPreferences()
		{
			return _app.getSquirrelPreferences();
		}
	}
   
   
}
