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
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.gui.SortedListModel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
/**
 * Model for a <CODE>DriverList</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class DriversListModel extends SortedListModel
{
    private static final long serialVersionUID = 1L;

    /** Application API. */
	private IApplication _app;

	/**
	 * Specify whether only JDBC drivers that can be loaded should be loaded
	 * into this model.
	 */
	private boolean _showLoadedDriversOnly;

	/**
	 * Load drivers from the <CODE>DataCache</CODE>.
	 *
	 * @param	app	 Application API.
	 */
	public DriversListModel(IApplication app) throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		load();
		_app.getDataCache().addDriversListener(new MyDriversListener());
	}

	/**
	 * Specify whether only JDBC drivers that can be loaded
	 * are displayed.
	 *
	 * @param	show	If <TT>true</TT> only show JDBC drivers
	 * 					that can be loaded.
	 */
	public void setShowLoadedDriversOnly(boolean show)
	{
		if (show != _showLoadedDriversOnly)
		{
			_showLoadedDriversOnly = show;
			load();
		}
	}

	/**
	 * Load from <CODE>DataCache</CODE>.
	 */
	private void load()
	{
		clear();
		Iterator<ISQLDriver> it = _app.getDataCache().drivers();
		while (it.hasNext())
		{
			addDriver(it.next());
		}
	}

	/**
	 * Add an <CODE>ISQLDriver</CODE> to this model.
	 *
	 * @param	driver	<CODE>ISQLDriver</CODE> to be added.
	 */
	private void addDriver(ISQLDriver driver)
	{
		if (!_showLoadedDriversOnly || driver.isJDBCDriverClassLoaded())
		{
			addElement(driver);
		}
	}

	/**
	 * Remove an <CODE>ISQLDriver</CODE> from this model.
	 *
	 * @param	driver	<CODE>ISQLDriver</CODE> to be removed.
	 */
	private void removeDriver(ISQLDriver driver)
	{
		removeElement(driver);
	}

	/**
	 * Listener to changes in <CODE>ObjectCache</CODE>. As drivers are
	 * added to/removed from <CODE>DataCache</CODE> this model is updated.
	 */
	private class MyDriversListener implements IObjectCacheChangeListener
	{
		/**
		 * A driver has been added to the cache.
		 *
		 * @param	evt	 Describes the event in the cache.
		 */
		public void objectAdded(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLDriver)
			{
				addDriver((ISQLDriver)obj);
			}
		}

		/**
		 * A driver has been removed from the cache.
		 *
		 * @param	evt	 Describes the event in the cache.
		 */
		public void objectRemoved(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLDriver)
			{
				removeDriver((ISQLDriver)obj);
			}
		}
	}
}
