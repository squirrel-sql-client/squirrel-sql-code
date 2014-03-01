/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbdiff.prefs;

import java.io.Serializable;

/**
 * A bean class to store preferences for the DB Diff plugin.
 */
public class DBDiffPreferenceBean implements Cloneable, Serializable, IPluginPreferenceBean
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	static final String UNSUPPORTED = "Unsupported";

	/** Client Name. */
	private String _clientName;

	/** Client version. */
	private String _clientVersion;

	private boolean useTabularDiffPresenation;

	/** whether or not to use a local file to stream bytes for copying blobs */
	private boolean useExternalGraphicalDiffTool = false;

	/** The graphical tool command that launches the diff tool. It should handle two file arguments */
	private String graphicalToolCommand = "";
	
	/** Whether or not to sort columns by column name when generating table definitions */
	private boolean sortColumnsForSideBySideComparison = false; 

	public DBDiffPreferenceBean()
	{
		super();
	}

	/**
	 * Return a copy of this object.
	 */
	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (final CloneNotSupportedException ex)
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
	 * @return the useExternalGraphicalDiffTool
	 */
	public boolean isUseExternalGraphicalDiffTool()
	{
		return useExternalGraphicalDiffTool;
	}

	/**
	 * @param useExternalGraphicalDiffTool
	 *           the useExternalGraphicalDiffTool to set
	 */
	public void setUseExternalGraphicalDiffTool(boolean useExternalGraphicalDiffTool)
	{
		this.useExternalGraphicalDiffTool = useExternalGraphicalDiffTool;
	}

	/**
	 * @return the graphicalToolCommand
	 */
	public String getGraphicalToolCommand()
	{
		return graphicalToolCommand;
	}

	/**
	 * @param graphicalToolCommand
	 *           the graphicalToolCommand to set
	 */
	public void setGraphicalToolCommand(String graphicalToolCommand)
	{
		this.graphicalToolCommand = graphicalToolCommand;
	}

	/**
	 * @param useTabularDiffPresenation
	 *           the useTabularDiffPresenation to set
	 */
	public void setUseTabularDiffPresenation(boolean useTabularDiffPresenation)
	{
		this.useTabularDiffPresenation = useTabularDiffPresenation;
	}

	/**
	 * @return the useTabularDiffPresenation
	 */
	public boolean isUseTabularDiffPresenation()
	{
		return useTabularDiffPresenation;
	}

	/**
	 * @return the sortColumnsForSideBySideComparison
	 */
	public boolean isSortColumnsForSideBySideComparison()
	{
		return sortColumnsForSideBySideComparison;
	}

	/**
	 * @param sortColumnsForSideBySideComparison the sortColumnsForSideBySideComparison to set
	 */
	public void setSortColumnsForSideBySideComparison(boolean sortColumnsForSideBySideComparison)
	{
		this.sortColumnsForSideBySideComparison = sortColumnsForSideBySideComparison;
	}

}
