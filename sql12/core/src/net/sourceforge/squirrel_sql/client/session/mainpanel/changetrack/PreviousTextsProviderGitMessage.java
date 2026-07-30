package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import java.io.File;
import java.util.List;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.gui.texteditdlg.PreviousTextsProvider;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;

public class PreviousTextsProviderGitMessage implements PreviousTextsProvider
{
   private GitCommitMessageJsonBean _gitCommitMessageJsonBean = new GitCommitMessageJsonBean();

   public PreviousTextsProviderGitMessage()
   {
      File jsonBeanFile = new ApplicationFiles().getGitCommitMessageJsonBeanFile();

      if(jsonBeanFile.exists())
      {
         _gitCommitMessageJsonBean = JsonMarshalUtil.readObjectFromFileSave(jsonBeanFile, GitCommitMessageJsonBean.class, new GitCommitMessageJsonBean());
      }
   }

   @Override
   public String getLastEditorContent()
   {
      return _gitCommitMessageJsonBean.getLastEditorContent();
   }

   @Override
   public List<String> getPreviousTexts()
   {
      return _gitCommitMessageJsonBean.getPreviousCommitMessages();
   }

   @Override
   public void setLastEditorContent(String text)
   {
      _gitCommitMessageJsonBean.setLastEditorContent(text);
   }

   @Override
   public void save()
   {
      JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getGitCommitMessageJsonBeanFile(), _gitCommitMessageJsonBean);
   }
}
