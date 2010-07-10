package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 * Modifications copyright (C) 2001-2002 Johan Compagner
 * jcompagner@j-com.nl
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This DataSet destination will write out to a text file.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataSetViewerTextFileDestination
	extends BaseDataSetViewerDestination
{
	private final ILogger s_log =
		LoggerController.createLogger(DataSetViewerTextFileDestination.class);

	private File _outFile;

	private PrintWriter _outFileWtr;

	private int _rowCount = 0;

	public DataSetViewerTextFileDestination(File outFile)
	{
		super();
		if (outFile == null)
		{
			throw new IllegalArgumentException("File == null");
		}
		_outFile = outFile;
	}

	public java.awt.Component getComponent()
	{
		throw new UnsupportedOperationException("DataSetViewerTextFileDestination.getComponent()");
	}


	/**
	 * Clear the data.
	 */
	public void clear()
	{
	}

	/**
	 * Add a row.
	 *
	 * @param	row		Array of objects specifying the row data.
	 */
	protected void addRow(Object[] row) throws DataSetException
	{
		PrintWriter wtr = getWriter();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < row.length; ++i)
		{
			buf.append("\"").append(row[i] != null ? row[i].toString() : "null").append("\"");
			if (i < (row.length - 1))
			{
				buf.append(",");
			}
		}
		wtr.println(buf.toString());
		++_rowCount;
	}

	/**
	 * Called once all rows have been added.
	 */
	protected void allRowsAdded() throws DataSetException
	{
		closeWriter();
	}

	/**
	 * Indicates that the output display should scroll to the top.
	 */
	public void moveToTop()
	{
	}

	/**
	 * Return number of rows in model.
	 *
	 * @return	Number of rows.
	 */
	public int getRowCount()
	{
		return _rowCount;
	}

	private PrintWriter getWriter() throws DataSetException
	{
		if (_outFileWtr == null)
		{
			try
			{
				_outFileWtr = new PrintWriter(new FileWriter(_outFile));
			}
			catch (IOException ex)
			{
				s_log.error("Error closing file writer", ex);
				throw new DataSetException(ex);
			}
		}
		return _outFileWtr;
	}

	private void closeWriter() throws DataSetException
	{
		getWriter().close();
		_outFileWtr = null;
		_rowCount = 0;
	}
}
