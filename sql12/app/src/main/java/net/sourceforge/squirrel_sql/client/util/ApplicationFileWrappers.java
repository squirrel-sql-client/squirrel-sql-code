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

public interface ApplicationFileWrappers
{

	void setFileWrapperFactory(FileWrapperFactory factory);

	void setApplicationFiles(ApplicationFiles files);

	FileWrapper getUserSettingsDirectory();

	FileWrapper getPluginsDirectory();

	FileWrapper getLibraryDirectory();

	FileWrapper getUpdateDirectory();

	FileWrapper getDatabaseAliasesFile();

	FileWrapper getDatabaseAliasesTreeStructureFile();

	FileWrapper getDatabaseAliasesFile_before_version_2_3();

	FileWrapper getDatabaseDriversFile();

	FileWrapper getUserPreferencesFile();

	FileWrapper getCellImportExportSelectionsFile();

	FileWrapper getDTPropertiesFile();

	FileWrapper getEditWhereColsFile();

	FileWrapper getExecutionLogFile();

	FileWrapper getJDBCDebugLogFile();

	FileWrapper getUserSQLHistoryFile();

	FileWrapper getSquirrelHomeDir();

	FileWrapper getPluginsUserSettingsDirectory();

	FileWrapper getQuickStartGuideFile();

	FileWrapper getFAQFile();

	FileWrapper getChangeLogFile();

	FileWrapper getLicenceFile();

	FileWrapper getWelcomeFile();

	FileWrapper getSQuirrelJarFile();

	FileWrapper getFwJarFile();

}