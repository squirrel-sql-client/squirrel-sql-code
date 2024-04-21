package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.gui.session.IToolsPopupDescription;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ActionUtil;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.MultipleSavedSessionOpener;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionGrouped;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
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
      final List<SavedSessionGrouped> savedSessionsGrouped = Main.getApplication().getSavedSessionsManager().getSavedSessionsGrouped();

      if(ae.getSource() instanceof JButton)
      {
         JPopupMenu popupMenu = new JPopupMenu();

         for (int i = 0; i < Math.min(10, savedSessionsGrouped.size()); i++)
         {
            SavedSessionGrouped savedSessionGrouped =  savedSessionsGrouped.get(i);
            final JMenuItem item = new JMenuItem(savedSessionGrouped.getName(), getIcon(savedSessionGrouped));
            item.addActionListener(e -> onOpenSavedSessionGrouped(savedSessionGrouped, _session, false));
            popupMenu.add(item);
         }

         final String sessionOpenAccel = ActionUtil.getAcceleratorString(Main.getApplication().getResources(),
                                                                         Main.getApplication().getActionCollection().get(SessionOpenAction.class));

         final JMenuItem item = new JMenuItem(s_stringMgr.getString("SessionOpenAction.popup.more", sessionOpenAccel));
         item.addActionListener(e -> onOpenSavedSessionsMoreDialog());
         popupMenu.add(item);

         JButton toolBarButton = (JButton) ae.getSource();

         popupMenu.show(toolBarButton, 0, toolBarButton.getHeight());
      }
      else
      {
         onOpenSavedSessionsMoreDialog();
      }
   }

   private ImageIcon getIcon(SavedSessionGrouped savedSessionGrouped)
   {
      if(false == savedSessionGrouped.isGroup())
      {
         return null;
      }


      return Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SESSION_GROUP_SAVE);
   }

   public void onOpenSavedSessionsMoreDialog()
   {
      SavedSessionMoreCtrlSingleton.openDialog(_session, (ssg, inNewSess) -> onSavedSessionMoreResultReceived(ssg, inNewSess));
   }

   private void onSavedSessionMoreResultReceived(SavedSessionGrouped savedSessionGrouped, boolean openInNewSession)
   {
      if(null != savedSessionGrouped)
      {
         if(openInNewSession)
         {
            onOpenSavedSessionGrouped(savedSessionGrouped, null, true);
         }
         else
         {
            onOpenSavedSessionGrouped(savedSessionGrouped, _session, true);
         }
      }
   }

   private void onOpenSavedSessionGrouped(SavedSessionGrouped savedSessionGrouped, ISession session, boolean silent)
   {
      final MainFrame mainFrame = Main.getApplication().getMainFrame();
      if( null != session && false == savedSessionGrouped.isGroup())
      {
         if(false == silent)
         {
            OpenInSessionDlg openInSessionDlg =
                  new OpenInSessionDlg(mainFrame, savedSessionGrouped.getName(),false == SavedSessionUtil.isSQLVirgin(session));

            if(false == openInSessionDlg.isOk())
            {
               return;
            }

            if(openInSessionDlg.isOpenInNewSession())
            {
               onOpenSavedSessionGrouped(savedSessionGrouped, null, silent);
               return;
            }
         }

         SavedSessionUtil.makeSessionSQLVirgin(session);

         loadSavedSession(session.getSessionInternalFrame(), savedSessionGrouped.getNoGroupedSavedSession());
      }
      else
      {
         List<SavedSessionJsonBean> savedSessionsToOpen = new ArrayList<>();

         for (SavedSessionJsonBean savedSession : savedSessionGrouped.getSavedSessions())
         {
            final SQLAlias alias = SavedSessionUtil.getAliasForIdString(savedSession.getDefaultAliasIdString());

            if(null == alias)
            {
               if (savedSessionGrouped.isGroup())
               {
                  JOptionPane.showMessageDialog(mainFrame, s_stringMgr.getString("SessionOpenAction.missing.alias.for.group"));
               }
               else
               {
                  JOptionPane.showMessageDialog(mainFrame, s_stringMgr.getString("SessionOpenAction.missing.alias"));
               }

               // In case of a Saved Session savedSessionGrouped.getSavedSessions() contains one element only hence this
               // continue means return.
               // In case of a Saved Session group we skip
               continue;
            }

            savedSessionsToOpen.add(savedSession);
         }

         MultipleSavedSessionOpener.openSavedSessions(savedSessionsToOpen);
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
