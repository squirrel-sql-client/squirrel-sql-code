/*
 * Copyright (C) 2006 Rob Manning
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

import org.hibernate.dialect.SAPDBDialect;

public class MAXDBDialect extends SAPDBDialect 
                          implements HibernateDialect {
    
    public MAXDBDialect() {
        super();
        registerColumnType( Types.BIGINT, "fixed(19,0)" );
        registerColumnType( Types.BINARY, 8000, "char($l) byte" );
        registerColumnType( Types.BINARY, "long varchar byte" );
        registerColumnType( Types.BIT, "boolean" );
        registerColumnType( Types.BLOB, "long byte" );
        registerColumnType( Types.BOOLEAN, "boolean" );
        registerColumnType( Types.CLOB, "long varchar" );
        registerColumnType( Types.CHAR, 8000, "char($l) ascii" );
        registerColumnType( Types.CHAR, "long varchar ascii" );
        registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
        registerColumnType( Types.DOUBLE, "double precision" );
        registerColumnType( Types.DATE, "date" );
        registerColumnType( Types.FLOAT, "float($p)" );
        registerColumnType( Types.INTEGER, "int" );
        registerColumnType( Types.LONGVARBINARY, 8000, "varchar($l) byte");
        registerColumnType( Types.LONGVARBINARY, "long byte");
        registerColumnType( Types.LONGVARCHAR, "long ascii");
        registerColumnType( Types.NUMERIC, "fixed($p,$s)" );
        registerColumnType( Types.REAL, "float($p)");
        registerColumnType( Types.SMALLINT, "smallint" );
        registerColumnType( Types.TIME, "time" );
        registerColumnType( Types.TIMESTAMP, "timestamp" );
        registerColumnType( Types.TINYINT, "fixed(3,0)" );
        registerColumnType( Types.VARBINARY, "long byte" );
        registerColumnType( Types.VARCHAR, 8000, "varchar($l)");
        registerColumnType( Types.VARCHAR, "long ascii");
    }
    
    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        boolean result = true;
        DatabaseObjectType type = info.getDatabaseObjectType();
        if (type.getName().equalsIgnoreCase("database")) {
            result = false;
        }
        return result;
    }

    public boolean supportsSchemasInTableDefinition() {
        return true;
    }

    public String getMaxFunction() {
        return "max";
    }

    public String getLengthFunction(int dataType) {
        return "length";
    }

    public int getMaxPrecision(int dataType) {
        return 38;
    }

    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }

    public int getPrecisionDigits(int columnSize, int dataType) {
        return columnSize * 2;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getColumnLength(int, int)
     */
    public int getColumnLength(int columnSize, int dataType) {
        // driver returns 8 for "long byte", yet it can store 2GB of data. 
        if (dataType == Types.LONGVARBINARY) {
            return Integer.MAX_VALUE;
        }
        return columnSize;
    }
    
    /**
     * The string which identifies this dialect in the dialect chooser.
     * 
     * @return a descriptive name that tells the user what database this dialect
     *         is design to work with.
     */
    public String getDisplayName() {
        return "MaxDB";
    }
    
}
