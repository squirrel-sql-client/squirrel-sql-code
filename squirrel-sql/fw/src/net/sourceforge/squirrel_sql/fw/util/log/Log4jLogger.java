package net.sourceforge.squirrel_sql.fw.util.log;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/**
 * This is a logger that logs using the Apache log4j package.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Log4jLogger implements ILogger {
	/** Log4j category to log to. */
	private Category  _cat;

	/**
	 * Ctor specifying the object requesting the logger. A
	 * Log4J <TT>Category</TT> is created using as a name the
	 * fully qualified class of <TT>requester</TT>.
	 * 
	 * @param	clazz	The class requesting a logger.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>clazz</TT> is <TT>null</TT>.
	 */
	public Log4jLogger(Class clazz) throws IllegalArgumentException {
		if (clazz == null) {
			throw new IllegalArgumentException("Empty requesterClass passed");
		}

		_cat = Category.getInstance(clazz);
	}

	/**
	 * @see ILogger#debug(Object)
	 */
	public void debug(Object message) {
		_cat.debug(message);
	}

	/**
	 * @see ILogger#debug(Object, Throwable)
	 */
	public void debug(Object message, Throwable th) {
		_cat.debug(message, th);
	}

	/**
	 * @see ILogger#info(Object)
	 */
	public void info(Object message) {
		_cat.info(message);
	}

	/**
	 * @see ILogger#info(Object, Throwable)
	 */
	public void info(Object message, Throwable th) {
		_cat.info(message, th);
	}

	/**
	 * @see ILogger#warn(Object)
	 */
	public void warn(Object message) {
		_cat.warn(message);
	}

	/**
	 * @see ILogger#warn(Object, Throwable)
	 */
	public void warn(Object message, Throwable th) {
		_cat.warn(message, th);
	}

	/**
	 * @see ILogger#error(Object)
	 */
	public void error(Object message) {
		_cat.error(message);
	}

	/**
	 * @see ILogger#error(Object, Throwable)
	 */
	public void error(Object message, Throwable th) {
		_cat.error(message, th);
	}

	/**
	 * @see ILogger#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return _cat.isDebugEnabled();
	}

	/**
	 * @see ILogger#isInfoEnabled()
	 */
	public boolean isInfoEnabled() {
		return _cat.isInfoEnabled();
	}
	
	public void setPriority(LoggingLevel level) {
		if (level == LoggingLevel.OFF) {
			//??
		} else if (level == LoggingLevel.ERROR) {
			_cat.getRoot().setPriority(Priority.ERROR);
		} else if (level == LoggingLevel.WARN) {
			_cat.getRoot().setPriority(Priority.WARN);
		} else if (level == LoggingLevel.INFO) {
			_cat.getRoot().setPriority(Priority.INFO);
		} else {
			_cat.getRoot().setPriority(Priority.DEBUG);
		}
	}
}

