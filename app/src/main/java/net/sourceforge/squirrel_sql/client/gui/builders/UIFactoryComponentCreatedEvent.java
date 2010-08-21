package net.sourceforge.squirrel_sql.client.gui.builders;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.awt.Component;
import java.util.EventObject;
/**
 * This class is an event fired bu the <TT>UIFactory</TT> class.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class UIFactoryComponentCreatedEvent extends EventObject
{
	private static final long serialVersionUID = -4353644204236711872L;

	/** The <CODE>UIFactory</CODE> involved. */
	private transient UIFactory _factory;

	/** The <CODE>Component</CODE> created. */
	private Component _comp;

	/**
	 * Ctor.
	 *
	 * @param	source	The <CODE>UIFactory</CODE> that change has
	 *					happened to.
	 * @param	comp	The component created.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>UIFactory/TT> or component passed.
	 */
	UIFactoryComponentCreatedEvent(UIFactory source, Component comp)
	{
		super(checkParams(source, comp));
		_factory = source;
		_comp = comp;
	}

	/**
	 * Return the <CODE>UIFactory</CODE>.
	 */
	public UIFactory getUIFactory()
	{
		return _factory;
	}

	/**
	 * Return the <CODE>Component</CODE>.
	 */
	public Component getComponent()
	{
		return _comp;
	}

	private static UIFactory checkParams(UIFactory source, Component comp)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("UIFactory == null");
		}
		if (comp == null)
		{
			throw new IllegalArgumentException("Component == null");
		}
		return source;
	}
}
