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

import net.sourceforge.squirrel_sql.client.gui.db.modifyaliases.SQLAliasPropType;

import java.io.Serializable;

public class SQLAliasColorProperties implements Serializable
{
   private boolean _overrideToolbarBackgroundColor = false;
   
   private int _toolbarBackgroundColorRgbValue = 0;
   
   private boolean _overrideObjectTreeBackgroundColor = false;
   
   private int _objectTreeBackgroundColorRgbValue = 0;
   
   private boolean _overrideStatusBarBackgroundColor = false;

   private int _statusBarBackgroundColorRgbValue = 0;

   private boolean _overrideAliasBackgroundColor = false;

   private int _aliasBackgroundColorRgbValue = 0;

   private SQLAliasVersioner _versioner = new SQLAliasVersioner();

	/**
	 * @return the overrideToolbarBackgroundColor
	 */
	@SQLAliasProp(sqlAliasPropType = SQLAliasPropType.colorProp_overrideToolbarBackgroundColor)
	public boolean isOverrideToolbarBackgroundColor()
	{
		return _overrideToolbarBackgroundColor;
	}

	/**
	 * @param overrideToolbarBackgroundColor the overrideToolbarBackgroundColor to set
	 */
	public void setOverrideToolbarBackgroundColor(boolean overrideToolbarBackgroundColor)
	{
		_versioner.trigger(this._overrideToolbarBackgroundColor, overrideToolbarBackgroundColor);
		this._overrideToolbarBackgroundColor = overrideToolbarBackgroundColor;
	}

	/**
	 * @return the toolbarBackgroundColor
	 */
	@SQLAliasProp(sqlAliasPropType = SQLAliasPropType.colorProp_toolbarBackgroundColor)
	public int getToolbarBackgroundColorRgbValue()
	{
		return _toolbarBackgroundColorRgbValue;
	}

	/**
	 * @param toolbarBackgroundColorRgbValue the toolbarBackgroundColor to set
	 */
	public void setToolbarBackgroundColorRgbValue(int toolbarBackgroundColorRgbValue)
	{
		_versioner.trigger(_toolbarBackgroundColorRgbValue, toolbarBackgroundColorRgbValue);
		this._toolbarBackgroundColorRgbValue = toolbarBackgroundColorRgbValue;
	}

	/**
	 * @return the overrideObjectTreeBackgroundColor
	 */
	@SQLAliasProp(sqlAliasPropType = SQLAliasPropType.colorProp_overrideObjectTreeBackgroundColor)
	public boolean isOverrideObjectTreeBackgroundColor()
	{
		return _overrideObjectTreeBackgroundColor;
	}

	/**
	 * @param overrideObjectTreeBackgroundColor the overrideObjectTreeBackgroundColor to set
	 */
	public void setOverrideObjectTreeBackgroundColor(boolean overrideObjectTreeBackgroundColor)
	{
		_versioner.trigger(_overrideObjectTreeBackgroundColor, overrideObjectTreeBackgroundColor);
		this._overrideObjectTreeBackgroundColor = overrideObjectTreeBackgroundColor;
	}

	/**
	 * @return the objectTreeBackgroundColorRgbValue
	 */
	@SQLAliasProp(sqlAliasPropType = SQLAliasPropType.colorProp_objectTreeBackgroundColor)
	public int getObjectTreeBackgroundColorRgbValue()
	{
		return _objectTreeBackgroundColorRgbValue;
	}

	/**
	 * @param objectTreeBackgroundColorRgbValue the objectTreeBackgroundColor to set
	 */
	public void setObjectTreeBackgroundColorRgbValue(int objectTreeBackgroundColorRgbValue)
	{
		_versioner.trigger(_objectTreeBackgroundColorRgbValue, objectTreeBackgroundColorRgbValue);
		this._objectTreeBackgroundColorRgbValue = objectTreeBackgroundColorRgbValue;
	}

	/**
	 * @return the overrideStatusBarBackgroundColor
	 */
	@SQLAliasProp(sqlAliasPropType = SQLAliasPropType.colorProp_overrideStatusBarBackgroundColor)
	public boolean isOverrideStatusBarBackgroundColor()
	{
		return _overrideStatusBarBackgroundColor;
	}

	/**
	 * @param overrideStatusBarBackgroundColor the overrideStatusBarBackgroundColor to set
	 */
	public void setOverrideStatusBarBackgroundColor(boolean overrideStatusBarBackgroundColor)
	{
		_versioner.trigger(_overrideStatusBarBackgroundColor, overrideStatusBarBackgroundColor);
		this._overrideStatusBarBackgroundColor = overrideStatusBarBackgroundColor;
	}

	/**
	 * @return the statusBarBackgroundColorRgbValue
	 */
	@SQLAliasProp(sqlAliasPropType = SQLAliasPropType.colorProp_statusBarBackgroundColor)
	public int getStatusBarBackgroundColorRgbValue()
	{
		return _statusBarBackgroundColorRgbValue;
	}

	/**
	 * @param statusBarBackgroundColorRgbValue the statusBarBackgroundColor to set
	 */
	public void setStatusBarBackgroundColorRgbValue(int statusBarBackgroundColorRgbValue)
	{
		_versioner.trigger(_statusBarBackgroundColorRgbValue , statusBarBackgroundColorRgbValue);
		this._statusBarBackgroundColorRgbValue = statusBarBackgroundColorRgbValue;
	}



	@SQLAliasProp(sqlAliasPropType = SQLAliasPropType.colorProp_overrideAliasBackgroundColor)
	public boolean isOverrideAliasBackgroundColor()
	{
		return _overrideAliasBackgroundColor;
	}

	public void setOverrideAliasBackgroundColor(boolean overrideStatusBarBackgroundColor)
	{
		_versioner.trigger(_overrideAliasBackgroundColor, overrideStatusBarBackgroundColor);
		_overrideAliasBackgroundColor = overrideStatusBarBackgroundColor;
	}

	@SQLAliasProp(sqlAliasPropType = SQLAliasPropType.colorProp_aliasBackgroundColor)
	public int getAliasBackgroundColorRgbValue()
	{
		return _aliasBackgroundColorRgbValue;
	}

	/**
	 * @param statusBarBackgroundColorRgbValue the statusBarBackgroundColor to set
	 */
	public void setAliasBackgroundColorRgbValue(int statusBarBackgroundColorRgbValue)
	{
		_versioner.trigger(_aliasBackgroundColorRgbValue, statusBarBackgroundColorRgbValue);
		this._aliasBackgroundColorRgbValue = statusBarBackgroundColorRgbValue;
	}


	public void acceptAliasVersioner(SQLAliasVersioner versioner)
	{
		_versioner = versioner;
	}
}
