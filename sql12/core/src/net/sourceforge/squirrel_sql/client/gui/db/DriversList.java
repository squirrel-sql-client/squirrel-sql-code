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
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
/**
 * This is a <CODE>JList</CODE> that dispays all the <CODE>ISQLDriver</CODE>
 * objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriversList extends BaseList implements IDriversList
{
   private static final String PREF_KEY_SELECTED_DRIVER_INDEX = "Squirrel.selDriverIndex";


   /** Application API. */
	private IApplication _app;

	/** Model for this component. */
	private DriversListModel _model;

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DriversList.class);    


   /**
	 * Ctor specifying Application API object.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public DriversList(IApplication app) throws IllegalArgumentException
	{
      super(new DriversListModel(app), app);
		_app = app;
		_model = (DriversListModel) getList().getModel();

      getList().setLayout(new BorderLayout());

		SquirrelResources res = _app.getResources();
		getList().setCellRenderer(new DriverListCellRenderer(res.getIcon("list.driver.found"),res.getIcon("list.driver.notfound")));

		propertiesChanged(null);


		_app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				final String propName = evt != null ? evt.getPropertyName() : null;
				propertiesChanged(propName);
			}
		});
	}

	/**
	 * Component has been added to its parent.
	 */
	public void addNotify()
	{
		getList().addNotify();
		// Register so that we can display different tooltips depending
		// which entry in list mouse is over.
		ToolTipManager.sharedInstance().registerComponent(getList());
	}

	/**
	 * Component has been removed from its parent.
	 */
	public void removeNotify()
	{
		getList().removeNotify();
		// Don't need tooltips any more.
		ToolTipManager.sharedInstance().unregisterComponent(getList());
	}

	/**
	 * Return the <CODE>DriversListModel</CODE> that controls this list.
	 */
	public DriversListModel getTypedModel()
	{
		return _model;
	}

	/**
	 * Return the <CODE>ISQLDriver</CODE> that is currently selected.
	 */
	public ISQLDriver getSelectedDriver()
	{
		return (ISQLDriver)getList().getSelectedValue();
	}

	/**
	 * Return the description for the driver that the mouse is currently
	 * over as the tooltip text.
	 *
	 * @param	event	Used to determine the current mouse position.
	 */
	public String getToolTipText(MouseEvent evt)
	{
		String tip = null;
		final int idx = getList().locationToIndex(evt.getPoint());
		if (idx != -1)
		{
			tip = ((ISQLDriver)getList().getModel().getElementAt(idx)).getName();
		}
		else
		{
			tip = getToolTipText();
		}
		return tip;
	}

	/**
	 * Return the tooltip used for this component if the mouse isn't over
	 * an entry in the list.
	 */
	public String getToolTipText()
	{
        // i18n[DriversList.tooltiptext=List of database drivers that can be used to configure an alias]
		return s_stringMgr.getString("DriversList.tooltiptext");
	}

	/**
	 * Application properties have changed so update this object.
	 *
	 * @param	propName	Name of property that has changed or <TT>null</TT>
	 * 						if multiple properties have changed.
	 */
	private void propertiesChanged(String propName)
	{
		if (propName == null
			|| propName.equals(SquirrelPreferences.IPropertyNames.SHOW_LOADED_DRIVERS_ONLY))
		{
			boolean show = _app.getSquirrelPreferences().getShowLoadedDriversOnly();
			_model.setShowLoadedDriversOnly(show);
		}
	}

   public String getSelIndexPrefKey()
   {
      return PREF_KEY_SELECTED_DRIVER_INDEX;
   }

   public void requestFocus()
   {
      getList().requestFocus();
   }
}

