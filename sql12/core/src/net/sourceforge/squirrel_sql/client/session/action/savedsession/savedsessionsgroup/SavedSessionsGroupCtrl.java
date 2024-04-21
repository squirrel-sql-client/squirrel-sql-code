package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SavedSessionsGroupCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionsGroupCtrl.class);
   public static final String PROPS_KEY_DEFAULT_BUTTON = "GroupOfSavedSessionsCtrl.GroupSaveDefaultButton";

   private final SavedSessionsGroupDlg _dlg;

   private SavedSessionsGroupJsonBean _savedSessionsGroup;

   private boolean _inOnListSelectionChanged;
   private boolean _groupNameEditedByUser;

   public SavedSessionsGroupCtrl()
   {
      _dlg = new SavedSessionsGroupDlg();

      DefaultListModel<ISession> sessionListModel = new DefaultListModel<>();
      sessionListModel.addAll(Main.getApplication().getSessionManager().getOpenSessions());

      String groupId = null;
      if (null != Main.getApplication().getSessionManager().getActiveSession().getSavedSession())
      {
         groupId = Main.getApplication().getSessionManager().getActiveSession().getSavedSession().getGroupId();
      }
      _savedSessionsGroup = Main.getApplication().getSavedSessionsManager().getGroup(groupId);

      if (null != _savedSessionsGroup)
      {
         _dlg.txtGroupName.setText(_savedSessionsGroup.getGroupName());
      }

      _dlg.lstSessions.setModel(sessionListModel);
      _dlg.lstSessions.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

      List<Integer> selectedIndices = new ArrayList<>();
      for (int i = 0; i < _dlg.lstSessions.getModel().getSize(); i++)
      {
         if ( null != _savedSessionsGroup )
         {
            if(Objects.equals(_savedSessionsGroup.getGroupId(), (_dlg.lstSessions.getModel().getElementAt(i).getSavedSession().getGroupId())))
            {
               selectedIndices.add(i);
            }
         }
         else
         {
            selectedIndices.add(i);
         }
      }
      _dlg.lstSessions.setSelectedIndices(selectedIndices.stream().mapToInt(i -> i).toArray());

      _dlg.lstSessions.addListSelectionListener(e -> onListSelectionChanged());
      onListSelectionChanged();


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
      List<ISession> toSave = _dlg.lstSessions.getSelectedValuesList();

      if(toSave.isEmpty())
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

      for (ISession sess : toSave)
      {
         if(null != sess.getSavedSession())
         {
            if(null == _savedSessionsGroup || false == Objects.equals(_savedSessionsGroup.getGroupId(), sess.getSavedSession().getGroupId()))
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

      if(null == _savedSessionsGroup)
      {
         _savedSessionsGroup = new SavedSessionsGroupJsonBean();
      }

      _savedSessionsGroup.setGroupName(groupName);

      for (ISession sess : toSave)
      {
         SessionPersister.saveSessionGroup(sess, _savedSessionsGroup, gitCommit, sess.equals(Main.getApplication().getSessionManager().getActiveSession()));
      }

      close();
   }

   private void close()
   {
      Props.putString(PROPS_KEY_DEFAULT_BUTTON, ((SavedSessionsGroupDlgDefaultButton)_dlg.cboDefaultButton.getSelectedItem()).name());

      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onListSelectionChanged()
   {
      try
      {
         _inOnListSelectionChanged = true;
         if(_groupNameEditedByUser || null != _savedSessionsGroup)
         {
            return;
         }

         if(_dlg.lstSessions.getSelectedValuesList().isEmpty())
         {
            _dlg.txtGroupName.setText(s_stringMgr.getString("SavedSessionsGroupCtrl.error.cannot.save.empty.group"));
         }
         _dlg.txtGroupName.setText(SavedSessionUtil.createSessionGroupNameTemplate(_dlg.lstSessions.getSelectedValuesList()));
      }
      finally
      {
         _inOnListSelectionChanged = false;
      }
   }

}
