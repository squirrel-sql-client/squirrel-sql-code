package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2002-2003 Colin Bell
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

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.ListMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
/**
 * This <CODE>Action</CODE> dumps the current session status to an XML file.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DumpApplicationAction extends SquirrelAction
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DumpApplicationAction.class);

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(DumpApplicationAction.class);

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 */
	public DumpApplicationAction(IApplication app)
	{
		super(app);
	}

	/**
	 * Perform this action.
	 *
	 * @param	evt	The current event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		final IApplication app = getApplication();
		final Frame parentFrame = getParentFrame(evt);
		final FileExtensionFilter[] filters = new FileExtensionFilter[1];
		filters[0] = new FileExtensionFilter(s_stringMgr.getString("DumpApplicationAction.textfiles"), new String[] { ".txt" });
		final JLabel lbl = new JLabel(s_stringMgr.getString("DumpApplicationAction.warning"));
		lbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		final File outFile = Dialogs.selectFileForWriting(parentFrame, filters, lbl);
		if (outFile != null)
		{
			ListMessageHandler msgHandler = new ListMessageHandler();
			ICommand cmd = new DumpApplicationCommand(app, outFile, msgHandler);
			try
			{
				cmd.execute();
				String[] msgs = msgHandler.getMessages();
            String[] warnings = msgHandler.getWarningMessages();
				Throwable[] errors = msgHandler.getExceptions();
            if (msgs.length > 0 || errors.length > 0 || warnings.length > 0)
				{
					for (int i = 0; i < msgs.length; ++i)
					{
						app.showErrorDialog(msgs[i]);
					}
					for (int i = 0; i < warnings.length; ++i)
					{
						app.showErrorDialog(warnings[i]);
					}
					for (int i = 0; i < errors.length; ++i)
					{
						app.showErrorDialog(errors[i]);
					}
				}
				else
				{
					final String msg = s_stringMgr.getString("DumpApplicationAction.success", outFile.getAbsolutePath());
					ErrorDialog dlg = new ErrorDialog(getApplication().getMainFrame(), msg);
					// i18n[DumpApplicationAction.titleSuccess=Dump successful]
					dlg.setTitle(s_stringMgr.getString("DumpApplicationAction.titleSuccess"));
					dlg.setVisible(true);
				}
			}
			catch (Throwable ex)
			{
				final String msg = s_stringMgr.getString("DumpApplicationAction.failure");
				app.showErrorDialog(msg, ex);
				s_log.error(msg, ex);
			}
		}
	}
}
