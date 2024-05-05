package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SaveSessionResult;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionUtil;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionPersister;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SavedSessionsGroupCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionsGroupCtrl.class);

   private static final String PROPS_KEY_DEFAULT_BUTTON = "GroupOfSavedSessionsCtrl.GroupSaveDefaultButton";

   private final SavedSessionsGroupDlg _dlg;
   private final SessionsListCtrl _sessionsListCtrl;

   private SavedSessionsGroupJsonBean _groupBeingEdited;

   private boolean _inOnGroupMembersChanged;
   private boolean _groupNameEditedByUser;

   public SavedSessionsGroupCtrl()
   {
      _dlg = new SavedSessionsGroupDlg();

      String groupId = null;
      if (null != Main.getApplication().getSessionManager().getActiveSession().getSavedSession())
      {
         groupId = Main.getApplication().getSessionManager().getActiveSession().getSavedSession().getGroupId();
      }
      _groupBeingEdited = Main.getApplication().getSavedSessionsManager().getGroup(groupId);

      if (null != _groupBeingEdited)
      {
         _dlg.lblOfTxtGroupName.setText(s_stringMgr.getString("SavedSessionsGroupDlg.name.of.existing.group"));
         _dlg.txtGroupName.setText(_groupBeingEdited.getGroupName());
      }
      else
      {
         _dlg.lblOfTxtGroupName.setText(s_stringMgr.getString("SavedSessionsGroupDlg.name.of.new.group"));
      }

      _sessionsListCtrl = new SessionsListCtrl(_dlg.lstSessions, _groupBeingEdited, () -> onGroupMembersChanged());
      onGroupMembersChanged();


      _dlg.txtGroupName.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            handleGroupNameUpdated();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            handleGroupNameUpdated();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            handleGroupNameUpdated();
         }
      });

      _dlg.lstSessions.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });


      _dlg.btnSaveGroup.addActionListener(e -> onSaveGroup(false));
      _dlg.btnGitCommitGroup.addActionListener(e -> onSaveGroup(true));
      _dlg.btnCancel.addActionListener(e -> close());

      SavedSessionsGroupDlgDefaultButton defaultButton = SavedSessionsGroupDlgDefaultButton.valueOf(Props.getString(PROPS_KEY_DEFAULT_BUTTON, SavedSessionsGroupDlgDefaultButton.SAVE.name()));
      _dlg.cboDefaultButton.setSelectedItem(defaultButton);
      _dlg.cboDefaultButton.addActionListener(e -> initDefaultButton((SavedSessionsGroupDlgDefaultButton) _dlg.cboDefaultButton.getSelectedItem()));
      initDefaultButton(defaultButton);

      GUIUtils.forceFocus(_dlg.txtGroupName);
      GUIUtils.initLocation(_dlg, 600, 500);
      GUIUtils.enableCloseByEscape(_dlg);
      _dlg.setVisible(true);
   }

   private void onKeyPressed(KeyEvent e)
   {
      if(0 == _dlg.lstSessions.getModel().getSize())
      {
         return;
      }

      if (   e.getKeyCode() == KeyEvent.VK_K
          && (0 != (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK)))
      {
         boolean allInGroup = (false == _dlg.lstSessions.getSelectedValuesList().stream().anyMatch(w -> !w.isGroupMember()));

         for (GroupDlgSessionWrapper wrapper : _dlg.lstSessions.getSelectedValuesList())
         {
            wrapper.setGroupMemberFlag(!allInGroup);
         }
         _dlg.lstSessions.repaint();
      }
   }

   private void initDefaultButton(SavedSessionsGroupDlgDefaultButton defaultButton)
   {
      switch (defaultButton)
      {
         case SAVE:
            _dlg.getRootPane().setDefaultButton(_dlg.btnSaveGroup);
            break;
         case GIT_COMMIT:
            _dlg.getRootPane().setDefaultButton(_dlg.btnGitCommitGroup);
            break;
         default:
            throw new IllegalStateException("Unknown default button: " + defaultButton);
      }
   }

   private void handleGroupNameUpdated()
   {
      if(_inOnGroupMembersChanged)
      {
         return;
      }

      _groupNameEditedByUser = true;
   }


   private void onSaveGroup(boolean gitCommit)
   {
      List<GroupDlgSessionWrapper> toSaveWrappers = _sessionsListCtrl.getInCurrentGroupList();

      if(toSaveWrappers.isEmpty())
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("SavedSessionsGroupCtrl.error.cannot.save.empty.group.message.box"));
         return;
      }

      String groupName = _dlg.txtGroupName.getText();
      if(StringUtilities.isEmpty(groupName, true))
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("SavedSessionsGroupCtrl.error.no.group.name"));
         return;
      }

      for (GroupDlgSessionWrapper sessWrp : toSaveWrappers)
      {
         if(null != sessWrp.getSession().getSavedSession())
         {
            if(null == _groupBeingEdited || false == Objects.equals(_groupBeingEdited.getGroupId(), sessWrp.getSession().getSavedSession().getGroupId()))
            {
               int res = JOptionPane.showConfirmDialog(_dlg,
                                                     s_stringMgr.getString("SavedSessionsGroupCtrl.saved.session.exists.message"),
                                                     s_stringMgr.getString("SavedSessionsGroupCtrl.saved.session.exists.title"),
                                                     JOptionPane.YES_NO_CANCEL_OPTION);

               if (res != JOptionPane.YES_OPTION)
               {
                  return;
               }
            }
         }
      }

      close();

      // Needed to do after closing the modal dialog to make focusing and setting caret work.
      SwingUtilities.invokeLater(() -> saveSessionGroup(gitCommit, groupName, toSaveWrappers.stream().map(w -> w.getSession()).collect(Collectors.toList())));
   }

   private void saveSessionGroup(boolean gitCommit, String groupName, List<ISession> toSave)
   {
      if(null == _groupBeingEdited)
      {
         _groupBeingEdited = new SavedSessionsGroupJsonBean();
      }

      _groupBeingEdited.setGroupName(groupName);

      ISession activeSessionInGroup = Main.getApplication().getSessionManager().getActiveSession();
      SaveSessionResult saveSessionResultOfActiveSession = null;

      Collections.reverse(toSave); // Reverse because saved is moved to the top of SavedSessionsJsonBean._savedSessionJsonBeans
      for (ISession sess : toSave)
      {
         SaveSessionResult buf = SessionPersister.saveSessionGroup(sess, _groupBeingEdited, gitCommit, sess == activeSessionInGroup);
         if(sess == activeSessionInGroup)
         {
            saveSessionResultOfActiveSession = buf;
         }
      }

      if(null != saveSessionResultOfActiveSession && saveSessionResultOfActiveSession.isSessionWasSaved())
      {
         saveSessionResultOfActiveSession.activatePreviousSqlEditor();
      }
   }

   private void close()
   {
      Props.putString(PROPS_KEY_DEFAULT_BUTTON, ((SavedSessionsGroupDlgDefaultButton)_dlg.cboDefaultButton.getSelectedItem()).name());

      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onGroupMembersChanged()
   {
      try
      {
         _inOnGroupMembersChanged = true;
         if(_groupNameEditedByUser || null != _groupBeingEdited)
         {
            return;
         }

         if(_sessionsListCtrl.getInCurrentGroupList().isEmpty())
         {
            _dlg.txtGroupName.setText(s_stringMgr.getString("SavedSessionsGroupCtrl.error.cannot.save.empty.group"));
            return;
         }
         _dlg.txtGroupName.setText(SavedSessionUtil.createSessionGroupNameTemplate(_sessionsListCtrl.getInCurrentGroupList().stream().map(w -> w.getSession()).collect(Collectors.toList())));
      }
      finally
      {
         _inOnGroupMembersChanged = false;
      }
   }

}
