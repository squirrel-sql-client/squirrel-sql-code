/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.sql;

/**
 * AbortController is a widget for allowing the user to express their desire to interrupt the current 
 * (possibly) long-running process.  This interface describes the operations that an AbortController must 
 * support.
 */
public interface IAbortController
{

	
	/**
	 * @return true if the user has indicated their desire to stop the long-running process; false otherwise. 
	 */
	public abstract boolean isStop();

	/**
	 * @return true if the widget has been displayed and is visible to the user.
	 */
	public abstract boolean isVisble();

	
	/**
	 * Sets whether or not the widget should become visible or should hide.
	 * 
	 * @param b true displays the widget; false causes the widget to hide.
	 */
	public abstract void setVisible(final boolean b);

}