package net.sourceforge.squirrel_sql.fw.sql;

/*
 * Copyright (C) 2006 Gerd Wagner
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
 * Interface for ProgressCallBack implementations.
 */
public interface ProgressCallBack
{
   /**
    * @param simpleName
    */
   void currentlyLoading(String simpleName);

	/**
	 * Sets the text that is displayed before each thing being loaded. For example, it could be a string like
	 * "Loading:".  See the implementation for details.
	 * 
	 * @param loadingPrefix the string to use as the loadingPrefix. 
	 */
	void setLoadingPrefix(String loadingPrefix);

	/**
	 * Whether or not to make this callback visible.
	 * 
	 * @param b
	 */
	void setVisible(boolean b);

	/**
	 * Sets the total number of items for the progress bar
	 * 
	 * @param totalItems
	 */
	void setTotalItems(int totalItems);

	/**
	 * Whether or not the progress bar has reached the total count of items
	 *  
	 * @return true if finished loading; false otherwise
	 */
	boolean finishedLoading();

	/**
	 * Dispose of the resources used by this progress callback.
	 */
	void dispose();
}
