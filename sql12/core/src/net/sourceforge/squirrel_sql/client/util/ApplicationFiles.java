package net.sourceforge.squirrel_sql.client.util;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.util.IJavaPropertyNames;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;

/**
 * This class contains information about files and directories used by the
 * application.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ApplicationFiles
{
   public static final String STANDARD_RELATIVE_USER_DIR = ".squirrel-sql";
   public static final String ALIASES_FILE_NAME = "SQLAliases23";
   public static final String ALIASES_FILE_NAME_EXTENSION = "xml";

   private static ILogger s_log = LoggerController.createLogger(ApplicationFiles.class);

   /**
    * Name of directory to contain users settings.
    */
   private String _userSettingsDir;

   /**
    * Name of folder that contains Squirrel app.
    */
   private final File _squirrelHomeDir;

   /**
    * Name of folder that contains plugins.
    */
   private String _squirrelPluginsDir;

   /**
    * Documentation folder.
    */
   private String _documentationDir;

   /**
    * Name of folder that contains library jars
    */
   private String _libraryDir;

   /**
    * Name of folder that contains update files
    */
   private String _updateDir;

   /**
    * Flag for cleaning up execution log files on app entry.
    **/
   private static boolean needExecutionLogCleanup = true;

   /**
    * Flag for cleaning up debug log files on app entry.
    **/
   private static boolean needDebugLogCleanup = true;

   /**
    * Ctor.
    */
   public ApplicationFiles()
   {
      ApplicationArguments args = ApplicationArguments.getInstance();

      final String homeDir = args.getSquirrelHomeDirectory();
      _squirrelHomeDir = homeDir != null ? new File(homeDir) : getDefaultSquirrelHomeDir();
      String homeDirPath = _squirrelHomeDir.getPath() + File.separator;
      _squirrelPluginsDir = homeDirPath + "plugins";
      _documentationDir = homeDirPath + "doc";
      _libraryDir = homeDirPath + "lib";
      _updateDir = homeDirPath + "update";

      _userSettingsDir = args.getUserSettingsDirectoryOverride();
      if (_userSettingsDir == null)
      {
         _userSettingsDir = getStandardUserDir();
      }
      try
      {
         new File(_userSettingsDir).mkdirs();
      }
      catch (Exception ex)
      {
         System.out.println("Error creating user settings directory: " + _userSettingsDir);
         ex.printStackTrace();
      }
      try
      {
         final File logsDir = getExecutionLogFile().getParentFile();
         logsDir.mkdirs();
      }
      catch (Exception ex)
      {
         System.out.println("Error creating logs directory: " + getExecutionLogFile().getParentFile());
         ex.printStackTrace();
      }
      try
      {
         final File savedSessionsDir = getSavedSessionsDir();
         savedSessionsDir.mkdirs();
      }
      catch (Exception ex)
      {
         System.out.println("Error creating saved sessions directory: " + getSavedSessionsDir());
         ex.printStackTrace();
      }
   }

   private static File getDefaultSquirrelHomeDir()
   {
      try
      {
         ProtectionDomain protectionDomain = ApplicationFiles.class.getProtectionDomain();
         CodeSource codeSource = protectionDomain.getCodeSource();
         URL location = codeSource.getLocation();
         URI uri = location.toURI();
         return new File(uri).getParentFile();

      }
      catch (Exception ex)
      {
         s_log.warn("Application home directory is neither specified nor could be established; using current directory as best guess", ex);

         return new File(System.getProperty(IJavaPropertyNames.USER_DIR));
      }
   }

   public static String getStandardUserDir()
   {
      return System.getProperty(IJavaPropertyNames.USER_HOME)  + File.separator + STANDARD_RELATIVE_USER_DIR;
   }


   public File getUserSettingsDirectory()
   {
      return new File(_userSettingsDir);
   }

   public File getPluginsDirectory()
   {
      return new File(_squirrelPluginsDir);
   }

   public File getLibraryDirectory()
   {
      return new File(_libraryDir);
   }

   public File getUpdateDirectory()
   {
      return new File(_updateDir);
   }

   /**
    * @return file that contains database aliases.
    */
   public File getDatabaseAliasesFile()
   {
      return new File(_userSettingsDir + File.separator + ALIASES_FILE_NAME + "." + ALIASES_FILE_NAME_EXTENSION);
   }

   public File getDatabaseAliasesBackupDir()
   {
      return new File(_userSettingsDir + File.separator + "sqlAliases23Backup" + File.separator);
   }


   public File getDatabaseAliasesTreeStructureFile()
   {
      return new File(_userSettingsDir + File.separator + ALIASES_FILE_NAME + "_treeStructure.xml");
   }

   public File getRecentFilesXmlBeanFile_oldXmlVersion()
   {
      return new File(_userSettingsDir + File.separator + "RecentFilesXmlBean.xml");
   }

   public File getRecentFilesJsonBeanFile()
   {
      return new File(_userSettingsDir + File.separator + "RecentFilesJsonBean.json");
   }

   public File getShortCutsJsonBeanFile()
   {
      return new File(_userSettingsDir + File.separator + "ShortcutsJsonBean.json");
   }


   public File getDatabaseAliasesFile_before_version_2_3()
   {
      return new File(_userSettingsDir + File.separator + "SQLAliases.xml");
   }


   /**
    * @return file that contains JDBC driver definitions.
    */
   public File getDatabaseDriversFile()
   {
      return new File(_userSettingsDir + File.separator + "SQLDrivers.xml");
   }

   /**
    * @return file that contains JDBC driver definitions.
    */
   public File getUserPreferencesFile()
   {
      return new File(_userSettingsDir + File.separator + "prefs.xml");
   }

   /**
    * @return file that contains the selections user chose for Cell import/export.
    */
   public File getCellImportExportSelectionsFile()
   {
      return new File(_userSettingsDir + File.separator + "cellImportExport.xml");
   }

   /**
    * @return file that contains the selections user chose
    * for DataType-specific properties.
    */
   public File getDTPropertiesFile()
   {
      return new File(_userSettingsDir + File.separator + "DTproperties.xml");
   }

   /**
    * @return file that contains the selections user chose for specific columns to use
    * in the WHERE clause when editing a cell in a DB table.
    */
   public File getEditWhereColsFile()
   {
      return new File(_userSettingsDir + File.separator + "editWhereCols.xml");
   }

   /**
    * @return file to log execution information to.
    */
   public File getExecutionLogFile()
   {
      final String dirPath = _userSettingsDir + File.separator + "logs";
      final String logBaseName = "squirrel-sql.log";

      if (needExecutionLogCleanup)
      {
         // first time through this method in program, so go cleanup
         // old log files
         deleteOldFiles(dirPath, logBaseName);
         needExecutionLogCleanup = false;
      }
      return new File(dirPath + File.separator + logBaseName);
   }

   /**
    * @return file to log JDBC debug information to.
    */
   public File getJDBCDebugLogFile()
   {
      final String dirPath = _userSettingsDir + File.separator + "logs";
      final String logBaseName = "jdbcdebug.log";

      if (needDebugLogCleanup)
      {
         // first time through this method in program, so go cleanup
         // old log files
         deleteOldFiles(dirPath, logBaseName);
         needDebugLogCleanup = false;
      }
      return new File(dirPath + File.separator + logBaseName);
   }

   /**
    * @return file to log debug information to.
    */
