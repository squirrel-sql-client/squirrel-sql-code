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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.squirrel_sql.fw.gui.SortedComboBoxModel;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExitAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.MaximizeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.NewSessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileAction;

/**
 * Toolbar for <CODE>MainFrame</CODE>.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class MainFrameToolBar extends ToolBar
{
	/** Application API. */
	private IApplication _app;

	/**
	 * ctor.
	 * 
	 * @param	app		Application API
	 * @param	frame	Application main frame
	 *
	 * @throws	IllegalArgumentException
	 *			<TT>null</TT> <TT>IApplication</TT> or <TT>MainFrame</TT>
	 *			passed.
	 */
	MainFrameToolBar(IApplication app, MainFrame frame)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("null IApplication passed.");
		}
		if (frame == null)
		{
			throw new IllegalArgumentException("null MainFrame passed.");
		}
		_app = app;
		setUseRolloverButtons(true);
		setFloatable(true);

		ActionCollection actions = _app.getActionCollection();
		JLabel lbl = new JLabel(" Connect to: ");
		lbl.setAlignmentY(0.5f);
		add(lbl);
		AliasesDropDown drop = new AliasesDropDown(app, frame);
		drop.setAlignmentY(0.5f);
		add(drop);
		addSeparator();
		add(actions.get(GlobalPreferencesAction.class));
		add(actions.get(NewSessionPropertiesAction.class));
		addSeparator();
		add(actions.get(TileAction.class));
		add(actions.get(CascadeAction.class));
		add(actions.get(MaximizeAction.class));
//		addSeparator();
//		add(actions.get(ExitAction.class));
	}

	/**
	 * Add an action to the toolbar. Centre it vertically so that
	 * it lines up with the dropdown.
	 *
	 * @param	action	<TT>Action</TT> to be added.
	 */
//	private void addAction(Action action)
//	{
//		JButton btn = add(action);
//		btn.setAlignmentY(0.5f);
//	}

	/**
	 * Dropdown holding all the current <TT>ISQLAlias</TT> objects. When one is
	 * selected the user will be prompted to connect to it.
	 */
	private static class AliasesDropDown
		extends JComboBox
		implements ActionListener
	{
		private IApplication _app;
		private MainFrame _mainFrame;

		AliasesDropDown(IApplication app, MainFrame mainFrame)
		{
			super();
			_app = app;
			_mainFrame = mainFrame;
			final AliasesDropDownModel model = new AliasesDropDownModel(_app);
			setModel(model);

			// Under JDK1.4 the first item in a JComboBox
			// is no longer automatically selected.
			if (getModel().getSize() > 0)
			{
				setSelectedIndex(0);
			}
			else
			{
				// Under JDK1.4 an empty JComboBox has an almost zero width.
				Dimension dm = getPreferredSize();
				dm.width = 100;
				setPreferredSize(dm);
			}
			addActionListener(this);
			setMaximumSize(getPreferredSize());

			_app.getDataCache().addAliasesListener(new MyAliasesListener(model, this));
		}

		/**
		 * An alias has been selected in the list so attempt to connect to it.
		 *
		 * @param   evt	 Describes the event that has just occured.
		 */
		public void actionPerformed(ActionEvent evt)
		{
			try
			{
				Object obj = getSelectedItem();
				if (obj instanceof ISQLAlias)
				{
					new ConnectToAliasCommand(_app, _mainFrame, (ISQLAlias) obj).execute();
				}
			}
			finally
			{
				if (getModel().getSize() > 0)
				{
					setSelectedIndex(0);
				}
			}
		}
	}

	/**
	 * Data model for AliasesDropDown.
	 */
	private static class AliasesDropDownModel extends SortedComboBoxModel
	{
		private IApplication _app;

		/**
		 * Default ctor. Listen to the <TT>DataCache</TT> object for additions
		 * and removals of aliases from the cache.
		 */
		public AliasesDropDownModel(IApplication app)
		{
			super();
			_app = app;
			load();
			//			_app.getDataCache().addAliasesListener(new MyAliasesListener(this));
		}

		/**
		 * Load from <TT>DataCache</TT>.
		 */
		private void load()
		{
			Iterator it = _app.getDataCache().aliases();
			while (it.hasNext())
			{
				addAlias((ISQLAlias) it.next());
			}
		}

		/**
		 * Add an <TT>ISQLAlias</TT> to this model.
		 *
		 * @param   alias   <TT>ISQLAlias</TT> to be added.
		 */
		private void addAlias(ISQLAlias alias)
		{
			addElement(alias);
		}

		/**
		 * Remove an <TT>ISQLAlias</TT> from this model.
		 *
		 * @param   alias   <TT>ISQLAlias</TT> to be removed.
		 */
		private void removeAlias(ISQLAlias alias)
		{
			removeElement(alias);
		}
	}

	/**
	 * Listener to changes in <TT>ObjectCache</TT>. As aliases are
	 * added to/removed from <TT>DataCache</TT> this model is updated.
	 */
	private static class MyAliasesListener implements ObjectCacheChangeListener
	{
		/** Model that is listening. */
		private AliasesDropDownModel _model;

		/** Control for _model. */
		AliasesDropDown _control;

		/**
		 * Ctor specifying the model and control that is listening.
		 */
		MyAliasesListener(AliasesDropDownModel model, AliasesDropDown control)
		{
			super();
			_model = model;
			_control = control;
		}

		/**
		 * An alias has been added to the cache.
		 *
		 * @param	evt	Describes the event in the cache.
		 */
		public void objectAdded(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				_model.addAlias((ISQLAlias) obj);
			}
			if (_control.getItemCount() == 1)
			{
				_control.setSelectedIndex(0);
			}
		}

		/**
		 * An alias has been removed from the cache.
		 *
		 * @param	evt	Describes the event in the cache.
		 */
		public void objectRemoved(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				_model.removeAlias((ISQLAlias) obj);
			}
		}
	}
}
