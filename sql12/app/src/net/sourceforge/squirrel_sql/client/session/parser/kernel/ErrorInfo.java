package net.sourceforge.squirrel_sql.client.session.parser.kernel;

/*
 * Copyright (C) 2008 Gerd Wagner
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


public class ErrorInfo
{
	public String message;
	public int beginPos;
	public int endPos;

	private String key;

	public ErrorInfo(String message, int beginPos, int endPos)
	{
		this.message = message;
		this.beginPos = beginPos;
		this.endPos = endPos;

		key = message + "_" + beginPos + "_" + endPos;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		ErrorInfo other = (ErrorInfo) obj;
		if (key == null)
		{
			if (other.key != null) { return false; }
		}
		else if (!key.equals(other.key)) { return false; }
		return true;
	}
	
	
}
