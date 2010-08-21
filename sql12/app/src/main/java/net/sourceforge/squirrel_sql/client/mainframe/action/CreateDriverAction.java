package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.DriversListInternalFrame;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
/**
 * This <CODE>Action</CODE> allows the user to create a new <TT>ISQLDriver</TT>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CreateDriverAction extends SquirrelAction
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(CreateDriverAction.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CreateDriverAction.class);
    
	/**
	 * Ctor.
	 *
	 * @param	app	 Application API.
	 */
	public CreateDriverAction(IApplication app)
	{
		super(app);
	}

	/**
	 * Perform this action. Execute the create driver command.
	 *
	 * @param	evt	 The current event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		IApplication app = getApplication();
		DriversListInternalFrame tw = app.getWindowManager().getDriversListInternalFrame();
		tw.moveToFront();
		try
		{
			tw.setSelected(true);
		}
		catch (PropertyVetoException ex)
		{
            //i18n[CreateDriverAction.error.selectingwindow=Error selecting window]
			s_log.error(s_stringMgr.getString("CreateDriverAction.error.selectingwindow"), ex);
		}
            
        try {
            final URL url = app.getResources().getDefaultDriversUrl();
            net.sourceforge.squirrel_sql.client.gui.db.DataCache cache = _app.getDataCache();
            ISQLDriver[] missingDrivers = cache.findMissingDefaultDrivers(url);
            if (missingDrivers != null) {
                String msg =
                    s_stringMgr.getString("CreateDriverAction.confirm");
                if (Dialogs.showYesNo(_app.getMainFrame(), msg)) {
                    for (int i = 0; i < missingDrivers.length; i++) {
                        try {
                            cache.addDriver(missingDrivers[i], null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                }
            }
        } catch (XMLException e) {
            // i18n[CreateDriverAction.error.loadDefaultDrivers]
            String msg = 
                s_stringMgr.getString("CreateDriverAction.error.loadDefaultDrivers");
            s_log.error(msg, e);
        } catch (IOException e) {
            // i18n[CreateDriverAction.error.loadDefaultDrivers]
            String msg = 
                s_stringMgr.getString("CreateDriverAction.error.loadDefaultDrivers");
            s_log.error(msg, e);
        }

		new CreateDriverCommand(app).execute();
	}
}
