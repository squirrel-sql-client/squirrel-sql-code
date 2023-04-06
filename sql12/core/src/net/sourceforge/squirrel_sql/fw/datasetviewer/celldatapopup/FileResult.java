package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.io.File;

class FileResult
{
   public final String canonicalFilePathName;
   public final File file;

   public FileResult(String canonicalFilePathName, File file)
   {
      this.canonicalFilePathName = canonicalFilePathName;
      this.file = file;
   }
}
