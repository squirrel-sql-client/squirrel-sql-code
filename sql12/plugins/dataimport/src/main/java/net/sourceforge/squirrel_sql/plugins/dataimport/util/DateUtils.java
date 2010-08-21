package net.sourceforge.squirrel_sql.plugins.dataimport.util;
/*
 * Copyright (C) 2007 Thorsten Mürell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Contains a date utility function.
 * 
 * @author Thorsten Mürell
 */
public class DateUtils {
	private static Vector<DateFormat> formats = new Vector<DateFormat>();
	
	static {
		formats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
		formats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		formats.add(new SimpleDateFormat("yyyy-MM-dd"));
		formats.add(new SimpleDateFormat("HH:mm:ss"));
		formats.add(new SimpleDateFormat("dd.MM.yyyy"));
	
	}
	/**
	 * This method tries to parse a date value with several formats.
	 *  
	 * @param value
	 * @return A date or <code>null</code> if it couldn't be parsed
	 */
	public static Date parseSQLFormats(String value) {
	
		Date parsedDate = null;
		for (DateFormat f : formats) {
			parsedDate = parse(f, value);
			if (parsedDate != null)
				break;
		}
		return parsedDate;
	}
	
	private static Date parse(DateFormat format, String value) {
		Date d = null;
		try {
			d = format.parse(value);
		} catch (ParseException pe) {
			/* Do nothing */
		}
		return d;
	}

}
