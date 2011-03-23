/*
 * Copyright (C) 2005 Gerd Wagner
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

package net.sourceforge.squirrel_sql.plugins.syntax;

import java.io.File;
import java.util.Hashtable;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

public class AutoCorrectProviderImpl
{
	private FileWrapper _pluginUserSettingsFolder;

	private AutoCorrectData _autoCorrectData;

	private Hashtable<String, String> _emptyHashtable = new Hashtable<String, String>();

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private FileWrapperFactory _fileWrapperFactory = new FileWrapperFactoryImpl();

	public static final String AUTO_CORRECT_DATA_FILE_NAME = "autocorrectdata.xml";

	AutoCorrectProviderImpl(FileWrapper pluginUserSettingsFolder)
	{
		_pluginUserSettingsFolder = pluginUserSettingsFolder;
	}

	public Hashtable<String, String> getAutoCorrects()
	{
		AutoCorrectData acd = getAutoCorrectData();

		if (acd.isEnableAutoCorrects())
		{
			return acd.getAutoCorrectsHash();
		}
		else
		{
			return _emptyHashtable;
		}
	}

	public AutoCorrectData getAutoCorrectData()
	{
		try
		{
			if (null == _autoCorrectData)
			{
				XMLBeanReader br = new XMLBeanReader();

				FileWrapper path =
					_fileWrapperFactory.create(_pluginUserSettingsFolder, AUTO_CORRECT_DATA_FILE_NAME);

				if (path.exists())
				{
					br.load(path, this.getClass().getClassLoader());
					_autoCorrectData = (AutoCorrectData) br.iterator().next();
				}
				else
				{
					_autoCorrectData = getDefaultAutoCorrectData();
				}
			}

			return _autoCorrectData;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

	}

	private AutoCorrectData getDefaultAutoCorrectData()
	{
		Hashtable<String, String> ret = new Hashtable<String, String>();
		ret.put("SLECT", "SELECT");
		ret.put("FORM", "FROM");
		ret.put("WERE", "WHERE");
		ret.put("SF", "SELECT * FROM");

		return new AutoCorrectData(ret, true);

	}

	public void setAutoCorrects(Hashtable<String, String> newAutoCorrects, boolean enableAutoCorrects)
	{
		try
		{
			_autoCorrectData = new AutoCorrectData(newAutoCorrects, enableAutoCorrects);
			XMLBeanWriter bw = new XMLBeanWriter(_autoCorrectData);
			bw.save(_pluginUserSettingsFolder.getPath() + File.separator + AUTO_CORRECT_DATA_FILE_NAME);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param fileWrapperFactory
	 *           the fileWrapperFactory to set
	 */
	public void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		Utilities.checkNull("setFileWrapperFactory", "fileWrapperFactory", fileWrapperFactory);
		this._fileWrapperFactory = fileWrapperFactory;
	}

}
