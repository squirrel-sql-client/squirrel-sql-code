package net.sourceforge.squirrel_sql.client;
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
import java.lang.reflect.Method;
/**
 * Application entry point.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Main
{
	/**
	 * Default ctor. private as class should never be instantiated.
	 */
	private Main()
	{
		super();
	}

	/**
	 * Application entry point.
	 *
	 * @param   args	Arguments passed on command line.
	 */
	public static void main(String[] args)
	{
		// Fix for the jEdit control under JDK1.4. This cannot be called from
		// within the jEdit plugin as the keyboard focus initialisation code doesn't
		// work when run that late in the application startup. The sympton of it being
		// run too late is tabbing no longer changes focus within components
		// inside JInternalFrames.
		try
		{
			if (Class.forName("java.awt.KeyboardFocusManager") != null)
			{
				Class clazz = Class.forName("net.sourceforge.squirrel_sql.client.Java14");
				if (clazz != null)
				{
					Method m = clazz.getMethod("init", null);
					m.invoke(null, null);
				}
			}
		}
		catch (Throwable ignore)
		{
		}

		ApplicationArguments.initialize(args);
		new Application().startup();
	}
}
