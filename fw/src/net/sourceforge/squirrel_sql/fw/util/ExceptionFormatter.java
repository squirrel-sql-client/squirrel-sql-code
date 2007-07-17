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
package net.sourceforge.squirrel_sql.fw.util;


/**
 * An interface that will allow plugin writers to customize the MessageHandler
 * in SQuirreL to handle sometimes cryptic messages embedded in SQLExceptions
 * from JDBC drivers.(FR#1731251)
 *  
 * @author manningr
 */
public interface ExceptionFormatter {
    
    /**
     * Returns a boolean indicating whether or not this formatter can format the
     * specified exception.
     * 
     * @param t the exception to determine formatting compatibility
     * 
     * @return a boolean value to indicate whether format should be called on the 
     *         given throwable
     */
    boolean formatsException(Throwable t);
    
    /**
     * Returns a custom-formatted message based on the contents of the specified
     * Throwable. An exception can be thrown to indicate that custom formatting
     * couldn't be done and that the default formatting should be applied. 
     * 
     * 
     * @param t the Throwable to be formatted
     * 
     * @return A formatted version of the message encapsulated by the specified
     *         throwable.
     *         
     * @throws Exception in the event that the specified Throwable for whatever
     *        reason could not be formatted, an exception can be thrown to 
     *        indicate that custom formatting wasn't done and that the 
     *        default formatting should be applied.                    
     */
    String format(Throwable t) throws Exception;
    
}
