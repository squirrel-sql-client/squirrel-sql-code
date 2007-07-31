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
import java.util.Enumeration;
import java.util.Iterator;
/**
 * An <TT>EnumerationIterator</TT> object will allow you to treat
 * an <TT>Enumeration</TT> as an <TT>Iterator</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class EnumerationIterator<E> implements Iterator<E>
{
	/** <TT>Enumeration</TT> that this <TT>Iterator</TT> is built over. */
	private Enumeration<E> _en;

	/**
	 * Ctor.
	 *
	 * @param	en	<TT>Enumeration</TT> that <TT>Iterator</TT> will be built
	 *				over. If <TT>null</TT> pretends it was an empty
	 *				<TT>Enumeration</TT> passed.
	 */
	public EnumerationIterator(Enumeration<E> en)
	{
		super();
		_en = en != null ? en : new EmptyEnumeration<E>();
	}

	/**
	 * Returns <TT>true</TT> if the iteration has more elements.
	 *
	 * @return	<TT>true</TT> if the <TT>Iterator</TT> has more elements.
	 */
	public boolean hasNext()
	{
		return _en.hasMoreElements();
	}

	/**
	 * Returns the next element in the interation.
	 *
	 * @return	the next element in the iteration.
	 *
	 * @throws	<TT>NoSuchElementException</TT>
	 *			iteration has no more elements.
	 */
	public E next()
	{
		return _en.nextElement();
	}

	/**
	 * Unsupported operation. <TT>Enumeration</TT> objects don't
	 * support <TT>remove</TT>.
	 *
	 * @throws	<TT>UnsupportedOperationException</TT>
	 *			This is an unsupported operation.
	 */
	public void remove()
	{
		throw new UnsupportedOperationException("remove()");
	}
}
