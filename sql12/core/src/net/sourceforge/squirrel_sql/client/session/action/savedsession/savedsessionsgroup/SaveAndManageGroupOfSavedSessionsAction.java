package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.IToolsPopupDescription;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;

public class SaveAndManageGroupOfSavedSessionsAction extends SquirrelAction implements ISessionAction, IToolsPopupDescription
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SaveAndManageGroupOfSavedSessionsAction.class);

   public SaveAndManageGroupOfSavedSessionsAction()
   {
      super(Main.getApplication());
      setEnabled(false);
   }

   @Override
   public void setSession(ISession session)
   {
      setEnabled(null != session);
   }


   @Override
   public void actionPerformed(ActionEvent evt)
   {
      new SavedSessionsGroupCtrl();
   }

   public String getToolsPopupDescription()
   {
      return s_stringMgr.getString("SaveAndManageGroupOfSavedSessionsAction.tools.popup.description");
   }

}
