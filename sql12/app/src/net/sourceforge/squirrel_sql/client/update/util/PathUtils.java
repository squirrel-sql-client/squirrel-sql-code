/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.update.util;

public interface PathUtils
{

	/**
	 * Constructs a path out of the pathElements.
	 * 
	 * @param prependSlash
	 *           if true this will ensure that the result begins with "/".
	 * @param pathElements
	 *           the strings to connect. They can have "/" in them which will be de-duped in the result, if
	 *           necessary.
	 * @return the path that was constructed.
	 */
	public abstract String buildPath(boolean prependSlash, String... pathElements);

	/**
	 * Returns the file part of the specified path.
	 * 
	 * @param path
	 *           the path
	 * @return the file part of the path
	 */
	public abstract String getFileFromPath(String path);

}