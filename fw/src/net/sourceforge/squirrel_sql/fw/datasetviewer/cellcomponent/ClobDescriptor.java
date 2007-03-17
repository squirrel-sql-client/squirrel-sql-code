package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.sql.Clob;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * @author gwg
 *
 * This is the object that is actually stored in the ContentsTab table
 * for a CLOB field.
 * The data in a CLOB is handled differently than other data types.
 * When the row in the DB is read, what is returned is actually a
 * java.sql.Clob object that points to the data rather than the data itself.
 * Since CLOBs can be very large (and thus take a long time to read), we
 * provide the user the flexibility to read only part of the CLOB data, or
 * to not read any of it.  We use the user selected options in various operations
 * such as deciding if the field is editable or not.
 * These options are set in the Session Parameters,
 * and since those parameters can be changed after the data has been read, we
 * make a copy of the appropriate information here.
 */
public class ClobDescriptor {

	/**
	 * The java.sql.Clob object that was read.
	 */
	Clob _clob;

	/**
	 * The data read from the Clob.
	 */
	String _data = null;

	/**
	 * If <TT>_clobRead</TT> is <TT>true</TT> then at least some
	 * of the data in the CLOB should have been read.  If <TT>false</TT>,
	 * then we have not even tried to read the data.
	 */
	private boolean _clobRead = false;

	/**
	 * If <TT>_wholeClobRead</TT> is <TT>true</TT> then all of the
	 * data in this CLOB has been read into _data..
	 */
	private boolean _wholeClobRead = false;

	/**
	 * If <TT>_wholeClobRead</TT> is false, this is the size limit
	 * set by the user for how much to read.
	 */
	private int _userSetClobLimit;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ClobDescriptor.class);

    public static interface i18n {
        String CLOB_LABEL = s_stringMgr.getString("ClobDescriptor.clob");
    }
    
	/**
	 * Ctor
	 */
	public ClobDescriptor (
		Clob clob, String data,
		boolean clobRead, boolean wholeClobRead, int userSetClobLimit) {
		_clob = clob;
		_data = data;
		_clobRead = clobRead;
		_wholeClobRead = wholeClobRead;
		_userSetClobLimit = userSetClobLimit;
	}

	/**
	 * Equals for Clobs means that the internal strings are identical,
	 * including their length.
	 * We need to account for the fact that one or both of them may not
	 * have actually had their data read.  If both have not had their data read,
	 * then they are "equal", in a wierd kind of way.
	 */
	public boolean equals(ClobDescriptor c) {
		if (c == null) {
			// the other obj is null, so see if this non-null obj contains
			// a null value, which is equivilent.
			// Assume that if we have read some of the data and we still have
			// _data == null, then the value in the DB is actually null.
			if (_clobRead == true && _data == null)
				return true;
			else
				return false;
		}

		if (c.getClobRead() == false) {
			// the other obj has not read the data yet.
			if (_clobRead == true)
				return false;	// we have read data, so that is not the same state
			else return true;	//  odd-ball case: assume if neither has read data that they are equal
		}

		// the other object has real data
		if (_clobRead == false)
			return false;	// this one does not, so they are not equal

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
		if (_clobRead)
		{
			if (_data == null)
			{
				return s_stringMgr.getString("ClobDescriptor.null");
			}
			if (_wholeClobRead || _userSetClobLimit > _data.length())
			{
				return _data;	// we have the whole contents of the CLOB
			}
			return _data + "...";	// tell user the data is truncated
		}
		return i18n.CLOB_LABEL;
	}

	/*
	 * Getters and Setters
	 */

	public Clob getClob(){return _clob;}
	public void setClob(Clob clob){_clob = clob;}

	public String getData(){return _data;}
	public void setData(String data){_data = data;}

	public boolean getClobRead(){return _clobRead;}
	public void setClobRead(boolean clobRead){_clobRead = clobRead;}

	public boolean getWholeClobRead(){return _wholeClobRead;}
	public void setWholeClobRead(boolean wholeClobRead){_wholeClobRead = wholeClobRead;}

	public int getUserSetClobLimit(){return _userSetClobLimit;}
	public void setUserSetClobLimit(int userSetClobLimit)
		{_userSetClobLimit = userSetClobLimit;}

}
