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
package net.sourceforge.squirrel_sql.fw.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class IOUtilitiesImpl implements IOUtilities
{

	/** The size of the byte array which is used to fetch data from disk to do various I/O operations */
	public static final int DISK_DATA_BUFFER_SIZE = 8192;

	/** Logger for this class. */
	private final ILogger s_log = LoggerController.createLogger(IOUtilitiesImpl.class);

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#closeOutputStream(java.io.OutputStream)
	 */
	public void closeOutputStream(OutputStream os)
	{
		if (os != null)
		{
			try
			{
				os.close();
			}
			catch (Exception e)
			{
				s_log.error("closeOutpuStream: Unable to close OutputStream - " + e.getMessage(), e);
			}
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#closeReader(java.io.Reader)
	 */
	public void closeReader(Reader reader)
	{
		if (reader != null)
		{
			try
			{
				reader.close();
			}
			catch (Exception e)
			{
				s_log.error("closeReader: Unable to close Reader - " + e.getMessage(), e);
			}
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#closeWriter(java.io.Writer)
	 */
	public void closeWriter(Writer writer)
	{
		if (writer != null)
		{
			try
			{
				writer.close();
			}
			catch (Exception e)
			{
				s_log.error("closeReader: Unable to close Writer - " + e.getMessage(), e);
			}
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#flushWriter(java.io.Writer)
	 */
	@Override
	public void flushWriter(Writer writer)
	{
		if (writer != null)
		{
			try
			{
				writer.flush();
			}
			catch (Exception e)
			{
				s_log.error("flushReader: Unable to flush Writer - " + e.getMessage(), e);
			}
		}
	}


	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities# copyBytesToFile(java.io.InputStream,
	 *      net.sourceforge.squirrel_sql.fw.util.FileWrapper)
	 */
	public int copyBytesToFile(InputStream is, FileWrapper outputFile) throws IOException
	{
		BufferedOutputStream outputFileStream = null;
		int totalLength = 0;
		try
		{
			if (!outputFile.exists())
			{
				outputFile.createNewFile();
			}
			outputFileStream = new BufferedOutputStream(new FileOutputStream(outputFile.getAbsolutePath()));
			byte[] buffer = new byte[DISK_DATA_BUFFER_SIZE];
			int length = 0;
			while ((length = is.read(buffer)) != -1)
			{
				totalLength += length;
				outputFileStream.write(buffer, 0, length);
			}

		}
		finally
		{
			closeOutputStream(outputFileStream);
		}
		return totalLength;
	}
}
