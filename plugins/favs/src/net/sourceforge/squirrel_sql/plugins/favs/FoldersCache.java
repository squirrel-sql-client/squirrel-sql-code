package net.sourceforge.squirrel_sql.plugins.favs;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
/**
 * XML cache of <CODE>Folder</CODE> objects.
 */
public final class FoldersCache
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(FoldersCache.class);

	/** Application API. */
    @SuppressWarnings("unused")
	private IApplication _app;

	/** Root folder. */
	private Folder _rootFolder = null;

	private String _queriesFileName;

	public FoldersCache(IApplication app, File userSettingsFolder)
	{
		super();
		_app = app;
		_queriesFileName = userSettingsFolder.getAbsolutePath() + File.separator + "queries.xml";
	}

	public Folder getRootFolder()
	{
		return _rootFolder;
	}

	public void setRootFolder(Folder folder)
	{
		_rootFolder = folder;
	}
	void load()
{
		try
		{
			if (new File(_queriesFileName).exists())
			{
				XMLObjectCache<Folder> cache = new XMLObjectCache<Folder>();
				cache.load(_queriesFileName, getClass().getClassLoader());
				Iterator<Folder> it = cache.getAllForClass(Folder.class);
				if (it.hasNext())
				{
					_rootFolder = it.next();
				}
			}
		}
		catch (FileNotFoundException ignore)
		{
			// first time user has run pgm.
		}
		catch (XMLException ex)
		{
			s_log.error("Error loading queries file: " + _queriesFileName, ex);
		}
		catch (DuplicateObjectException ex)
		{
			s_log.error("Error loading queries file: " + _queriesFileName, ex);
		}
	}
	/**
	 * Save cached objects.
	 */
	void save()
	{
		try
		{
			XMLObjectCache<Folder> cache = new XMLObjectCache<Folder>();
			try
			{
				if (_rootFolder != null)
				{
					cache.add(_rootFolder);
				}
			}
			catch (DuplicateObjectException ignore)
			{
			}
			cache.save(_queriesFileName);
		}
		catch (IOException ex)
		{
			s_log.error("Error occured saving queries to " + _queriesFileName, ex);
		}
		catch (XMLException ex)
		{
			s_log.error("Error occured saving queries to " + _queriesFileName, ex);
		}
	}
}
