package net.sourceforge.squirrel_sql.plugins.sqlval;
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
import java.io.Serializable;

public class WebServicePreferences implements Cloneable, Serializable
{
	static final String UNSUPPORTED = "Unsupported";

	/**
	 * If <TT>true</TT> then show confirmation dialog whenever validation
	 * requested.
	 */
//	private boolean _showConfirmationDialog = true;

	/** If <TT>true</TT> use anonymous logon. */
	private boolean _useAnonymousLogon = true;

	/** User Name to use to logon to web service. */
	private String _userName = "";

	/** Password to use to logon to web service. */
	private String _password = "";

	/** If <TT>true</TT> use anonymous client. */
	private boolean _useAnonymousClient = false;

	/** Client Name. */
	private String _clientName;

	/** Client version. */
	private String _clientVersion;

	public WebServicePreferences()
	{
		super();
	}

	/**
	 * Return a copy of this object.
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	/**
	 * If <TT>true</TT> then show confirmation dialog whenever validation
	 * requested.
	 */
//	public boolean getShowConfirmationDialog()
//	{
//		return _showConfirmationDialog;
//	}

	/**
	 * Specify whether to show confirmation dialog.
	 *
	 * @param	value	<TT>true</TT> if confirmation dialog is to be shown.
	 */
//	public void setShowConfirmationDialog(boolean value)
//	{
//		_showConfirmationDialog = value;
//	}

	/**
	 * If <TT>true</TT> use anonymous logon to the web service.
	 *
	 * @return	<TT>true</TT> if anonymous logon to be used.
	 */
	public boolean getUseAnonymousLogon()
	{
		return _useAnonymousLogon;
	}

	/**
	 * Specify whether to use anonymous logon.
	 *
	 * @param	value	<TT>true</TT> if anonymous logon to be used.
	 */
	public void setUseAnonymousLogon(boolean value)
	{
		_useAnonymousLogon = value;
	}

	/**
	 * Retrieve the name to use to logon to the web service. This is only
	 * used if <TT>useAnonymousLogon</TT> is false.
	 *
	 * @return	User name.
	 */
	public String getUserName()
	{
		return _userName;
	}

	/**
	 * Set the user name for logging on to the web service.
	 *
	 * @param	value	User name
	 */
	public void setUserName(String value)
	{
		_userName = value;
	}

	/**
	 * Retrieve the password to use to logon to the web service. This is only
	 * used if <TT>useAnonymousLogon</TT> is false. Deliberately not a JavaBean
	 * method so that unencrypted passwords will not be stored to disk.
	 *
	 * @return	Password.
	 */
	public String retrievePassword()
	{
		return _password;
	}

	/**
	 * Set the password name for logging on to the web service.
	 *
	 * @param	value	Password
	 */
	public void setPassword(String value)
	{
		_password = value;
	}

	/**
	 * If <TT>true</TT> use anonymous client.
	 *
	 * @return	<TT>true</TT> if anonymous client to be used.
	 */
	public boolean getUseAnonymousClient()
	{
		return _useAnonymousClient;
	}

	/**
	 * Specify whether to use anonymous client.
	 *
	 * @param	value	<TT>true</TT> if anonymous client to be used.
	 */
	public void setUseAnonymousClient(boolean value)
	{
		_useAnonymousClient = value;
	}

	/**
	 * Retrieve the client to use. This is only
	 * used if <TT>useAnonymousClient</TT> is false.
	 *
	 * @return	Client name.
	 */
	public String getClientName()
	{
		return _clientName;
	}

	/**
	 * Set the client name.
	 *
	 * @param	value	Client name
	 */
	public void setClientName(String value)
	{
		_clientName = value;
	}

	/**
	 * Retrieve the client version to use. This is only
	 * used if <TT>useAnonymousLogon</TT> is false.
	 *
	 * @return	Client version.
	 */
	public String getClientVersion()
	{
		return _clientVersion;
	}

	/**
	 * Set the client version.
	 *
	 * @param	value	Client version
	 */
	public void setClientVersion(String value)
	{
		_clientVersion = value;
	}
}
