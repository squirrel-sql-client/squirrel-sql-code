package net.sourceforge.squirrel_sql.plugins.sessionscript;
/*
 * Copyright (C) 2002 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

/**
 * XML cache of SQL scripts.
 * 
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasScriptCache
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(AliasScriptCache.class);

	/** Current plugin. */
	private SessionScriptPlugin _plugin;

	/** Cache that contains data. */
	private XMLObjectCache _cache = new XMLObjectCache();

	/** File name to save scripts to. */
	private String _scriptsFileName;

	/**
	 * Ctor. Loads scripts from the XML document.
	 * 
	 * @param	sqlScriptPlugin	Current plugin.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT> <TT>SQLScriptPlugin</TT> passed.
	 */
	public AliasScriptCache(SessionScriptPlugin plugin)
		throws IOException
	{
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null SessionScriptPlugin passed");
		}

		_plugin = plugin;

		_scriptsFileName = _plugin.getPluginUserSettingsFolder().getAbsolutePath()
							+ File.separator + "session-scripts.xml";
	}

	/**
	 * Return the <TT>AliasScript</TT> for the passed <TT>ISQLAlias</TT>.
	 * 
	 * @param	alias	<TT>SQLALias</TT> to retrieve collection of scripts for.
	 * 
	 * @throws	IllegalArgumentException	Thrown if null<TT>ISQLAlias</TT> passed.
	 * 
	 * @throws	InternalError	Thrown if we try to add a script for an alias
	 * 							and one already exists. Programming error.
	 */
	public synchronized AliasScript get(ISQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLALias == null");
		}

		AliasScript script = (AliasScript)_cache.get(AliasScript.class, alias.getIdentifier());
		if (script == null)
		{
			script = new AliasScript(alias);
			try
			{
				_cache.add(script);
			}
			catch (DuplicateObjectException ex)
			{
				// This should never happen as we check above for the duplicate.
				throw new InternalError(ex.getMessage());
			}
		}
		return script;
	}

	/**
	 * Load scripts from XML document.
	 */
	public synchronized void load()
	{
		try
		{
			_cache.load(_scriptsFileName, getClass().getClassLoader());
		}
		catch (FileNotFoundException ignore)
		{ // first time user has run pgm.
		}
		catch (XMLException ex)
		{
			String msg = "Error loading scripts file: " + _scriptsFileName;
			s_log.error(msg, ex);
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
		catch (DuplicateObjectException ex)
		{
			String msg = "Error loading scripts file: " + _scriptsFileName;
			s_log.error(msg, ex);
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
	}

	/**
	 * Save scripts.
	 */
	public synchronized void save()
	{
		try
		{
			_cache.save(_scriptsFileName);
		}
		catch (IOException ex)
		{
			String msg = "Error occured saving scripts to " + _scriptsFileName;
			s_log.error(msg, ex);
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
		catch (XMLException ex)
		{
			String msg = "Error occured saving scripts to " + _scriptsFileName;
			s_log.error(msg, ex);
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
	}
}
