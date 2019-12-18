package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.filemanager.FileManagementUtil;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


/**
 * https://github.com/centic9/jgit-cookbook
 * <p>
 * https://git-scm.com/book/uz/v2/Appendix-B%3A-Embedding-Git-in-your-Applications-JGit
 * <p>
 * https://github.com/eclipse/jgit
 * <p>
 * http://download.eclipse.org/jgit/site/4.10.0.201712302008-r/apidocs/
 * <p>
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
      try
      {
         if (null == file)
         {
            return null;
         }

         System.out.println("GitHandler.getCurrentRepoVersion READING FROM GIT");

         Git git = Git.open(file.getParentFile());



         Repository repository = git.getRepository();

         String filePathRelativeToRepoRoot = repository.getWorkTree().toURI().relativize(file.toURI()).getPath();

         ObjectId headId = repository.resolve(Constants.HEAD);

         // a RevWalk allows to walk over commits based on some filtering that is defined
         try (RevWalk revWalk = new RevWalk(repository))
         {
            RevCommit commit = revWalk.parseCommit(headId);
            RevTree tree = commit.getTree();

            String ret = null;

            try (TreeWalk treeWalk = new TreeWalk(repository))
            {
               treeWalk.addTree(tree);
               treeWalk.setRecursive(true);
               treeWalk.setFilter(PathFilter.create(filePathRelativeToRepoRoot));
               if (!treeWalk.next())
               {
                  throw new IllegalStateException("Did not find expected file '" + filePathRelativeToRepoRoot + "'");
               }

               ObjectId objectId = treeWalk.getObjectId(0);
               ObjectLoader loader = repository.open(objectId);

               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               loader.copyTo(baos);

               ret = new String(baos.toByteArray());
            }

            revWalk.dispose();

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
         if (false == fileEditorAPI.getFileHandler().fileSave())
         {
            return null;
         }

         File file = fileEditorAPI.getFileHandler().getFile();

         Git git;
         File repoRootDir;

         if (false == isInRepository(file.getParentFile()))
         {

            repoRootDir = new SelectGitRepoRootDirController().getDir(file.getParentFile());

            if (null == repoRootDir)
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
