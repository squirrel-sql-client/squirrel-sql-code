package net.sourceforge.squirrel_sql.plugins.dbcopy.prefs;
/*
 * Copyright (C) 2005 Rob Manning
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
 * A bean class to store preferences for the DB Copy plugin.
 */
public class DBCopyPreferenceBean implements Cloneable, 
                                             Serializable {
	static final String UNSUPPORTED = "Unsupported";

    /** Client Name. */
	private String _clientName;

	/** Client version. */
	private String _clientVersion;

    /** whether or not to use a local file to stream bytes for copying blobs */
    private boolean useFileCaching = true;
    
    /** How many bytes to read/write at a time from memory to disk and back */
    private int fileCacheBufferSize = 8192;
    
    /** Use the truncate command instead of delete if the database supports it */
    private boolean useTruncate = true;
        
    /** whether or not commit after each statement on the destination connection */
    private boolean autoCommitEnabled = true;
    
    /** How many statements to issue before committing when auto-commit is off */
    private int commitCount = 100;
    
    /** whether or not to write each SQL executed to a script file */
    private boolean writeScript = false;
    
    /** whether or not to copy the records from the source to dest table */
    private boolean copyData = true;
    
    /** whether or not to copy the index definitions */
    private boolean copyIndexDefs = true;
    
    /** whether or not to copy the foreign keys */
    private boolean copyForeignKeys = true;
    
    /** whether or not to copy the primary keys */
    private boolean copyPrimaryKeys = false;
    
    /** whether or not to discard logically identical index defs */
    private boolean pruneDuplicateIndexDefs = true;
    
    /** whether or not to commit after issuing a create table statement */
    private boolean commitAfterTableDefs = true;
    
    /** whether to prompt the user for dest db dialect, or try to auto-detect */ 
    private boolean promptForDialect = false;
    
    /** whether or not to check column names in source db for keywords in dest */
    private boolean checkKeywords = true;
    
    /** whether or not to test column names in the destination database */
    private boolean testColumnNames = true;
        
    /** default number of records to retrieve from the database in selects */
    private int selectFetchSize = 1000;
    
    /** default for whether or not to delay between copying objects */
    private boolean delayBetweenObjects = false;
    
    /** default number of milliseconds to wait between copying tables */
    private long tableDelayMillis = 0;
    
    /** default number of milliseconds to wait between copying records */
    private long recordDelayMillis = 0;

    private boolean _appendRecordsToExisting;

   public DBCopyPreferenceBean() {
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
     * Sets whether or not to use a local file to stream bytes for copying blobs
     * @param useFileCaching The useFileCaching to set.
     */
    public void setUseFileCaching(boolean useFileCaching) {
        this.useFileCaching = useFileCaching;
    }

    /**
     * Returns a boolean value indicating whether or not to use a local file 
     * to stream bytes for copying blobs
     * 
     * @return Returns the useFileCaching.
     */
    public boolean isUseFileCaching() {
        return useFileCaching;
    }

    /**
     * @param fileCacheBufferSize The fileCacheBufferSize to set.
     */
    public void setFileCacheBufferSize(int fileCacheBufferSize) {
        this.fileCacheBufferSize = fileCacheBufferSize;
    }

    /**
     * @return Returns the fileCacheBufferSize.
     */
    public int getFileCacheBufferSize() {
        return fileCacheBufferSize;
    }

    /**
     * @param useTruncate The useTruncate to set.
     */
    public void setUseTruncate(boolean useTruncate) {
        this.useTruncate = useTruncate;
    }

    /**
     * @return Returns the useTruncate.
     */
    public boolean isUseTruncate() {
        return useTruncate;
    }

    /**
     * @param autoCommitEnabled The autoCommitEnabled to set.
     */
    public void setAutoCommitEnabled(boolean autoCommitEnabled) {
        this.autoCommitEnabled = autoCommitEnabled;
    }

    /**
     * @return Returns the autoCommitEnabled.
     */
    public boolean isAutoCommitEnabled() {
        return autoCommitEnabled;
    }

    /**
     * @param commitCount The commitCount to set.
     */
    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    /**
     * @return Returns the commitCount.
     */
    public int getCommitCount() {
        return commitCount;
    }

    /**
     * @param writeScript The writeScript to set.
     */
    public void setWriteScript(boolean writeScript) {
        this.writeScript = writeScript;
    }

    /**
     * @return Returns the writeScript.
     */
    public boolean isWriteScript() {
        return writeScript;
    }

    /**
     * @param copyData The copyData to set.
     */
    public void setCopyData(boolean copyData) {
        this.copyData = copyData;
    }

    /**
     * @return Returns the copyData.
     */
    public boolean isCopyData() {
        return copyData;
    }

    /**
     * @param copyIndexDefs The copyIndexDefs to set.
     */
    public void setCopyIndexDefs(boolean copyIndexDefs) {
        this.copyIndexDefs = copyIndexDefs;
    }

    /**
     * @return Returns the copyIndexDefs.
     */
    public boolean isCopyIndexDefs() {
        return copyIndexDefs;
    }

    /**
     * @param copyForeignKeys The copyForeignKeys to set.
     */
    public void setCopyForeignKeys(boolean copyForeignKeys) {
        this.copyForeignKeys = copyForeignKeys;
    }

    /**
     * @return Returns the copyForeignKeys.
     */
    public boolean isCopyForeignKeys() {
        return copyForeignKeys;
    }

    /**
     * @param pruneDuplicateIndexDefs The pruneDuplicateIndexDefs to set.
     */
    public void setPruneDuplicateIndexDefs(boolean pruneDuplicateIndexDefs) {
        this.pruneDuplicateIndexDefs = pruneDuplicateIndexDefs;
    }

    /**
     * @return Returns the pruneDuplicateIndexDefs.
     */
    public boolean isPruneDuplicateIndexDefs() {
        return pruneDuplicateIndexDefs;
    }

    /**
     * @param commitAfterTableDefs The commitAfterTableDefs to set.
     */
    public void setCommitAfterTableDefs(boolean commitAfterTableDefs) {
        this.commitAfterTableDefs = commitAfterTableDefs;
    }

    /**
     * @return Returns the commitAfterTableDefs.
     */
    public boolean isCommitAfterTableDefs() {
        return commitAfterTableDefs;
    }

    /**
     * @param promptForDialect The promptForDialect to set.
     */
    public void setPromptForDialect(boolean promptForDialect) {
        this.promptForDialect = promptForDialect;
    }

    /**
     * @return Returns the promptForDialect.
     */
    public boolean isPromptForDialect() {
        return promptForDialect;
    }

    /**
     * @param checkKeywords The checkKeywords to set.
     */
    public void setCheckKeywords(boolean checkKeywords) {
        this.checkKeywords = checkKeywords;
    }

    /**
     * @return Returns the checkKeywords.
     */
    public boolean isCheckKeywords() {
        return checkKeywords;
    }

    /**
     * @param testColumnNames The testColumnNames to set.
     */
    public void setTestColumnNames(boolean testColumnNames) {
        this.testColumnNames = testColumnNames;
    }

    /**
     * @return Returns the testColumnNames.
     */
    public boolean isTestColumnNames() {
        return testColumnNames;
    }

    /**
     * @param copyPrimaryKeys The copyPrimaryKeys to set.
     */
    public void setCopyPrimaryKeys(boolean copyPrimaryKeys) {
        this.copyPrimaryKeys = copyPrimaryKeys;
    }

    /**
     * @return Returns the copyPrimaryKeys.
     */
    public boolean isCopyPrimaryKeys() {
        return copyPrimaryKeys;
    }

    /**
     * @param selectFetchSize The selectFetchSize to set.
     */
    public void setSelectFetchSize(int selectFetchSize) {
        this.selectFetchSize = selectFetchSize;
    }

    /**
     * @return Returns the selectFetchSize.
     */
    public int getSelectFetchSize() {
        return selectFetchSize;
    }

    /**
     * @param tableDelayMillis The tableDelayMillis to set.
     */
    public void setTableDelayMillis(long tableDelayMillis) {
        this.tableDelayMillis = tableDelayMillis;
    }

    /**
     * @return Returns the tableDelayMillis.
     */
    public long getTableDelayMillis() {
        return tableDelayMillis;
    }

    /**
     * @param recordDelayMillis The recordDelayMillis to set.
     */
    public void setRecordDelayMillis(long recordDelayMillis) {
        this.recordDelayMillis = recordDelayMillis;
    }

    /**
     * @return Returns the recordDelayMillis.
     */
    public long getRecordDelayMillis() {
        return recordDelayMillis;
    }

    /**
     * @param delayBetweenObjects The delayBetweenObjects to set.
     */
    public void setDelayBetweenObjects(boolean delayBetweenObjects) {
        this.delayBetweenObjects = delayBetweenObjects;
    }

    /**
     * @return Returns the delayBetweenObjects.
     */
    public boolean isDelayBetweenObjects() {
        return delayBetweenObjects;
    }

   public boolean isAppendRecordsToExisting()
   {
      return _appendRecordsToExisting;
   }

   public void setAppendRecordsToExisting(boolean appendRecordsToExisting)
   {
      _appendRecordsToExisting = appendRecordsToExisting;
   }
}

