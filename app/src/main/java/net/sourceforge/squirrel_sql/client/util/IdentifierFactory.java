package net.sourceforge.squirrel_sql.client.util;
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
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifierFactory;
/**
 * This class is a factory that generates unique identifiers for various
 * classes within SQuirreL. All identifiers created are instances of
 * <TT>net.sourceforge.squirrel_sql.fw.id.UidIdentifier</TT>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class IdentifierFactory implements IIdentifierFactory
{
	/** The single instance of this class. */
	private static final IIdentifierFactory s_instance = new UidIdentifierFactory();

	/**
	 * Retrieve the singleton instance of this class.
	 *
	 * @return	The singleton instance of this class.
	 */
	public static IIdentifierFactory getInstance()
	{
		return s_instance;
	}

	/**
	 * Create a new identifier.
	 *
	 * @return	The new identifier.
	 */
	public IIdentifier createIdentifier()
	{
		return s_instance.createIdentifier();
	}
}
