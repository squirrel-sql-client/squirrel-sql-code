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
import java.io.Serializable;

import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;

/**
 * This JavaBean class represents the user specific
 * preferences for this plugin.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFPreferences implements Cloneable, Serializable {
	private String _lafClassName;
	private String _skinThemePackName;

	private FontInfo _fiMenu;
	private FontInfo _fiStatic;
	private FontInfo _fiToolBar;
	private FontInfo _fiOther;

	private boolean _fiMenuEnabled;
	private boolean _fiStaticEnabled;
	private boolean _fiToolBarEnabled;
	private boolean _fiOtherEnabled;

	public LAFPreferences() {
		super();
		_lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
		_skinThemePackName = "";
	}

	/**
	 * Return a copy of this object.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage());   // Impossible.
		}
	}

	public boolean equals(Object rhs) {
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass())) {
			LAFPreferences obj = (LAFPreferences)rhs;
			if (_fiMenuEnabled == obj._fiMenuEnabled &&
					_fiStaticEnabled == obj._fiStaticEnabled &&
					_fiToolBarEnabled == obj._fiToolBarEnabled &&
					_fiOtherEnabled == obj._fiOtherEnabled &&
					_fiOther.equals(obj._fiOther) &&
					_fiStatic.equals(obj._fiStatic) &&
					_fiMenu.equals(obj._fiMenu) &&
					_fiToolBar.equals(obj._fiToolBar) &&
					_skinThemePackName.equals(obj._skinThemePackName) &&
					_lafClassName.equals(obj._lafClassName)) {
				rc = true;
			}
		}
		return rc;
	}

	public String getLookAndFeelClassName() {
		return _lafClassName;
	}

	public void setLookAndFeelClassName(String data) {
		_lafClassName = data;
	}

	public String getSkinThemePackName() {
		return _skinThemePackName;
	}

	public void setSkinThemePackName(String data) {
		_skinThemePackName = data;
	}

	public FontInfo getMenuFontInfo() {
		return _fiMenu;
	}

	public void setMenuFontInfo(FontInfo data) {
		_fiMenu = data;
	}

	public FontInfo getStaticFontInfo() {
		return _fiStatic;
	}

	public void setStaticFontInfo(FontInfo data) {
		_fiStatic = data;
	}

	public FontInfo getToolBarFontInfo() {
		return _fiToolBar;
	}

	public void setToolBarFontInfo(FontInfo data) {
		_fiToolBar = data;
	}

	public FontInfo getOtherFontInfo() {
		return _fiOther;
	}

	public void setOtherFontInfo(FontInfo data) {
		_fiOther = data;
	}

	public boolean isMenuFontEnabled() {
		return _fiMenuEnabled;
	}

	public void setMenuFontEnabled(boolean data) {
		_fiMenuEnabled = data;
	}

	public boolean isStaticFontEnabled() {
		return _fiStaticEnabled;
	}

	public void setStaticFontEnabled(boolean data) {
		_fiStaticEnabled = data;
	}

	public boolean isToolBarFontEnabled() {
		return _fiToolBarEnabled;
	}

	public void setToolBarFontEnabled(boolean data) {
		_fiToolBarEnabled = data;
	}

	public boolean isOtherFontEnabled() {
		return _fiOtherEnabled;
	}

	public void setOtherFontEnabled(boolean data) {
		_fiOtherEnabled = data;
	}
}
