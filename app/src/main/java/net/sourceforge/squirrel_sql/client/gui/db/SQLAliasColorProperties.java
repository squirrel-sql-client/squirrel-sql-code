package net.sourceforge.squirrel_sql.client.gui.db;

/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
 * 
 * Based on initial work from Colin Bell
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

import java.io.Serializable;

public class SQLAliasColorProperties implements Serializable
{
   private static final long serialVersionUID = 1L;

   private boolean overrideToolbarBackgroundColor = false;
   
   private int toolbarBackgroundColorRgbValue = 0;
   
   private boolean overrideObjectTreeBackgroundColor = false;
   
   private int objectTreeBackgroundColorRgbValue = 0;
   
   private boolean overrideStatusBarBackgroundColor = false;

   private int statusBarBackgroundColorRgbValue = 0;
   
   /**
	 * @return the overrideToolbarBackgroundColor
	 */
	public boolean isOverrideToolbarBackgroundColor()
	{
		return overrideToolbarBackgroundColor;
	}

	/**
	 * @param overrideToolbarBackgroundColor the overrideToolbarBackgroundColor to set
	 */
	public void setOverrideToolbarBackgroundColor(boolean overrideToolbarBackgroundColor)
	{
		this.overrideToolbarBackgroundColor = overrideToolbarBackgroundColor;
	}

	/**
	 * @return the toolbarBackgroundColor
	 */
	public int getToolbarBackgroundColorRgbValue()
	{
		return toolbarBackgroundColorRgbValue;
	}

	/**
	 * @param toolbarBackgroundColorRgbValue the toolbarBackgroundColor to set
	 */
	public void setToolbarBackgroundColorRgbValue(int toolbarBackgroundColorRgbValue)
	{
		this.toolbarBackgroundColorRgbValue = toolbarBackgroundColorRgbValue;
	}

	/**
	 * @return the overrideObjectTreeBackgroundColor
	 */
	public boolean isOverrideObjectTreeBackgroundColor()
	{
		return overrideObjectTreeBackgroundColor;
	}

	/**
	 * @param overrideObjectTreeBackgroundColor the overrideObjectTreeBackgroundColor to set
	 */
	public void setOverrideObjectTreeBackgroundColor(boolean overrideObjectTreeBackgroundColor)
	{
		this.overrideObjectTreeBackgroundColor = overrideObjectTreeBackgroundColor;
	}

	/**
	 * @return the objectTreeBackgroundColorRgbValue
	 */
	public int getObjectTreeBackgroundColorRgbValue()
	{
		return objectTreeBackgroundColorRgbValue;
	}

	/**
	 * @param objectTreeBackgroundColorRgbValue the objectTreeBackgroundColor to set
	 */
	public void setObjectTreeBackgroundColorRgbValue(int objectTreeBackgroundColorRgbValue)
	{
		this.objectTreeBackgroundColorRgbValue = objectTreeBackgroundColorRgbValue;
	}

	/**
	 * @return the overrideStatusBarBackgroundColor
	 */
	public boolean isOverrideStatusBarBackgroundColor()
	{
		return overrideStatusBarBackgroundColor;
	}

	/**
	 * @param overrideStatusBarBackgroundColor the overrideStatusBarBackgroundColor to set
	 */
	public void setOverrideStatusBarBackgroundColor(boolean overrideStatusBarBackgroundColor)
	{
		this.overrideStatusBarBackgroundColor = overrideStatusBarBackgroundColor;
	}

	/**
	 * @return the statusBarBackgroundColorRgbValue
	 */
	public int getStatusBarBackgroundColorRgbValue()
	{
		return statusBarBackgroundColorRgbValue;
	}

	/**
	 * @param statusBarBackgroundColorRgbValue the statusBarBackgroundColor to set
	 */
	public void setStatusBarBackgroundColorRgbValue(int statusBarBackgroundColorRgbValue)
	{
		this.statusBarBackgroundColorRgbValue = statusBarBackgroundColorRgbValue;
	}

	
   
   
}
