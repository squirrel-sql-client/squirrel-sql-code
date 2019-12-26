package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import java.util.ArrayList;
import java.util.List;

public class GitCommitMessageJsonBean
{
   private String _lastEditorContent;
   private List<String> _previousCommitMessages = new ArrayList<>();

   public String getLastEditorContent()
   {
      return _lastEditorContent;
   }

   public void setLastEditorContent(String lastEditorContent)
   {
      _lastEditorContent = lastEditorContent;
   }

   public List<String> getPreviousCommitMessages()
   {
      return _previousCommitMessages;
   }

   public void setPreviousCommitMessages(List<String> previousCommitMessages)
   {
      _previousCommitMessages = previousCommitMessages;
   }
}
