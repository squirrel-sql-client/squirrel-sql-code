package net.sourceforge.squirrel_sql.fw.util;
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
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sourceforge.squirrel_sql.fw.util.EmptyEnumeration;

/**
 * An <TT>EnumerationIterator</TT> object will allow you to treat
 * an <TT>Enumeration</TT> as an <TT>Iterator</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class EnumerationIterator implements Iterator {
	/** <TT>Enumeration</TT> that this <TT>Iterator</TT> is built over. */
	private Enumeration _en;
	
	/** An <TT>Iterator</TT> over an empty collection. */
	private Enumeration s_emptyEn = new EmptyEnumeration();

	/**
	 * Ctor.
	 *
	 * @param	en	<TT>Enumeration</TT> that <TT>Iterator</TT> will be built
	 *				over. If <TT>null</TT> pretends it was an empty
	 *				<TT>Enumeration</TT> passed.
	 */
	public EnumerationIterator(Enumeration en) throws IllegalArgumentException {
		super();
		if (en == null) {
			throw new IllegalArgumentException("Null Enumeration passed");
		}
		_en = en != null ? en : s_emptyEn;
	}

	/**
	 * Returns <TT>true</TT> if the iteration has more elements.
	 *
	 * @return	<TT>true</TT> if the <TT>Iterator</TT> has more elements.
	 */
	public boolean hasNext() {
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
	public Object next() throws NoSuchElementException {
		return _en.nextElement();
	}

	/**
	 * Unsupported operation. <TT>Enumeration</TT> objects don't
	 * support <TT>remove</TT>.
	 *
	 * @throws	<TT>UnsupportedOperationException</TT>
	 *			This is an unsupported operation.
	 */
	public void remove() {
		throw new UnsupportedOperationException("remove()");
	}
}
