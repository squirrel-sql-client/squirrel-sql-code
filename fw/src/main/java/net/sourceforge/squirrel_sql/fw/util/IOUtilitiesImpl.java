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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class IOUtilitiesImpl implements IOUtilities
{

	/** The size of the byte array which is used to fetch data from disk to do various I/O operations */
	public static final int DISK_DATA_BUFFER_SIZE = 8192;

	/** Logger for this class. */
	private final ILogger s_log = LoggerController.createLogger(IOUtilitiesImpl.class);

	/* Spring-Injected */
	
	private FileWrapperFactory fileWrapperFactory;
	/**
	 * @param fileWrapperFactory the fileWrapperFactory to set
	 */
	public void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		this.fileWrapperFactory = fileWrapperFactory;
	}
	
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#closeInputStream(java.io.InputStream)
	 */
	public void closeInputStream(InputStream is)
	{
		if (is != null)
		{
			try
			{
				is.close();
			}
			catch (Exception e)
			{
				s_log.error("closeInputStream: Unable to close InputStream - " + e.getMessage(), e);
			}
		}
	}

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
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities# copyBytes(java.io.InputStream,
	 *      java.io.OutputStream)
	 */
	public void copyBytes(InputStream is, OutputStream os) throws IOException
	{
		byte[] buffer = new byte[DISK_DATA_BUFFER_SIZE];
		int length;
		while ((length = is.read(buffer)) > 0)
		{
			os.write(buffer, 0, length);
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

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#getCheckSum(java.io.File)
	 */
	public long getCheckSum(File f) throws IOException
	{
		CRC32 result = new CRC32();
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(f);
			int len = 0;
			byte[] buffer = new byte[DISK_DATA_BUFFER_SIZE];
			while ((len = fis.read(buffer)) != -1)
			{
				result.update(buffer, 0, len);
			}
		}
		finally
		{
			closeInputStream(fis);
		}
		return result.getValue();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#
	 *      getCheckSum(net.sourceforge.squirrel_sql.fw.util.FileWrapper)
	 */
	public long getCheckSum(FileWrapper f) throws IOException
	{
		return getCheckSum(new File(f.getAbsolutePath()));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#
	 *      copyFile(net.sourceforge.squirrel_sql.fw.util.FileWrapper,
	 *      net.sourceforge.squirrel_sql.fw.util.FileWrapper)
	 */
	public void copyFile(FileWrapper from, FileWrapper to) throws IOException
	{
		FileInputStream in = null;
		FileOutputStream out = null;
		try
		{
			in = new FileInputStream(from.getAbsolutePath());
			out = new FileOutputStream(to.getAbsolutePath());
			byte[] buffer = new byte[8192];
			int len;
			while ((len = in.read(buffer)) != -1)
			{
				out.write(buffer, 0, len);
			}
		}
		finally
		{
			closeInputStream(in);
			closeOutputStream(out);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities# constructHttpUrl(java.lang.String, int,
	 *      java.lang.String)
	 */
	public URL constructHttpUrl(final String host, final int port, final String fileToGet)
		throws MalformedURLException
	{
		URL url = null;
		String server = host;
		if (server.startsWith(HTTP_PROTOCOL_PREFIX))
		{
			int beginIdx = server.indexOf("://") + 3;
			server = server.substring(beginIdx, host.length());
		}
		if (port == 80)
		{
			url = new URL(HTTP_PROTOCOL_PREFIX, server, fileToGet);
		}
		else
		{
			url = new URL(HTTP_PROTOCOL_PREFIX, server, port, fileToGet);
		}
		return url;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities# downloadHttpFile(java.lang.String, int,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.util.FileWrapper)
	 */
	public int downloadHttpFile(URL url, FileWrapper destFile, IProxySettings proxySettings)
		throws IOException
	{
		BufferedInputStream is = null;
		HttpMethod method = null;
		int resultCode = -1;
		int result = -1;
		try
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("downloadHttpFile: downloading file (" + destFile.getName() + ") from url: " + url);
			}
			HttpClient client = new HttpClient();
			setupProxy(proxySettings, client, url);

			method = new GetMethod(url.toString());
			method.setFollowRedirects(true);

			resultCode = client.executeMethod(method);
			if (s_log.isDebugEnabled())
			{
				s_log.debug("downloadHttpFile: response code was: " + resultCode);
			}

			if (resultCode != 200) { throw new FileNotFoundException("Failed to download file from url (" + url
				+ "): HTTP Response Code=" + resultCode); }
			InputStream mis = method.getResponseBodyAsStream();

			is = new BufferedInputStream(mis);

			if (s_log.isDebugEnabled())
			{
				s_log.debug("downloadHttpFile: writing http response body to file: " + destFile.getAbsolutePath());
			}

			result = copyBytesToFile(mis, destFile);
		}
		catch (IOException e)
		{
			s_log.error("downloadHttpFile: Unexpected exception while "
				+ "attempting to open an HTTP connection to url (" + url + ") to download a file ("
				+ destFile.getAbsolutePath() + "): " + e.getMessage(), e);
			throw e;
		}
		finally
		{
			closeInputStream(is);
			method.releaseConnection();
		}
		return result;
	}

	/**
	 * Setup proxy configuration specified in proxySettings. This setup is skipped if: 1) proxySettings is
	 * null. 2) proxySettings.getHttpUseProxy() is false (HttpClient doesn't support SOCKS proxy) 3) The url's
	 * host component is in the "non-proxy" host list
	 * 
	 * @param proxySettings
	 *           the ProxySettings to use
	 * @param client
	 *           the instance of HttpClient to configure
	 * @param url
	 *           the URL of the file to be retrieved
	 */
	private void setupProxy(IProxySettings proxySettings, HttpClient client, URL url)
	{
		if (proxySettings == null) { return; }
		if (!proxySettings.getHttpUseProxy()) { return; }
		if (proxySettings.getHttpNonProxyHosts() != null
			&& proxySettings.getHttpNonProxyHosts().contains(url.getHost())) { return; }

		String proxyHost = proxySettings.getHttpProxyServer();
		int proxyPort = Integer.parseInt(proxySettings.getHttpProxyPort());
		String proxyUsername = proxySettings.getHttpProxyUser();
		String proxyPassword = proxySettings.getHttpProxyPassword();

		client.getHostConfiguration().setProxy(proxyHost, proxyPort);
		if (proxyUsername != null && !"".equals(proxyUsername))
		{
			Credentials credentials = new UsernamePasswordCredentials(proxyUsername, proxyPassword);
			client.getState().setProxyCredentials(AuthScope.ANY, credentials);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#getLinesFromFile(java.lang.String, java.util.List)
	 */
	@Override
	public List<String> getLinesFromFile(String filename, List<ScriptLineFixer> lineFixers) throws IOException
	{
		ArrayList<String> lines = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = null;

		while ((line = reader.readLine()) != null)
		{
			if (lineFixers != null)
			{
				for (ScriptLineFixer fixer : lineFixers)
				{
					line = fixer.fixLine(filename, line);
				}
			}
			lines.add(line);
		}
		reader.close();
		return lines;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities#writeLinesToFile(java.lang.String, java.util.List)
	 */
	@Override
	public void writeLinesToFile(String filename, List<String> lines) throws FileNotFoundException
	{
		PrintWriter out = new PrintWriter(new File(filename));
		for (String outline : lines)
		{
			out.write(outline);
			out.write(NEW_LINE);
		}
		out.close();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.util.IOUtilities# copyResourceFromJarFile(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void copyResourceFromJarFile(String jarFilename, String resourceName, String destinationFile)
		throws ZipException, IOException
	{
		ZipFile appJar = new ZipFile(new File(jarFilename));
		Enumeration<? extends ZipEntry> entries = appJar.entries();
		while (entries.hasMoreElements())
		{
			ZipEntry entry = entries.nextElement();
			String entryName = entry.getName();
			if (entryName.endsWith(resourceName))
			{
				InputStream is = null;
				try {
					FileWrapper destinationFileWrapper = fileWrapperFactory.create(destinationFile); 
					is = appJar.getInputStream(entry);
					copyBytesToFile(is, destinationFileWrapper);
				} finally {
					closeInputStream(is);
				}
				break;
			}
		}

	}

}
