/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.dialects;

import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

/**
 * An extension to the standard Hibernate Ingres dialect
 * 
 */
public class IngresDialect extends org.hibernate.dialect.IngresDialect 
                             implements HibernateDialect {
    
    public IngresDialect() {
        super();
        
        registerColumnType(Types.BIGINT, "bigint");
        // SQL Reference Guide says 32k, but I get:
        //
        // The specified  row size exceeded the maximum allowable row width., 
        // SQL State: 50002, Error Code: 2045
        //
        // when I go above 8000.
        registerColumnType(Types.BINARY, 8000,"byte($l)");
        registerColumnType(Types.BINARY, "long byte" );
        registerColumnType(Types.BIT, "tinyint" );
        registerColumnType(Types.BLOB, "long byte");
        registerColumnType(Types.BOOLEAN, "tinyint");
        registerColumnType(Types.CHAR, 2000, "char($l)");
        registerColumnType(Types.CHAR, "long varchar");
        registerColumnType(Types.CLOB, "long varchar");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p, $s)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, "float($p)" );
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, "long byte" );
        registerColumnType(Types.LONGVARCHAR, "long varchar" );
        registerColumnType(Types.NUMERIC, "numeric($p, $s)" );
        registerColumnType(Types.REAL, "real" );
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "date");
        registerColumnType(Types.TIMESTAMP, "date");
        registerColumnType(Types.TINYINT, "tinyint");
        // I tried the following for values under 8000 but I get 
        // Encountered unexpected exception - line 1, You cannot assign  a 
        // value of type 'long byte' to a column of type 'byte varying'. 
        // Explicitly convert the value to the required type.
        // registerColumnType(Types.VARBINARY, 8000, "byte varying($l)");
        registerColumnType(Types.VARBINARY, "long byte" );        
        // I tried 8000 for the max length of VARCHAR and ingres gives an exception
        // (cannot assign a value of type long varchar to a varchar field).  So
        // I limit this field to 4000 for now - the Ingres product documentation
        // indicated that 32k was acceptable.  I've tested 4k and it seems to 
        // work fine.
        registerColumnType(Types.VARCHAR, 4000, "varchar($l)" );
        registerColumnType(Types.VARCHAR, "long varchar" );
    }    
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType)
     */
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        boolean result = true;
        DatabaseObjectType type = info.getDatabaseObjectType();
        if (type.getName().equalsIgnoreCase("database")) {
            result = false;
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#supportsSchemasInTableDefinition()
     */
    public boolean supportsSchemasInTableDefinition() {
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getLengthFunction()
     */
    public String getLengthFunction(int dataType) {
        return "length";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxFunction()
     */
    public String getMaxFunction() {
        return "max";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxPrecision(int)
     */
    public int getMaxPrecision(int dataType) {
        // float(54) produces an exception:
        //
        // invalid column format 'float' on column 'float_column'., 
        // SQL State: 42000, Error Code: 2014        
        if (dataType == Types.FLOAT)
        {
            return 53;
        } else {
            return 31;
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxScale(int)
     */
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getPrecisionDigits(int, int)
     */
    public int getPrecisionDigits(int columnSize, int dataType) {
        return columnSize;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getColumnLength(int, int)
     */
    public int getColumnLength(int columnSize, int dataType) {
        return columnSize;
    }
    
    /**
     * The string which identifies this dialect in the dialect chooser.
     * 
     * @return a descriptive name that tells the user what database this dialect
     *         is design to work with.
     */
    public String getDisplayName() {
        return "Ingres";
    }
    
    /**
     * Returns boolean value indicating whether or not this dialect supports the
     * specified database product/version.
     * 
     * @param databaseProductName the name of the database as reported by 
     * 							  DatabaseMetaData.getDatabaseProductName()
     * @param databaseProductVersion the version of the database as reported by
     *                              DatabaseMetaData.getDatabaseProductVersion()
     * @return true if this dialect can be used for the specified product name
     *              and version; false otherwise.
     */
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("informix")) {
    		// We don't yet have the need to discriminate by version.
    		return true;
    	}
		return false;
	}        
}
