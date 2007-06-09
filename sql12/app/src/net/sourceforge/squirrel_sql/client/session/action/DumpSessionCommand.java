package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextFileDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ObjectArrayDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

/**
 * This <CODE>ICommand</CODE> will dump the status of a session to a text
 * file.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DumpSessionCommand implements ICommand
{
	/** Logger for this class. */
	private final ILogger s_log =
		LoggerController.createLogger(DumpSessionCommand.class);

	/** Prefix for temp file names. */
	private static final String PREFIX = "dump";

	/** Suffix for temp file names. */
	private static final String SUFFIX = "tmp";

	/** Used to separate lines of data in the dump file. */
	private static String SEP = "===================================================";

	/** Session to be dumped. */
	private ISession _session;

	/** File to dump session to. */
	private File _outFile;

	/** Message handler to write errors to. */
	private IMessageHandler _msgHandler;

	/**
	 * Default ctor.
	 */
	public DumpSessionCommand()
	{
		this(null, null);
	}

	/**
	 * Ctor using sessions message handler for errors.
	 *
	 * @param	outFile		File to dump session to.
	 */
	public DumpSessionCommand(File outFile)
	{
		this(outFile, null);
	}

	/**
	 * Ctor.
	 *
	 * @param	outFile		File to dump session to.
	 * @param	msgHandler	Message handler. If <TT>null</TT> then the sessions
	 * 						message handler will be used for errors.
	 */
	public DumpSessionCommand(File outFile, IMessageHandler msgHandler)
	{
		super();
		_outFile = outFile;
		_msgHandler = msgHandler;
	}

	/**
	 * Set the file to dump to.
	 *
	 * @param	file	File to dump to.
	 * 
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> or <TT>File</TT> passed.
	 */
	public void setDumpFile(File file)
	{
		if (file == null)
		{
			throw new IllegalArgumentException("Null Dump File passed");
		}
		_outFile = file;
	}

	/**
	 * Set the session to dump.
	 *
	 * @param	session	Session to be dumped.
	 * 
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> or <TT>File</TT> passed.
	 */
	public void setSession(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
	}

	/**
	 * Dump the session.
	 */
	public void execute()
	{
		if (_session == null)
		{
			throw new IllegalStateException("Trying to dump null session");
		}
		if (_outFile == null)
		{
			throw new IllegalStateException("Trying to dump session to null file");
		}

		final List<File> files = new ArrayList<File>();
		final List<String> titles = new ArrayList<String>();
		synchronized (_session)
		{
			final ISQLConnection conn = _session.getSQLConnection();
			final SQLDatabaseMetaData md = conn.getSQLMetaData();

			// Dump session properties.
			try
			{
				files.add(createJavaBeanDumpFile(_session.getProperties()));
				titles.add("Session Properties");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping driver info";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			// Dump driver information.
			try
			{
				files.add(createJavaBeanDumpFile(_session.getDriver()));
				titles.add("Driver");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping driver info";
                showErrorMessage(msg);
                showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			// Dump alias information.
			try
			{
				files.add(createJavaBeanDumpFile(_session.getAlias()));
				titles.add("Alias");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping alias info";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			// Dump general connection info.
			try
			{
				files.add(createGeneralConnectionDumpFile(conn));
				titles.add("Connection - General");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping general connection info";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			// Dump meta data.
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(conn.getSQLMetaData().getMetaDataSet());
				files.add(tempFile);
				titles.add("Metadata");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping metadata";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			// Dump catalogs.
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new ObjectArrayDataSet(md.getCatalogs()));
				files.add(tempFile);
				titles.add("Catalogs");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping catalogs";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			// Dump schemas.
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new ObjectArrayDataSet(_session.getSchemaInfo().getSchemas()));
				files.add(tempFile);
				titles.add("Schemas");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping schemas";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			// Dump data types.
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(conn.getSQLMetaData().getTypesDataSet());
				files.add(tempFile);
				titles.add("Data Types");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping data types";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			// Dump table types.
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new ObjectArrayDataSet(md.getTableTypes()));
				files.add(tempFile);
				titles.add("Table Types");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping table types";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
		}

		combineTempFiles(titles, files);
		deleteTempFiles(files);
	}

	private void combineTempFiles(List<String> titles, List<File> files)
	{
		try
		{
			PrintWriter wtr = new PrintWriter(new FileWriter(_outFile));
			try
			{
				wtr.println("SQuirreL SQL Client Session Dump " +
								Calendar.getInstance().getTime());
				for (int i = 0, limit = files.size(); i < limit; ++i)
				{
					wtr.println();
					wtr.println();
					wtr.println(SEP);
					wtr.println(titles.get(i));
					wtr.println(SEP);
					File file = files.get(i);
					BufferedReader rdr = new BufferedReader(new FileReader(file));
					try
					{
						String line = null;
						while((line = rdr.readLine()) != null)
						{
							wtr.println(line);
						}
					}
					finally
					{
						rdr.close();
					}
				}
			}
			finally
			{
				wtr.close();
			}
		}
		catch (IOException ex)
		{
			final String msg = "Error combining temp files into dump file";
			showErrorMessage(msg);
			showErrorMessage(ex);
			s_log.error(msg, ex);
		}
	}

	private void deleteTempFiles(List<File> files)
	{
		for (int i = 0, limit = files.size(); i < limit; ++i)
		{
			if (!(files.get(i)).delete())
			{
				s_log.error("Couldn't delete temporary DumpSession file");
			}
		}
	}

	private File createJavaBeanDumpFile(Object obj)
		throws IOException, XMLException
	{
		File tempFile = File.createTempFile(PREFIX, SUFFIX);
		XMLBeanWriter wtr = new XMLBeanWriter(obj);
		wtr.save(tempFile);

		return tempFile;
	}

	private File createGeneralConnectionDumpFile(ISQLConnection conn)
		throws IOException
	{
		Connection myConn = conn.getConnection();
	
		File tempFile = File.createTempFile(PREFIX, SUFFIX);
		PrintWriter wtr = new PrintWriter(new FileWriter(tempFile));
		try
		{
			// Dump general connection info.
			String line = null;
			try
			{
				line = String.valueOf(myConn.getTransactionIsolation());
			}
			catch (Throwable th)
			{
				line = th.toString();
			}
			wtr.println("transIsolation: " + line);
			try
			{
				line = String.valueOf(myConn.isReadOnly());
			}
			catch (Throwable th)
			{
				line = th.toString();
			}
			wtr.println("readonly: " + line);

			return tempFile;
		}
		finally
		{
			wtr.close();
		}
	}
    
    private void showErrorMessage(String msg) {
        if (_session != null) {
            _session.showErrorMessage(msg);
        } else if (_msgHandler != null) {
            _msgHandler.showErrorMessage(msg);
        } else {
            s_log.error("No IMessageHandler or ISession configured");
        }        
    }
    
    private void showErrorMessage(Throwable th) {
        if (_session != null) {
            _session.showErrorMessage(th);
        } else if (_msgHandler != null) {
            _msgHandler.showErrorMessage(th, null);
        } else {
            s_log.error("No IMessageHandler or ISession configured");
        }
    }
}
