package net.sourceforge.squirrel_sql.plugins.derby.types;
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
/**
 * Stores the data (text) of a Clob.  Clobs are invalidated after a transaction
 * is complete, so we always read fully and store the entire value.
 */
public class DerbyClobDescriptor {
	/**
	 * The data read from the Clob.
	 */
	String _data = null;
    
	/**
	 * Ctor
	 */
	public DerbyClobDescriptor (String data) {
		_data = data;
	}

	/**
	 * Equals for Clobs means that the internal strings are identical,
	 * including their length.
	 * We need to account for the fact that one or both of them may not
	 * have actually had their data read.  If both have not had their data read,
	 * then they are "equal", in a weird kind of way.
	 */
	public boolean equals(DerbyClobDescriptor c) {
		if (c == null) {
			// the other obj is null, so see if this non-null obj contains
			// a null value, which is equivilent.
			// Assume that if we have read some of the data and we still have
			// _data == null, then the value in the DB is actually null.
			if (_data == null)
				return true;
			else
				return false;
		}

		// both have actual data, so compare the strings
		// Note that if one has read all of the data and the other has read only part
		// of the data that we will say that they are NOT equal.
		return c.getData().equals(_data);
	}

	/**
	 * toString means print the data string, unless the data has not been
	 * read at all.
	 */
	public String toString()
	{
		if (_data == null)
		{
			return "<null>";
		} else {
			return _data;	// we have the whole contents of the CLOB
		}

	}

	/*
	 * Getters and Setters
	 */
	public String getData(){return _data;}
	public void setData(String data){_data = data;}

}
