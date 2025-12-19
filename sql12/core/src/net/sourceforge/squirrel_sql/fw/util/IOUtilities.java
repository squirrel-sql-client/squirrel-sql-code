/*
 * Copyright (C) 2008 Rob Manning
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface IOUtilities
{
	void closeOutputStream(OutputStream os);

	/**
	 * Closes the specified Reader which can be null. Logs an error if an exception occurs while closing.
	 * 
	 * @param reader
	 *           the Reader to close.
	 */
	void closeReader(Reader reader);

	/**
	 * Closes the specified writer which can be null. Logs an error if an exception occurs while closing.
	 * 
	 * @param writer
	 *           the Writer to close.
	 */
	void closeWriter(Writer writer);

	/**
	 * Flushes the specified writer which can be null. Logs an error if an exception occurs while closing.
	 * 
	 * @param writer
	 *           the Writer to flush.
	 */	
	void flushWriter(Writer writer);

	/**
	 * Copies bytes from the specified InputStream to the specified output file. This will create the file if
	 * it doesn't already exist. The specified inputstream is not closed in this method.
	 * 
	 * @param is
	 *           the InputStream to read from.
	 * @param outputFile
	 *           the file to write to.
	 * @return the number of bytes that were read and written to the file.
	 * @throws IOException
	 */
	int copyBytesToFile(InputStream is, FileWrapper outputFile) throws IOException;


}