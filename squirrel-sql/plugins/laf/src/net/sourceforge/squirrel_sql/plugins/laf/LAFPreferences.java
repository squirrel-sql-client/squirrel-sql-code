package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

/**
 * This JavaBean class represents the user specific
 * preferences for this plugin.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFPreferences implements Serializable {
	public interface IPropertyNames {
		String LAF_CLASS = "LookAndFeelClassName";
		String THEME_PACK = "ThemePackName";
		String FONT_INFO = "FontInformation";
	}

	private String _lafClassName;
	private String _themePackName;

	/** <TT>FontInfo</TT> objects */
	private List _fontInfoList = new ArrayList();

	public LAFPreferences() {
		super();
		_lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
		_themePackName = "";
	}


	public String getLookAndFeelClassName() {
		return _lafClassName;
	}

	public String getThemePackName() {
		return _themePackName;
	}


	public void setLookAndFeelClassName(String data) {
		_lafClassName = data;
	}


	public void setThemePackName(String data) {
		_themePackName = data;
	}

	public void setFontInfo(String[] propertyNames, Font font)
			throws IllegalArgumentException {
		if (propertyNames == null) {
			throw new IllegalArgumentException("Null propertyNames passed");
		}
		if (font == null) {
			throw new IllegalArgumentException("Null Font passed");
		}

		for (int i = 0; i < propertyNames.length; ++i) {
			FontInfo fi = new FontInfo(propertyNames[i]);
			int idx = _fontInfoList.indexOf(fi);
			if (idx != -1) {
				fi = (FontInfo)_fontInfoList.get(idx);
			} else {
				_fontInfoList.add(fi);
			}
			fi.setFont(font);
		}
	}

	public FontInfo[] getFontInfo() {
		return (FontInfo[])_fontInfoList.toArray(new FontInfo[_fontInfoList.size()]);
	}

	public FontInfo getFontInfo(int idx) throws ArrayIndexOutOfBoundsException {
		return (FontInfo)_fontInfoList.get(idx);
	}

	public void setFontInfo(FontInfo[] value) {
		_fontInfoList.clear();
		if (value != null) {
			for (int i = 0; i < value.length; ++i) {
				_fontInfoList.add(value[i]);
			}
		}
	}

	public void setFontInfo(int idx, FontInfo value) throws ArrayIndexOutOfBoundsException {
		_fontInfoList.set(idx, value);
	}

}
