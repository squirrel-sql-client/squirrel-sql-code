/*
 * Copyright (C) CollabraSpace Inc. All rights reserved.
 */
package net.sourceforge.squirrel_sql.plugins.dbcopy.dialects;

public class Oracle9iDialectTest extends DialectTestCase {

    private Oracle9iDialect dialect = new Oracle9iDialect();
    
    /*
     * Test method for 'org.hibernate.dialect.Dialect.getTypeName(int)'
     */
    public void testGetTypeNameInt() {
        testAllTypes(dialect);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(Oracle9iDialectTest.class);
    }

}
