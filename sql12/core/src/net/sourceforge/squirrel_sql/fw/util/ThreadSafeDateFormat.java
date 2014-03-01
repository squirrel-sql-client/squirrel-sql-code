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

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

/**
 * Maintains a single reference to a DateFormat and synchronizes access to 
 * methods that delegate to it's methods of the same name.
 */
public class ThreadSafeDateFormat {
    
    /** internal protected instance of DateFormat */
    private DateFormat dateFormat;

   /**
     * Constructor
     * @param style the given formatting style. For example,
     * SHORT for "M/d/yy" in the US locale.
     */
    public ThreadSafeDateFormat(int style) {
        this(style, false);
    }

    /**
     * Constructor
     * @param style the given formatting style. For example,
     * SHORT for "M/d/yy" in the US locale if isTime is false, 
     * and SHORT for "h:mm a" in the US locale if isTime is true
     */    
    public ThreadSafeDateFormat(int style, boolean isTime) {
        if (isTime) {
            this.dateFormat = DateFormat.getTimeInstance(style);
        } else {
            this.dateFormat = DateFormat.getDateInstance(style);
        }
    }
    
    /**
     * Constructor
     * @param dateStyle the given date formatting style. For example,
     * SHORT for "M/d/yy" in the US locale.
     * @param timeStyle the given time formatting style. For example,
     * SHORT for "h:mm a" in the US locale.
     */
    public ThreadSafeDateFormat(int dateSytle, int timeStyle) {
        this.dateFormat = DateFormat.getDateTimeInstance(dateSytle, timeStyle);
    }
    
    /**
     * Formats an object to produce a string. This is equivalent to
     * <blockquote>
     * {@link #format(Object, StringBuffer, FieldPosition) format}<code>(obj,
     *         new StringBuffer(), new FieldPosition(0)).toString();</code>
     * </blockquote>
     *
     * @param obj    The object to format
     * @return       Formatted string.
     * @exception IllegalArgumentException if the Format cannot format the given
     *            object 
     */
    public synchronized String format(Object obj) {
        return dateFormat.format(obj);
    }
    
    /**
     * Parses text from the beginning of the given string to produce a date.
     * The method may not use the entire text of the given string.
     * <p>
     * See the {@link #parse(String, ParsePosition)} method for more information
     * on date parsing.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return A <code>Date</code> parsed from the string.
     * @exception ParseException if the beginning of the specified string
     *            cannot be parsed. 
     */
    public synchronized Date parse(String str) throws ParseException {
        return dateFormat.parse(str);
    }
    
    /**
     * Specify whether or not date/time parsing is to be lenient.  With
     * lenient parsing, the parser may use heuristics to interpret inputs that
     * do not precisely match this object's format.  With strict parsing,
     * inputs must match this object's format.
     * @param lenient when true, parsing is lenient
     * @see java.util.Calendar#setLenient
     */
    public synchronized void setLenient(boolean lenient) {
        dateFormat.setLenient(lenient);
    }
}
