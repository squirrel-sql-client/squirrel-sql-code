package net.sourceforge.squirrel_sql.plugins.sqlscript.prefs;
/*
 * Copyright (C) 2006 Rob Manning
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
 * A bean class to store preferences for the SQLScript plugin.
 */
public class SQLScriptPreferenceBean implements Cloneable, 
                                             Serializable {
	static final String UNSUPPORTED = "Unsupported";

    /** Client Name. */
	private String _clientName;

	/** Client version. */
	private String _clientVersion;

    /** whether or not to qualify table names with the schema when generating 
     *  scripts*/
    private boolean qualifyTableNames = true;
    private boolean useDoubleQuotes = true;



    public static final int NO_ACTION = 0;
    
    public static final int CASCADE_DELETE = 1;
    
    public static final int SET_DEFAULT = 2;
    
    public static final int SET_NULL = 3;
    
    private int deleteAction = NO_ACTION;
    
    private int updateAction = NO_ACTION;
    
    /**
     * whether or not to override the delete referential action for FK defs.
     */
    private boolean deleteRefAction = false;
    
    /**
     * whether or not to override the update referential action for FK defs.
     */
    private boolean updateRefAction = false;

	public SQLScriptPreferenceBean() {
		super();
	}

	/**
	 * Return a copy of this object.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	/**
	 * Retrieve the client to use. This is only
	 * used if <TT>useAnonymousClient</TT> is false.
	 *
	 * @return	Client name.
	 */
	public String getClientName() {
		return _clientName;
	}

	/**
	 * Set the client name.
	 *
	 * @param	value	Client name
	 */
	public void setClientName(String value) {
		_clientName = value;
	}

	/**
	 * Retrieve the client version to use. This is only
	 * used if <TT>useAnonymousLogon</TT> is false.
	 *
	 * @return	Client version.
	 */
	public String getClientVersion() {
		return _clientVersion;
	}

	/**
	 * Set the client version.
	 *
	 * @param	value	Client version
	 */
	public void setClientVersion(String value) {
		_clientVersion = value;
	}

    /**
     * Sets whether or not to qualify table names with the schema when 
     * generating scripts
     * 
     * @param qualifyTableNames a boolean value
     */
    public void setQualifyTableNames(boolean qualifyTableNames) {
        this.qualifyTableNames = qualifyTableNames;
    }

    /**
     * Returns a boolean value indicating whether or not to qualify table names 
     * with the schema when generating scripts
     * 
     * @return Returns the value of qualifyTableNames.
     */
    public boolean isQualifyTableNames() {
        return qualifyTableNames;
    }

    public void setDeleteRefAction(boolean deleteRefAction) {
        this.deleteRefAction = deleteRefAction;
    }

    public boolean isDeleteRefAction() {
        return deleteRefAction;
    }

    public void setDeleteAction(int action) {
        this.deleteAction = action;
    }

    public int getDeleteAction() {
        return deleteAction;
    }

    public void setUpdateAction(int updateAction) {
        this.updateAction = updateAction;
    }

    public int getUpdateAction() {
        return updateAction;
    }

    public void setUpdateRefAction(boolean updateRefAction) {
        this.updateRefAction = updateRefAction;
    }

    public boolean isUpdateRefAction() {
        return updateRefAction;
    }

    public String getRefActionByType(int type) {
        switch (type) {
            case NO_ACTION:
                return "NO ACTION";
            case CASCADE_DELETE:
                return "CASCADE";
            case SET_DEFAULT:
                return "SET DEFAULT";
            case SET_NULL:
                return "SET NULL";
            default:
                return "NO ACTION";
        }
    }

   public boolean isUseDoubleQuotes()
   {
      return useDoubleQuotes;
   }

   public void setUseDoubleQuotes(boolean b)
   {
      useDoubleQuotes = b;
   }


}

