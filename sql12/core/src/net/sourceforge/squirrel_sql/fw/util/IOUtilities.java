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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipException;

public interface IOUtilities
{

	public static String NEW_LINE = System.getProperty("line.separator");

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
	 * Flushes the specified writer which can be null. Logs an error if an exception occurs while closing.
	 * 
	 * @param writer
	 *           the Writer to flush.
	 */	
	void flushWriter(Writer writer);
		
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
	 * @param url
	 *           the URL of the file to be retrieved
	 * @param destFile
	 *           the file to download the URL file into
	 * @param proxySettings
	 *           the ProxySettings to use
	 * @return the number of bytes that were read and written to the file.
	 * @throws Exception
	 */
	int downloadHttpFile(final URL url, FileWrapper destFile, IProxySettings proxySettings) throws IOException;

	URL constructHttpUrl(final String host, final int port, final String fileToGet)
		throws MalformedURLException;

	/**
	 * Reads the file specified by filename and builds a list of lines, applying the line fixers specified.
	 * 
	 * @param filename
	 *           the name of the file to read lines from.
	 * @param lineFixers
	 *           a list of fixers to apply to each line. This can be null if no line manipulation is required.
	 * @return a list of lines
	 * @throws IOException
	 *            if an I/O error occurs.
	 */
	List<String> getLinesFromFile(String filename, List<ScriptLineFixer> lineFixers) throws IOException;

	/**
	 * Writes the specified list of line to the specified filename. This will overrite the current contents of
	 * the file.
	 * 
	 * @param filename
	 *           the file to overwrite
	 * @param lines
	 *           the lines to write to the file.
	 * @throws FileNotFoundException
	 */
	void writeLinesToFile(String filename, List<String> lines) throws FileNotFoundException;

	/**
	 * Copies the resource specified from the jarfile specified to the specified destination directory.
	 * 
	 * @param jarFilename
	 *           the jarfile to look in.
	 * @param resourceName
	 *           the resource to pull out.
	 * @param destinationDir
	 *           the directory to write the resource to.
	 * @throws IOException
	 * @throws ZipException
	 */
	void copyResourceFromJarFile(String jarFilename, String resourceName, String destinationDir)
		throws ZipException, IOException;
	
	

}