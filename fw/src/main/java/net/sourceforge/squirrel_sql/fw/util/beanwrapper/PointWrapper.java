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
import java.awt.Point;

public class PointWrapper
{
	public interface IPropertyNames
	{
		String X = "x";
		String Y = "y";
	}

	private int _x;
	private int _y;

	public PointWrapper()
	{
		this(null);
	}

	public PointWrapper(Point pt)
	{
		super();
		setFrom(pt);
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

	public Point createPoint()
	{
		return new Point(_x, _y);
	}

	public void setFrom(Point pt)
	{
		if (pt != null)
		{
			_x = pt.x;
			_y = pt.y;
		}
	}
}