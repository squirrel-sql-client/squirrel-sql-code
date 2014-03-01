package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.util.EventListener;

/**
 * Listener for events in the <TT>IDataSetModel</TT> interface.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IDataSetModelListener extends EventListener {
	/**
	 * All rows have been added to the model.
	 *
	 * @param	evt		Describes this event.
	 */
	void allRowsAdded(DataSetModelEvent evt);

	/**
	 * Indicates that the output display should scroll to the top.
	 *
	 * @param	evt		Describes this event.
	 */
	void moveToTop(DataSetModelEvent evt);
}
