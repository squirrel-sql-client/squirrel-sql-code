package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.stream.Collectors;

public class RevisionWrapper
{
   private String _revisionDateString;
   private String _branchesListString;
   private String _committerName;
   private String _revisionIdString;
   private String _commitMsg;
   private ObjectId _revCommitId;

   private boolean _headRevision;
   private HashSet<String> _previousNamesOfFileRelativeToRepositoryRoot;

   public RevisionWrapper(RevCommit revCommit, Git git, ObjectId headCommitId, HashSet<String> previousNamesOfFileRelativeToRepositoryRoot)
   {
      try
      {
         _revisionDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(revCommit.getAuthorIdent().getWhen());
         _branchesListString = String.join("; ", git.branchList().setContains(revCommit.getId().getName()).call().stream().map(b -> b.getName()).collect(Collectors.toList()));
         _committerName = revCommit.getAuthorIdent().getName()  + "; Mail: " + revCommit.getAuthorIdent().getEmailAddress();
         _revisionIdString = revCommit.getId().getName();
         _commitMsg = revCommit.getFullMessage();
         _revCommitId = revCommit.toObjectId();
         _headRevision = headCommitId.equals(_revCommitId);
         _previousNamesOfFileRelativeToRepositoryRoot = previousNamesOfFileRelativeToRepositoryRoot;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   boolean isHeadRevision()
   {
      return _headRevision;
   }

   public String getRevisionDateString()
   {
      return _revisionDateString;
   }

   public String getBranchesListString()
   {
      return _branchesListString;
   }

   public String getCommitterName()
   {
      return _committerName;
   }

   public String getRevisionIdString()
   {
      return _revisionIdString;
   }

   public String getCommitMsgBegin()
   {
      return firstTwoLines(_commitMsg);
   }

   public String getCommitMsg()
   {
      return _commitMsg;
   }

   public ObjectId getRevCommitId()
   {
      return _revCommitId;
   }

   public HashSet<String> getPreviousNamesOfFileRelativeToRepositoryRoot()
   {
      return _previousNamesOfFileRelativeToRepositoryRoot;
   }

   private String firstTwoLines(String fullMessage)
   {
      String ret = "";

      String[] splits = fullMessage.split("\n");
      for (int i = 0; i < splits.length; i++)
      {
         if (i == 0)
         {
            ret = splits[i];
         }
         else
         {
            ret += "\n" + splits[i];
         }

         if(i >= 1)
         {
            return ret + " ...";
         }
      }

      return ret;
   }


   public String getDisplayString()
   {
      return _revisionDateString +
            "\n  Branches: " + _branchesListString +
            "\n  User: " + _committerName +
            "\n  Revision-Id: " + _revisionIdString +
            "\n  Msg: " + getCommitMsgBegin();
   }
}
