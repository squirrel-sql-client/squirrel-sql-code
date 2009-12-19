package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.IValidatable;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
/**
 * This represents a Database alias which is a description of the means
 * required to connect to a JDBC complient database.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ISQLAlias extends IHasIdentifier, IValidatable
{
	/**
	 * JavaBean property names for this class.
	 */
	public interface IPropertyNames
	{
		String AUTO_LOGON = "autoLogon";
		String CONNECT_AT_STARTUP = "connectAtStartup";
		String DRIVER = "driverIdentifier";
		String DRIVER_PROPERTIES = "driverProperties";
		String ID = "identifier";
		String NAME = "name";
		String PASSWORD = "password";
		String URL = "url";
		String USE_DRIVER_PROPERTIES = "useDriverProperties";
		String USER_NAME = "userName";
		String SCHEMA_PROPERTIES = "schemaProperties";
		String COLOR_PROPERTIES = "colorProperties";
		String CONNECTION_PROPERTIES = "connectionProperties";
	}

	/**
	 * Compare this <TT>ISQLAlias</TT> to another object. If the passed object
	 * is a <TT>ISQLAlias</TT>, then the <TT>getName()</TT> functions of the two
	 * <TT>ISQLAlias</TT> objects are used to compare them. Otherwise, it throws
	 * a ClassCastException (as <TT>ISQLAlias</TT> objects are comparable only
	 * to other <TT>ISQLAlias</TT> objects).
	 */
	int compareTo(Object rhs);

	String getName();
	void setName(String name) throws ValidationException;

	IIdentifier getDriverIdentifier();
	void setDriverIdentifier(IIdentifier data) throws ValidationException;

	String getUrl();
	void setUrl(String url) throws ValidationException;

	String getUserName();
	void setUserName(String userName) throws ValidationException;

	/**
	 * Retrieve the saved password.
	 *
	 * @return	The saved password.
	 */
	String getPassword();

	/**
	 * Set the password for this alias.
	 *
	 * @param	password	The new password.
	 *
	 * @throws	ValidationException
	 * 			TODO: What conditions?
	 */
	void setPassword(String password) throws ValidationException;

	/**
	 * Should this alias be logged on automatically.
	 *
	 * @return	<TT>true</TT> if this alias should be logged on automatically
	 * 			else <TT>false</TT>.
	 */
	boolean isAutoLogon();

	/**
	 * Set whether this alias should be logged on automatically.
	 *
	 * @param	value	<TT>true</TT> if alias should be autologged on
	 * 					else <TT>false</TT>.
	 */
	void setAutoLogon(boolean value);

	/**
	 * Should this alias be connected when the application is started up.
	 *
	 * @return	<TT>true</TT> if this alias should be connected when the
	 *			application is started up.
	 */
	boolean isConnectAtStartup();

	/**
	 * Set whether alias should be connected when the application is started up.
	 *
	 * @param	value	<TT>true</TT> if alias should be connected when the
	 *					application is started up.
	 */
	void setConnectAtStartup(boolean value);

	boolean getUseDriverProperties();
	void setUseDriverProperties(boolean value);

	SQLDriverPropertyCollection getDriverPropertiesClone();
	void setDriverProperties(SQLDriverPropertyCollection value);

	void addPropertyChangeListener(PropertyChangeListener listener);
	void removePropertyChangeListener(PropertyChangeListener listener);
}

