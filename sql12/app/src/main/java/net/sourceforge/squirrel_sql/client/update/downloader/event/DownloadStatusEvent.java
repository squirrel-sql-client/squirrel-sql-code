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
package net.sourceforge.squirrel_sql.client.update.downloader.event;

/**
 * Describes something that happened while downloading updates. This might be download started/ended, file
 * started/ended, etc.
 * 
 * @author manningr
 */
public class DownloadStatusEvent
{

	private DownloadEventType _type = null;

	private String _filename = null;

	private int _fileCountTotal = 0;

	private Exception _exception = null;

	public DownloadStatusEvent(DownloadEventType type)
	{
		this._type = type;
	}

	public DownloadEventType getType()
	{
		return this._type;
	}

	/**
	 * @return the _filename
	 */
	public String getFilename()
	{
		return _filename;
	}

	/**
	 * @param _filename
	 *           the _filename to set
	 */
	public void setFilename(String _filename)
	{
		this._filename = _filename;
	}

	/**
	 * @return the _exception
	 */
	public Exception getException()
	{
		return _exception;
	}

	/**
	 * @param _exception
	 *           the _exception to set
	 */
	public void setException(Exception _exception)
	{
		this._exception = _exception;
	}

	/**
	 * @return the _fileCountTotal
	 */
	public int getFileCountTotal()
	{
		return _fileCountTotal;
	}

	/**
	 * @param countTotal
	 *           the _fileCountTotal to set
	 */
	public void setFileCountTotal(int countTotal)
	{
		_fileCountTotal = countTotal;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	public String toString()
	{
		final String TAB = "    ";

		String retValue = "";

		retValue = "DownloadStatusEvent ( "
         + super.toString() + TAB
         + "_type = " + this._type + TAB
         + "_filename = " + this._filename + TAB
         + "_fileCountTotal = " + this._fileCountTotal + TAB
         + "_exception = " + this._exception + TAB
         + " )";

		return retValue;
	}

}
