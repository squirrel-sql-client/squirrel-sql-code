package net.sourceforge.squirrel_sql.fw.gui.action;
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
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;

/**
 * This is the base class for all <CODE>Action</CODE> classes.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseAction extends AbstractAction
{
	/** Names of properties stored in action. */
	public interface IBaseActionPropertyNames
	{
		String DISABLED_ICON = "squirrelDisabledIcon";
		String ROLLOVER_ICON = "squirrelRolloverIcon";
	}

	/**
	 * Default ctor.
	 */
	protected BaseAction()
	{
		super();
	}

	/**
	 * Ctor specifying the title.
	 *
	 * @param   title   Title for this action.
	 */
	protected BaseAction(String title)
	{
		super(title);
	}

	/**
	 * Ctor specifying the title and icon.
	 *
	 * @param   title   Title for this action.
	 * @param   icon	Icon for this action.
	 */
	protected BaseAction(String title, Icon icon)
	{
		super(title, icon);
	}

	/**
	 * Set the default icon for this action.
	 * 
	 * @param	icon	The new icon.
	 */
	//public void setIcon(Icon icon)
	//{
	//	putValue(Action.SMALL_ICON, icon);
	//}

	/**
	 * Set the "rollover" icon for this action.
	 * 
	 * @param	icon	The new icon.
	 */
	//public void setRolloverIcon(Icon icon)
	//{
	//	putValue(IBaseActionPropertyNames.ROLLOVER_ICON, icon);
	//}

	/**
	 * Return the <CODE>Frame</CODE> object associated with the passed event.<P>
	 *
	 * @param   evt	 <CODE>ActionEvent</CODE> to find frame for.
	 *
	 * @return  <COD>Frame</CODE> or <CODE>null</CODE> if none found.
	 */
	protected Frame getParentFrame(ActionEvent evt)
	{
		Frame parent = null;
		if (evt != null)
		{
			Object src = evt.getSource();
			if (src instanceof Component)
			{
				Component comp = (Component) src;
				while (comp != null && parent == null)
				{
					if (comp instanceof Frame)
					{
						parent = (Frame) comp;
					}
					else if (comp instanceof JPopupMenu)
					{
						comp = ((JPopupMenu) comp).getInvoker();
					}
					else
					{
						comp = comp.getParent();
					}
				}
			}
		}
		return parent;
	}
}