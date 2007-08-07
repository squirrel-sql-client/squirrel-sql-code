package net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile;

/*
 * Copyright (C) 2004 Ryan Walberg <generalpf@yahoo.com>
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

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseFileInfo {
    
    private String _databaseName;
    private String _databaseSize;
    private String _owner;
    private short _compatibilityLevel;
    private String _createdDate;
    private HashMap<String, String> _options;
    private ArrayList<DatabaseFile> _dataFiles;
    private ArrayList<Object> _logFiles;
    
    /** Creates a new instance of DatabaseFileInfo */
    public DatabaseFileInfo() {
        _options = new HashMap<String, String>();
        _dataFiles = new ArrayList<DatabaseFile>();
        _logFiles = new ArrayList<Object>();
    }
    
    /**
     * Getter for property databaseSize.
     * @return Value of property databaseSize.
     */
    public String getDatabaseSize() {
        return this._databaseSize;
    }
    
    /**
     * Setter for property databaseSize.
     * @param databaseSize New value of property databaseSize.
     */
    public void setDatabaseSize(String databaseSize) {
        this._databaseSize = databaseSize;
    }
    
    /**
     * Getter for property owner.
     * @return Value of property owner.
     */
    public String getOwner() {
        return this._owner;
    }
    
    /**
     * Setter for property owner.
     * @param owner New value of property owner.
     */
    public void setOwner(String owner) {
        this._owner = owner;
    }
    
    /**
     * Getter for property compatibilityLevel.
     * @return Value of property compatibilityLevel.
     */
    public short getCompatibilityLevel() {
        return this._compatibilityLevel;
    }
    
    /**
     * Setter for property compatibilityLevel.
     * @param compatibilityLevel New value of property compatibilityLevel.
     */
    public void setCompatibilityLevel(short compatibilityLevel) {
        this._compatibilityLevel = compatibilityLevel;
    }
    
    /**
     * Getter for property createdDate.
     * @return Value of property createdDate.
     */
    public String getCreatedDate() {
        return this._createdDate;
    }
    
    /**
     * Setter for property createdDate.
     * @param createdDate New value of property createdDate.
     */
    public void setCreatedDate(String createdDate) {
        this._createdDate = createdDate;
    }
    
    public void setOption(String option, String value) {
        _options.put(option,value);
    }
    
    public String getOption(String option) {
        if (_options.containsKey(option))
            return _options.get(option);
        else
            return "";
    }
    
    /**
     * Getter for property databaseName.
     * @return Value of property databaseName.
     */
    public String getDatabaseName() {
        return this._databaseName;
    }
    
    /**
     * Setter for property databaseName.
     * @param databaseName New value of property databaseName.
     */
    public void setDatabaseName(String databaseName) {
        this._databaseName = databaseName;
    }
    
    public Object[] getLogFiles() {
        return this._logFiles.toArray();
    }
    
    public Object[] getDataFiles() {
        return this._dataFiles.toArray();
    }
    
    public void addLogFile(DatabaseFile file) {
        _logFiles.add(file);
    }
    
    public void addDataFile(DatabaseFile file) {
        // not so simple -- we want to keep the filegroups together.
        for (int i = 0; i < _dataFiles.size(); i++) {
            DatabaseFile f = _dataFiles.get(i);
            if (f.getFilegroup().equals(file.getFilegroup())) {
                // keep scanning until we're either EOL or find a different filegroup.
                for (int j = i + 1; j < _dataFiles.size(); j++) {
                    f = _dataFiles.get(i);
                    if (!f.getFilegroup().equals(file.getFilegroup())) {
                        // j is the index of the first file in the next filegroup.
                        _dataFiles.add(j,file);
                        return;
                    }
                }
            }
        }
        // if we're still in this function, just add it at the end.
        _dataFiles.add(file);
    }
}
