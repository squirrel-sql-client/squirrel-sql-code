/*
 * Copyright (C) 2014 David Greene
 * david@trumpetx.com
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
package net.sourceforge.squirrel_sql.client;

import java.util.Collection;

/**
 * Interface to encapsulate the concept of an application argument which is non-coupled to the argument implementation.
 * 
 * Use a structure of a simplified (string only, etc) version of the Apache CLI Option
 */
public interface IApplicationArgument {
    /**
     * Single dash argument name:
     * 
     * java -jar squirrel.jar -arg argumentValue
     * 
     * @return string value of the 'short' arg
     */
    String getArgumentName();
    
    /**
     * Double dash argument name:
     * 
     * java -jar squirrel.jar --long-argument argumentValue
     * 
     * @return string value of the 'long' argument
     */    
    String getLongArgumentName();
    
    /**
     * 
     * @return Description used when help is printed or args are not properly set
     */    
    String getDescription();
    
    /**
     * 
     * @return true if the argument is required
     */
    boolean isRequired();
    
    /**
     * 
     * java -jar squirrel.jar -arg value1 value2 value 3
     * 
     * @return number of String values to accept from the command line
     */
    int getNumberOfArgumentValues();
    
    /**
     * Allows the values to be set by the parser
     * 
     * @param argumentValues 
     */
    void setArgumentValues(Collection<String> argumentValues);
    
    /**
     * 
     * @return the argument values, if set
     */
    Collection<String> getArgumentValues();
    
    /**
     * Retrieve one value from the argument value collection (should be the first one)
     * 
     * @return the first argument value
     */
    String getValue();
    
    /**
     * Useful for 0 value arguments
     * 
     * @return true if the argument was set
     */
    boolean isSet();
}
