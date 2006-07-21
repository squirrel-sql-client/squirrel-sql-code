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
package net.sourceforge.squirrel_sql.plugins.dbcopy.dialects;

import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;

import junit.framework.TestCase;

public class DialectTestCase extends TestCase {

    protected void testAllTypes(Dialect d) {
        testType(Types.ARRAY, d);
        testType(Types.BIGINT, d);
        testType(Types.BINARY, d);
        testType(Types.BIT, d);
        testType(Types.BLOB, d);
        testType(Types.BOOLEAN, d);
        testType(Types.CHAR, d);
        testType(Types.CLOB, d);
        testType(Types.DATALINK, d);
        testType(Types.DATE, d);
        testType(Types.DECIMAL, d);
        testType(Types.DISTINCT, d);
        testType(Types.DOUBLE, d);
        testType(Types.FLOAT, d);
        testType(Types.INTEGER, d);
        testType(Types.JAVA_OBJECT, d);
        testType(Types.LONGVARBINARY, d);
        testType(Types.LONGVARCHAR, d);
        testType(Types.NULL, d);
        testType(Types.NUMERIC, d);
        testType(Types.OTHER, d);
        testType(Types.REAL, d);
        testType(Types.REF, d);
        testType(Types.SMALLINT, d);
        testType(Types.STRUCT, d);
        testType(Types.TIME, d);
        testType(Types.TIMESTAMP, d);
        testType(Types.TINYINT, d);
        testType(Types.VARBINARY, d);
        testType(Types.VARCHAR, d);        
    }
    
    protected void testType(int type, Dialect dialect) {
        try {
            dialect.getTypeName(type);
        } catch (MappingException e) {
            String[] parts = e.getMessage().split(":");
            int typeCode = Integer.parseInt(parts[1].trim());
            System.out.println(
                    "No mapping for type: "+typeCode+"="+
                    JDBCTypeMapper.getJdbcTypeName(typeCode));            
        }
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DialectTestCase.class);
    }

}
