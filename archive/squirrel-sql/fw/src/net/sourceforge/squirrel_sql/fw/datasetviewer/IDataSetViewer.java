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
import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public interface IDataSetViewer
{
	int MAX_COLUMN_WIDTH = 50;

	/**
	 * Get the rowcount of the DataSet
	 */
	int getRowCount();

	/**
	 * Clear the output.
	 */
	void clear();

	/**
	 * Specify the column headings to use.
	 *
	 * @param	hdgs	Column headings to use.
	 */
	void setColumnDefinitions(ColumnDisplayDefinition[] hdgs);

	/**
	 * Return the column headings to use.
	 *
	 * @return the column headings to use.
	 */
	ColumnDisplayDefinition[] getColumnDefinitions();

	/**
	 * Specify whether to show the column headings.
	 *
	 * @param	show	<TT>true</TT> if headibgs to be shown else <TT>false</TT>.
	 */
	void showHeadings(boolean show);

	/**
	 * Return whether to show the column headings.
	 *
	 * @return whether to show the column headings.
	 */
	boolean getShowHeadings();

	void show(IDataSet ds) throws DataSetException;

	void show(IDataSet ds, IMessageHandler msgHandler) throws DataSetException;

	/**
	 * Indicates that the output display should scroll to the top.
	 */
	void moveToTop();

	/**
	 * Get the component for this viewer.
	 *
	 * @return	The component for this viewer.
	 */
	Component getComponent();

	/**
	 * Get the column renderers for the specified column.
	 * 
	 * @param	columnIdx	Column we want a renderer for.
	 *
	 * @return	the column renderer.
	 */
	IColumnRenderer getColumnRenderer(int columnIdx);

	/**
	 * Set the column renderers for this viewer.
	 * 
	 * @param	renderer	the new column renderers. If <TT>null</TT> then the
	 *						default renderer should be used.
	 */
	void setColumnRenderers(IColumnRenderer[] renderers);
}