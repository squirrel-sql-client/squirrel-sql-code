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

import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

import net.sourceforge.squirrel_sql.client.IApplication;

import net.sourceforge.squirrel_sql.plugins.sessionscript.SessionScriptPlugin;

/**
 * XML cache of SQL scripts.
 */
public class SessionScriptCache {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SessionScriptCache.class);

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
	public SessionScriptCache(SessionScriptPlugin plugin) throws IOException {
		super();
		if (plugin == null) {
			throw new IllegalArgumentException("Null SessionScriptPlugin passed");
		}
	
		_plugin = plugin;

		_scriptsFileName = _plugin.getPluginUserSettingsFolder().getAbsolutePath() +
									File.separator + "session-scripts.xml";
	}

	/**
	 * Load scripts from XML document.
	 */
	public void load() {
		try {
			_cache.load(_scriptsFileName, getClass().getClassLoader());
		} catch (FileNotFoundException ignore) { // first time user has run pgm.
		} catch (XMLException ex) {
			s_log.error("Error loading aliases file: " + _scriptsFileName, ex);
		} catch (DuplicateObjectException ex) {
			s_log.error("Error loading aliases file: " + _scriptsFileName, ex);
		}
// Create dummy script for testing purposes.
//		try {
//SessionScript ss = new SessionScript();
//ss.setScript("set dateformat(\"*DMY\")");
//_cache.add(ss);
//		} catch(Exception ignore) {}
	}

	/**
	 * Save scripts.
	 */
	public void save() {
		try {
			_cache.save(_scriptsFileName);
		} catch (IOException ex) {
			s_log.error("Error occured saving scripts to " + _scriptsFileName, ex);
		} catch (XMLException ex) {
			s_log.error("Error occured saving scripts to " + _scriptsFileName, ex);
		}
	}
}

