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
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This provides base behaviour for implemtations of <TT>IDataSetViewerDestination</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseDataSetViewerDestination implements IDataSetViewer
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(BaseDataSetViewerDestination.class);

	/** Specifies whether to show the column headings. */
	private boolean _showHeadings = true;

	/** Column definitions. */
	protected ColumnDisplayDefinition[] _colDefs = new ColumnDisplayDefinition[0];

	/** Column renderers. */
	private IColumnRenderer[] _columnRenderers = new IColumnRenderer[0];

	private IDataSetUpdateableModel _updateableModelReference = null;	
	
	/**
	 * Some types of DataSetViewers require extra initialization to set up
	 * a return reference to the originating data object (e.g. to allow
	 * editing of that object later).  The reference is needed to distinguish
	 * which tables/text may be edited and which may not.  This cannot be included
	 * in the object creation because the Class.newInstance() method used in the 
	 * getInstance() method of this class does not allow for arguments.  Since this
	 * info is needed for the DataSetViewerTablePanel and DataSetViewerTextPanel classes
	 * to be able to set up the
	 * popup menu correctly, those classes cannot complete their initialization without
	 * this added info.  Therefore we need to initialize that class in two stages.
	 */
	public void init(IDataSetUpdateableModel updateableObject)
	{
		// default is to do nothing.  Derived classes must override this
		// to accomplish anything.
	}

	/**
	 * Specify the column definitions to use.
	 *
	 * @param	hdgs	Column definitions to use.
	 */
	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
	{
		_colDefs = colDefs != null ? colDefs : new ColumnDisplayDefinition[0];
	}

	/**
	 * Return the column definitions to use.
	 *
	 * @return the column definitions to use.
	 */
	public ColumnDisplayDefinition[] getColumnDefinitions()
	{
		return _colDefs;
	}

	/**
	 * Specify whether to show the column headings.
	 *
	 * @param	show	<TT>true</TT> if headibgs to be shown else <TT>false</TT>.
	 */
	public void showHeadings(boolean show)
	{
		_showHeadings = show;
	}

	/**
	 * Return whether to show the column headings.
	 *
	 * @return whether to show the column headings.
	 */
	public boolean getShowHeadings()
	{
		return _showHeadings;
	}

	public synchronized void show(IDataSet ds) throws DataSetException
	{
		show(ds, null);
	}

	public synchronized void show(IDataSet ds, IMessageHandler msgHandler)
		throws DataSetException
	{
		clear();
		setColumnDefinitions(ds.getDataSetDefinition().getColumnDefinitions());
		final int colCount = ds.getColumnCount();
		while (ds.next(msgHandler))
		{
			addRow(ds, colCount);
		}
		allRowsAdded();
		moveToTop();
	}

	/**
	 * Get the column renderer for the specified column.
	 * 
	 * @param	columnIdx	Column we want a renderer for.
	 * 
	 * @return	the column renderer.
	 */
	public IColumnRenderer getColumnRenderer(int columnIdx)
	{
		if (columnIdx >= 0 && columnIdx < _columnRenderers.length)
		{
			return _columnRenderers[columnIdx];
		}
		return DefaultColumnRenderer.getInstance();
	}

	/**
	 * Set the column renderers for this viewer.
	 * 
	 * @param	renderers	the new column renderer. If <TT>null</TT> then the
	 *						default renderer should be used.
	 */
	public void setColumnRenderers(IColumnRenderer[] renderers)
	{
		_columnRenderers = renderers != null ? renderers : new IColumnRenderer[0];
	}

	protected void addRow(IDataSet ds, int columnCount) throws DataSetException
	{
		Object[] row = new Object[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			row[i] = ds.get(i);
		}
		addRow(row);
	}
	
	/**
	 * Setter and getter for the reference to the updateable object
	 * representing the actual underlying data.  This will be null if
	 * the underlying data is not updateable.
	 */
	public void setUpdateableModelReference(IDataSetUpdateableModel updateableObject)
	{
		_updateableModelReference = updateableObject;
	}

	public IDataSetUpdateableModel getUpdateableModelReference(){
		return _updateableModelReference;
	}

	protected abstract void allRowsAdded() throws DataSetException;
	protected abstract void addRow(Object[] row) throws DataSetException;

	/**
	 * factory method for getting IDataSetViewer instances
	 * If no instance can be made then the default
	 * will be returned.
	 */
	public static IDataSetViewer getInstance(String sName, 
		IDataSetUpdateableModel updateableModel)
	{
		IDataSetViewer dsv = null;
		try
		{
			Class cls = Class.forName(sName);
			dsv = (IDataSetViewer) cls.newInstance();
			dsv.init(updateableModel);
		}
		catch (Exception e)
		{
			s_log.error("Error", e);
		}
		if (dsv == null)
		{
			dsv = new DataSetViewerTablePanel();
			((DataSetViewerTablePanel)dsv).init(updateableModel);
		}
		return dsv;
	}
}
