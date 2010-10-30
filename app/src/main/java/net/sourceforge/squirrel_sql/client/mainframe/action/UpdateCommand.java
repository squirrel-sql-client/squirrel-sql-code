package net.sourceforge.squirrel_sql.client.mainframe.action;
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
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.client.update.UpdateControllerFactory;
import net.sourceforge.squirrel_sql.client.update.UpdateControllerFactoryImpl;
import net.sourceforge.squirrel_sql.client.update.UpdateUtilImpl;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactoryImpl;
import net.sourceforge.squirrel_sql.fw.gui.JOptionPaneService;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
/**
 * This <CODE>ICommand</CODE> allows the user to check for updates and apply changes to the currently 
 * installed software.
 */
public class UpdateCommand implements ICommand
{
	/** Application API. */
	private IApplication _app;

	/** The factory that creates the UpdateController or re-uses the one previously created one */
	private UpdateControllerFactory updateControllerFactory = new UpdateControllerFactoryImpl();
	
	/**
	 * Ctor.
	 *
	 * @param	app	 Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public UpdateCommand(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
	}

	/**
	 * Display the software update dialog
	 */
	public void execute()
	{
	   UpdateController updateController = 
	   	updateControllerFactory.createUpdateController(_app,  new ArtifactDownloaderFactoryImpl(),
	   		new UpdateUtilImpl(), new JOptionPaneService(), new FileWrapperFactoryImpl());
	   updateController.showUpdateDialog();
	}
}
