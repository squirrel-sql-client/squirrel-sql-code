package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.IValidatable;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;

/**
 * This represents a Database alias which is a description of the means
 * required to connect to a JDBC complient database.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ISQLAlias extends IHasIdentifier, IValidatable { //, Comparable {
	/**
	 * JavaBean property names for this class.
	 */
	public interface IPropertyNames {
		String DRIVER = "driverIdentifier";
		String ID = "identifier";
		String NAME = "name";
		String URL = "url";
		String USER_NAME = "userName";
	}

	/**
	 * Assign data from the passed <CODE>ISQLAlias</CODE> to this one.
	 *
	 * @param   rhs	 <CODE>ISQLAlias</CODE> to copy data from.
	 *
	 * @exception   ValidationException
	 *				  Thrown if an error occurs assigning data from
	 *				  <CODE>rhs</CODE>.
	 */
	void assignFrom(ISQLAlias rhs) throws ValidationException;

	/**
	 * Compare this <TT>ISQLAlias</TT> to another object. If the passed object
	 * is a <TT>ISQLAlias</TT>, then the <TT>getName()</TT> functions of the two
	 * <TT>ISQLAlias</TT> objects are used to compare them. Otherwise, it throws a
	 * ClassCastException (as <TT>ISQLAlias</TT> objects are comparable only to
	 * other <TT>ISQLAlias</TT> objects).
	 */
	int compareTo(Object rhs);

//  IIdentifier getIdentifier();

	String getName();

	IIdentifier getDriverIdentifier();

	String getUrl();

	String getUserName();

	void setName(String name) throws ValidationException;

	void setDriverIdentifier(IIdentifier data) throws ValidationException;

	void setUrl(String url) throws ValidationException;

	void setUserName(String userName) throws ValidationException;

	void addPropertyChangeListener(PropertyChangeListener listener);
	void removePropertyChangeListener(PropertyChangeListener listener);
}

