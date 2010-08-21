/*
 * Copyright (C) 2008 Michael Romankiewicz
 * microm at users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.IFirebirdManagerSessionPreferencesBean;

/**
 * Bean to save the global preferences  
 * @author Michael Romankiewicz
 */
public class FirebirdManagerPreferenceBean 
implements IFirebirdManagerSessionPreferencesBean, Cloneable, Serializable {
	private static final long serialVersionUID = -2450004977019415947L;
	
	private String databaseFolder = "";
	private String port = "3050";
	private String user = "SYSDBA";
	private String server = "localhost";
	private String propertiesFolder = "";

	/**
	 * Get the default server
	 * @return default server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Set the default server
	 * @param server default server
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * Get the default folder for database files
	 * @return database folder
	 */
	public String getDatabaseFolder() {
		return databaseFolder;
	}

	/**
	 * Set the default folder for database files
	 * @param databaseFolder database folder
	 */
	public void setDatabaseFolder(String databaseFolder) {
		this.databaseFolder = databaseFolder;
	}

	/**
	 * Get the default folder for properties files
	 * @return properties folder
	 */
	public String getPropertiesFolder() {
		return propertiesFolder;
	}

	/**
	 * Set the default folder for properties files
	 * @param databaseFolder properties folder
	 */
	public void setPropertiesFolder(String propertiesFolder) {
		this.propertiesFolder = propertiesFolder;
	}

	/**
	 * Get the default port of the firebird server
	 * @return firebird server port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Set the default port of the firebird server
	 * @param port firebird server port
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * Get the default database user for database creation
	 * @return database user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the default database user for database creation
	 * @param user database user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	
	/**
	 * Bean class for firebird manager global preferences
	 *
	 */
	public FirebirdManagerPreferenceBean() {
		super();
	}

	
	/**
	 * @see Cloneable
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage());
		}
	}
}

