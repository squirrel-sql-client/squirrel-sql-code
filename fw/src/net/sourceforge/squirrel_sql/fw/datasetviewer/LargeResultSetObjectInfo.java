package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;

public class LargeResultSetObjectInfo implements Cloneable, Serializable
{
	public interface IPropertyNames
	{
		String READ_BINARY = "readBinary";
		String READ_VARBINARY = "readVarBinary";
		String READ_LONGVARBINARY = "readLongVarBinary";
		String READ_BLOBS = "readBlobs";
		String READ_BLOBS_COMPLETE = "readCompleteBlobs";
		String READ_BLOBS_SIZE = "readBlobsSize";
		String READ_CLOBS = "readClobs";
		String READ_CLOBS_COMPLETE = "readCompleteClobs";
		String READ_CLOBS_SIZE = "readClobsSize";
		String READ_SQL_OTHER = "readSQLOther";
		String READ_ALL_OTHER = "readAllOther";
	}

	private static int LARGE_COLUMN_DEFAULT_READ_LENGTH = 255;

	/** Object to handle property change events. */
	private PropertyChangeReporter _propChgReporter = new PropertyChangeReporter(this);

	/** Read binary from Result sets. */
	private boolean _readBinary = false;

	/** Read varbinary from Result sets. */
	private boolean _readVarBinary = false;

	/** Read longvarbinary from Result sets. */
	private boolean _readLongVarBinary = false;

	/** Read blobs from Result sets. */
	private boolean _readBlobs = false;

	/**
	 * If <TT>_readBlobs</TT> is <TT>true</TT> this specifies if the complete
	 * BLOB should be read in.
	 */
	private boolean _readCompleteBlobs = false;

	/**
	 * If <TT>_readBlobs</TT> is <TT>true</TT> and <TT>_readCompleteBlobs</TT>
	 * is <tt>false</TT> then this specifies the number of bytes to read.
	 */
	private int _readBlobsSize = LARGE_COLUMN_DEFAULT_READ_LENGTH;

	/** Read clobs from Result sets. */
	private boolean _readClobs = false;

	/**
	 * If <TT>_readClobs</TT> is <TT>true</TT> this specifies if the complete
	 * CLOB should be read in.
	 */
	private boolean _readCompleteClobs = false;

	/**
	 * If <TT>_readClobs</TT> is <TT>true</TT> and <TT>_readCompleteClobs</TT>
	 * is <tt>false</TT> then this specifies the number of characters to read.
	 */
	private int _readClobsSize = LARGE_COLUMN_DEFAULT_READ_LENGTH;

	/**
	 * If <TT>_readSQLOther</TT> is <TT>true</TT> then read in
	 * columns that have a data type of <TT>java.sql.Types.OTHER</TT>
	 * using <TT>getObject()</TT>.
	 */
	private boolean _readSQLOther = false;

	/**
	 * If <TT>_readAllOther</TT> is <TT>true</TT> then read in all other data
	 * types as objects.
	 */
	private boolean _readAllOther = false;

	/**
	 * Return a copy of this object.
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage());   // Impossible.
		}
	}

	public boolean getReadBinary()
	{
		return _readBinary;
	}

	public void setReadBinary(boolean value)
	{
		if (_readBinary != value)
		{
			final boolean oldValue = _readBinary;
			_readBinary = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_BINARY,
												oldValue, _readBinary);
		}
	}

	public boolean getReadVarBinary()
	{
		return _readVarBinary;
	}

	public void setReadVarBinary(boolean value)
	{
		if (_readVarBinary != value)
		{
			final boolean oldValue = _readVarBinary;
			_readVarBinary = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_VARBINARY,
												oldValue, _readVarBinary);
		}
	}

	public boolean getReadLongVarBinary()
	{
		return _readLongVarBinary;
	}

	public void setReadLongVarBinary(boolean value)
	{
		if (_readLongVarBinary != value)
		{
			final boolean oldValue = _readLongVarBinary;
			_readLongVarBinary = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_LONGVARBINARY,
												oldValue, _readLongVarBinary);
		}
	}

	public boolean getReadBlobs()
	{
		return _readBlobs;
	}

	public void setReadBlobs(boolean value)
	{
		if (_readBlobs != value)
		{
			final boolean oldValue = _readBlobs;
			_readBlobs = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_BLOBS,
												oldValue, _readBlobs);
		}
	}

	public boolean getReadCompleteBlobs()
	{
		return _readCompleteBlobs;
	}

	public void setReadCompleteBlobs(boolean value)
	{
		if (_readCompleteBlobs != value)
		{
			final boolean oldValue = _readCompleteBlobs;
			_readCompleteBlobs = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_BLOBS_COMPLETE,
												oldValue, _readCompleteBlobs);
		}
	}

	public int getReadBlobsSize()
	{
		return _readBlobsSize;
	}

	public void setReadBlobsSize(int value)
	{
		if (_readBlobsSize != value)
		{
			final int oldValue = _readBlobsSize;
			_readBlobsSize = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_BLOBS_SIZE,
												oldValue, _readBlobsSize);
		}
	}

	public boolean getReadClobs()
	{
		return _readClobs;
	}

	public boolean getReadCompleteClobs()
	{
		return _readCompleteClobs;
	}

	public void setReadCompleteClobs(boolean value)
	{
		if (_readCompleteClobs != value)
		{
			final boolean oldValue = _readCompleteClobs;
			_readCompleteClobs = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_CLOBS_COMPLETE,
												oldValue, _readCompleteClobs);
		}
	}

	public void setReadClobs(boolean value)
	{
		if (_readClobs != value)
		{
			final boolean oldValue = _readClobs;
			_readClobs = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_CLOBS,
												oldValue, _readClobs);
		}
	}

	public int getReadClobsSize()
	{
		return _readClobsSize;
	}

	public void setReadClobsSize(int value)
	{
		if (_readClobsSize != value)
		{
			final int oldValue = _readClobsSize;
			_readClobsSize = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.READ_CLOBS_SIZE,
				oldValue,
				_readClobsSize);
		}
	}

	public boolean getReadSQLOther()
	{
		return _readSQLOther;
	}

	public void setReadSQLOther(boolean value)
	{
		if (_readSQLOther != value)
		{
			final boolean oldValue = _readSQLOther;
			_readSQLOther = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_SQL_OTHER,
													oldValue, _readSQLOther);
		}
	}

	public boolean getReadAllOther()
	{
		return _readAllOther;
	}

	public void setReadAllOther(boolean value)
	{
		if (_readAllOther != value)
		{
			final boolean oldValue = _readAllOther;
			_readAllOther = value;
			_propChgReporter.firePropertyChange(IPropertyNames.READ_ALL_OTHER,
													oldValue, _readAllOther);
		}
	}
}
