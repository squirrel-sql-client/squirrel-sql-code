package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class SessionManageAction extends SquirrelAction implements ISessionAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionManageAction.class);

   private ISession _session;

   public SessionManageAction(IApplication app)
   {
      super(app);
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

         JButton toolBarButton = (JButton) ae.getSource();

         popupMenu.show(toolBarButton, 0, toolBarButton.getHeight());
      }
   }

   private void onSaveAsNewSavedSession()
   {
      System.out.println("SessionManageAction.onSaveAsNewSavedSession");
   }

   private void onRenameSavedSession()
   {
      SessionSaveDlg sessionSaveDlg = new SessionSaveDlg(Main.getApplication().getMainFrame(), _session.getSavedSession().getName());

      if(false == sessionSaveDlg.isOk())
      {
         return;
      }

      _session.getSavedSession().setName(sessionSaveDlg.getSavedSessionName());

      System.out.println("SessionManageAction.onRenameSavedSession");
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