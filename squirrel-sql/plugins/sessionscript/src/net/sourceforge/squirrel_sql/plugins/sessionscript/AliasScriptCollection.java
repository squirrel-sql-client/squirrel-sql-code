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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

public class AliasScriptCollection implements Serializable, IHasIdentifier {
	/** Property names for this bean. */
	public interface IPropertyNames {
		String ID = "SQLAliasId";
		String SCRIPTS = "Scripts";
	}

	/**
	 * Identifier of the <TT>SQLAlias</TT> that these scripts are
	 * associated with.
	 */
	private IIdentifier _sqlAliasId;

	/** <TT>AliasScript</TT> that this collection contains. */
	private List _scripts = new ArrayList();

	/**
	 * Default ctor.
	 */
	public AliasScriptCollection() {
		super();
	}

	/**
	 * ctor specifying the alias for this collection.
	 * 
	 * @param	alias	<TT>SQLAlias</TT> for this collection.
	 * 
	 * @throws	IllegalArgumentException	if null <TT>ISQLAlias</TT> passed.
	 */
	public AliasScriptCollection(ISQLAlias alias) {
		super();
		if (alias == null) {
			throw new IllegalArgumentException("ISQLAlias == null");
		}
		setSQLAliasId(alias.getIdentifier());
	}

	/**
	 * Returns a hash code value for this object.
	 */
	public int hashCode() {
		return getIdentifier().hashCode();
	}

	/**
	 * Returns <TT>true</TT> if this objects is equal to the passed one. Two
	 * <TT>AliasScriptCollection</TT> objects are considered equal if they
	 * are for the same <TT>SQLAlias</TT>.
	 */
	public boolean equals(Object rhs) {
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass())) {
			rc = ((AliasScriptCollection)rhs).getSQLAliasId().equals(getSQLAliasId());
		}
		return rc;
	}

	/*
	 * @see IHasIdentifier#getIdentifier()
	 */
	public IIdentifier getIdentifier() {
		return getSQLAliasId();
	}

	public IIdentifier getSQLAliasId() {
		return _sqlAliasId;
	}

	public void setSQLAliasId(IIdentifier value) {
		_sqlAliasId = value;
	}

	public synchronized AliasScript getScript(int idx) throws ArrayIndexOutOfBoundsException {
		return (AliasScript)_scripts.get(idx);
	}

	public synchronized void setScript(int idx, AliasScript script)
			throws ArrayIndexOutOfBoundsException {
		_scripts.set(idx, script);
	}

	public synchronized void addScript(AliasScript script) {
		if (script == null) {
			throw new IllegalArgumentException("AliasScript == null");
		}
		_scripts.add(script);
	}

	public synchronized boolean removeScript(AliasScript script) throws IllegalArgumentException {
		if (script == null) {
			throw new IllegalArgumentException("AliasScript == null");
		}
		return _scripts.remove(script);
	}

	public synchronized Iterator scripts() {
		return _scripts.iterator();
	}

	public synchronized AliasScript[] getScripts() {
		return (AliasScript[])_scripts.toArray(new AliasScript[_scripts.size()]);
	}

	public synchronized void setScripts(AliasScript[] value) {
		_scripts.clear();
		if (value != null) {
			for (int i = 0; i < value.length; ++i) {
				_scripts.add(value[i]);
			}
		}
	}
}
