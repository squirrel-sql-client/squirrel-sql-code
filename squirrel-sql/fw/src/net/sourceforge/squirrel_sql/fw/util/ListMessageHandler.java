package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.util.ArrayList;
import java.util.List;
/**
 * This message handler stores msgs in 2 <TT>java.util.List</TT> objects. One
 * for exceptions and the other for strings.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ListMessageHandler implements IMessageHandler
{
	/** Stores msgs. */
	private List _msgs = new ArrayList();

	/** Stores exceptions. */
	private List _errors = new ArrayList();

	/**
	 * Ctor.
	 */
	public ListMessageHandler()
	{
		super();
	}

	/**
	 * Store this exception.
	 * 
	 * @param	th	Exception to be stored.
	 */
	public void showMessage(Throwable th)
	{
		_errors.add(th);
	}

	/**
	 * Store this msg.
	 * 
	 * @param	msg	Message to be stored.
	 */
	public void showMessage(String msg)
	{
		_msgs.add(msg);
	}

	/**
	 * Return array of stored exceptions.
	 * 
	 * @return	array of stored exceptions.
	 */
	public Throwable[] getExceptions()
	{
		return (Throwable[])_errors.toArray(new Throwable[_errors.size()]);
	}

	/**
	 * Return array of stored messages.
	 * 
	 * @return	array of stored messages.
	 */
	public String[] getMessages()
	{
		return (String[])_msgs.toArray(new String[_msgs.size()]);
	}
}
