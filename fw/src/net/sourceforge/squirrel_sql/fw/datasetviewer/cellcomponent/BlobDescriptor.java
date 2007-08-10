package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;
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
 
import java.sql.Blob;
import java.util.Arrays;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * @author gwg
 *
 * This is the object that is actually stored in the ContentsTab table
 * for a BLOB field.
 * The data in a BLOB is handled differently than other data types.
 * When the row in the DB is read, what is returned is actually a
 * java.sql.Blob object that points to the data rather than the data itself.
 * Since BLOBs can be very large (and thus take a long time to read), we
 * provide the user the flexibility to read only part of the BLOB data, or
 * to not read any of it.  We use the user selected options in various operations
 * such as deciding if the field is editable or not.
 * These options are set in the Session Parameters,
 * and since those parameters can be changed after the data has been read, we
 * make a copy of the appropriate information here.
 */
public class BlobDescriptor {
	
	/**
	 * The java.sql.Blob object that was read.
	 */
	Blob _blob;
	
	/**
	 * The data read from the Blob.
	 */
	byte[] _data = null;
	
	/**
	 * If <TT>_blobRead</TT> is <TT>true</TT> then at least some
	 * of the data in the BLOB should have been read.  If <TT>false</TT>,
	 * then we have not even tried to read the data.
	 */
	private boolean _blobRead = false;
	
	/**
	 * If <TT>_wholeBlobRead</TT> is <TT>true</TT> then all of the
	 * data in this BLOB has been read into _data..
	 */
	private boolean _wholeBlobRead = false;

	/**
	 * If <TT>_wholeBlobRead</TT> is false, this is the size limit
	 * set by the user for how much to read.
	 */
	private int _userSetBlobLimit;
	
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(BlobDescriptor.class);
	
    public static interface i18n {
        String BLOB_LABEL = s_stringMgr.getString("BlobDescriptor.blob");
    }
    
	/**
	 * Ctor
	 */
	public BlobDescriptor (
		Blob blob, byte[] data, 
		boolean blobRead, boolean wholeBlobRead, int userSetBlobLimit) {
		_blob = blob;
		_data = data;
		_blobRead = blobRead;
		_wholeBlobRead = wholeBlobRead;
		_userSetBlobLimit = userSetBlobLimit;
	}
	
	/**
	 * Equals for Blobs means that the internal byte arrays are identical,
	 * including their length.
	 * We need to account for the fact that one or both of them may not
	 * have actually had their data read.  If both have not had their data read,
	 * then they are "equal", in a wierd kind of way.
	 */
	public boolean equals(BlobDescriptor c) {
		if (c == null) {
			// the other obj is null, so see if this non-null obj contains
			// a null value, which is equivilent.
			// Assume that if we have read some of the data and we still have
			// _data == null, then the value in the DB is actually null.
			if (_blobRead == true && _data == null)
				return true;
			else
				return false;
		}
		
		if (c.getBlobRead() == false) {
			// the other obj has not read the data yet.
			if (_blobRead == true)
				return false;	// we have read data, so that is not the same state
			else return true;	//  odd-ball case: assume if neither has read data that they are equal
		}
		
		// the other object has real data
		if (_blobRead == false)
			return false;	// this one does not, so they are not equal
		
		// both have actual data, so compare the strings
		// Note that if one has read all of the data and the other has read only part
		// of the data that we will say that they are NOT equal.
		return Arrays.equals(c.getData(), _data);
	}
	
	/**
	 * toString means print the data string, unless the data has not been
	 * read at all.
	 */
	public String toString() {
		if (_blobRead) {
			if (_data == null)
				return null;
			
			// Convert the data into an ascii representation
			// using the standard convention
			Byte[] useValue = new Byte[_data.length];
					for (int i=0; i<_data.length; i++)
						useValue[i] = Byte.valueOf(_data[i]);
			String outString = BinaryDisplayConverter.convertToString(useValue,
						                            BinaryDisplayConverter.HEX, 
                                                    false);
			if (_wholeBlobRead || _userSetBlobLimit > _data.length)
				return outString;	// we have the whole contents of the BLOB
			else return outString+"...";	// tell user the data is truncated
		}
		else return i18n.BLOB_LABEL;
	}
	
	/* 
	 * Getters and Setters
	 */
	 
	public Blob getBlob(){return _blob;}
	public void setBlob(Blob blob){_blob = blob;}
	 
	public byte[] getData(){return _data;}
	public void setData(byte[] data){_data = data;}
	
	public boolean getBlobRead(){return _blobRead;}
	public void setBlobRead(boolean blobRead){_blobRead = blobRead;}
	 
	public boolean getWholeBlobRead(){return _wholeBlobRead;}
	public void setWholeBlobRead(boolean wholeBlobRead){_wholeBlobRead = wholeBlobRead;}

	public int getUserSetBlobLimit(){return _userSetBlobLimit;}
	public void setUserSetBlobLimit(int userSetBlobLimit)
		{_userSetBlobLimit = userSetBlobLimit;}

}
