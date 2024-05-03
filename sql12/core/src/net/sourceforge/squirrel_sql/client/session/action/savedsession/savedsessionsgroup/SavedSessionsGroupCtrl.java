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

   private SavedSessionsGroupJsonBean _activeSavedSessionsGroup;

   private boolean _inOnListSelectionChanged;
   private boolean _groupNameEditedByUser;

   public SavedSessionsGroupCtrl()
   {
      _dlg = new SavedSessionsGroupDlg();

      String groupId = null;
      if (null != Main.getApplication().getSessionManager().getActiveSession().getSavedSession())
      {
         groupId = Main.getApplication().getSessionManager().getActiveSession().getSavedSession().getGroupId();
      }
      _activeSavedSessionsGroup = Main.getApplication().getSavedSessionsManager().getGroup(groupId);

      if (null != _activeSavedSessionsGroup)
      {
         _dlg.txtGroupName.setText(_activeSavedSessionsGroup.getGroupName());
      }

      _sessionsListCtrl = new SessionsListCtrl(_dlg.lstSessions, _activeSavedSessionsGroup, () -> onSessionsListSelectionChanged());
      onSessionsListSelectionChanged();


      _dlg.txtGroupName.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            handleGroupNameUpdatedByUser();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            handleGroupNameUpdatedByUser();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            handleGroupNameUpdatedByUser();
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

   private void handleGroupNameUpdatedByUser()
   {
      if(_inOnListSelectionChanged)
      {
         return;
      }

      _groupNameEditedByUser = true;
   }


   private void onSaveGroup(boolean gitCommit)
   {
      List<GroupDlgSessionWrapper> toSaveWrappers = _sessionsListCtrl.getSelectedValuesList();

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
            if(null == _activeSavedSessionsGroup || false == Objects.equals(_activeSavedSessionsGroup.getGroupId(), sessWrp.getSession().getSavedSession().getGroupId()))
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
      if(null == _activeSavedSessionsGroup)
      {
         _activeSavedSessionsGroup = new SavedSessionsGroupJsonBean();
      }

      _activeSavedSessionsGroup.setGroupName(groupName);

      ISession activeSessionInGroup = Main.getApplication().getSessionManager().getActiveSession();
      SaveSessionResult saveSessionResultOfActiveSession = null;

      Collections.reverse(toSave); // Reverse because saved is moved to the top of SavedSessionsJsonBean._savedSessionJsonBeans
      for (ISession sess : toSave)
      {
         SaveSessionResult buf = SessionPersister.saveSessionGroup(sess, _activeSavedSessionsGroup, gitCommit, sess == activeSessionInGroup);
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

   private void onSessionsListSelectionChanged()
   {
      try
      {
         _inOnListSelectionChanged = true;
         if(_groupNameEditedByUser || null != _activeSavedSessionsGroup)
         {
            return;
         }

         if(_sessionsListCtrl.getSelectedValuesList().isEmpty())
         {
            _dlg.txtGroupName.setText(s_stringMgr.getString("SavedSessionsGroupCtrl.error.cannot.save.empty.group"));
         }
         _dlg.txtGroupName.setText(SavedSessionUtil.createSessionGroupNameTemplate(_sessionsListCtrl.getSelectedValuesList().stream().map(w -> w.getSession()).collect(Collectors.toList())));
      }
      finally
      {
         _inOnListSelectionChanged = false;
      }
   }

}
