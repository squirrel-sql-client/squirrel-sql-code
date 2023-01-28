package net.sourceforge.squirrel_sql.client.session.filemanager;

import java.io.File;

public class FileSaveResult
{
   private boolean _success;
   private File _previousFile;
   private File _newFile;
   private boolean _moveFileRequested;

   public FileSaveResult(boolean success)
   {
      _success = success;
   }

   public boolean isSuccess()
   {
      return _success;
   }

   public void setSuccess(boolean result)
   {
      _success = result;
   }

   public void setSavedToNewFile(File previousFile, File newFile, boolean moveFileRequested)
   {
      _previousFile = previousFile;
      _newFile = newFile;
      _moveFileRequested = moveFileRequested;
   }

   public boolean wasSavedToNewFile()
   {
      return _success && null != _previousFile && null != _newFile && false == _previousFile.equals(_newFile);
   }

   public boolean isMoveFileRequested()
   {
      return _moveFileRequested;
   }

   public File getPreviousFile()
   {
      return _previousFile;
   }

   public File getNewFile()
   {
      return _newFile;
   }
}
