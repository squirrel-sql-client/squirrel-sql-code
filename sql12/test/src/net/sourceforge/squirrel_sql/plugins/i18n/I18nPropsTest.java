/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.i18n;


import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for I18nProps
 * 
 * @author manningr
 */
public class I18nPropsTest {

    InMemoryI18nProperties propsUnderTest = null; 
        
    @Before
    public void setUp() throws Exception {
        propsUnderTest = new InMemoryI18nProperties();
    }

    @After
    public void tearDown() throws Exception {
        propsUnderTest = null;
    }

    /** 
     * This tests the bug-fix for the following exception:
     * 
         java.util.ConcurrentModificationException
            at java.util.Hashtable$Enumerator.next(Hashtable.java:1020)
            at net.sourceforge.squirrel_sql.plugins.i18n.I18nProps.getTranslateableProperties(I18nProps.java:221)
            at net.sourceforge.squirrel_sql.plugins.i18n.I18nPropsTest.testGetTranslateableProperties_Bug1787731(I18nPropsTest.java:57)
     */
    @Test
    public void testGetTranslateableProperties_Bug1787731() {
        /* Call class under test */
        propsUnderTest.getTranslateableProperties();
    }
    
    
    private class InMemoryI18nProperties extends I18nProps {
        
        public InMemoryI18nProperties() {
            super(new File("foo"), new URL[] {});
        }
        
        @Override
        Properties getProperties() {
            StringBuilder propsFile = new StringBuilder();
            propsFile.append("test1.image=test1.jpg\n");
            propsFile.append("test2.image=test2.jpg\n");
            propsFile.append("test3.image=test3.jpg\n");
            propsFile.append("test4.image=test4.jpg\n");
            ByteArrayInputStream is = 
                new ByteArrayInputStream(propsFile.toString().getBytes());
            Properties result = new Properties();
            try {
                result.load(is);
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
            return result;
        }
        
    }
}
