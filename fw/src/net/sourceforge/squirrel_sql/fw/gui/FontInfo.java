package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.io.Serializable;

public class FontInfo implements Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;

    public interface IPropertyNames
	{
		String FAMILY = "family";
		String IS_BOLD = "isBold";
		String IS_ITALIC = "isItalic";
		String SIZE = "size";
	}

	private static String DEFAULT_FAMILY = "Monospaced";

	private String _familyName;
	private boolean _isBold;
	private boolean _isItalic;
	private int _size;

	public FontInfo()
	{
		super();
		setFamily(DEFAULT_FAMILY);
		setSize(12);
	}

	public FontInfo(Font font)
	{
		super();
		if (font == null)
		{
			throw new IllegalArgumentException("Null Font passed");
		}
		setFont(font);
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

	public String getFamily()
	{
		return _familyName;
	}

	public void setFamily(String value)
	{
		_familyName = value != null ? value : DEFAULT_FAMILY;
	}

	public boolean isBold()
	{
		return _isBold;
	}

	public void setIsBold(boolean value)
	{
		_isBold = value;
	}

	public boolean isItalic()
	{
		return _isItalic;
	}

	public void setIsItalic(boolean value)
	{
		_isItalic = value;
	}

	public int getSize()
	{
		return _size;
	}

	public void setSize(int value)
	{
		_size = value;
	}

	public void setFont(Font font) throws IllegalArgumentException
	{
		if (font == null)
		{
			throw new IllegalArgumentException("Null Font passed");
		}
		_familyName = font.getFamily();
		_isBold = font.isBold();
		_isItalic = font.isItalic();
		_size = font.getSize();
	}

	public boolean doesFontMatch(Font font)
	{
		if (font == null)
		{
			return false;
		}
		return font.getFamily().equals(_familyName)
			&& font.getSize() == getSize()
			&& font.getStyle() == generateStyle();
	}

	public int generateStyle()
	{
		int style = 0;
		if (!_isBold && !_isItalic)
		{
			style = Font.PLAIN;
		}
		else
		{
			if (_isBold)
			{
				style |= Font.BOLD;
			}
			if (_isItalic)
			{
				style |= Font.ITALIC;
			}
		}
		return style;
	}

	public Font createFont()
	{
		return new Font(_familyName, generateStyle(), _size);
	}

	// i18n ? What is this used for?
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(_familyName).append(", " + _size);
		if (_isBold)
		{
			buf.append(", bold");
		}
		if (_isItalic)
		{
			buf.append(", italic");
		}
		return buf.toString();
	}

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((_familyName == null) ? 0 : _familyName.hashCode());
        result = PRIME * result + (_isBold ? 1231 : 1237);
        result = PRIME * result + (_isItalic ? 1231 : 1237);
        result = PRIME * result + _size;
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FontInfo other = (FontInfo) obj;
        if (_familyName == null) {
            if (other._familyName != null)
                return false;
        } else if (!_familyName.equals(other._familyName))
            return false;
        if (_isBold != other._isBold)
            return false;
        if (_isItalic != other._isItalic)
            return false;
        if (_size != other._size)
            return false;
        return true;
    }
}
