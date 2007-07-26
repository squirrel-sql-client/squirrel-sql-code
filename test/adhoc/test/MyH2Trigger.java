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
package test;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.api.Trigger;

public class MyH2Trigger implements Trigger {

    public void foo() {
        System.out.println("foo was called");
    }

    /**
     * @see org.h2.api.Trigger#fire(java.sql.Connection, java.lang.Object[], java.lang.Object[])
     */
    public void fire(Connection arg0, Object[] arg1, Object[] arg2) throws SQLException {
        System.out.println("fire was called");
        
    }

    /**
     * @see org.h2.api.Trigger#init(java.sql.Connection, java.lang.String, java.lang.String, java.lang.String)
     */
    public void init(Connection arg0, String arg1, String arg2, String arg3) throws SQLException {
        System.out.println("init was called");
        
    }
    
    
}