//	public File getDebugLogFile()
//	{
//		return new File(_userSettingsDir + File.separator + "squirrel-sql-debug.log");
//	}

   /**
    * @return serialized Vector containing history of SQL queries executed
    */
   public File getUserSQLHistoryFile()
   {
      return new File(_userSettingsDir + File.separator + "sql_history.xml");
   }

   public File getSquirrelHomeDir()
   {
      return _squirrelHomeDir;
   }


   /**
    * @return directory that contains plugin specific user settings
    */
   public File getPluginsUserSettingsDirectory()
   {
      return new File(_userSettingsDir + File.separator + "plugins");
   }

   /**
    * @return the quickstart guide.
    */
   public File getQuickStartGuideFile()
   {
      return new File(_documentationDir + File.separator + "quick_start.html");
   }

   /**
    * @return the FAQ.
    */
   public File getFAQFile()
   {
      return new File(_documentationDir + File.separator + "faq.html");
   }

   /**
    * @return the changelog.
    */
   public File getChangeLogFile()
   {
      return new File(_documentationDir + File.separator + "changes.txt");
   }

   /**
    * @return the licence file.
    */
   public File getLicenceFile()
   {
      return new File(_documentationDir + File.separator + "licences/squirrel_licence.txt");
   }

   /**
    * @return the Welcome document..
    */
   public File getWelcomeFile()
   {
      return new File(_documentationDir + File.separator + "welcome.html");
   }

   /**
    * Internal method to remove old files such as log files.
    * The dirPath is the path name of the directory containing the files.
    * The fileBase is the base name of all files in the set to be culled,
    * i.e. this method removes old versions of files named <fileBase>*,
    * but not the file named <fileBase> or recent versions of that file.
    * It is assumed that files are named with dates such that the names of
    * older files are alphabetically before newer files.
    */
   private void deleteOldFiles(String dirPath, String fileBase)
   {

      // the number of files to keep is arbitrarilly set here
      final int numberToKeep = 3;

      // define filter to select only names using the fileBase
      class OldFileNameFilter implements FilenameFilter
      {
         String fBase;

         OldFileNameFilter(String fileBase)
         {
            fBase = fileBase;
         }

         public boolean accept(File dir, String name)
         {
            if (name.startsWith(fBase))
               return true;
            return false;
         }
      }

      // get the directory
      File dir = new File(dirPath);

      // create filename filter and attach to directory
      OldFileNameFilter fileFilter = new OldFileNameFilter(fileBase);

      // get list of files using that base name
      String fileNames[] = dir.list(fileFilter);
      if (fileNames == null || fileNames.length <= numberToKeep)
         return;   // not too many old files

      // we do not expect a lot of files in this directory,
      // so just do things linearly

      // sort the list
      Arrays.sort(fileNames);

      // If the file using the base name with no extention exists,
      // it is first.  The other files are in order from oldest to newest.
      // The set of files to delete is slightly different depending on
      // whether the base name file exists or not.
      int startIndex = 0;
      int endIndex = fileNames.length - numberToKeep;
      if (fileNames[0].equals(fileBase))
      {
         // since the base name file exists, we need to skip it
         // and bump up the endIndex
         startIndex = 1;
         endIndex++;
      }

      for (int i = startIndex; i < endIndex; i++)
      {
         // delete the old file
         File oldFile = new File(dirPath + File.separator + fileNames[i]);
         oldFile.delete();
      }
   }

   public File getSQuirrelJarFile()
   {
      File ret = new File(_squirrelHomeDir.getPath() + File.separator + "lib" + File.separator + "squirrel-sql.jar");

      if (false == ret.exists())
      {
         ret = new File(_squirrelHomeDir.getPath() + File.separator + "squirrel-sql.jar");
      }
      return ret;
   }


   /**
    * @return serialized List containing user specific configurations for WIKI tables.
    */
   public File getUserSpecificWikiConfigurationsFile()
   {
      return new File(_userSettingsDir + File.separator + "userSpecificWikiTableConfigurations.xml");
   }

   public File getPropsFile()
   {
      return new File(_userSettingsDir + File.separator + "props.properties");
   }

   public File getGitCommitMessageJsonBeanFile()
   {
      return new File(_userSettingsDir + File.separator + "gitCommitMessageJsonBean.json");
   }

   public File getRecentAliasesJsonBeanFile()
   {
      return new File(_userSettingsDir + File.separator + "recentAliasesJsonBean.json");
   }

   public File getSavedSessionsJsonFile()
   {
      return new File(getSavedSessionsDir(), "savedSessions.json");
   }
   public File getSavedSessionGroupsJsonFile()
   {
      return new File(getSavedSessionsDir(), "savedSessionGroups.json");
   }

   public File getSavedSessionsDir()
   {
      return new File(_userSettingsDir + File.separator + "savedSessions");
   }

   public File getPopupMenuAtticFile()
   {
      return new File(_userSettingsDir + File.separator + "popupMenuAttic.json");
   }

   public File getExcelTabOrFileNamesJsonBeanFile()
   {
      return new File(_userSettingsDir + File.separator + "excelTabOrFileNames.json");
   }

   public File getDBDiffPrefsJsonBeanFile()
   {
      return new File(_userSettingsDir + File.separator + "dbDiffPrefs.json");
   }

   public File getCatalogLoadModelJsonFile()
   {
      return new File(_userSettingsDir + File.separator + "catalogLoadModel.json");
   }

   public File getScriptPrefsJsonFile()
   {
      return new File(_userSettingsDir + File.separator + "scriptPrefs.json");
   }

   public File getObjectTreeSelectionStoreJsonFile()
   {
      return new File(_userSettingsDir + File.separator + "objectTreeSelectionStore.json");
   }

   public File getSyntaxPreferencesFile()
   {
      return new File(new ApplicationFiles().getUserSettingsDirectory(), "syntaxPrefs.xml");
   }

   public File getAutocorrectDataFile()
   {
      return new File(new ApplicationFiles().getUserSettingsDirectory(), "autocorrectdata.xml");
   }
}
