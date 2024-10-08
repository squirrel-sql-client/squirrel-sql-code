package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;


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

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

/**
 * Default renderer.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DefaultColumnRenderer
{
	/**
	 * Return the rendered version of the passed object. If the passed object
	 * is not <TT>null</TT> then <TT>toString() is called on it else
	 * StringUtilities.NULL_AS_STRING is returned.
	 * 
	 * 
	 * @param	obj	Object to be rendered.
	 * @param	idx	The column number being rendered.
	 * 
	 * @return	The rendered object.
	 */
	public static String renderObject(Object obj)
	{
		if (obj != null)
		{
			return obj.toString();
		}
		return StringUtilities.NULL_AS_STRING;
	}
}
