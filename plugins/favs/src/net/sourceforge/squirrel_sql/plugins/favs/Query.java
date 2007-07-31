package net.sourceforge.squirrel_sql.plugins.favs;
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
import java.io.Serializable;

import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.IValidatable;

/**
 * This class represents a query that can be saved and restored.
 */
final class Query implements Cloneable, Serializable, IHasIdentifier, IValidatable {

    private static final long serialVersionUID = 1L;

    private static final String EMPTY_STRING = "";


	public interface IPropertyNames {
		String DESCRIPTION = "Description";
		String ID = "Identifier";
		String NAME = "Name";
		String SQL = "Sql";
	}
	/** The <CODE>IIdentifier</CODE> that uniquely identifies this object. */
	private IIdentifier _id;

	/** Name. */
	private String _name;

	/** Description for this query. */
    @SuppressWarnings("unused")
	private String _description;

	/** SQL. */
	private String _sql;

	/**
	 * Default ctor.
	 */
	public Query() {
		this(IdentifierFactory.getInstance().createIdentifier(), EMPTY_STRING,
				EMPTY_STRING, EMPTY_STRING);
	}

	/**
	 * Ctor specifying this objects attributes.
	 *
	 * @param	id			Uniquely identifies this object.
	 * @param	name		Name of thi query.
	 * @param	description	The description of this alias.
	 * @param	sql			The SQL to execute.
	 */
	public Query(IIdentifier id, String name, String description, String sql) {
		super();
		_id = id != null ? id : IdentifierFactory.getInstance().createIdentifier();
		_name = getString(name);
		_description = getString(description);
		_sql = getString(sql);
	}

	/**
	 * Two <CODE>Query</CODE> objects are considered equal if their ID's are
	 * identical unless their ID's are
	 *
	 * @return	<CODE>true</CODE> if this object is equal to the passed one.
	 */
	public boolean equals(Object rhs) {
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass())) {
			rc = ((Query)rhs).getIdentifier().equals(getIdentifier());
		}
		return rc;
	}

	/**
	 * Return a copy of this object.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage());   // Impossible.
		}
	}

	public synchronized int hashCode() {
		return getIdentifier().hashCode();
	}

	public String toString() {
		return getName();
	}

	/**
	 * Returns <CODE>true</CODE> if this object is valid.<P>
	 * Implementation for <CODE>IPersistable</CODE>.
	 */
	public synchronized boolean isValid() {
		return _name.trim().length() > 0 && _sql.trim().length() > 0;
	}

	public IIdentifier getIdentifier() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	private String getString(String data) {
		return data != null ? data.trim() : "";
	}
}
