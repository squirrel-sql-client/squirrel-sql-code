package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate;
/**
 * This defines the display information for a column.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ColumnDisplayDefinition
{
	/** Number of characters to display. */
	private int _displayWidth;

	/** Full name of the column, including the table Catalog, Schema and Table names. */
	private String _fullTableColumnName;

    /** Column name to be used in SQL statements */
    private String _columnName;
    
	/** Column heading. */
	private String _label;

	/**
	 * Type of data displayed in column. When set to Types.NULL, the value is unknown.
	 * Value is from java.sql.Types.
	 * This is needed, for example, when editing the column to know
	 * what operations to apply during cell editing.
	 */
	private int _sqlType;

	/**
	 * The name of the data type as know to the DBMS.
	 * This is used to identify a sub-type when multiple data types
	 * have been defined using the same SQL type code.
	 * This may occur when a DBMS defines several DBMS-specific types
	 * using SQL type OTHER (1111).
	 * The only time this is used is when a plugin has registered a
	 * handler for the data type.
	 */
	private String _sqlTypeName;

	/**
	 * A boolean indicating whether this field is nullable/may-be-nullable vs. known
	 * to be not nullable.
	 */
	private boolean _isNullable;

	/**
	 * The column's normal maximum width in characters as known to the DB.
	 * This is different from _columnWidth in that this is the size known to the
	 * DB whereas _columnWidth may be a different size used in the initial display
	 * of the data on the screen.
	 */
	private int _columnSize;

	/**
	 * The number of decimal digits in the column.
	 */
	private int _precision;

	/**
	 * The number of decimal digits to the right of the decimal point.
	 */
	private int _scale;

	/**
	 * Flag for whether or not this column is signed or unsigned.
	 */
	private boolean _isSigned;

	/**
	 * Flag for whether this column represents currency or not.
	 */
	private boolean _isCurrency;

    /** 
     * Flag for whether the column is automatically numbered, thus read-only. 
     */
    private boolean _isAutoIncrement;
    
	/**
	 * Ctor.
	 *
	 * @param	displayWidth	Number of characters to display.
	 * @param	label			Column heading.
	 */
	public ColumnDisplayDefinition(int displayWidth, String label)
	{
		super();
		init(displayWidth, null, null, label, Types.NULL, null, true, 0, 0, 0, true, false, false);
	}

	/**
	 * Constructor for use when the type of data in the column is known/needed.
	 *
	 * @param	displayWidth	Number of characters to display.
	 * @param	label			Column heading.
	 * @param	className		Name of the class for the type of data in the column.
	 */
	public ColumnDisplayDefinition(int displayWidth, String fullTableColumnName,
                String columnName, String label, int sqlType, String sqlTypeName,
				boolean isNullable, int columnSize, int precision, int scale,
				boolean isSigned, boolean isCurrency, boolean isAutoIncrement) {
		super();
		init(displayWidth, fullTableColumnName, columnName, label, sqlType, 
             sqlTypeName, isNullable, columnSize, precision, scale,
             isSigned, isCurrency, isAutoIncrement);
	}

	/**
	 * Constructs a new ColumnDisplayDefinition using ResultSetMetaData from 
	 * the specified ResultSet.
	 *  
	 * @param rs the ResultSet to use
	 * @param idx the index of the column to build a display definition for.
	 * 
	 * @throws SQLException
	 */
	public ColumnDisplayDefinition(ResultSet rs, int idx) throws SQLException {
	    super();
	    ResultSetMetaData md = rs.getMetaData();
	    
	    String columnLabel = md.getColumnLabel(idx);
	    String columnName = md.getColumnName(idx);
	    int displayWidth = columnLabel.length();
	    String fullTableColumnName = 
	        new StringBuilder(md.getTableName(idx))
	                .append(":")
	                .append(columnName)
	                .toString();
	    int sqlType = md.getColumnType(idx);
	    String sqlTypeName = md.getColumnTypeName(idx);
	    boolean isNullable = 
	        md.isNullable(idx) == ResultSetMetaData.columnNullable;
	    int columnSize = md.getColumnDisplaySize(idx);
	    int precision = md.getPrecision(idx);
	    int scale = md.getScale(idx);
        boolean isSigned = md.isSigned(idx);
        boolean isCurrency = md.isCurrency(idx);
        boolean isAutoIncrement = md.isAutoIncrement(idx);
        
        init(displayWidth, fullTableColumnName, columnName, columnLabel, sqlType, 
             sqlTypeName, isNullable, columnSize, precision, scale,
             isSigned, isCurrency, isAutoIncrement);	    
	}
	
	/**
	 * Return the number of characters to display.
	 *
	 * @return	The number of characters to display.
	 */
	public int getDisplayWidth()
	{
		return _displayWidth;
	}

	/**
	 * Return the full name of the column including the table Catalog, Schema and Table names.
	 *
	 * @return	The full table name.
	 */
	public String getFullTableColumnName()
	{
		return _fullTableColumnName;
	}

	/**
	 * Return the column heading.
	 *
	 * @return	The column heading.
	 */
	public String getLabel()
	{
		return _label;
	}

	/**
	 * Return the column data type, which may be Types.NULL.
	 *
	 * @return	The type of data in the column (may be Types.NULL).
	 */
	public int getSqlType()
	{
		return _sqlType;
	}

    public void setSqlType(int sqlType) {
        _sqlType = sqlType;
    }
    
	/**
	 * Return the column data type name.
	 *
	 * @return	The DBMS-specific name of the type of data in the column.
	 */
	public String getSqlTypeName()
	{
		return _sqlTypeName;
	}

    public void setSqlTypeName(String sqlTypeName) {
        _sqlTypeName = sqlTypeName;
    }
    
	/**
	 * Return a boolean indicating column is nullable or not.
	 *
	 * @return	true = column may contain null (with some uncertainty);
	 *			false= definitely no nulls allowed.
	 */
	public boolean isNullable()
	{
		return _isNullable;
	}

	/**
	 * Override the isNullable field after creation.
	 */
	public void setIsNullable(boolean isNullable)
	{
		_isNullable = isNullable;
	}

	/**
	 * Return the size of the column as known to the DB in number of characters,
	 * For non-character fields (e.g. Integer) this will be the number of characters used
	 * in the DB to represent the data (e.g. 4 for an Int) rather than the number of
	 * characters (e.g. decimal digits) that the user may enter.
	 */
	public int getColumnSize()
	{
		return _columnSize;
	}

	/**
	 * Return the number of decimal digits that may be entered into this field.
	 */
	public int getPrecision()
	{
		return _precision;
	}

	/**
	 * Return the number of decimal digits to the right of the decimal point.
	 */
	public int getScale()
	{
		return _scale;
	}

	/**
	 * Return the flag for whether this column is signed or unsigned.
	 */
	public boolean isSigned()
	{
		return _isSigned;
	}

	/**
	 * Return the flag for whether this column represents currency or not.
	 */
	public boolean isCurrency()
	{
		return _isCurrency;
	}


	/**
	 * Return the class name associated with the sql data type.
	 * When the type is unknown or cannot be edited we return
	 * "java.lang.Object".
	 *
	 * @return	The java class name for the data type
	 */
	public String getClassName()
	{
		return CellComponentFactory.getClassName(this);
	}

	/**
	 * Private initializer method for ctors. If the display width
	 * is less than the width of the heading then make the display
	 * width the same as the width of the heading.
	 *
	 * @param	displayWidth	Number of characters to display.
	 * @param	label			Column heading.
	 * @param	sqlType			Type of data (from java.sql.Types).
	 */
	private void init(int displayWidth, String fullTableColumnName, 
                      String columnName, String label,
						int sqlType, String sqlTypeName,
						boolean isNullable, int columnSize, int precision,
						int scale, boolean isSigned, boolean isCurrency, 
                        boolean isAutoIncrement)
	{
		if (label == null)
		{
			label = " "; // Some drivers will give null.
		}
		_displayWidth = displayWidth;
		if (_displayWidth < label.length())
		{
			_displayWidth = label.length();
		}
		_fullTableColumnName = fullTableColumnName;
		_columnName = columnName;
		// If all columns in a table have empty strings as the headings then the
		// row height of the label row is zero. We dont want this.
		_label = label.length() > 0 ? label : " ";

		_sqlType = sqlType;
		_sqlTypeName = sqlTypeName;
        if (sqlType == Types.DATE && DataTypeDate.getReadDateAsTimestamp()) {
            _sqlType = Types.TIMESTAMP;
            _sqlTypeName = "TIMESTAMP";
        }
		_isNullable = isNullable;
		_columnSize = columnSize;
		_precision = precision;
		_scale = scale;
		_isSigned = isSigned;
		_isCurrency = isCurrency;
        _isAutoIncrement = isAutoIncrement;
	}
    
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[ columnName=");
        result.append(_columnName);
        result.append(", sqlType=");
        result.append(_sqlType);
        result.append(", sqlTypeName=");
        result.append(_sqlTypeName);
        result.append(", className=");
        result.append(getClassName());
        result.append(" ]");
        return result.toString();
    }

    public void setIsAutoIncrement(boolean autoIncrement) {
        _isAutoIncrement = autoIncrement;
    }
    
    public boolean isAutoIncrement() {
        return _isAutoIncrement;
    }

    /**
     * @param _columnName the _columnName to set
     */
    public void setColumnName(String _columnName) {
        this._columnName = _columnName;
    }

    /**
     * @return the _columnName
     */
    public String getColumnName() {
        return _columnName;
    }
}
