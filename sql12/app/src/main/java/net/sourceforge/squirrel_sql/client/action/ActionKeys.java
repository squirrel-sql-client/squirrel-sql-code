package net.sourceforge.squirrel_sql.client.action;

import java.awt.event.KeyEvent;

/*
 * Copyright (C) 2001 - 2006 Colin Bell
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
 * Describes the accelerator and mnemonic keys associated with an <TT>Action</TT>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ActionKeys
{
	private String _actionClassName;
	private String _accelerator;
	private int _mnemonic;

	public ActionKeys()
	{
		super();
		_accelerator = "";
		_mnemonic = KeyEvent.VK_UNDEFINED;
	}

	/**
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>actionClassName == null</TT>.
	 */
	public ActionKeys(String actionClassName, String accelerator, int mnemonic)
	{
		super();
		setActionClassName(actionClassName);
		setAccelerator(accelerator);
		setMnemonic(mnemonic);
	}

	public String getActionClassName()
	{
		return _actionClassName;
	}

	public int getMnemonic()
	{
		return _mnemonic;
	}

	public String getAccelerator()
	{
		return _accelerator;
	}

	/**
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>actionClassName == null</TT>.
	 */
	public void setActionClassName(String value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("ActionClassName == null");
		}
		_actionClassName = value;
	}

	public void setAccelerator(String value)
	{
		_accelerator = value != null ? value : "";
	}

	public void setMnemonic(int value)
	{
		_mnemonic = value;
	}
}
