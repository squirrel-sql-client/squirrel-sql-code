package net.sourceforge.squirrel_sql.fw.datasetviewer;
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

/**
 * @author gwg
 *
 * For CellEditor components implementing our DataType-specific operations.
 * This specifies the calls that need to be made by functions that do not
 * need to know the specific type of data.
 */
public interface IDataTypeComponent
{
	/**
	 * Validate the user-input string data and convert it into an object
	 * of the type appropriate for the specific DataType component.
	 * Also returns a message if there is a problem, such that the message
	 * can be displayed to the user to tell them what is wrong.
	 * Since it is valid for the returned object to be null, success or failure
	 * is indicated by the lack of or presence of a message in the StringBuffer.
	 */
	public Object validateAndConvert(String value, StringBuffer message);
}
