package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.io.File;
import java.util.List;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.gui.texteditdlg.PreviousTextsProvider;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;

public class PreviousTextsAiDisapprovalMessages implements PreviousTextsProvider
{
   private AiDisapprovalMessagesJsonBean _aiDisapprovalMessagesJsonBean;

   public PreviousTextsAiDisapprovalMessages()
   {
      File jsonBeanFile = new ApplicationFiles().getAiDisapprovalMessagesJsonBeanFile();

      _aiDisapprovalMessagesJsonBean = JsonMarshalUtil.readObjectFromFileSave(jsonBeanFile, AiDisapprovalMessagesJsonBean.class, new AiDisapprovalMessagesJsonBean());
   }

   @Override
   public String getLastEditorContent()
   {
      return _aiDisapprovalMessagesJsonBean.getLastEditorContent();
   }

   @Override
   public List<String> getPreviousTexts()
   {
      return _aiDisapprovalMessagesJsonBean.getPreviousAiDisapprovalMessages();
   }

   @Override
   public void setLastEditorContent(String text)
   {
      _aiDisapprovalMessagesJsonBean.setLastEditorContent(text);
   }

   @Override
   public void save()
   {
      JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getAiDisapprovalMessagesJsonBeanFile(), _aiDisapprovalMessagesJsonBean);
   }

}
