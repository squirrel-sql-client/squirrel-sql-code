/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.client.update;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactory;
import net.sourceforge.squirrel_sql.fw.gui.JOptionPaneService;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;

public class UpdateControllerFactoryImpl implements UpdateControllerFactory
{

	private static UpdateControllerImpl _instance = null;

	public UpdateController createUpdateController(IApplication app,
		ArtifactDownloaderFactory downloaderFactory, UpdateUtil util, JOptionPaneService jOptionPaneService,
		FileWrapperFactory fileWrapperFactory)
	{
		if (_instance == null)
		{
			_instance = new UpdateControllerImpl(app);
			_instance.setArtifactDownloaderFactory(downloaderFactory);
			_instance.setUpdateUtil(util);
			_instance.setJOptionPaneService(jOptionPaneService);
			_instance.setFileWrapperFactory(fileWrapperFactory);
		}
		return _instance;
	}

}
