package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileManagementUtil;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.RevisionWrapper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * https://github.com/eclipse/jgit
 * <p>
 * https://download.eclipse.org/jgit/site/5.6.0.201912101111-r/apidocs/
 * <p>
 * <p>
 * https://github.com/centic9/jgit-cookbook
 * <p>
 * https://git-scm.com/book/uz/v2/Appendix-B%3A-Embedding-Git-in-your-Applications-JGit
 * <p>
 * https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/CreateNewRepository.java
 */
public class GitHandler
{
   public static final String GIT_MSG_FILE_NAME_PLACEHOLDER = "@file";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GitHandler.class);
   private static final ILogger s_log = LoggerController.createLogger(GitHandler.class);

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
      return getVersionOfFile(file, null);
   }

   /**
    * Returns the content of the file for the revision given by revCommitId.
    * @param revCommitId If null this will be replaced by the HEAD revision.
    */
   public static String getVersionOfFile(File file, ObjectId revCommitId)
   {
      if (null == file)
      {
         return null;
      }

      try(Repository repository = findRepository(file))
      {
         if(null == repository)
         {
            return null;
         }

         String filePathRelativeToRepoRoot = getPathRelativeToRepo(repository, file);


         if (null == revCommitId)
         {
            revCommitId = repository.resolve(Constants.HEAD);
         }

         if(null == revCommitId)
         {
            // headId is null when no file has yet been committed.
            return null;
         }

         s_log.info("GIT: Reading " + Constants.HEAD + " revision of " + file.getPath());

         // a RevWalk allows to walk over commits based on some filtering that is defined
         try (RevWalk revWalk = new RevWalk(repository))
         {
            RevCommit commit = revWalk.parseCommit(revCommitId);
            RevTree tree = commit.getTree();

            String ret;

            try (TreeWalk treeWalk = new TreeWalk(repository))
            {
               treeWalk.addTree(tree);
               treeWalk.setRecursive(true);
               treeWalk.setFilter(PathFilter.create(filePathRelativeToRepoRoot));
               if (!treeWalk.next())
               {
                  // We get here when the file was saved but not yet added.
                  revWalk.dispose();
                  return null;
                  //throw new IllegalStateException("Did not find expected file '" + filePathRelativeToRepoRoot + "'");
               }

               ObjectId objectId = treeWalk.getObjectId(0);
               ObjectLoader loader = repository.open(objectId);

               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               loader.copyTo(baos);

               ret = StringUtilities.removeCarriageReturn(new String(baos.toByteArray()));
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

   public static String getPathRelativeToRepo(File file)
   {
      try(Repository repository = findRepository(file))
      {
         if(null == repository)
         {
            throw new IllegalStateException("Couldn't find repository for file " + file.getAbsolutePath());
         }

         return getPathRelativeToRepo(repository, file);
      }
   }

   public static String getFilesRepositoryWorkTreePath(File file)
   {
      try(Repository repository = findRepository(file))
      {
         if(null == repository)
         {
            throw new IllegalStateException("Couldn't find repository for file " + file.getAbsolutePath());
         }

         return getRepositoryWorkTreePath(repository);
      }
   }




   private static String commit(IFileEditorAPI fileEditorAPI)
   {
      Repository repository = null;
      Git git = null;

      try
      {
         if (false == fileEditorAPI.getFileHandler().fileSave())
         {
            return null;
         }

         File file = fileEditorAPI.getFileHandler().getFile();



         repository = findRepository(file);

         if (null == repository)
         {

            File gitInitDir = new SelectGitRepoRootDirController().getDir(file);

            if (null == gitInitDir)
            {
               return null;
            }

            git = Git.init().setDirectory(gitInitDir).call();
            repository = git.getRepository();
         }
         else
         {
            git = Git.open(repository.getDirectory());
            repository = git.getRepository();
         }

         String filePathRelativeToRepoRoot = getPathRelativeToRepo(repository, file);

         // Add seems to do no harm for already added files so we call it always.
         git.add().addFilepattern(filePathRelativeToRepoRoot).call();

         if (isModifiedOrAdded(filePathRelativeToRepoRoot, git))
         {
            String msg;

            if(Main.getApplication().getSquirrelPreferences().isGitCommitMsgManually())
            {
               msg = new GitCommitMessageController(fileEditorAPI.getOwningFrame(), file.getName(), filePathRelativeToRepoRoot, repository).getMessage();

               if(null == msg)
               {
                  return getCurrentRepoVersion(file);
               }
            }
            else
            {
               msg = Main.getApplication().getSquirrelPreferences().getGitCommitMsgDefault().replaceAll(GIT_MSG_FILE_NAME_PLACEHOLDER, filePathRelativeToRepoRoot);
            }

            RevCommit revCommit = git.commit().setOnly(filePathRelativeToRepoRoot).setMessage(msg).call();

            logCommit(repository, filePathRelativeToRepoRoot, revCommit);
         }
         else
         {
            Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("GitHandler.unmodified", file.getPath()));
         }

         return FileManagementUtil.readFileAsString(file);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
      finally
      {
         if(null != repository)
         {
            try
            {
               repository.close();
            }
            catch (Exception e)
            {
               s_log.warn("Error closing org.eclipse.jgit.lib.Repository", e);
            }
         }

         if(null != git)
         {
            try
            {
               git.close();
            }
            catch (Exception e)
            {
               s_log.warn("Error closing org.eclipse.jgit.api.Git", e);
            }
         }
      }
   }

   private static boolean isModifiedOrAdded(String filePathRelativeToRepoRoot, Git git)
   {
      try
      {
         Status status = git.status().addPath(filePathRelativeToRepoRoot).call();
         return 0 < status.getChanged().size() + status.getAdded().size();
      }
      catch (GitAPIException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static void logCommit(Repository repository, String filePathRelativeToRepoRoot, RevCommit revCommit)
   {
      try
      {
         String msg = s_stringMgr.getString("GitHandler.commitMsg",
                        filePathRelativeToRepoRoot,
                        repository.getBranch(),
                        getRepositoryWorkTreePath(repository),
                        revCommit.getCommitterIdent().getName()
                        );

         Main.getApplication().getMessageHandler().showMessage(msg);

         String log = s_stringMgr.getString("GitHandler.commitLog",
               filePathRelativeToRepoRoot,
               repository.getBranch(),
               getRepositoryWorkTreePath(repository),
               revCommit.getCommitterIdent().getName(),
               revCommit.getId()
         );

         s_log.info(log);

      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static String getRepositoryWorkTreePath(Repository repository)
   {
      return repository.getWorkTree().getPath();
   }


   private static Repository findRepository(File file)
   {
      try
      {
         FileRepositoryBuilder builder = new FileRepositoryBuilder().findGitDir(file);

         if(null == builder.getGitDir())
         {
            return null;
         }

         return builder.build();
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static String getPathRelativeToRepo(Repository repository, File file)
   {
      return repository.getWorkTree().toURI().relativize(file.toURI()).getPath();
   }

   public static boolean isInRepository(File file)
   {
      try
      {
         Repository repository = findRepository(file);

         if(null == repository)
         {
            return false;
         }

         Git git = Git.open(repository.getDirectory());

         String filePathRelativeToRepoRoot = getPathRelativeToRepo(repository, file);

         Status status = git.status().addPath(filePathRelativeToRepoRoot).call();

         return null != status && 0 == status.getUntracked().size();
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public static List<RevisionWrapper> getRevisions(File file)
   {

      List<RevisionWrapper> ret = new ArrayList<>();

      try(Repository repository = findRepository(file);
          Git git = Git.open(repository.getDirectory());)
      {

         String filePathRelativeToRepoRoot = getPathRelativeToRepo(repository, file);

         ObjectId headCommitId = repository.resolve(Constants.HEAD);

         Iterable<RevCommit> revCommits = git.log().addPath(filePathRelativeToRepoRoot).call();

         for (RevCommit revCommit : revCommits)
         {
            ret.add(new RevisionWrapper(revCommit, git, headCommitId));

//         String buf =
//         "GitHandler.getRevisions " + revCommit.getAuthorIdent().getWhen() + "  "
//               + "   Branches: " + String.join("; ", git.branchList().setContains(revCommit.getId().getName()).call().stream().map(b -> b.getName()).collect(Collectors.toList())) + "   "
//               + revCommit.getAuthorIdent().getName() + "   "
//               + revCommit.getAuthorIdent().getEmailAddress() + "   "
//               + revCommit.getId().getName()  + "   "
//               + revCommit.getFullMessage();

         }

         return ret;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
