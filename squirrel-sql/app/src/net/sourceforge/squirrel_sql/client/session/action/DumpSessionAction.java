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
import java.awt.event.ActionEvent;
import java.io.File;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IClientSession;
/**
 * This <CODE>Action</CODE> dumps the current session status to an XML file.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DumpSessionAction extends SquirrelAction
											implements IClientSessionAction
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(DumpSessionAction.class);

	/** Current session. */
	private IClientSession _session;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 */
	public DumpSessionAction(IApplication app)
	{
		super(app);
	}

	/**
	 * Set the current session.
	 *
	 * @param	session		The current session.
	 */
	public void setClientSession(IClientSession session)
	{
		_session = session;
	}

	/**
	 * Perform this action.
	 *
	 * @param	evt	The current event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		final Frame parentFrame = getParentFrame(evt);
		FileExtensionFilter[] filters = new FileExtensionFilter[1];
		filters[0] = new FileExtensionFilter("Text files", new String[] { ".txt" });
		File outFile = Dialogs.selectFileForWriting(parentFrame, filters);
		if (outFile != null)
		{
			DumpSessionCommand cmd = new DumpSessionCommand(outFile);
			cmd.setSession(_session);
			try
			{
				cmd.execute();
				final String msg = "Session successfuly dumped to: "
									+ outFile.getAbsolutePath();
				_session.getMessageHandler().showMessage(msg);
			}
			catch (Throwable ex)
			{
				final String msg = "Error occured dumping session";
				_session.getMessageHandler().showErrorMessage(msg + ": " + ex);
				s_log.error(msg, ex);
			}
		}
	}
}
