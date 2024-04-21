package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.mainframe.action.findprefs.PreferencesFindSupport;
import net.sourceforge.squirrel_sql.client.mainframe.action.findprefs.SavedSessionMoreDialogFindInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SavedSessionGrouped;

public class SavedSessionMoreCtrlSingleton
{
   private static SavedSessionMoreCtrl _openInstance;

   private static SavedSessionMoreDialogFindInfo createPropertiesFinderInfo()
   {
      SavedSessionMoreDialogFindInfo ret = new SavedSessionMoreDialogFindInfo();

      SavedSessionMoreCtrl savedSessionMoreCtrl = _openInstance;
      if(null == savedSessionMoreCtrl)
      {
         savedSessionMoreCtrl = new SavedSessionMoreCtrl(null, null, true);
      }

      ret.setTitle(savedSessionMoreCtrl.getTitle());

      ret.setContentPane(savedSessionMoreCtrl.getContentPane());

      return ret;
   }

   /**
    * @param session nullable.
    */
   public static void openDialog(ISession session, SavedSessionMoreCtrlClosingListener savedSessionMoreCtrlClosingListener)
   {
      if(null == _openInstance)
      {
         _openInstance =
               new SavedSessionMoreCtrl(session,
                                        (ssjb, newSess) -> onClosed(ssjb, newSess, savedSessionMoreCtrlClosingListener),
                                        false);
      }
      else
      {
         _openInstance.toFront();
      }
   }

   private static void onClosed(SavedSessionGrouped ssg, boolean newSess, SavedSessionMoreCtrlClosingListener listener)
   {
      _openInstance = null;
      listener.closed(ssg, newSess);
   }

   public static PreferencesFindSupport<SavedSessionMoreDialogFindInfo> getPreferencesFindSupport()
   {
      return ofOpenDialog -> onCreateFindInfo(ofOpenDialog);
   }

   private static SavedSessionMoreDialogFindInfo onCreateFindInfo(boolean ofOpenDialog)
   {
      if(ofOpenDialog)
      {
         // Ensures that _openInstance is initialized.
         ((SessionOpenAction) Main.getApplication().getActionCollection().get(SessionOpenAction.class)).onOpenSavedSessionsMoreDialog();
      }


      SavedSessionMoreDialogFindInfo ret = new SavedSessionMoreDialogFindInfo();

      SavedSessionMoreCtrl savedSessionMoreCtrl = _openInstance;
      if(null == savedSessionMoreCtrl)
      {
         savedSessionMoreCtrl = new SavedSessionMoreCtrl(null, null, true);
      }

      ret.setTitle(savedSessionMoreCtrl.getTitle());

      ret.setContentPane(savedSessionMoreCtrl.getContentPane());

      return ret;
   }
}
