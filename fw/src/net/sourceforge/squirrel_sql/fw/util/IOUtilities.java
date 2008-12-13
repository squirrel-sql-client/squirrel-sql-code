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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

public interface IOUtilities
{

	String HTTP_PROTOCOL_PREFIX = "http";

	void closeInputStream(InputStream is);

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
	 * Reads from the specified InputStream and copies bytes read to the specified OuputStream.
	 * 
	 * @param is
	 *           the InputStream to read from
	 * @param os
	 *           the OutputStream to write to
	 * @throws IOException
	 *            in an exception occurs while reading/writing
	 */
	void copyBytes(InputStream is, OutputStream os) throws IOException;

	/**
	 * Reads from the specified FileWrapper(from) and copies bytes read to the specified FileWrapper(to).
	 * 
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	void copyFile(FileWrapper from, FileWrapper to) throws IOException;

	/**
	 * Computes the CRC32 checksum for the specified file. This doesn't appear to be compatible with cksum.
	 * 
	 * @param f
	 *           the file to compute a checksum for.
	 * @return the checksum value for the file specified
	 */
	long getCheckSum(File f) throws IOException;

	/**
	 * @param f
	 * @return
	 * @throws IOException
	 */
	long getCheckSum(FileWrapper f) throws IOException;

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
	public int copyBytesToFile(InputStream is, FileWrapper outputFile) throws IOException;

	/**
	 * Downloads a file using HTTP.
	 * 
    * @param url the URL of the file to be retrieved
	 * @param destFile the file to download the URL file into
    * @param proxySettings the ProxySettings to use
	 * @return
	 * @throws Exception
	 */
	public int downloadHttpFile(final URL url, FileWrapper destFile, IProxySettings proxySettings)
		throws Exception;

	public URL constructHttpUrl(final String host, final int port, final String fileToGet)
		throws MalformedURLException;

}