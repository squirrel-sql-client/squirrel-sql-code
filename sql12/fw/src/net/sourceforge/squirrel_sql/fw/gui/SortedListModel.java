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
import javax.swing.DefaultListModel;
/**
 * This class is a descendant of <CODE>DefaultListModel</CODE> that will
 * keep its data in a sorted order.
 */
public class SortedListModel extends DefaultListModel
{
	/**
	 * Default ctor.
	 */
	public SortedListModel()
	{
		super();
	}

	/**
	 * Add <CODE>obj</CODE> to list ignoring <CODE>index</CODE> as list
	 * is sorted.
	 */
	public void add(int index, Object obj)
	{
		addElement(obj);
	}

	/**
	 * Add <CODE>obj</CODE> to list ignoring <CODE>index</CODE> as list
	 * is sorted.
	 */
	public void insertElementAt(int index, Object obj)
	{
		addElement(obj);
	}

	/**
	 * Add <CODE>obj</CODE> to list sorting by <CODE>obj.toString()</CODE>.
	 */
	public void addElement(Object obj)
	{
		super.add(getIndexInList(obj), obj);
	}

	public Object remove(int index)
	{
		Object obj = get(index);
		removeElement(obj);
		return obj;
	}

	public void removeElementAt(int index)
	{
		removeElement(get(index));
	}

	public void removeRange(int fromIndex, int toIndex)
	{
		for (int i = fromIndex; i <= toIndex; ++i)
		{
			remove(i);
		}
	}

	/**
	 * Return the appropriate position for the passed object in the list. This
	 * does a sequential read through the list so you wouldn't want to use it
	 * for large lists.
	 */
	protected int getIndexInList(Object obj)
	{
		final int limit = getSize();
		final String objStr = obj.toString();
		for (int i = 0; i < limit; ++i)
		{
			if (objStr.compareToIgnoreCase(get(i).toString()) <= 0)
			{
				return i;
			}
		}
		return limit;
	}
}
