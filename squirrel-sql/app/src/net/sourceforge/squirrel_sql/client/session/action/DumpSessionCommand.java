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
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextFileDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ObjectArrayDataSet;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.MetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import net.sourceforge.squirrel_sql.client.session.IClientSession;

/**
 * This <CODE>ICommand</CODE> will dump the status of a session to a text
 * file after allowing the user to select the file.
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

	/** Session to be closed. */
	private IClientSession _session;

	/** Parent of any dialogs. */
	private Frame _parentFrame;

	/**
	 * Ctor.
	 *
	 * @param	session		Session to be dumped.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public DumpSessionCommand(IClientSession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
		_parentFrame = session.getApplication().getMainFrame();
	}

	/**
	 * Dump the session.
	 */
	public void execute()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(
			new FileExtensionFilter("XML files", new String[] { ".xml" }));

		for (;;)
		{
			if (chooser.showSaveDialog(_parentFrame) == chooser.APPROVE_OPTION)
			{
				if (dump(chooser.getSelectedFile()))
				{
					break;
				}
			}
			else
			{
				break;
			}
		}
	}

	private boolean dump(File outFile)
	{
		boolean doSave = false;
		if (outFile.exists())
		{
			String msg = outFile.getAbsolutePath() +
				"\nalready exists. Do you want to replace it?";
			doSave = Dialogs.showYesNo(_parentFrame, msg);
			if (!doSave)
			{
				return false;
			}
			if (!outFile.canWrite())
			{
				msg = "File " + outFile.getAbsolutePath()
						+ "\ncannot be written to.";
				Dialogs.showOk(_parentFrame, msg);
				return false;
			}
			outFile.delete();
		}
		else
		{
			doSave = true;
		}

		if (doSave)
		{
			SQLConnection conn = _session.getSQLConnection();
			List files = new ArrayList();
			List titles = new ArrayList();

			// Dump application properties.
			try
			{
				files.add(createJavaBeanDumpFile(_session.getApplication().getSquirrelPreferences()));
				titles.add("Application Preferences");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping driver info";
				_session.getMessageHandler().showMessage(msg);
				_session.getMessageHandler().showMessage(th.toString());
				s_log.error(msg, th);
			}

			// Dump session properties.
			try
			{
				files.add(createJavaBeanDumpFile(_session.getProperties()));
				titles.add("Session Properties");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping driver info";
				_session.getMessageHandler().showMessage(msg);
				_session.getMessageHandler().showMessage(th.toString());
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
				_session.getMessageHandler().showMessage(msg);
				_session.getMessageHandler().showMessage(th.toString());
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
				_session.getMessageHandler().showMessage(msg);
				_session.getMessageHandler().showMessage(th.toString());
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
				_session.getMessageHandler().showMessage(msg);
				_session.getMessageHandler().showMessage(th.toString());
				s_log.error(msg, th);
			}

			// Dump meta data.
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				files.add(tempFile);
				titles.add("Metadata");
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new MetaDataDataSet(conn.getMetaData()));
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping metadata";
				_session.getMessageHandler().showMessage(msg);
				_session.getMessageHandler().showMessage(th.toString());
				s_log.error(msg, th);
			}

			// Dump data types.
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				files.add(tempFile);
				titles.add("Data Types");
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new DatabaseTypesDataSet(conn.getTypeInfo()));
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping data types";
				_session.getMessageHandler().showMessage(msg);
				_session.getMessageHandler().showMessage(th.toString());
				s_log.error(msg, th);
			}

			// Dump table types.
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				files.add(tempFile);
				titles.add("Table Types");
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new ObjectArrayDataSet(conn.getTableTypes()));
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping table types";
				_session.getMessageHandler().showMessage(msg);
				_session.getMessageHandler().showMessage(th.toString());
				s_log.error(msg, th);
			}

			// Combine the multiple dump files into one file.
			
			try
			{
				PrintWriter wtr = new PrintWriter(new FileWriter(outFile));
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
						File file = (File)files.get(i);
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
					final String msg = "Session successfuly dumped to: "
										+ outFile.getAbsolutePath();
					_session.getMessageHandler().showMessage(msg);
				}
				finally
				{
					wtr.close();
				}
			}
			catch (IOException ex)
			{
				final String msg = "Error combining temp files into dump file";
				_session.getMessageHandler().showMessage(ex);
				_session.getMessageHandler().showMessage(ex.toString());
				s_log.error(msg, ex);
			}
		}
		return true;
	}

	private File createJavaBeanDumpFile(Object obj)
		throws IOException, XMLException
	{
		File tempFile = File.createTempFile(PREFIX, SUFFIX);
		XMLBeanWriter wtr = new XMLBeanWriter(obj);
		wtr.save(tempFile);

		return tempFile;
	}

	private File createGeneralConnectionDumpFile(SQLConnection conn)
		throws IOException, SQLException, BaseSQLException
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
}
