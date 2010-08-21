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
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.IValidatable;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This class represents a folder within which queries that can be stored.
 */
public final class Folder implements Cloneable, Serializable, IHasIdentifier,
										IValidatable /*, IHasName*/ {

    private static final long serialVersionUID = 1L;

    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(Folder.class);

	private static final String EMPTY_STRING = "";

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		// i18n[favs.nameMustNotBeBlank=Name cannot be blank.]
		static String ERR_BLANK_NAME = s_stringMgr.getString("favs.nameMustNotBeBlank");
	}

	public interface IPropertyNames {
		String ID = "Identifier";
		String NAME = "Name";
		String SUB_FOLDERS = "SubFolders";
	}

	/** The <CODE>IIdentifier</CODE> that uniquely identifies this object. */
	private IIdentifier _id;

	/** Name. */
	private String _name;

	/** Folders that this object contains. */
	private List<Folder> _subFolders = new ArrayList<Folder>();

	/** Object to handle property change events. */
	private transient PropertyChangeSupport _propChgNotifier = null;

	/**
	 * Default ctor.
	 */
	public Folder() {
		this(IdentifierFactory.getInstance().createIdentifier(), EMPTY_STRING);
	}

	/**
	 * Ctor specifying this objects attributes.
	 *
	 * @param   id		  Uniquely identifies this object.
	 * @param   name		Name of this folder.
	 */
	public Folder(IIdentifier id, String name) {
		super();
		_id = id != null ? id : IdentifierFactory.getInstance().createIdentifier();
		_name = getString(name);
	}

	/**
	 * Two <CODE>Folder</CODE> objects are considered equal if their ID's are
	 * identical.
	 *
	 * @return  <CODE>true</CODE> if this objects is equal to the passed one.
	 */
	public boolean equals(Object rhs) {
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass())) {
			rc = ((Folder)rhs).getIdentifier().equals(getIdentifier());
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
		return _name.trim().length() > 0;
	}

	public IIdentifier getIdentifier() {
		return _id;
	}

	public void setIdentifier(IIdentifier id) {
		_id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name)
			throws ValidationException {
		String data = getString(name);
		if (data.length() == 0) {
			throw new ValidationException(i18n.ERR_BLANK_NAME);
		}
		if (_name != data) {
			final String oldValue = _name;
			_name = data;
			getPropertyChangeNotifier().firePropertyChange(IPropertyNames.NAME, oldValue, _name);
		}
	}

	//public Folder getSubFolder(int idx) throws IndexOutOfBoundsException {
	/// return (Folder)_folders.get(idx);
	//}

	public void addSubFolder(Folder subFolder) throws IllegalArgumentException {
		if (subFolder == null) {
			throw new IllegalArgumentException("Null Folder passed");
		}
		_subFolders.add(subFolder);
	}

	public boolean removeSubFolder(Folder subFolder) throws IllegalArgumentException {
		if (subFolder == null) {
			throw new IllegalArgumentException("Null Folder passed");
		}
		return _subFolders.remove(subFolder);
	}

	public Iterator<Folder> subFolders() {
		return _subFolders.iterator();
	}

	public Folder[] getSubFolders() {
		return _subFolders.toArray(new Folder[_subFolders.size()]);
	}

	public Folder getSubFolder(int idx) throws ArrayIndexOutOfBoundsException {
		return _subFolders.get(idx);
	}

	public void setSubFolders(Folder[] value) {
		_subFolders.clear();
		if (value != null) {
			for (int i = 0; i < value.length; ++i) {
				_subFolders.add(value[i]);
			}
		}
	}

	public void setSubFolder(int idx, Folder value) throws ArrayIndexOutOfBoundsException {
		_subFolders.set(idx, value);
	}

	private PropertyChangeSupport getPropertyChangeNotifier() {
		if (_propChgNotifier == null) {
			_propChgNotifier = new PropertyChangeSupport(this);
		}
		return _propChgNotifier;
	}

	private String getString(String data) {
		return data != null ? data.trim() : "";
	}
}
