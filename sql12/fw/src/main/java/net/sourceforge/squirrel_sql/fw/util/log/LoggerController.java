package net.sourceforge.squirrel_sql.fw.util.log;

import java.util.Vector;

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
public class LoggerController
{
	private static Vector<ILoggerFactory> s_oldfactories = new Vector<ILoggerFactory>();
	private static ILoggerFactory s_factory = new Log4jLoggerFactory();

   public static void registerLoggerFactory(ILoggerFactory factory)
	{
		s_oldfactories.add(s_factory);
		s_factory = factory != null ? factory : new Log4jLoggerFactory();
	}

	public static ILogger createLogger(Class<?> clazz)
	{
      return s_factory.createLogger(clazz);
	}

	public static void shutdown()
	{
		s_factory.shutdown();
	}

	public static void addLoggerListener(ILoggerListener l)
	{
		s_factory.addLoggerListener(l);

		for (int i = 0; i < s_oldfactories.size(); i++)
		{
			ILoggerFactory iLoggerFactory = s_oldfactories.get(i);
			iLoggerFactory.addLoggerListener(l);
		}
	}

	public static void removeLoggerListener(ILoggerListener l)
	{
		s_factory.removeLoggerListener(l);

		for (int i = 0; i < s_oldfactories.size(); i++)
		{
			ILoggerFactory iLoggerFactory = s_oldfactories.get(i);
			iLoggerFactory.removeLoggerListener(l);
		}

	}

}