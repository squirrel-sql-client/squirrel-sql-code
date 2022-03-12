package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.gui.session.IToolsPopupDescription;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ActionUtil;
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


public class SessionOpenAction extends SquirrelAction implements ISessionAction, IToolsPopupDescription
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
            item.addActionListener(e -> onOpenSavedSession(savedSessionJsonBean, _session, false));
            popupMenu.add(item);
         }

         final String sessionOpenAccel = ActionUtil.getAcceleratorString(Main.getApplication().getResources(),
                                                                         Main.getApplication().getActionCollection().get(SessionOpenAction.class));

         final JMenuItem item = new JMenuItem(s_stringMgr.getString("SessionOpenAction.popup.more", sessionOpenAccel));
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

   public void onOpenSavedSessionsDialog()
   {
      SavedSessionMoreCtrl savedSessionOpenCtrl = new SavedSessionMoreCtrl(_session);
		SavedSessionJsonBean savedSessionJsonBean = savedSessionOpenCtrl.getSelectedSavedSession();

      if(null != savedSessionJsonBean)
      {
         if(savedSessionOpenCtrl.isOpenInNewSession())
         {
            onOpenSavedSession(savedSessionJsonBean, null, true);
         }
         else
         {
            onOpenSavedSession(savedSessionJsonBean, _session, true);
         }
      }
   }

   private void onOpenSavedSession(SavedSessionJsonBean savedSessionJsonBean, ISession session, boolean silent)
   {
      final MainFrame mainFrame = Main.getApplication().getMainFrame();
      if( null != session )
      {
         if(false == silent)
         {
            OpenInSessionDlg openInSessionDlg =
                  new OpenInSessionDlg(mainFrame, savedSessionJsonBean.getName(),false == SavedSessionUtil.isSQLVirgin(session));

            if(false == openInSessionDlg.isOk())
            {
               return;
            }

            if(openInSessionDlg.isOpenInNewSession())
            {
               onOpenSavedSession(savedSessionJsonBean, null, silent);
               return;
            }
         }

         SavedSessionUtil.makeSessionSQLVirgin(session);

         loadSavedSession(session.getSessionInternalFrame(), savedSessionJsonBean);
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

   @Override
   public String getToolsPopupDescription()
   {
      return s_stringMgr.getString("SessionOpenAction.tools.popup.description");
   }
}
