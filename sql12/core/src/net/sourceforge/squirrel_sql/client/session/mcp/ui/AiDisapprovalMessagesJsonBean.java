package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.util.ArrayList;
import java.util.List;

public class AiDisapprovalMessagesJsonBean
{
   private String _lastEditorContent;
   private List<String> _previousAiDisapprovalMessages = new ArrayList<>();

   public String getLastEditorContent()
   {
      return _lastEditorContent;
   }

   public void setLastEditorContent(String lastEditorContent)
   {
      _lastEditorContent = lastEditorContent;
   }

   public List<String> getPreviousAiDisapprovalMessages()
   {
      return _previousAiDisapprovalMessages;
   }

   public void setPreviousAiDisapprovalMessages(List<String> _previousAiDisapprovalMessages)
   {
      this._previousAiDisapprovalMessages = _previousAiDisapprovalMessages;
   }
}
