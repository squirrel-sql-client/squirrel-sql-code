/*
 * Copyright (C) 2008 Michael Romankiewicz
 * mirommail(at)web.de
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
 * Bean to save session preferences of the backup and restore frame 
 * @author Michael Romankiewicz
 */
public class FirebirdManagerBackupAndRestorePreferenceBean 
implements IFirebirdManagerSessionPreferencesBean, Cloneable, Serializable {
	private static final long serialVersionUID = 5308526497399393529L;

	private String port = "";
	private String user = "";
	private String server = "";
	private String bckDatabaseFilename = "";
	private String bckBackupFilename = "";
	private boolean displayProcess = true;

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
	 * Get the default database user 
	 * @return database user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the default database user 
	 * @param user database user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get the default backup database filename
	 * @return default database filename
	 */
	public String getBckDatabaseFilename() {
		return bckDatabaseFilename;
	}

	/**
	 * Set the default database filename
	 * @param default database filename
	 */
	public void setBckDatabaseFilename(String bckDatabaseFilename) {
		this.bckDatabaseFilename = bckDatabaseFilename;
	}

	/**
	 * Get the default backup filename
	 * @return default backup filename
	 */
	public String getBckBackupFilename() {
		return bckBackupFilename;
	}

	/**
	 * Set the default backup filename
	 * @param default backup filename
	 */
	public void setBckBackupFilename(String bckBackupFilename) {
		this.bckBackupFilename = bckBackupFilename;
	}

	/**
	 * Get the display process selection
	 * @return display process selection
	 */
	public boolean isDisplayProcess() {
		return displayProcess;
	}

	/**
	 * Set the display process selection
	 * @param displayProcess display process selection
	 */
	public void setDisplayProcess(boolean displayProcess) {
		this.displayProcess = displayProcess;
	}

	

	/**
	 * Bean class for firebird manager global preferences
	 */
	public FirebirdManagerBackupAndRestorePreferenceBean() {
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

