package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
/**
 *
 * Defines the different types of database objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DatabaseObjectType implements IHasIdentifier
{
	/** Factory to generate unique IDs for these objects. */
	private final static IntegerIdentifierFactory s_idFactory = new IntegerIdentifierFactory();

	/** Other - general purpose. */
	public final static DatabaseObjectType OTHER = createNewDatabaseObjectType("Other");

	/** Catalog. */
	public final static DatabaseObjectType CATALOG = createNewDatabaseObjectType("Catalog");

	/** Column. */
	public final static DatabaseObjectType COLUMN = createNewDatabaseObjectType("Column");

	/** Database. */
	public final static DatabaseObjectType SESSION = createNewDatabaseObjectType("Database");

	/** Standard datatype. */
	public final static DatabaseObjectType DATATYPE = createNewDatabaseObjectType("Data Type");

	/** Foreign Key relationship. */
	public final static DatabaseObjectType FOREIGN_KEY = createNewDatabaseObjectType(" Foreign Key");

	/** Function. */
	public final static DatabaseObjectType FUNCTION = createNewDatabaseObjectType("Function");

	/** Index. */
	public final static DatabaseObjectType INDEX = createNewDatabaseObjectType("Index");

	/** Stored procedure. */
	public final static DatabaseObjectType PROCEDURE = createNewDatabaseObjectType("Stored Procedure");

	/** Schema. */
	public final static DatabaseObjectType SCHEMA = createNewDatabaseObjectType("Schema");

	/**
	 * An object that generates uniques IDs for primary keys. E.G. an Oracle
	 * sequence.
	 */
	public final static DatabaseObjectType SEQUENCE = createNewDatabaseObjectType("Sequence");

	/** TABLE. */
	public final static DatabaseObjectType TABLE = createNewDatabaseObjectType("Table");

	/** Trigger. */
	public final static DatabaseObjectType TRIGGER = createNewDatabaseObjectType("Trigger");

	/** User defined type. */
	public final static DatabaseObjectType UDT = createNewDatabaseObjectType("UDT");

	/** A database user. */
	public final static DatabaseObjectType USER = createNewDatabaseObjectType("User");

	/** Uniquely identifies this Object. */
	private final IIdentifier _id;

	/** Describes this object type. */
	private final String _name;

	/**
	 * Default ctor.
	 */
	private DatabaseObjectType(String name)
	{
		super();
		_id = s_idFactory.createIdentifier();
		_name = name != null ? name : _id.toString();
	}

	/**
	 * Return the object that uniquely identifies this object.
	 *
	 * @return	Unique ID.
	 */
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	/**
	 * Retrieve the descriptive name of this object.
	 *
	 * @return	The descriptiev name of this object.
	 */
	public String getName()
	{
		return _name;
	}

	public String toString()
	{
		return getName();
	}

	public static DatabaseObjectType createNewDatabaseObjectType(String name)
	{
		return new DatabaseObjectType(name);
	}
}
