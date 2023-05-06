package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DiffFileUtil
{
   static Path createSqlEditorContentTempFile(String editorContent)
   {
      return createTempFile(editorContent, "SQuirreLSQL.changeTrack.revisionList.sqlEditor");
   }
   static Path createGitRevisionTempFile(String gitRevision)
   {
      return createTempFile(gitRevision, "SQuirreLSQL.changeTrack.revisionList.gitRevision");
   }

   private static Path createTempFile(String contentText, String fileNamePrefix)
   {
      try
      {
         Path leftFile = Files.createTempFile(fileNamePrefix, ".sql");
         leftFile.toFile().deleteOnExit();
         return Files.write(leftFile, contentText.getBytes(StandardCharsets.UTF_8));
      }
      catch(IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
