package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2006 Rob Manning
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
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.DriversListInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.IDriversList;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This <CODE>Action</CODE> allows the user to create a new <TT>ISQLDriver</TT>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ShowDriverWebsiteAction extends SquirrelAction
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ShowDriverWebsiteAction.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ShowDriverWebsiteAction.class);
    
    /**
     * List of all the users drivers.
     */
    private IDriversList _drivers;

	/**
	 * Ctor.
	 *
	 * @param	app	 Application API.
	 */
	public ShowDriverWebsiteAction(IApplication app, IDriversList list)
	{
		super(app);
        if (list == null)
        {
            throw new IllegalArgumentException("Null DriversList passed");
        }        
        _drivers = list;
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
            
        ISQLDriver driver = _drivers.getSelectedDriver();
        if (driver != null)
        {
            new ShowDriverWebsiteCommand(app, driver).execute();
        }
	}
}
