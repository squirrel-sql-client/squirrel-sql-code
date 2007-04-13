package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

/*
 * Copyright (C) 2006 Rob Manning
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

/**
 * JUnit test for DataTypeBinary class.
 * 
 * @author manningr
 */
public class DataTypeBinaryTest extends AbstractDataTypeTest {

	public void setUp() throws Exception {
		super.setUp();
		iut = new DataTypeBinary(null, getColDef());
	}

	public void testTextComponents() {
		testTextComponents(iut);
	}
    
    public void testAreEqual() {
        String val1Str = "value1";
        String val2Str = "value2";
        Byte[] val1ByteArr = StringUtilities.getByteArray(val1Str.getBytes());
        Byte[] val2ByteArr = StringUtilities.getByteArray(val2Str.getBytes());
        iut.areEqual(null, null);
        iut.areEqual(val1Str, val2Str);
        iut.areEqual(val1ByteArr, val2ByteArr);
        iut.areEqual(null, val2Str);
        iut.areEqual(val1Str, null);
        iut.areEqual(val1ByteArr, null);
        iut.areEqual(val1Str, val2ByteArr);
        iut.areEqual(null, val2ByteArr);
        iut.areEqual(val1ByteArr, val2Str);
    }

}
