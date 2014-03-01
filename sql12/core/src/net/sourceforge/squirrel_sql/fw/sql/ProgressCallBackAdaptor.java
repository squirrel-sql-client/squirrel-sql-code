/**
 * 
 */
package net.sourceforge.squirrel_sql.fw.sql;


/*
 * Copyright (C) 2010 Rob Manning
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

/**
 * This adaptor allows callers of methods that accept a ProgressCallBack to get feedback on progress from 
 * callee's and do whatever is necessary to show that progress to the user.  This allows for custom behavior
 * of some of the ProgressCallBack interface without requiring implementation all of the call back methods.
 */
public class ProgressCallBackAdaptor implements ProgressCallBack
{

	/**
	 * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#currentlyLoading(java.lang.String)
	 */
	@Override
	public void currentlyLoading(String simpleName)
	{
		/* override in sub-class */
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#dispose()
	 */
	@Override
	public void dispose()
	{
		/* override in sub-class */
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#finishedLoading()
	 */
	@Override
	public boolean finishedLoading()
	{
		/* override in sub-class */
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#setLoadingPrefix(java.lang.String)
	 */
	@Override
	public void setLoadingPrefix(String loadingPrefix)
	{
		/* override in sub-class */
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#setTotalItems(int)
	 */
	@Override
	public void setTotalItems(int totalItems)
	{
		/* override in sub-class */
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b)
	{
		/* override in sub-class */
	}

}
