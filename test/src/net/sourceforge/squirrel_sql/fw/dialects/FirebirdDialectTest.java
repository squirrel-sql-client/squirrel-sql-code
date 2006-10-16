/*
 * Copyright (C) CollabraSpace Inc. All rights reserved.
 */
package net.sourceforge.squirrel_sql.fw.dialects;


public class FirebirdDialectTest extends DialectTestCase {

    private FirebirdDialect dialect = new FirebirdDialect();
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FirebirdDialectTest.class);
    }

    /*
     * Test method for 'org.hibernate.dialect.Dialect.getTypeName(int)'
     */
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
}
