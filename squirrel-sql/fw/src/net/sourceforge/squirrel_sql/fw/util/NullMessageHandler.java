package net.sourceforge.squirrel_sql.fw.util;
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
 * This message handler just swallows messages sent to it.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class NullMessageHandler implements IMessageHandler
{
	private static NullMessageHandler s_handler = new NullMessageHandler();

	/**
	 * Ctor private becuase this is a singleton.
	 */
	private NullMessageHandler()
	{
		super();
	}

	/**
	 * Return the only instance of this class.
	 * 
	 * @return	the only instance of this class.
	 */
	public static NullMessageHandler getInstance()
	{
		return s_handler;
	}

	/**
	 * Swallow this msg.
	 */
	public void showMessage(Throwable th)
	{
	}

	/**
	 * Swallow this msg.
	 */
	public void showMessage(String msg)
	{
	}

	/**
	 * Swallow this msg.
	 */
	public void showErrorMessage(Throwable th)
	{
	}

	/**
	 * Swallow this msg.
	 */
	public void showErrorMessage(String msg)
	{
	}
}
