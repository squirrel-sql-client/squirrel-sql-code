package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;

import javax.swing.*;
import java.util.HashMap;
import net.sourceforge.squirrel_sql.fw.props.Props;

public class FileChooserManager
{

   private static final String PREF_PRE_SELECTED_FILE_FILTER = "Squirrel.filechoosermanager.preselfilefilter";
   public static final String FILE_ENDING_TXT = ".txt";
   public static final String FILE_ENDING_SQL = ".sql";
   public static final String FILE_ENDING_NONE = "FILE_ENDING_NONE";


   private JFileChooser _fileChooser;

   private HashMap<FileExtensionFilter, String> _fileAppenixes = new HashMap<FileExtensionFilter, String>();
   private final FileExtensionFilter _txtFilter;
   private final FileExtensionFilter _sqlFilter;
   private boolean _saveFileFilterProps = true;


   public FileChooserManager()
   {
      _fileChooser = new JFileChooser();

      _txtFilter = new FileExtensionFilter("Text files", new String[]{FILE_ENDING_TXT});
      _fileChooser.addChoosableFileFilter(_txtFilter);
      _fileAppenixes.put(_txtFilter, FILE_ENDING_TXT);

      _sqlFilter = new FileExtensionFilter("SQL files", new String[]{FILE_ENDING_SQL});
      _fileChooser.addChoosableFileFilter(_sqlFilter);
      _fileAppenixes.put(_sqlFilter, FILE_ENDING_SQL);


      String fileEndingPref = Props.getString(PREF_PRE_SELECTED_FILE_FILTER, FILE_ENDING_NONE);

      if(FILE_ENDING_SQL.equals(fileEndingPref))
      {
         _fileChooser.setFileFilter(_sqlFilter);
      }
      else if(FILE_ENDING_TXT.equals(fileEndingPref))
      {
         _fileChooser.setFileFilter(_txtFilter);
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
   }

   public void replaceSqlFileExtensionFilterBy(FileExtensionFilter fileExtensionFilter, String fileEndingWithDot)
   {
      _fileChooser.removeChoosableFileFilter(_sqlFilter);
      _fileChooser.addChoosableFileFilter(fileExtensionFilter);
      _fileChooser.setFileFilter(fileExtensionFilter);

      _fileAppenixes.remove(_sqlFilter);
      _fileAppenixes.put(fileExtensionFilter, fileEndingWithDot);

      _saveFileFilterProps = false;

   }
}
