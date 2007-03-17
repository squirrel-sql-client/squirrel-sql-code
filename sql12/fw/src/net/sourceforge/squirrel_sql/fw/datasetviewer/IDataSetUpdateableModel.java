package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2002 Colin Bell
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

/**
 * @author gwg
 *
 * If the underlying data maintained by the application may be
 * changed by the user through the framework interface, then the fw
 * objects must be able to get back to the application objects to
 * tell them what the change is.  The signature of the method to be
 * used to signal that change will depend on the type of underlying data
 * (e.g. list, table, etc.), but the setup of the fw code from the
 * application is done at a fairly high level, so that data type is not
 * known at the time that the application object creates the fw objects.
 * Therefore, we define this interface as a data type so that the application
 * code (e.g. 
 * net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab)
 * may tell the fw code in 
 * net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel
 * that the calling object is editable and must be saved in the fw
 * objects for later reference.
 * 
 * While this interface is defined within the fw object tree, it is
 * actually for use by application objects.  Thus, you will find the
 * objects implementing this interface in the application tree, not here.
 * The reason for this is that the fw code is intended to be reusable, so
 * we need a reusable data type for making callbacks into the application,
 * and that data type must be defined in fw.
 * 
 * This interface must be extended by another interface that handles
 * the actual call from fw to application to update the data. For example:
 * net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel
 * is the interface used to update table objects.
 */
public interface IDataSetUpdateableModel
{
	/**
	 * The fw gui may contain hooks that let the user force the application
	 * into editing mode, and this function is how the framework tells the application
	 * to go into editing mode.
	 */
	public void forceEditMode(boolean mode);
	
	/**
	 * The fw gui may need to query the application on whether it is in "forced edit" mode
	 * or not.
	 */
	public boolean editModeIsForced();
}
