package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.util.List;


public class SessionOpenAction extends SquirrelAction implements ISessionAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionOpenAction.class);

   private ISession _session;

   public SessionOpenAction(IApplication app)
   {
      super(app);
   }

   public void actionPerformed(ActionEvent ae)
   {
      final List<SavedSessionJsonBean> savedSessions = Main.getApplication().getSavedSessionsManager().getSavedSessions();

      if(ae.getSource() instanceof JButton)
      {
         JPopupMenu popupMenu = new JPopupMenu();

         for (int i = 0; i < Math.min(10, savedSessions.size()); i++)
         {
            SavedSessionJsonBean savedSessionJsonBean =  savedSessions.get(i);
            final JMenuItem item = new JMenuItem(savedSessionJsonBean.getName());
            item.addActionListener(e -> onOpenSavedSession(savedSessionJsonBean));
            popupMenu.add(item);
         }

         final JMenuItem item = new JMenuItem(s_stringMgr.getString("SessionOpenAction.popup.more"));
         item.addActionListener(e -> onOpenSavedSessionsDialog());
         popupMenu.add(item);

         JButton toolBarButton = (JButton) ae.getSource();

         popupMenu.show(toolBarButton, 0, toolBarButton.getHeight());
      }
      else
      {
         onOpenSavedSessionsDialog();
      }
   }

   private void onOpenSavedSessionsDialog()
   {
      System.out.println("SessionOpenAction.onOpenSavedSessionsDialog");
//		SavedSessionOpenCtrl savedSessionOpenCtrl = new SavedSessionOpenCtrl(_session);
//		SavedSessionJsonBean savedSessionJsonBean = savedSessionOpenCtrl.getSelectedSavedSession();
   }

   private void onOpenSavedSession(SavedSessionJsonBean savedSessionJsonBean)
   {
      final MainFrame mainFrame = Main.getApplication().getMainFrame();
      if(null != _session)
      {
         if( false == SavedSessionUtil.isSQLVirgin(_session))
         {
            final String msg = s_stringMgr.getString("SessionOpenAction.discard.open.session", _session.getTitle());
            if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(mainFrame, msg))
            {
               return;
            }

            SavedSessionUtil.makeSessionSQLVirgin(_session);
         }

         loadSavedSession(_session.getSessionInternalFrame(), savedSessionJsonBean);
      }
      else
      {
         final ISQLAlias alias = SavedSessionUtil.getAliasForIdString(savedSessionJsonBean.getDefaultAliasIdString());

         if(null == alias)
         {
            JOptionPane.showMessageDialog(mainFrame, s_stringMgr.getString("SessionOpenAction.missing.alias"));
            return;
         }

         final ConnectToAliasCallBack callback = new ConnectToAliasCallBack((SQLAlias) alias)
         {
            @Override
            public void sessionInternalFrameCreated(SessionInternalFrame sessionInternalFrame)
            {
               loadSavedSession(sessionInternalFrame, savedSessionJsonBean);
            }
         };

         new ConnectToAliasCommand((SQLAlias) alias, true, callback).execute();

      }
   }

   private void loadSavedSession(SessionInternalFrame sessionInternalFrame, SavedSessionJsonBean savedSessionJsonBean)
   {
      SavedSessionLoader.load(sessionInternalFrame, savedSessionJsonBean);
   }


   @Override
   public void setSession(ISession session)
   {
      _session = session;
   }
}
