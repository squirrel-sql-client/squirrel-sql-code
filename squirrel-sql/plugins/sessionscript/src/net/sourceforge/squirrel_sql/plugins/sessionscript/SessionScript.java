package net.sourceforge.squirrel_sql.plugins.sessionscript;
/*
 * Copyright (C) 2002 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;

import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

/**
 * An SQL script run when a session is started.
 */
public class SessionScript implements Cloneable, Serializable, IHasIdentifier {
	/** The <TT>IIdentifier</TT> that uniquely identifies this object. */
	private IIdentifier _id;
	
	/** The script. */
	private String _script;

	/** Sequence number of the script. */
	private int _sequenceNbr;
	
	/**
	 * Identifier of the <TT>SQLAlias</TT> that this script is
	 * associated with.
	 */
	private IIdentifier _sqlAliasId;
	
	/**
	 * Ctor.
	 */
	public SessionScript() {
		super();
		_id = IdentifierFactory.getInstance().createIdentifier();
	}

	/**
	 * Returns <TT>true</TT> if this objects is equal to the passed one. Two
	 * <TT>SessionScript</TT> objects are considered equal if they have the same
	 * identifier.
	 */
	public boolean equals(Object rhs) {
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass())) {
			rc = ((SessionScript)rhs).getIdentifier().equals(getIdentifier());
		}
		return rc;
	}

	/**
	 * Returns a hash code value for this object.
	 */
	public int hashCode() {
		return getIdentifier().hashCode();
	}

	/**
	 * Return the identifier that uniquely identifies this object.
	 * 
	 * @return	the identifier that uniquely identifies this object.
	 */
	public IIdentifier getIdentifier() {
		return _id;
	}

	public int getSequenceNumber() {
		return _sequenceNbr;
	}
	
	/**
	 * Return the script to be run.
	 * 
	 * @return	the script to be run.
	 */
	public String getScript() {
		return _script;
	}

	public IIdentifier getSQLAliasId() {
		return _sqlAliasId;
	}

	/**
	 * Set the identifier that uniquely identifies this object.
	 * 
	 * @param	the identifier that uniquely identifies this object.
	 */
	public void setIdentifier(IIdentifier id) {
		_id = id;
	}
	
	/**
	 * Set the script to be run.
	 * 
	 * @param	value	The script.
	 */
	public void setScript(String value) {
		_script = value;
	}
	
	public void setSequenceNumber(int value) {
		_sequenceNbr = value;
	}

	public void setSQLAliasId(IIdentifier value) {
		_sqlAliasId = value;
	}
}

