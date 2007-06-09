package net.sourceforge.squirrel_sql.client.session.action;
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This <CODE>Action</CODE> dumps the current session status to an XML file.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DumpSessionAction extends SquirrelAction
											implements ISessionAction
{
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DumpSessionAction.class);  
    
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(DumpSessionAction.class);

	/** Current session. */
	private ISession _session;

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
	public void setSession(ISession session)
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
        // i18n[DumpSessionAction.warning=<HTML><BODY><B>Warning:</B> Plain<BR>text passwords<BR>may be saved<BR>in this file.</BODY></HTML>]
        String label = s_stringMgr.getString("DumpSessionAction.warning");
		final JLabel lbl = new JLabel(label);
		lbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		final File outFile = Dialogs.selectFileForWriting(parentFrame, filters, lbl);
		if (outFile != null)
		{
			DumpSessionCommand cmd = new DumpSessionCommand(outFile);
			cmd.setSession(_session);
			try
			{
				cmd.execute();
                
                // i18n[DumpSessionAction.success=Session successfuly dumped to: {0}]
				final String msg = 
                    s_stringMgr.getString("DumpSessionAction.success",
                                          outFile.getAbsolutePath()); 

                _session.showMessage(msg);
			}
			catch (Throwable ex)
			{
			    // i18n[DumpSessionAction.error=Error occured dumping session: {0}]
                final String msg = 
                    s_stringMgr.getString("DumpSessionAction.error", ex);
				_session.showErrorMessage(msg);
				s_log.error(msg, ex);
			}
		}
	}
}
