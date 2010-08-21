package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 * This represents an iterator that is over an empty container.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class EmptyIterator<E> implements Iterator<E>
{
	/**
	 * Returns <CODE>false</CODE> as container is empty.
	 */
	public boolean hasNext()
	{
		return false;
	}

	/**
	 * Throws <CODE>NoSuchElementException</CODE> as container is empty.
	 */
	public E next()
	{
		throw new NoSuchElementException();
	}

	/**
	 * Throws <CODE>IllegalStateException</CODE> as container is empty.
	 */
	public void remove()
	{
		throw new IllegalStateException();
	}
}
