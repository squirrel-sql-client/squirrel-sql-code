package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.io.PrintStream;
import java.io.PrintWriter;
/**
 * Base runtime exception.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class BaseRuntimeException extends RuntimeException
{
	/** If this exception is wrapped around another it is stored here. */
	private Throwable _wrapee;

	/**
	 * Default ctor. Creates an exception with an empty string ("")
	 * as its message.
	 */
	public BaseRuntimeException()
	{
		this("");
	}

	/**
	 * Ctor specifying the message.
	 *
	 * @param	msg	 The message.
	 */
	public BaseRuntimeException(String msg)
	{
		super(msg != null ? msg : "");
	}

	/**
	 * Ctor specifying an exception that this one should
	 * be wrapped around.
	 *
	 * @param	wrapee	The wrapped exception.
	 */
	public BaseRuntimeException(Throwable wrapee)
	{
		super(getMessageFromException(wrapee));
		_wrapee = wrapee;
	}

	public String toString()
	{
		if (_wrapee != null)
		{
			return _wrapee.toString();
		}
		return super.toString();
	}

	public void printStackTrace()
	{
		if (_wrapee != null)
		{
			_wrapee.printStackTrace();
		}
		else
		{
			super.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream s)
	{
		if (_wrapee != null)
		{
			_wrapee.printStackTrace(s);
		}
		else
		{
			super.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintWriter wtr)
	{
		if (_wrapee != null)
		{
			_wrapee.printStackTrace(wtr);
		}
		else
		{
			super.printStackTrace(wtr);
		}
	}

	/**
	 * Retrieve the exception that this one is wrapped around. This can be
	 * <TT>null</TT>.
	 *
	 * @return	The wrapped exception or <TT>null</TT>.
	 */
	public Throwable getWrappedThrowable()
	{
		return _wrapee;
	}

	private static String getMessageFromException(Throwable th)
	{
		String rtn = "";
		if (th != null)
		{
			String msg = th.getMessage();
			if (msg != null)
			{
				rtn = msg;
			}
		}
		return rtn;
	}
}
