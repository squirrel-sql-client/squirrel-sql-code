package net.sourceforge.squirrel_sql.fw.util.log;

import org.apache.log4j.Level;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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
 * This interface describes a logging object.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ILogger
{
	public void info(Object message);
	public void info(Object message, Throwable th);
	public void warn(Object message);
	public void warn(Object message, Throwable th);
	public void error(Object message);
	public void error(Object message, Throwable th);
	public void debug(Object message);
	public void debug(Object message, Throwable th);

	boolean isDebugEnabled();
	boolean isInfoEnabled();
    
    /**
     * Sets the log level of the logger.  For instance:
     * 
     * Level.ALL
     * Level.DEBUG
     * Level.ERROR
     * Level.FATAL
     * Level.INFO
     * Level.OFF
     * Level.WARN
     *  
     * @param l the level to set the logger to.
     */
    void setLevel(Level l);
}

