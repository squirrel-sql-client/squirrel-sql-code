package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

import javax.swing.*;
import java.util.HashMap;

public class FileChooserManager
{

   private static final String PREF_PRE_SELECTED_FILE_FILTER = "Squirrel.filechoosermanager.preselfilefilter";
   private static final String PREF_FILE_CHOOSER_VIEW_TYPE = "Squirrel.filechoosermanager.fileChooserViewType";
   public static final String FILE_ENDING_TXT = ".txt";
   public static final String FILE_ENDING_SQL = ".sql";
   public static final String FILE_ENDING_NONE = "FILE_ENDING_NONE";

   private HashMap<FileExtensionFilter, String> _fileAppenixes = new HashMap<>();
   private FileExtensionFilter _txtFilter;
   private FileExtensionFilter _sqlFilter;
   private boolean _saveFileFilterProps = true;


   private JFileChooser _currentFileChooser;


   public FileChooserManager()
   {
      _txtFilter = new FileExtensionFilter("Text files", new String[]{FILE_ENDING_TXT});
      _fileAppenixes.put(_txtFilter, FILE_ENDING_TXT);

      _sqlFilter = new FileExtensionFilter("SQL files", new String[]{FILE_ENDING_SQL});
      _fileAppenixes.put(_sqlFilter, FILE_ENDING_SQL);

      initNewFileChooser();
   }

   public String getSelectedFileEnding()
   {
      return _fileAppenixes.get(_currentFileChooser.getFileFilter());
   }

   public JFileChooser initNewFileChooser()
   {
      _currentFileChooser = new JFileChooser();
      _currentFileChooser.addChoosableFileFilter(_txtFilter);
      _currentFileChooser.addChoosableFileFilter(_sqlFilter);

      String fileEndingPref = Props.getString(PREF_PRE_SELECTED_FILE_FILTER, FILE_ENDING_NONE);

      if(FILE_ENDING_SQL.equals(fileEndingPref))
      {
         _currentFileChooser.setFileFilter(_sqlFilter);
      }
      else if(FILE_ENDING_TXT.equals(fileEndingPref))
      {
         _currentFileChooser.setFileFilter(_txtFilter);
      }

      String viewTypeName = Props.getString(PREF_FILE_CHOOSER_VIEW_TYPE, FileChooserViewType.DETAILS.name());
      FileChooserViewType.valueOf(viewTypeName).tryApplyViewType(_currentFileChooser);

      return _currentFileChooser;
   }

   public void saveDisplayPrefs()
   {
      FileChooserViewType fileChooserViewType = FileChooserViewType.tryExtractChooserViewType(_currentFileChooser);

      Props.putString(PREF_FILE_CHOOSER_VIEW_TYPE, fileChooserViewType.name());
   }

   public void saveWasApproved()
   {
      if(false == _saveFileFilterProps)
      {
         return;
      }


      if (null != getSelectedFileEnding())
      {
         Props.putString(PREF_PRE_SELECTED_FILE_FILTER, getSelectedFileEnding());
      }
      else
      {
         Props.putString(PREF_PRE_SELECTED_FILE_FILTER, FILE_ENDING_NONE);
      }

      saveDisplayPrefs();
   }

   public void replaceSqlFileExtensionFilterBy(FileExtensionFilter fileExtensionFilter)
   {
      _sqlFilter = fileExtensionFilter;
      _saveFileFilterProps = false;

   }
}
