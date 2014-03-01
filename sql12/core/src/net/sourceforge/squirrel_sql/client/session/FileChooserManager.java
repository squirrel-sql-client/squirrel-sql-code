package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

import javax.swing.*;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class FileChooserManager
{

   private static final String PREF_PRE_SELECTED_FILE_FILTER = "Squirrel.filechoosermanager.preselfilefilter";
   public static final String FILE_ENDING_TXT = ".txt";
   public static final String FILE_ENDING_SQL = ".sql";
   public static final String FILE_ENDING_NONE = "FILE_ENDING_NONE";


   private JFileChooser _fileChooser;

   private HashMap<FileExtensionFilter, String> _fileAppenixes = new HashMap<FileExtensionFilter, String>();



   public FileChooserManager()
   {
      _fileChooser = new JFileChooser();

      FileExtensionFilter txtFilter = new FileExtensionFilter("Text files", new String[]{FILE_ENDING_TXT});
      _fileChooser.addChoosableFileFilter(txtFilter);
      _fileAppenixes.put(txtFilter, FILE_ENDING_TXT);

      FileExtensionFilter sqlFilter = new FileExtensionFilter("SQL files", new String[]{FILE_ENDING_SQL});
      _fileChooser.addChoosableFileFilter(sqlFilter);
      _fileAppenixes.put(sqlFilter, FILE_ENDING_SQL);


      String fileEndingPref = Preferences.userRoot().get(PREF_PRE_SELECTED_FILE_FILTER, FILE_ENDING_NONE);

      if(FILE_ENDING_SQL.equals(fileEndingPref))
      {
         _fileChooser.setFileFilter(sqlFilter);
      }
      else if(FILE_ENDING_TXT.equals(fileEndingPref))
      {
         _fileChooser.setFileFilter(txtFilter);
      }
   }

   public String getSelectedFileEnding()
   {
      return _fileAppenixes.get(_fileChooser.getFileFilter());
   }

   public JFileChooser getFileChooser()
   {
      return _fileChooser;
   }

   public void saveWasApproved()
   {
      if (null != getSelectedFileEnding())
      {
         Preferences.userRoot().put(PREF_PRE_SELECTED_FILE_FILTER, getSelectedFileEnding());
      }
      else
      {
         Preferences.userRoot().put(PREF_PRE_SELECTED_FILE_FILTER, FILE_ENDING_NONE);
      }
   }
}
