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
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This represents an enumeration that is over an empty container.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class EmptyEnumeration implements Enumeration {
	/**
	 * Returns <CODE>false</CODE> as container is empty.
	 */
	public boolean hasMoreElements() {
		return false;
	}

	/**
	 * Throws <CODE>NoSuchElementException</CODE> as container is empty.
	 */
	public Object nextElement() {
		throw new NoSuchElementException();
	}
}