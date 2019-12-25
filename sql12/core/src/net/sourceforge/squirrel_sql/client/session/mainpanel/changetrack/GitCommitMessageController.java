package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.eclipse.jgit.lib.Repository;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.io.IOException;

public class GitCommitMessageController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GitCommitMessageController.class);


   private final GitCommitMessageDialog _gitCommitMessageDialog;
   private boolean _ok;

   public GitCommitMessageController(Frame parentFrame, String fileName, String filePathRelativeToRepoRoot, Repository repository)
   {
      _gitCommitMessageDialog = new GitCommitMessageDialog(parentFrame, fileName, buildDescription(filePathRelativeToRepoRoot, repository));

      _gitCommitMessageDialog.btnOk.addActionListener(e -> onOk());
      _gitCommitMessageDialog.btnCancel.addActionListener(e -> onCancel());
   }

   private void onCancel()
   {
      close();
   }

   private void onOk()
   {
      if(StringUtilities.isEmpty(_gitCommitMessageDialog.txtMessage.getText(), true))
      {
         String title = s_stringMgr.getString("GitCommitMessageController.empty.title");
         String msg = s_stringMgr.getString("GitCommitMessageController.empty.message");
         JOptionPane.showMessageDialog(_gitCommitMessageDialog, msg, title, JOptionPane.ERROR_MESSAGE);
         return;
      }


      _ok = true;
      close();
   }

   private void close()
   {
      _gitCommitMessageDialog.setVisible(false);
      _gitCommitMessageDialog.dispose();
   }


   private String buildDescription(String filePathRelativeToRepoRoot, Repository repository)
   {
      try
      {
         return s_stringMgr.getString("GitCommitMessageController.description",
               filePathRelativeToRepoRoot,
               repository.getBranch(),
               repository.getWorkTree().getPath()
         );
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public String getMessage()
   {
      GUIUtils.enableCloseByEscape(_gitCommitMessageDialog);
      GUIUtils.initLocation(_gitCommitMessageDialog, 400, 400);

      SwingUtilities.invokeLater(() -> _gitCommitMessageDialog.txtMessage.requestFocus());
      _gitCommitMessageDialog.setVisible(true); // Stops here

      if (_ok)
      {
         return _gitCommitMessageDialog.txtMessage.getText();
      }

      return null;
   }
}
