package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.io.Serializable;

import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

/**
 * This JavaBean class represents the user specific
 * preferences for this plugin.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFPreferences implements Cloneable, Serializable, IHasIdentifier
{
	/** The <CODE>IIdentifier</CODE> that uniquely identifies this object. */
	private IIdentifier _id;

	private String _lafClassName;

	private FontInfo _fiMenu;
	private FontInfo _fiStatic;
	private FontInfo _fiStatusBar;
	private FontInfo _fiOther;

	private boolean _fiMenuEnabled;
	private boolean _fiStaticEnabled;
	private boolean _fiStatusBarEnabled;
	private boolean _fiOtherEnabled;

	public LAFPreferences()
	{
		super();
		_lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
	}

	public LAFPreferences(IIdentifier id)
	{
		this();
		_id = id;
	}

	/**
	 * Return a copy of this object.
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	/**
	 * Two preferences objects are considered equal if their preference
	 * attribues are the same.
	 */
	public boolean equals(Object rhs)
	{
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass()))
		{
			LAFPreferences obj = (LAFPreferences) rhs;
			if (_fiMenuEnabled == obj._fiMenuEnabled
				&& _fiStaticEnabled == obj._fiStaticEnabled
				&& _fiStatusBarEnabled == obj._fiStatusBarEnabled
				&& _fiOtherEnabled == obj._fiOtherEnabled
				&& _fiOther.equals(obj._fiOther)
				&& _fiStatic.equals(obj._fiStatic)
				&& _fiMenu.equals(obj._fiMenu)
				&& _fiStatusBar.equals(obj._fiStatusBar)
				&& _lafClassName.equals(obj._lafClassName))
			{
				rc = true;
			}
		}
		return rc;
	}

	public String getLookAndFeelClassName()
	{
		return _lafClassName;
	}

	public void setLookAndFeelClassName(String data)
	{
		_lafClassName = data;
	}

	public FontInfo getMenuFontInfo()
	{
		return _fiMenu;
	}

	public void setMenuFontInfo(FontInfo data)
	{
		_fiMenu = data;
	}

	public FontInfo getStaticFontInfo()
	{
		return _fiStatic;
	}

	public void setStaticFontInfo(FontInfo data)
	{
		_fiStatic = data;
	}

	public FontInfo getStatusBarFontInfo()
	{
		return _fiStatusBar;
	}

	public void setStatusBarFontInfo(FontInfo data)
	{
		_fiStatusBar = data;
	}

	public FontInfo getOtherFontInfo()
	{
		return _fiOther;
	}

	public void setOtherFontInfo(FontInfo data)
	{
		_fiOther = data;
	}

	public boolean isMenuFontEnabled()
	{
		return _fiMenuEnabled;
	}

	public void setMenuFontEnabled(boolean data)
	{
		_fiMenuEnabled = data;
	}

	public boolean isStaticFontEnabled()
	{
		return _fiStaticEnabled;
	}

	public void setStaticFontEnabled(boolean data)
	{
		_fiStaticEnabled = data;
	}

	public boolean isStatusBarFontEnabled()
	{
		return _fiStatusBarEnabled;
	}

	public void setStatusBarFontEnabled(boolean data)
	{
		_fiStatusBarEnabled = data;
	}

	public boolean isOtherFontEnabled()
	{
		return _fiOtherEnabled;
	}

	public void setOtherFontEnabled(boolean data)
	{
		_fiOtherEnabled = data;
	}

	/**
	 * @see IHasIdentifier#getIdentifier()
	 */
	public IIdentifier getIdentifier()
	{
		return null;
	}

	/**
	 * Gets the _id.
	 * @return Returns a IIdentifier
	 */
	public IIdentifier getId()
	{
		return _id;
	}

	/**
	 * Sets the _id.
	 * @param _id The _id to set
	 */
	public void setId(IIdentifier _id)
	{
		this._id = _id;
	}

}