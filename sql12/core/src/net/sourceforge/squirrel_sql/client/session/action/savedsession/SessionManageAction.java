package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ActionUtil;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionGrouped;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionsGroupJsonBean;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class SessionManageAction extends SquirrelAction implements ISessionAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionManageAction.class);

   private ISession _session;

   public SessionManageAction(IApplication app)
   {
      super(app);
      updateUI();
   }

   public void actionPerformed(ActionEvent ae)
   {
      if(ae.getSource() instanceof JButton )
      {
         JPopupMenu popupMenu = new JPopupMenu();

         JMenuItem item;

         if(StringUtilities.isEmpty(_session.getSavedSession().getGroupId(), true))
         {
            item = new JMenuItem(s_stringMgr.getString("SessionManageAction.rename.session", _session.getSavedSession().getName()));
            item.addActionListener(e -> onRenameSavedSession());
            popupMenu.add(item);

            item = new JMenuItem(s_stringMgr.getString("SessionManageAction.save.as.new.session", _session.getSavedSession().getName()));
            item.addActionListener(e -> onSaveAsNewSavedSession());
            popupMenu.add(item);
         }

         if(StringUtilities.isEmpty(_session.getSavedSession().getGroupId(), true))
         {
            item = new JMenuItem(s_stringMgr.getString("SessionManageAction.print.saved.session.details.msg.panel", _session.getSavedSession().getName()));
         }
         else
         {
            SavedSessionsGroupJsonBean group = Main.getApplication().getSavedSessionsManager().getGroup(_session.getSavedSession().getGroupId());
            item = new JMenuItem(s_stringMgr.getString("SessionManageAction.print.saved.session.in.group.details.msg.panel", group.getGroupName()));
         }
         item.addActionListener(e -> onPrintDetailsToMessagePanel());
         popupMenu.add(item);

         final String sessionOpenAccel = ActionUtil.getAcceleratorString(Main.getApplication().getResources(),
                                                                         Main.getApplication().getActionCollection().get(SessionOpenAction.class));

         item = new JMenuItem(s_stringMgr.getString("SessionManageAction.more", sessionOpenAccel));
         item.addActionListener(e -> onOpenManageSavedSessionDialog());
         popupMenu.add(item);

         JButton toolBarButton = (JButton) ae.getSource();

         popupMenu.show(toolBarButton, 0, toolBarButton.getHeight());
      }
   }

   private void onOpenManageSavedSessionDialog()
   {
      ((SessionOpenAction)Main.getApplication().getActionCollection().get(SessionOpenAction.class)).onOpenSavedSessionsMoreDialog();
   }

   private void onPrintDetailsToMessagePanel()
   {
      SavedSessionGrouped savedSessionsGroup = Main.getApplication().getSavedSessionsManager().getSavedSessionGrouped(_session.getSavedSession());
      SavedSessionUtil.printSavedSessionDetails(savedSessionsGroup);
   }

   private void onSaveAsNewSavedSession()
   {
      final SavedSessionJsonBean buf = _session.getSavedSession();
      _session.setSavedSession(null);
      if(false == SessionPersister.saveSession(_session))
      {
         // Save was canceled. So restore the former.
         _session.setSavedSession(buf);
      }
   }

   private void onRenameSavedSession()
   {
      SavedSessionGrouped savedSessionGrouped = Main.getApplication().getSavedSessionsManager().getSavedSessionGrouped(_session.getSavedSession());

      SessionSaveDlg sessionSaveDlg = new SessionSaveDlg(Main.getApplication().getMainFrame(), _session.getSavedSession().getName());

      if(false == sessionSaveDlg.isOk())
      {
         return;
      }

      _session.getSavedSession().setName(sessionSaveDlg.getSavedSessionName());
      Main.getApplication().getSavedSessionsManager().moveToTop(savedSessionGrouped);
      // SessionPersister.saveSession(session, false);

      Main.getApplication().getMainFrame().getMainFrameTitleHandler().updateMainFrameTitle();
   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;
      updateUI();
   }

   public void updateUI()
   {
      setEnabled(null != _session && null != _session.getSavedSession());
   }
}