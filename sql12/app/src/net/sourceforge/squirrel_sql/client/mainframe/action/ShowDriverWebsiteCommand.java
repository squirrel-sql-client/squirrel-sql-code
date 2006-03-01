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
import java.awt.Frame;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This <CODE>ICommand</CODE> allows the user to launch the default
 * web-browser to view the driver's website.
 *
 * @author <A HREF="mailto:manningr@users.sourceforge.net">Rob Manning</A>
 */
public class ShowDriverWebsiteCommand implements ICommand
{
	/** Application API. */
	private final IApplication _app;

	/** <TT>ISQLDriver</TT> to view the website for. */
	private final ISQLDriver _sqlDriver;

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ShowDriverWebsiteCommand.class);
    
    /** Owner of the maintenance dialog. */
    private Frame _frame;

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	sqlDriver	<TT>ISQLDriver</TT> to be copied.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLDriver</TT> passed.
	 */
	public ShowDriverWebsiteCommand(IApplication app, ISQLDriver sqlDriver)
		throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (sqlDriver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		_app = app;
		_sqlDriver = sqlDriver;
	}

	public void execute()
	{
        String url = _sqlDriver.getWebSiteUrl();
        if (url == null || "".equals(url)) {
            // prompt the user to add a website url for this driver
            final Object[] args = {_sqlDriver.getName()};
            // i18n[ShowDriverWebsiteCommand.comfirm=No WebSite URL for the 
            // specified driver.  Would you like to add one?]
            String msg = 
                s_stringMgr.getString("ShowDriverWebsiteCommand.comfirm", args);
            if (Dialogs.showYesNo(_frame, msg)) {
                new ModifyDriverCommand(_app, _sqlDriver).execute();
                url = _sqlDriver.getUrl();
            }
        } 
        if (url != null && !"".equals(url)) {
            _app.openURL(url);
        }
	}
}
