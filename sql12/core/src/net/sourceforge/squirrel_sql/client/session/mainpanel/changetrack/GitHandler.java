package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.filemanager.FileManagementUtil;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


/**
 * https://github.com/centic9/jgit-cookbook
 *
 * https://git-scm.com/book/uz/v2/Appendix-B%3A-Embedding-Git-in-your-Applications-JGit
 *
 * https://github.com/eclipse/jgit
 *
 * http://download.eclipse.org/jgit/site/4.10.0.201712302008-r/apidocs/
 *
 * https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/CreateNewRepository.java
 */
public class GitHandler
{
   public static String getChangeTrackBaseFromGit(IFileEditorAPI fileEditorAPI, boolean commitToGit)
   {
      if (commitToGit)
      {
         return commit(fileEditorAPI);
      }
      else
      {
         return getCurrentRepoVersion(fileEditorAPI.getFileHandler().getFile());
      }
   }

   private static String getCurrentRepoVersion(File file)
   {
//      if (null == file)
//      {
//         return null;
//      }
//
//      Git git = Git.open(file.getParentFile());
//
//
//      ArchiveCommand.registerFormat("plainString", new ArchiveCommand.Format<Closeable>() {
//         @Override
//         public Closeable createArchiveOutputStream(OutputStream outputStream) throws IOException
//         {
//            return new ByteArrayOutputStream();
//         }
//
//         @Override
//         public Closeable createArchiveOutputStream(OutputStream outputStream, Map<String, Object> map) throws IOException
//         {
//            return null;
//         }
//
//         @Override
//         public void putEntry(Closeable closeable, ObjectId objectId, String s, FileMode fileMode, ObjectLoader objectLoader) throws IOException
//         {
//
//         }
//
//         @Override
//         public Iterable<String> suffixes()
//         {
//            return null;
//         }
//      });
//      try
//      {
//         git.archive().setTree(git.getRepository().resolve("HEAD"))
//            .setFormat("zip")
//            .setOutputStream(out)
//            .call();
//      }
//      finally
//      {
//         ArchiveCommand.unregisterFormat("zip");
//      }





      try
      {
         if(null == file)
         {
            return null;
         }

         Git git = Git.open(file.getParentFile());

         Repository repository = git.getRepository();
         String currentBranch = repository.getBranch();

         Ref curRef = repository.exactRef(currentBranch);

         // https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/CreateNewRepository.java
         try (RevWalk walk = new RevWalk(repository))
         {
            RevCommit commit = walk.parseCommit(curRef.getObjectId());
            RevTree tree = walk.parseTree(commit.getTree().getId());
            System.out.println("Found Tree: " + tree);
            ObjectLoader loader = repository.open(tree.getId());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            loader.copyTo(baos);

            String ret = new String(baos.toByteArray());

            walk.dispose();

            return ret;
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static String commit(IFileEditorAPI fileEditorAPI)
   {
      try
      {
         if(false == fileEditorAPI.getFileHandler().fileSave())
         {
            return null;
         }

         File file = fileEditorAPI.getFileHandler().getFile();

         Git git;
         File repoRootDir;

         if(false == isInRepository(file.getParentFile()))
         {

            repoRootDir = new SelectGitRepoRootDirController().getDir(file.getParentFile());

            if(null == repoRootDir)
            {
               return null;
            }

            git = Git.init().setDirectory(repoRootDir).call();

            String filePathRelativeToRepoRoot = repoRootDir.toURI().relativize(file.toURI()).getPath();

            //git.add().addFilepattern(file.getAbsolutePath());
            git.add().addFilepattern(filePathRelativeToRepoRoot).call();

         }
         else
         {
            git = Git.open(file.getParentFile());

            repoRootDir = git.getRepository().getDirectory();
         }

         String filePathRelativeToRepoRoot = repoRootDir.toURI().relativize(file.toURI()).getPath();

         git.commit().setOnly(filePathRelativeToRepoRoot).setMessage("SQuirrelGeneratedMessage").call();

         return FileManagementUtil.readFileAsString(file);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static boolean isInRepository(File parentDir)
   {
      try
      {
         return new FileRepository(parentDir.getPath()).getObjectDatabase().exists();
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
