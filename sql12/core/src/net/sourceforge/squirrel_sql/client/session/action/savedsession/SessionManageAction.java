package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ActionUtil;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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

         item = new JMenuItem(s_stringMgr.getString("SessionManageAction.rename.session", _session.getSavedSession().getName()));
         item.addActionListener(e -> onRenameSavedSession());
         popupMenu.add(item);

         item = new JMenuItem(s_stringMgr.getString("SessionManageAction.save.as.new.session", _session.getSavedSession().getName()));
         item.addActionListener(e -> onSaveAsNewSavedSession());
         popupMenu.add(item);

         item = new JMenuItem(s_stringMgr.getString("SessionManageAction.print.saved.session.details.msg.panel", _session.getSavedSession().getName()));
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
      ((SessionOpenAction)Main.getApplication().getActionCollection().get(SessionOpenAction.class)).onOpenSavedSessionsDialog();
   }

   private void onPrintDetailsToMessagePanel()
   {

      final String savedSessionName = _session.getSavedSession().getName();
      final ISQLAlias alias = SavedSessionUtil.getAliasForIdString(_session.getSavedSession().getDefaultAliasIdString());
      String aliasName = "<unknown>";
      String jdbcUrl = "<unknown>";
      String jdbcUser = "<unknown>";
      if(null != alias)
      {
         aliasName = alias.getName();
         jdbcUrl = alias.getUrl();
         jdbcUser = alias.getUserName();
      }

      final String msg = s_stringMgr.getString("SessionManageAction.saved.session.details", savedSessionName, aliasName, jdbcUrl, jdbcUser);
      Main.getApplication().getMessageHandler().showMessage(msg);
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
      SessionSaveDlg sessionSaveDlg = new SessionSaveDlg(Main.getApplication().getMainFrame(), _session.getSavedSession().getName());

      if(false == sessionSaveDlg.isOk())
      {
         return;
      }

      _session.getSavedSession().setName(sessionSaveDlg.getSavedSessionName());

      Main.getApplication().getSavedSessionsManager().moveToTop(_session.getSavedSession());

      SessionPersister.saveSession(_session, false);
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