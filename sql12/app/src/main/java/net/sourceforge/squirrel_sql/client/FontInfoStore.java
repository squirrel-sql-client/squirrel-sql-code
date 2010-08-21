package net.sourceforge.squirrel_sql.client;
/*
 * Copyright (C) 2001-2006 Colin Bell
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
import java.awt.Font;

import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
/**
 * A store of <TT>FontInfo</TT> objects.
 */
public class FontInfoStore
{
	/** Default one. */
	private final FontInfo _defaultFontInfo = new FontInfo();

	/** For statusbars. */
	private FontInfo _statusBarFontInfo;

	/**
	 * Default ctor.
	 */
	public FontInfoStore()
	{
	    Font tmp = (Font)UIManager.get("Label.font");
	    if (tmp != null) {
	        Font font = tmp.deriveFont(10.0f);
	        _statusBarFontInfo = new FontInfo(font);	        
	    }
	}

	/**
	 * Gets the FontInfo for status bars.
	 * 
	 * @return	Returns FontInfo for statusbars
	 */
	public FontInfo getStatusBarFontInfo()
	{
		return _statusBarFontInfo != null ? _statusBarFontInfo : _defaultFontInfo;
	}

	/**
	 * Sets the FontInfo for status bars.
	 * 
	 * @param fi	The new FontInfo for status bars
	 */
	public void setStatusBarFontInfo(FontInfo fi)
	{
		_statusBarFontInfo = fi;
	}
}

