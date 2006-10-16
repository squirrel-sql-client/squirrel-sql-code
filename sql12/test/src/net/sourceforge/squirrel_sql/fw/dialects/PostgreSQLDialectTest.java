/*
 * Copyright (C) CollabraSpace Inc. All rights reserved.
 */
package net.sourceforge.squirrel_sql.fw.dialects;

public class PostgreSQLDialectTest extends DialectTestCase {

    private PostgreSQLDialect dialect = new PostgreSQLDialect();
    
    /*
     * Test method for 'org.hibernate.dialect.Dialect.getTypeName(int)'
     */
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PostgreSQLDialectTest.class);
    }

}
