package net.sourceforge.squirrel_sql.fw.dialects;

/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect.Dialect;

/**
 * This class was temporarily re-packaged and will remain so until it deployed to
 * Maven Central with the original package (org.teiid.dialect). Since it is part
 * of JBoss, and JBoss is moving to Central, it is believed that this will only
 * be a temporary necessity.
 */
public class TeiidDialect extends Dialect {

    public TeiidDialect() {
        // Register types
        registerColumnType(Types.CHAR, "char"); //$NON-NLS-1$
        registerColumnType(Types.VARCHAR, "string"); //$NON-NLS-1$

        registerColumnType(Types.BIT, "boolean"); //$NON-NLS-1$
        registerColumnType(Types.TINYINT, "byte"); //$NON-NLS-1$
        registerColumnType(Types.SMALLINT, "short"); //$NON-NLS-1$
        registerColumnType(Types.INTEGER, "integer"); //$NON-NLS-1$
        registerColumnType(Types.BIGINT, "long"); //$NON-NLS-1$

        registerColumnType(Types.REAL, "float"); //$NON-NLS-1$
        registerColumnType(Types.FLOAT, "float"); //$NON-NLS-1$
        registerColumnType(Types.DOUBLE, "double"); //$NON-NLS-1$
        registerColumnType(Types.NUMERIC, "bigdecimal"); //$NON-NLS-1$

        registerColumnType(Types.DATE, "date"); //$NON-NLS-1$
        registerColumnType(Types.TIME, "time"); //$NON-NLS-1$
        registerColumnType(Types.TIMESTAMP, "timestamp"); //$NON-NLS-1$

        registerColumnType(Types.BLOB, "blob"); //$NON-NLS-1$
        registerColumnType(Types.VARBINARY, "blob"); //$NON-NLS-1$
        registerColumnType(Types.CLOB, "clob"); //$NON-NLS-1$
        registerColumnType(Types.JAVA_OBJECT, "object"); //$NON-NLS-1$
    }
}

