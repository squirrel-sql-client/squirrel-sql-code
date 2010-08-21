package net.sourceforge.squirrel_sql.plugins.refactoring.prefs;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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

/**
 * A bean class to store preferences for the Refactoring plugin.
 */
public class RefactoringPreferenceBean implements Cloneable, Serializable
{
	private static final long serialVersionUID = 6377157814922907413L;

	static final String UNSUPPORTED = "Unsupported";

	/** Client Name. */
	private String _clientName;

	/** Client version. */
	private String _clientVersion;

	/**
	 * whether or not to qualify table names with the schema when generating scripts
	 */
	private boolean _qualifyTableNames = true;

	/**
	 * whether or not to quote identifiers when generating scripts
	 */
	private boolean _quoteIdentifiers = true;
	
	public RefactoringPreferenceBean()
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
		} catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	/**
	 * Retrieve the client to use. This is only used if <TT>useAnonymousClient</TT> is false.
	 * 
	 * @return Client name.
	 */
	public String getClientName()
	{
		return _clientName;
	}

	/**
	 * Set the client name.
	 * 
	 * @param value
	 *           Client name
	 */
	public void setClientName(String value)
	{
		_clientName = value;
	}

	/**
	 * Retrieve the client version to use. This is only used if <TT>useAnonymousLogon</TT> is false.
	 * 
	 * @return Client version.
	 */
	public String getClientVersion()
	{
		return _clientVersion;
	}

	/**
	 * Set the client version.
	 * 
	 * @param value
	 *           Client version
	 */
	public void setClientVersion(String value)
	{
		_clientVersion = value;
	}

	/**
	 * Sets whether or not to qualify table names with the schema when generating scripts
	 * 
	 * @param qualifyTableNames
	 *           a boolean value
	 */
	public void setQualifyTableNames(boolean qualifyTableNames)
	{
		this._qualifyTableNames = qualifyTableNames;
	}

	/**
	 * Returns a boolean value indicating whether or not to qualify table names with the schema when generating
	 * scripts
	 * 
	 * @return Returns the value of qualifyTableNames.
	 */
	public boolean isQualifyTableNames()
	{
		return _qualifyTableNames;
	}

	/**
	 * @return the quoteIdentifiers
	 */
	public boolean isQuoteIdentifiers()
	{
		return _quoteIdentifiers;
	}

	/**
	 * @param quoteIdentifiers the quoteIdentifiers to set
	 */
	public void setQuoteIdentifiers(boolean quoteIdentifiers)
	{
		this._quoteIdentifiers = quoteIdentifiers;
	}

}
