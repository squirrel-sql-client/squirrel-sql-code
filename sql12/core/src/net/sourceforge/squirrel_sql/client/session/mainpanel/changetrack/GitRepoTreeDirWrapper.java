package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.io.File;

public class GitRepoTreeDirWrapper
{
   private File _file;

   public GitRepoTreeDirWrapper(File file)
   {
      _file = file;
   }

   public File getFile()
   {
      return _file;
   }

   @Override
   public String toString()
   {
      if (StringUtilities.isEmpty(_file.getName(), true))
      {
         return File.separator;
      }
      else
      {
         return _file.getName();
      }
   }
}
