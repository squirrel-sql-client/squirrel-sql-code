package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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

public class DatabaseObjectInfo implements IDatabaseObjectInfo {
    /** Catalog name. Can be <CODE>null</CODE> */
    private final String _catalog;

    /** Schema name. Can be <CODE>null</CODE> */
    private final String _schema;

    /** Simple object name. */
    private final String _simpleName;

    private String _qualifiedName;

    /** Catalogue separator. */
    //private static String s_catSep;

    /** Identifier quote string. */
    //private static String s_identifierQuoteString;

    //private static boolean s_staticInitialized = false;

    public DatabaseObjectInfo(String catalog, String schema,
                                String simpleName, SQLConnection conn) {
        super();
        if (simpleName == null) {
            throw new IllegalArgumentException("Null simpleName passed");
        }
        if (conn == null) {
            throw new IllegalArgumentException("Null SQLConnection passed");
        }

//      staticInitialization(conn);

        _catalog = catalog;
        _schema = schema;
        _simpleName = simpleName;
        _qualifiedName = generateQualifiedName(conn);
    }

    public String getCatalogName() {
        return _catalog;
    }

    public String getSchemaName() {
        return _schema;
    }

    public String getSimpleName() {
        return _simpleName;
    }

    public String getQualifiedName() {
        return _qualifiedName;
    }

    protected String generateQualifiedName(SQLConnection conn) {
        String catSep = null;
        String identifierQuoteString = null;
        boolean supportsSchemasInDataManipulation = false;
        boolean supportsCatalogsInDataManipulation = false;
        try {
            supportsSchemasInDataManipulation = conn.supportsSchemasInDataManipulation();
        } catch (BaseSQLException ex) {
        }
        try {
            supportsCatalogsInDataManipulation = conn.supportsCatalogsInDataManipulation();
        } catch (BaseSQLException ex) {
        }
        try {
            if (supportsCatalogsInDataManipulation) {
                catSep = conn.getCatalogSeparator();
            }
        } catch (BaseSQLException ex) {
        }
        try {
            identifierQuoteString = conn.getIdentifierQuoteString();
            if (identifierQuoteString != null && identifierQuoteString.equals(" ")) {
                identifierQuoteString = null;
            }
        } catch (BaseSQLException ex) {
        }

        StringBuffer buf = new StringBuffer();
        if (catSep != null && catSep.length() > 0 && _catalog != null
                && _catalog.length() > 0) {
            if (identifierQuoteString != null) {
                buf.append(identifierQuoteString);
            }
            buf.append(_catalog);
            if (identifierQuoteString != null) {
                buf.append(identifierQuoteString);
            }
            buf.append(catSep);
        }

        //try {
            if (supportsSchemasInDataManipulation && _schema != null
                    && _schema.length() > 0) {
                if (identifierQuoteString != null) {
                    buf.append(identifierQuoteString);
                }
                buf.append(_schema);
                if (identifierQuoteString != null) {
                    buf.append(identifierQuoteString);
                }
                buf.append("."); //?? Is it always a full stop?
            }
        //} catch (BaseSQLException ex) {
            //com.bigfoot.colbell.fw.util.Debug.println(ex.toString());
        //}

        if (identifierQuoteString != null /*&& !identifierQuoteString.equals(" ")*/) {
            buf.append(identifierQuoteString);
        }
        buf.append(_simpleName);
        if (identifierQuoteString != null) {
            buf.append(identifierQuoteString);
        }
        return buf.toString();
    }
/*  private static void staticInitialization(SQLConnection conn) {
        synchronized (DatabaseObjectInfo.class) {
            if (!s_staticInitialized) {
                try {
                    if (conn.supportsCatalogsInDataManipulation()) {
                        s_catSep = conn.getCatalogSeparator();
                    }
                } catch (BaseSQLException ex) {
                }
                try {
                    s_identifierQuoteString = conn.getIdentifierQuoteString();

                } catch (BaseSQLException ex) {
                }
                try {
                    s_supportsSchemasInDataManipulation = conn.supportsSchemasInDataManipulation();

                } catch (BaseSQLException ex) {
                }
                s_staticInitialized = true;
            }
        }
    }
*/
}

