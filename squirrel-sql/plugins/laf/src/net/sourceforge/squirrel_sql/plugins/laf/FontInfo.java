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

public class FontInfo {
	public interface IPropertyNames {
		String UIDEFAULTS_PROP_NAME = "UIDefaultsPropertyName";
		String FONT_NAME = "FontName";
		String FONT_STYLE = "FontStyle";
		String FONT_SIZE = "FontSize";
	}

	private String _uiDefaultsPropertyName;
	private String _fontName;
	private int _fontStyle;
	private int _fontSize;

	public FontInfo() {
		super();
		_uiDefaultsPropertyName = "";
		_fontName = "Monospaced";
		_fontStyle = Font.PLAIN;
		_fontSize = 12;
	}

	public FontInfo(FontInfo rhs) {
		super();
		setUIDefaultsPropertyName(rhs.getUIDefaultsPropertyName());
		setFontName(rhs.getFontName());
		setFontStyle(rhs.getFontStyle());
		setFontSize(rhs.getFontSize());
	}

	public FontInfo(String propName) {
		super();
		setUIDefaultsPropertyName(propName);
		_fontName = "Monospaced";
		_fontStyle = Font.PLAIN;
		_fontSize = 12;
	}

	public FontInfo(String propName, Font font) {
		super();
		setUIDefaultsPropertyName(propName);
		setFont(font);
	}

    /**
     * Returns <TT>true</TT> if this objects is equal to the passed one. Two
     * <TT>FontInfo</TT> objects are considered equal if they have the same
     * UIDefaults property name.
     */
    public boolean equals(Object rhs) {
        boolean rc = false;
        if (rhs != null && rhs.getClass().equals(getClass())) {
            rc = ((FontInfo)rhs).getUIDefaultsPropertyName().equals(getUIDefaultsPropertyName());
        }
        return rc;
    }

    /**
     * Returns a hash code value for this object.
     */
    public int hashCode() {
        return getUIDefaultsPropertyName().hashCode();
    }

	public String getUIDefaultsPropertyName() {
		return _uiDefaultsPropertyName;
	}

	public void setUIDefaultsPropertyName(String value) {
		_uiDefaultsPropertyName = value;
	}

	public String getFontName() {
		return _fontName;
	}

	public void setFontName(String value) {
		_fontName = value;
	}

	public int getFontStyle() {
		return _fontStyle;
	}

	public void setFontStyle(int value) {
		_fontStyle = value;
	}

	public int getFontSize() {
		return _fontSize;
	}

	public void setFontSize(int value) {
		_fontSize = value;
	}
	
	public void setFont(Font font) {
		_fontName = font.getFamily();
		_fontStyle = font.getStyle();
		_fontSize = font.getSize();
	}
}

