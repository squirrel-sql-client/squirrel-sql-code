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
                                             DriverClassNames, 
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
    private boolean copyPrimaryKeys = true;
    
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
    
    /** default package for the Axion driver */
    private String axionDriverClass = AXION_CLASS_NAME;    
    
    /** default package for the Daffodil driver */
    private String daffodilDriverClass = DAFFODIL_CLASS_NAME;
    
    /** default package for the IBM DB2 driver */
    private String db2DriverClass = DB2_CLASS_NAME;
        
    /** default package for the Derby driver */
    private String derbyDriverClass = DERBY_CLASS_NAME;
    
    /** default package for the Firebird driver */
    private String firebirdDriverClass = FIREBIRD_CLASS_NAME;
    
    /** default package for the FrontBase driver */
    private String frontbaseDriverClass = FRONTBASE_CLASS_NAME;
    
    /** default package for the H2 Driver */
    private String h2DriverClass = H2_CLASS_NAME;
    
    /** default package for the HyperSonic driver */
    private String hypersonicDriverClass = HSQL_CLASS_NAME;
    
    /** default package for the Ingres driver */
    private String ingresDriverClass = INGRES_CLASS_NAME;

    /** default package for the Ingres driver */
    private String maxDbDriverClass = MAXDB_CLASS_NAME;
    
    /** default package for the McKoi driver */
    private String mckoiDriverClass = MCKOI_CLASS_NAME;
    
    /** default package for the MySQL driver */
    private String mysqlDriverClass = MYSQL_CLASS_NAME;
    
    /** default package for the Oracle driver */
    private String oracleDriverClass = ORACLE_CLASS_NAME;
    
    /** default package for the Pointbase driver */
    private String pointbaseDriverClass = POINTBASE_CLASS_NAME;
    
    /** default package for the PostgreSQL driver */
    private String postgresqlDriverClass = POSTGRES_CLASS_NAME;
    
    /** default packages for the Progress driver */
    private String progressDriverClass = PROGRESS_CLASS_NAME;    
    
    /** default packages for the Microsoft SQL-Server driver */
    private String mssqlserverDriverClass = MSSQL_CLASS_NAME;
    
    /** default package for the Sybase driver */
    private String sybaseDriverClass = SYBASE_CLASS_NAME;
    
    
    /** default number of records to retrieve from the database in selects */
    private int selectFetchSize = 1000;
    
    /** default for whether or not to delay between copying objects */
    private boolean delayBetweenObjects = false;
    
    /** default number of milliseconds to wait between copying tables */
    private long tableDelayMillis = 0;
    
    /** default number of milliseconds to wait between copying records */
    private long recordDelayMillis = 0;
    
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

    public void resetClassNames() {
        axionDriverClass = AXION_CLASS_NAME;
        daffodilDriverClass = DAFFODIL_CLASS_NAME;
        db2DriverClass = DB2_CLASS_NAME;
        derbyDriverClass = DERBY_CLASS_NAME;
        firebirdDriverClass = FIREBIRD_CLASS_NAME;
        frontbaseDriverClass = FRONTBASE_CLASS_NAME;
        h2DriverClass = H2_CLASS_NAME;
        hypersonicDriverClass = HSQL_CLASS_NAME;
        ingresDriverClass = INGRES_CLASS_NAME;
        maxDbDriverClass = MAXDB_CLASS_NAME;
        mckoiDriverClass = MCKOI_CLASS_NAME;
        mysqlDriverClass = MYSQL_CLASS_NAME;
        oracleDriverClass = ORACLE_CLASS_NAME;
        pointbaseDriverClass = POINTBASE_CLASS_NAME;
        postgresqlDriverClass = POSTGRES_CLASS_NAME;
        progressDriverClass = PROGRESS_CLASS_NAME;    
        mssqlserverDriverClass = MSSQL_CLASS_NAME;
        sybaseDriverClass = SYBASE_CLASS_NAME;
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
     * @param db2DriverClass The db2DriverClass to set.
     */
    public void setDb2DriverClass(String db2DriverClass) {
        this.db2DriverClass = db2DriverClass;
    }

    /**
     * @return Returns the db2DriverClass.
     */
    public String getDb2DriverClass() {
        return db2DriverClass;
    }

    /**
     * @param derbyDriverClass The derbyDriverClass to set.
     */
    public void setDerbyDriverClass(String derbyDriverClass) {
        this.derbyDriverClass = derbyDriverClass;
    }

    /**
     * @return Returns the derbyDriverClass.
     */
    public String getDerbyDriverClass() {
        return derbyDriverClass;
    }

    /**
     * @param mssqlserverDriverClass The mssqlserverDriverClass to set.
     */
    public void setMssqlserverDriverClass(String mssqlserverDriverClass) {
        this.mssqlserverDriverClass = mssqlserverDriverClass;
    }

    /**
     * @return Returns the mssqlserverDriverClass.
     */
    public String getMssqlserverDriverClass() {
        return mssqlserverDriverClass;
    }

    /**
     * @param firebirdDriverClass The firebirdDriverClass to set.
     */
    public void setFirebirdDriverClass(String firebirdDriverClass) {
        this.firebirdDriverClass = firebirdDriverClass;
    }

    /**
     * @return Returns the firebirdDriverClass.
     */
    public String getFirebirdDriverClass() {
        return firebirdDriverClass;
    }

    /**
     * @param frontbaseDriverClass The frontbaseDriverClass to set.
     */
    public void setFrontbaseDriverClass(String frontbaseDriverClass) {
        this.frontbaseDriverClass = frontbaseDriverClass;
    }

    /**
     * @return Returns the frontbaseDriverClass.
     */
    public String getFrontbaseDriverClass() {
        return frontbaseDriverClass;
    }

    /**
     * @param hypersonicDriverClass The hypersonicDriverClass to set.
     */
    public void setHypersonicDriverClass(String hypersonicDriverClass) {
        this.hypersonicDriverClass = hypersonicDriverClass;
    }

    /**
     * @return Returns the hypersonicDriverClass.
     */
    public String getHypersonicDriverClass() {
        return hypersonicDriverClass;
    }

    /**
     * @param pointbaseDriverClass The pointbaseDriverClass to set.
     */
    public void setPointbaseDriverClass(String pointbaseDriverClass) {
        this.pointbaseDriverClass = pointbaseDriverClass;
    }

    /**
     * @return Returns the pointbaseDriverClass.
     */
    public String getPointbaseDriverClass() {
        return pointbaseDriverClass;
    }

    /**
     * @param postgresqlDriverClass The postgresqlDriverClass to set.
     */
    public void setPostgresqlDriverClass(String postgresqlDriverClass) {
        this.postgresqlDriverClass = postgresqlDriverClass;
    }

    /**
     * @return Returns the postgresqlDriverClass.
     */
    public String getPostgresqlDriverClass() {
        return postgresqlDriverClass;
    }

    /**
     * @param oracleDriverClass The oracleDriverClass to set.
     */
    public void setOracleDriverClass(String oracleDriverClass) {
        this.oracleDriverClass = oracleDriverClass;
    }

    /**
     * @return Returns the oracleDriverClass.
     */
    public String getOracleDriverClass() {
        return oracleDriverClass;
    }

    /**
     * @param sybaseDriverClass The sybaseDriverClass to set.
     */
    public void setSybaseDriverClass(String sybaseDriverClass) {
        this.sybaseDriverClass = sybaseDriverClass;
    }

    /**
     * @return Returns the sybaseDriverClass.
     */
    public String getSybaseDriverClass() {
        return sybaseDriverClass;
    }

    /**
     * @param mysqlDriverClass The mysqlDriverClass to set.
     */
    public void setMysqlDriverClass(String mysqlDriverClass) {
        this.mysqlDriverClass = mysqlDriverClass;
    }

    /**
     * @return Returns the mysqlDriverClass.
     */
    public String getMysqlDriverClass() {
        return mysqlDriverClass;
    }

    /**
     * @param mckoiDriverClass The mckoiDriverClass to set.
     */
    public void setMckoiDriverClass(String mckoiDriverClass) {
        this.mckoiDriverClass = mckoiDriverClass;
    }

    /**
     * @return Returns the mckoiDriverClass.
     */
    public String getMckoiDriverClass() {
        return mckoiDriverClass;
    }

    /**
     * @param axionDriverClass The axionDriverClass to set.
     */
    public void setAxionDriverClass(String axionDriverClass) {
        this.axionDriverClass = axionDriverClass;
    }

    /**
     * @return Returns the axionDriverClass.
     */
    public String getAxionDriverClass() {
        return axionDriverClass;
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
     * @param progressDriverClass The progressDriverClass to set.
     */
    public void setProgressDriverClass(String progressDriverClass) {
        this.progressDriverClass = progressDriverClass;
    }

    /**
     * @return Returns the progressDriverClass.
     */
    public String getProgressDriverClass() {
        return progressDriverClass;
    }

    /**
     * @param ingresDriverClass The ingresDriverClass to set.
     */
    public void setIngresDriverClass(String ingresDriverClass) {
        this.ingresDriverClass = ingresDriverClass;
    }

    /**
     * @return Returns the ingresDriverClass.
     */
    public String getIngresDriverClass() {
        return ingresDriverClass;
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

    /**
     * @param daffodilDriverClass The daffodilDriverClass to set.
     */
    public void setDaffodilDriverClass(String daffodilDriverClass) {
        this.daffodilDriverClass = daffodilDriverClass;
    }

    /**
     * @return Returns the daffodilDriverClass.
     */
    public String getDaffodilDriverClass() {
        return daffodilDriverClass;
    }

    /**
     * @param h2DriverClass The h2DriverClass to set.
     */
    public void setH2DriverClass(String h2DriverClass) {
        this.h2DriverClass = h2DriverClass;
    }

    /**
     * @return Returns the h2DriverClass.
     */
    public String getH2DriverClass() {
        return h2DriverClass;
    }

    /**
     * @param maxDbDriverClass The maxDbDriverClass to set.
     */
    public void setMaxDbDriverClass(String maxDbDriverClass) {
        this.maxDbDriverClass = maxDbDriverClass;
    }

    /**
     * @return Returns the maxDbDriverClass.
     */
    public String getMaxDbDriverClass() {
        return maxDbDriverClass;
    }
 
}

