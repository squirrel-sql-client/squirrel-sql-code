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
package net.sourceforge.squirrel_sql.client.util;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;

/**
 * This class wraps access to ApplicationFiles presenting an interface that consists of FileWrappers instead
 * of Files.
 * 
 * @author manningr
 */
public class ApplicationFileWrappersImpl implements ApplicationFileWrappers
{
	private FileWrapperFactory _fileWrapperFactory = new FileWrapperFactoryImpl();
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#setFileWrapperFactory(net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory)
	 */
	public void setFileWrapperFactory(FileWrapperFactory factory) {
		_fileWrapperFactory = factory;
	}
	
	private ApplicationFiles _appFiles = new ApplicationFiles();
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#setApplicationFiles(net.sourceforge.squirrel_sql.client.util.ApplicationFiles)
	 */
	public void setApplicationFiles(ApplicationFiles files) {
		_appFiles = files;
	}
	
	public ApplicationFileWrappersImpl() {
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getUserSettingsDirectory()
	 */
	public FileWrapper getUserSettingsDirectory() {
		return _fileWrapperFactory.create(_appFiles.getUserSettingsDirectory());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getPluginsDirectory()
	 */
	public FileWrapper getPluginsDirectory() {
		return _fileWrapperFactory.create(_appFiles.getPluginsDirectory());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getLibraryDirectory()
	 */
	public FileWrapper getLibraryDirectory() {
		return _fileWrapperFactory.create(_appFiles.getLibraryDirectory());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getUpdateDirectory()
	 */
	public FileWrapper getUpdateDirectory() {
		return _fileWrapperFactory.create(_appFiles.getUpdateDirectory());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getDatabaseAliasesFile()
	 */
	public FileWrapper getDatabaseAliasesFile() {
		return _fileWrapperFactory.create(_appFiles.getDatabaseAliasesFile());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getDatabaseAliasesTreeStructureFile()
	 */
	public FileWrapper getDatabaseAliasesTreeStructureFile() {
		return _fileWrapperFactory.create(_appFiles.getDatabaseAliasesTreeStructureFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getDatabaseAliasesFile_before_version_2_3()
	 */
	public FileWrapper getDatabaseAliasesFile_before_version_2_3() {
		return _fileWrapperFactory.create(_appFiles.getDatabaseAliasesFile_before_version_2_3());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getDatabaseDriversFile()
	 */
	public FileWrapper getDatabaseDriversFile() {
		return _fileWrapperFactory.create(_appFiles.getDatabaseDriversFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getUserPreferencesFile()
	 */
	public FileWrapper getUserPreferencesFile() {
		return _fileWrapperFactory.create(_appFiles.getUserPreferencesFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getCellImportExportSelectionsFile()
	 */
	public FileWrapper getCellImportExportSelectionsFile() {
		return _fileWrapperFactory.create(_appFiles.getCellImportExportSelectionsFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getDTPropertiesFile()
	 */
	public FileWrapper getDTPropertiesFile() {
		return _fileWrapperFactory.create(_appFiles.getDTPropertiesFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getEditWhereColsFile()
	 */
	public FileWrapper getEditWhereColsFile() {
		return _fileWrapperFactory.create(_appFiles.getEditWhereColsFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getExecutionLogFile()
	 */
	public FileWrapper getExecutionLogFile() {
		return _fileWrapperFactory.create(_appFiles.getExecutionLogFile());
	}
		
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getJDBCDebugLogFile()
	 */
	public FileWrapper getJDBCDebugLogFile() {
		return _fileWrapperFactory.create(_appFiles.getJDBCDebugLogFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getUserSQLHistoryFile()
	 */
	public FileWrapper getUserSQLHistoryFile() {
		return _fileWrapperFactory.create(_appFiles.getUserSQLHistoryFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getSquirrelHomeDir()
	 */
	public FileWrapper getSquirrelHomeDir() {
		return _fileWrapperFactory.create(_appFiles.getSquirrelHomeDir());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getPluginsUserSettingsDirectory()
	 */
	public FileWrapper getPluginsUserSettingsDirectory() {
		return _fileWrapperFactory.create(_appFiles.getPluginsUserSettingsDirectory());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getQuickStartGuideFile()
	 */
	public FileWrapper getQuickStartGuideFile() {
		return _fileWrapperFactory.create(_appFiles.getQuickStartGuideFile());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getFAQFile()
	 */
	public FileWrapper getFAQFile() {
		return _fileWrapperFactory.create(_appFiles.getFAQFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getChangeLogFile()
	 */
	public FileWrapper getChangeLogFile() {
		return _fileWrapperFactory.create(_appFiles.getChangeLogFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getLicenceFile()
	 */
	public FileWrapper getLicenceFile() {
		return _fileWrapperFactory.create(_appFiles.getLicenceFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getWelcomeFile()
	 */
	public FileWrapper getWelcomeFile() {
		return _fileWrapperFactory.create(_appFiles.getWelcomeFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getSQuirrelJarFile()
	 */
	public FileWrapper getSQuirrelJarFile() {
		return _fileWrapperFactory.create(_appFiles.getSQuirrelJarFile());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers#getFwJarFile()
	 */
	public FileWrapper getFwJarFile() {
		return _fileWrapperFactory.create(_appFiles.getFwJarFile());
	}
	
	
}
