package net.sourceforge.squirrel_sql.fw.gui.texteditdlg;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class TextEditController
{
   private final TextEditDialog _dlg;
   private boolean _ok;

   private PreviousTextsProvider _previousTextsProvider;

   public TextEditController(Frame parentFrame,
                             String frameTitle,
                             String description,
                             String emptyMessageDlgTitle,
                             String emptyMessageDlgText,
                             PreviousTextsProvider previousTextsProvider)
   {
      _dlg = new TextEditDialog(parentFrame, frameTitle, description);

      _dlg.btnOk.addActionListener(e -> onOk(emptyMessageDlgTitle, emptyMessageDlgText));
      _dlg.btnCancel.addActionListener(e -> onCancel());

      _dlg.btnMessageHistory.addActionListener(e -> onShowMessageHistory());

      _previousTextsProvider = previousTextsProvider;


      setMessage(_previousTextsProvider.getLastEditorContent());

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

      for (String previousCommitMessage : _previousTextsProvider.getPreviousTexts())
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

   private void onOk(String emptyMessageDlgTitle, String emptyMessageDlgText)
   {
      if(StringUtilities.isEmpty(_dlg.txtMessage.getText(), true))
      {
         JOptionPane.showMessageDialog(_dlg, emptyMessageDlgText, emptyMessageDlgTitle, JOptionPane.ERROR_MESSAGE);
         return;
      }

      List<String> msgs = _previousTextsProvider.getPreviousTexts();

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
         _previousTextsProvider.setLastEditorContent(_dlg.txtMessage.getText());
      }

      _previousTextsProvider.save();
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
