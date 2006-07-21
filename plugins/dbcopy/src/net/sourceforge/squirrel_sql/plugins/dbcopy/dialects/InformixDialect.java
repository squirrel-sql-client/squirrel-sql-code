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
 * An extension to the standard Hibernate Informix dialect
 * 
 */
public class InformixDialect extends org.hibernate.dialect.InformixDialect 
                             implements HibernateDialect {
    
    public InformixDialect() {
        super();
        /*
         * TODO: hookup with informix spec and adjust these as necessary.
         * 
        registerColumnType(Types.BIGINT, "int8");
        registerColumnType(Types.BINARY, 2000000000,"bit varying($l)");
        registerColumnType(Types.BIT, "bit(1)");
        registerColumnType(Types.BLOB, 2000000000, "blob");
        registerColumnType(Types.BOOLEAN, "bit");
        registerColumnType(Types.CHAR, 2000000000, "char($l)");
        registerColumnType(Types.CLOB, 2000000000, "clob");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "money");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, 15, "float($l)");
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, 2000000000, "byte");
        registerColumnType(Types.LONGVARCHAR, 31995, "test");
        registerColumnType(Types.NUMERIC, "numeric(17,2)");
        registerColumnType(Types.REAL, "smallfloat");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "datetime hour to second");
        registerColumnType(Types.TIMESTAMP, "datetime year to second");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, 31995, "bit varying($l)");
        registerColumnType(Types.VARCHAR, 2000000000,"lvarchar($l)");  
        */      
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
        return 0;
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
