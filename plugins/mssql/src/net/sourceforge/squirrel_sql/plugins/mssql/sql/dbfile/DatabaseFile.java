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

public class DatabaseFile {
    
    private String _name;
    private short _id;
    private String _filename;
    private String _filegroup;
    private String _size;
    private String _maxSize;
    private String _growth;
    private String _usage;
    
    /** Creates a new instance of DatabaseFile */
    public DatabaseFile() {
    }
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this._name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this._name = name;
    }
    
    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public short getId() {
        return this._id;
    }
    
    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(short id) {
        this._id = id;
    }
    
    /**
     * Getter for property filename.
     * @return Value of property filename.
     */
    public String getFilename() {
        return this._filename;
    }
    
    /**
     * Setter for property filename.
     * @param filename New value of property filename.
     */
    public void setFilename(String filename) {
        this._filename = filename;
    }
    
    /**
     * Getter for property filegroup.
     * @return Value of property filegroup.
     */
    public String getFilegroup() {
        return this._filegroup;
    }
    
    /**
     * Setter for property filegroup.
     * @param filegroup New value of property filegroup.
     */
    public void setFilegroup(String filegroup) {
        this._filegroup = filegroup;
    }
    
    /**
     * Getter for property size.
     * @return Value of property size.
     */
    public String getSize() {
        return this._size;
    }
    
    /**
     * Setter for property size.
     * @param size New value of property size.
     */
    public void setSize(String size) {
        this._size = size;
    }
    
    /**
     * Getter for property maxSize.
     * @return Value of property maxSize.
     */
    public String getMaxSize() {
        return this._maxSize;
    }
    
    /**
     * Setter for property maxSize.
     * @param maxSize New value of property maxSize.
     */
    public void setMaxSize(String maxSize) {
        this._maxSize = maxSize;
    }
    
    /**
     * Getter for property growth.
     * @return Value of property growth.
     */
    public String getGrowth() {
        return this._growth;
    }
    
    /**
     * Setter for property growth.
     * @param growth New value of property growth.
     */
    public void setGrowth(String growth) {
        this._growth = growth;
    }
    
    /**
     * Getter for property usage.
     * @return Value of property usage.
     */
    public String getUsage() {
        return this._usage;
    }
    
    /**
     * Setter for property usage.
     * @param usage New value of property usage.
     */
    public void setUsage(String usage) {
        this._usage = usage;
    }
    
}
