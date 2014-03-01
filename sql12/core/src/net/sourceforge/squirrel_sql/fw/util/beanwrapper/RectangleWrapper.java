package net.sourceforge.squirrel_sql.fw.util.beanwrapper;
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
import java.awt.Rectangle;
import java.io.Serializable;

public class RectangleWrapper implements Serializable
{
	private static final long serialVersionUID = -188201653324691558L;

	public interface IPropertyNames
	{
		String X = "x";
		String Y = "y";
		String WIDTH = "width";
		String HEIGHT = "height";
	}

	private int _x;
	private int _y;
	private int _width;
	private int _height;

	public RectangleWrapper()
	{
		this(null);
	}

	public RectangleWrapper(Rectangle rc)
	{
		super();
		setFrom(rc);
	}

	public int getX()
	{
		return _x;
	}

	public void setX(int value)
	{
		_x = value;
	}

	public int getY()
	{
		return _y;
	}

	public void setY(int value)
	{
		_y = value;
	}

	public int getWidth()
	{
		return _width;
	}

	public void setWidth(int value)
	{
		_width = value;
	}

	public int getHeight()
	{
		return _height;
	}

	public void setHeight(int value)
	{
		_height = value;
	}

	public Rectangle createRectangle()
	{
		return new Rectangle(_x, _y, _width, _height);
	}

	public void setFrom(Rectangle rc)
	{
		if (rc != null)
		{
			_x = (int)rc.getX();
			_y = (int)rc.getY();
			_width = (int)rc.getWidth();
			_height = (int)rc.getHeight();
		}
	}
}
