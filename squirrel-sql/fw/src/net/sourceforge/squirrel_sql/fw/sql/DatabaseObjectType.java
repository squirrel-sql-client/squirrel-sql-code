package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002 Colin Bell
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
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DatabaseObjectType implements IHasIdentifier
{
	/** Factory to generate unique IDs for these objects. */
	private final static IntegerIdentifierFactory s_idFactory = new IntegerIdentifierFactory();

	/** Other - general purpose. */
	public final static DatabaseObjectType OTHER = createNewDatabaseObjectType();

	/** Catalog. */
	public final static DatabaseObjectType CATALOG = createNewDatabaseObjectType();

	/** Column. */
	public final static DatabaseObjectType COLUMN = createNewDatabaseObjectType();

	/** Database. */
	public final static DatabaseObjectType SESSION = createNewDatabaseObjectType();

	/** Standard datatype. */
	public final static DatabaseObjectType DATATYPE = createNewDatabaseObjectType();

	/** Foreign Key relationship. */
	public final static DatabaseObjectType FOREIGN_KEY = createNewDatabaseObjectType();

	/** Function. */
	public final static DatabaseObjectType FUNCTION = createNewDatabaseObjectType();

	/** Index. */
	public final static DatabaseObjectType INDEX = createNewDatabaseObjectType();

	/** Stored procedure. */
	public final static DatabaseObjectType PROCEDURE = createNewDatabaseObjectType();

	/** Schema. */
	public final static DatabaseObjectType SCHEMA = createNewDatabaseObjectType();

	/**
	 * An object that generates uniques IDs for primary keys. E.G. an Oracle
	 * sequence.
	 */
	public final static DatabaseObjectType SEQUENCE = createNewDatabaseObjectType();

	/** TABLE. */
	public final static DatabaseObjectType TABLE = createNewDatabaseObjectType();

	/** Trigger. */
	public final static DatabaseObjectType TRIGGER = createNewDatabaseObjectType();

	/** User defined type. */
	public final static DatabaseObjectType UDT = createNewDatabaseObjectType();

	/** A database user. */
	public final static DatabaseObjectType USER = createNewDatabaseObjectType();

	/** Uniquely identifies this Object. */
	private final IIdentifier _id;

	/**
	 * Default ctor.
	 */
	private DatabaseObjectType()
	{
		super();
		_id = s_idFactory.createIdentifier();
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

	public static DatabaseObjectType createNewDatabaseObjectType()
	{
		return new DatabaseObjectType();
	}
}
