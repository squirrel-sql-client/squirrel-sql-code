package net.sourceforge.squirrel_sql.plugins.sqlscript.session_script;
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

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;

import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

/**
 * An SQL script run when a session is started and/or closed.
 */
public class SessionScript implements Cloneable, Serializable, IHasIdentifier {
    /** The <TT>IIdentifier</TT> that uniquely identifies this object. */
    private IIdentifier _id;

	/** Run on session startup. */
	private boolean _runOnStartup = true;

	/** Run on session closing. */
	private boolean _runOnClosing = false;
	
	/** The script. */
	private String _script;
	
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

	/**
	 * Return <TT>true</TT> if script is to be run on session startup.
	 * 
	 * @return	<TT>true</TT> if script is to be run on session startup.
	 */
	public boolean getRunOnStartup() {
		return _runOnStartup;
	}
	
	/**
	 * Return <TT>true</TT> if script is to be run on session closing.
	 * 
	 * @return	<TT>true</TT> if script is to be run on session closing.
	 */
	public boolean getRunOnClosing() {
		return _runOnClosing;
	}
	
	/**
	 * Return the script to be run.
	 * 
	 * @return	the script to be run.
	 */
	public String getScript() {
		return _script;
	}

	/**
	 * Return the ID of the <TT>SQLAlias</TT> that this script is associated with.
	 * 
	 * @return	ID of the <TT>SQLAlias</TT> that this script is associated with.
	 */
//	public IIdentifier getSQLAliasIdentifier() {
//		return _sqlAliasId;
//	}

	/**
	 * Set the identifier that uniquely identifies this object.
	 * 
	 * @param	the identifier that uniquely identifies this object.
	 */
    public void setIdentifier(IIdentifier id) {
        _id = id;
    }
	
	/**
	 * Set whether script is to be run on session startup.
	 * 
	 * @param	value	<TT>true</TT> if script is to be run on session startup.
	 */
	public void setRunOnStartup(boolean value) {
		_runOnStartup = value;
	}
	
	/**
	 * Set whether script is to be run on session closing.
	 * 
	 * @param	value	<TT>true</TT> if script is to be run on session closing.
	 */
	public void setRunOnClosing(boolean value) {
		_runOnClosing = value;
	}
	
	/**
	 * Set the script to be run.
	 * 
	 * @param	value	The script.
	 */
	public void setScript(String value) {
		_script = value;
	}

	/**
	 * Set the ID of the <TT>SQLAlias</TT> that this script is associated with.
	 * 
	 * @param	value	ID of the <TT>SQLAlias</TT> that this script is associated with.
	 */
//	public void setSQLAliasIdentifier(IIdentifier value) {
//		_sqlAliasId = value;
//	}
}

