package net.sourceforge.squirrel_sql.fw.id;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
/**
 * This class is a factory that generates <tt>IntegerIdentifier</tt>
 * objects. Each identifier generated will have a value one greater
 * than the previously generated one.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class IntegerIdentifierFactory implements IIdentifierFactory
{
	private int _next;

	/**
	 * Default ctor. First identifier generated will have a value of zero.
	 */
	public IntegerIdentifierFactory()
	{
		this(0);
	}

	/**
	 * ctor specifying the value of the first identifier.
	 *
	 * @param	initialValue	Value for first identifier generated.
	 */
	public IntegerIdentifierFactory(int initialValue)
	{
		super();
		_next = initialValue;
	}

	/**
	 * Create a new identifier.
	 *
	 * @return	The new identifier object.
	 */
	public synchronized IIdentifier createIdentifier()
	{
		return new IntegerIdentifier(_next++);
	}
}
