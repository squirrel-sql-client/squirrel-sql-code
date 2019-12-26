package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.eclipse.jgit.lib.Repository;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GitCommitMessageController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GitCommitMessageController.class);


   private final GitCommitMessageDialog _dlg;
   private boolean _ok;

   private GitCommitMessageJsonBean _gitCommitMessageJsonBean = new GitCommitMessageJsonBean();

   public GitCommitMessageController(Frame parentFrame, String fileName, String filePathRelativeToRepoRoot, Repository repository)
   {
      _dlg = new GitCommitMessageDialog(parentFrame, fileName, buildDescription(filePathRelativeToRepoRoot, repository));

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> onCancel());

      _dlg.btnMessageHistory.addActionListener(e -> onShowMessageHistory());

      File jsonBeanFile = new ApplicationFiles().getGitCommitMessageJsonBeanFile();

      if(jsonBeanFile.exists())
      {
         _gitCommitMessageJsonBean = JsonMarshalUtil.readObjectFromFile(jsonBeanFile, GitCommitMessageJsonBean.class);
      }

      setMessage(_gitCommitMessageJsonBean.getLastEditorContent());

      _dlg.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e)
         {
            saveState();
         }
      });
   }

   private void onShowMessageHistory()
   {
      JPopupMenu popupMenu = new JPopupMenu();

      boolean[] messageClickedRef = new boolean[1];
      String currentMessage = _dlg.txtMessage.getText();

      for (String previousCommitMessage : _gitCommitMessageJsonBean.getPreviousCommitMessages())
      {
         JMenuItem menuItem = new JMenuItem(createMenuItemText(previousCommitMessage));
         menuItem.addChangeListener(e -> setMessage(previousCommitMessage));
         menuItem.addActionListener(e -> messageClickedRef[0] = true);
         popupMenu.add(menuItem);
      }

      popupMenu.show(_dlg.btnMessageHistory, 0, _dlg.btnMessageHistory.getHeight());

      popupMenu.addPopupMenuListener(new PopupMenuListener() {
         @Override
         public void popupMenuWillBecomeVisible(PopupMenuEvent e)
         {
         }

         @Override
         public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
         {
            onPopupClosed(currentMessage, messageClickedRef);
         }

         @Override
         public void popupMenuCanceled(PopupMenuEvent e)
         {
         }
      });
   }

   private void onPopupClosed(String formerMessage, boolean[] messageClickedRef)
   {
      if(false == messageClickedRef[0])
      {
         setMessage(formerMessage);
      }
   }

   private void onPreviousMessageClicked(String previousCommitMessage, boolean[] messageClickedRef)
   {
      setMessage(previousCommitMessage);
      messageClickedRef[0] = true;
   }

   private void setMessage(String previousCommitMessage)
   {
      _dlg.txtMessage.setText(previousCommitMessage);
      SwingUtilities.invokeLater(() -> _dlg.txtMessage.scrollRectToVisible(new Rectangle(0,0,1,1)));
   }

   private String createMenuItemText(String previousCommitMessage)
   {
      String ret = previousCommitMessage.split("\n")[0];

      if(ret.length() > 50)
      {
         ret = ret.substring(0, 45) + "...";
      }

      return ret;
   }

   private void onCancel()
   {
      close();
   }

   private void onOk()
   {
      if(StringUtilities.isEmpty(_dlg.txtMessage.getText(), true))
      {
         String title = s_stringMgr.getString("GitCommitMessageController.empty.title");
         String msg = s_stringMgr.getString("GitCommitMessageController.empty.message");
         JOptionPane.showMessageDialog(_dlg, msg, title, JOptionPane.ERROR_MESSAGE);
         return;
      }

      List<String> msgs = _gitCommitMessageJsonBean.getPreviousCommitMessages();

      msgs.remove(_dlg.txtMessage.getText());
      msgs.add(0, _dlg.txtMessage.getText());

      while(10 < msgs.size())
      {
         msgs.remove(msgs.size() - 1);
      }

      _ok = true;
      close();
   }

   private void close()
   {
      saveState();

      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void saveState()
   {
      if (false == StringUtilities.isEmpty(_dlg.txtMessage.getText(), true))
      {
         _gitCommitMessageJsonBean.setLastEditorContent(_dlg.txtMessage.getText());
      }

      JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getGitCommitMessageJsonBeanFile(), _gitCommitMessageJsonBean);
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
      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 400, 400);

      SwingUtilities.invokeLater(() -> _dlg.txtMessage.requestFocus());
      _dlg.setVisible(true); // Stops here

      if (_ok)
      {
         return _dlg.txtMessage.getText();
      }

      return null;
   }
}
