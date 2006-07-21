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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

/**
 * An extension to the standard Hibernate dialect
 * 
 * TODO: Progress specification says that the sum of all column lengths of a 
 *       table row may not exceed 31960.  Need to add an interface method to 
 *       the HibernateDialect interface that takes an array of lengths and 
 *       and checks the sum of columns for a row.  This maximum number may be 
 *       different across databases so this check method needs to be 
 *       implemented by each dialect.
 */
public class ProgressDialect extends org.hibernate.dialect.Dialect 
                             implements HibernateDialect {
    
    public ProgressDialect() {
        super();
        registerColumnType(Types.BIGINT, "integer");
        registerColumnType(Types.BINARY, 2000,"binary($l)");
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.BLOB, 999999999, "lvarbinary($l)");
        registerColumnType(Types.BOOLEAN, "bit");
        registerColumnType(Types.CHAR, 2000, "char($l)");
        registerColumnType(Types.CLOB, 31982, "varchar($l)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "numeric($p,2)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, 999999999, "lvarbinary($l)");
        registerColumnType(Types.LONGVARCHAR, 31982, "varchar($l)");
        registerColumnType(Types.NUMERIC, "numeric($p,2)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "date");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, 31982, "varbinary($l)");
        registerColumnType(Types.VARCHAR, 31982,"varchar($l)");        
    }    
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType)
     */
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        return true;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#supportsSchemasInTableDefinition()
     */
    public boolean supportsSchemasInTableDefinition() {
        return true;
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
        if (dataType == Types.FLOAT) {
            return 15;
        } else {
            return 32;
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

}
