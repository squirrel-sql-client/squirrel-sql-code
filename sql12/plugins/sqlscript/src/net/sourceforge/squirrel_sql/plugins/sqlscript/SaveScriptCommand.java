package net.sourceforge.squirrel_sql.plugins.sqlscript;

/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.HashMap;

import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class SaveScriptCommand
{

	/** The session that we are saving a script for. */
	private final ISession _session;

	/** The current plugin. */
	private SQLScriptPlugin _plugin;

	private File _toSaveTo = null;

	/**
	 * Ctor.
	 *
	 * @param   session The session that we are saving a script for.
	 * @param   plugin  The current plugin.
	 *
	 * @throws  IllegalArgumentException
	 *              Thrown if a <TT>null</TT> <TT>ISession</TT> or <TT>IPlugin</TT>
	 *              passed.
	 */
	public SaveScriptCommand(ISession session, SQLScriptPlugin plugin)
			throws IllegalArgumentException
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null IPlugin passed");
		}
		_session = session;
		_plugin = plugin;
	}

	/**
	 * Display the properties dialog.
	 */
	public void execute(Frame frame, boolean toNewFile)
	{
		if (_session != null)
		{
			if(toNewFile)
			{
				_toSaveTo = null;
			}


			JFileChooser chooser = new JFileChooser();

			HashMap fileAppenixes = new HashMap();
			FileExtensionFilter filter;
			filter = new FileExtensionFilter("Text files", new String[]{".txt"});
			chooser.addChoosableFileFilter(filter);
			fileAppenixes.put(filter, ".txt");

			filter = new FileExtensionFilter("SQL files", new String[]{".sql"});
			chooser.addChoosableFileFilter(filter);
			fileAppenixes.put(filter, ".sql");

			SQLScriptPreferences prefs = _plugin.getPreferences();

			for (; ;)
			{
				if (prefs.getOpenInPreviousDirectory())
				{
					String dirName = prefs.getPreviousDirectory();
					if (dirName != null)
					{
						chooser.setCurrentDirectory(new File(dirName));
					}
				}

				_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);

				if(null != _toSaveTo)
				{
					saveScript(frame, _toSaveTo, false);
					break;
				}

				if (chooser.showSaveDialog(frame) == chooser.APPROVE_OPTION)
				{
					_toSaveTo = chooser.getSelectedFile();

					if(!_toSaveTo.exists() && null != fileAppenixes.get(chooser.getFileFilter()))
					{
						if(   !_toSaveTo.getAbsolutePath().endsWith( fileAppenixes.get(chooser.getFileFilter()).toString() )   )
						{
							_toSaveTo = new File(_toSaveTo.getAbsolutePath() + fileAppenixes.get(chooser.getFileFilter()));
						}
					}

					if (saveScript(frame, _toSaveTo, true))
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
		}
	}

	private boolean saveScript(Frame frame, File file, boolean askReplace)
	{
		boolean doSave = false;
		if (askReplace && file.exists())
		{
			doSave =
					Dialogs.showYesNo(
							frame,
							file.getAbsolutePath() + "\nalready exists. Do you want to replace it?");
			//i18n
			if (!doSave)
			{
				return false;
			}
			if (!file.canWrite())
			{
				Dialogs.showOk(
						frame,
						"File " + file.getAbsolutePath() + "\ncannot be written to.");
				//i18n
				return false;
			}
			file.delete();
		}
		else
		{
			doSave = true;
		}

		if (doSave)
		{
			_plugin.getPreferences().setPreviousDirectory(file.getParent());

			_session.putPluginObject(
					_plugin,
					ISessionKeys.SAVE_SCRIPT_FILE_PATH_KEY,
					file.getAbsolutePath());
			FileOutputStream fos = null;
			try
			{
				fos = new FileOutputStream(file);
//                String sScript = _session.getEntireSQLScript();
				String sScript = _session.getSQLPanelAPI(_plugin).getEntireSQLScript();
				fos.write(sScript.getBytes());
				_session.getMessageHandler().showMessage(
						"SQL script saved to " + file.getAbsolutePath());
			}
			catch (IOException ex)
			{
				_session.getMessageHandler().showErrorMessage(ex);
			}
			finally
			{
				if (fos != null)
				{
					try
					{
						fos.close();
					}
					catch (IOException ignore)
					{
					}
				}
			}
		}
		return true;
	}

	public void setLoadedFile(File file)
	{
		_toSaveTo = file;
	}
}