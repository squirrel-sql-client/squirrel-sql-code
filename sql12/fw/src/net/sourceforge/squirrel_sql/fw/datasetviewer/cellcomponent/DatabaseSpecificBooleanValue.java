package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

/*
 * Copyright (C) 2005 Rob Manning, Gerd Wagner
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

public class DatabaseSpecificBooleanValue {
   
    private static final IBooleanValue[] _booleans =
                                            new IBooleanValue[] {
                                                        new SybaseBoolean(),
                                                        new MSSQLServerBoolean()
                                            };
    
    
    
    public static String getBooleanValue(String orig, 
                                         ISQLDatabaseMetaData md)
    {
        for (int i = 0; i < _booleans.length; i++) {
            if(_booleans[i].productMatches(md)) {
                return _booleans[i].getBooleanValue(orig);
            }
        }
        return orig;
    }
    
    private static interface IBooleanValue {
        public boolean productMatches(ISQLDatabaseMetaData md);
        public String getBooleanValue(String originalValue);
    }    
    
    private static class SybaseBoolean implements IBooleanValue {
        
        public boolean productMatches(ISQLDatabaseMetaData md) {
            return DialectFactory.isSyBase(md);
        }
        
        public String getBooleanValue(String orig) {
            String result = orig;
            if ("false".equalsIgnoreCase(orig)) {
                result = "0";
            }
            if ("true".equalsIgnoreCase(orig)) {
                result = "1";
            }
            return result;
        }
    }
    
    private static class MSSQLServerBoolean extends SybaseBoolean {
        
        public boolean productMatches(ISQLDatabaseMetaData md) {
            return DialectFactory.isMSSQLServer(md);
        }
        
    }

}
