package net.sourceforge.squirrel_sql.client.gui.session;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.Completor;
import net.sourceforge.squirrel_sql.fw.completion.CompletorListener;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class ToolsPopupController
{
   private ToolsPopupCompletorModel _toolsPopupCompletorModel;
   private ISQLEntryPanel _sqlEntryPanel;
   private ISession _session;
   private Completor _toolsCompletor;
   private static final String PREFS_KEY_CTRL_T_COUNT = "squirrelSql_toolsPopup_ctrl_t_count";
   private int _ctrlTCount;

   /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ToolsPopupController.class);

   public ToolsPopupController(ISession session, ISQLEntryPanel sqlEntryPanel)
   {
      _sqlEntryPanel = sqlEntryPanel;
      _session = session;
      
      _toolsPopupCompletorModel = new ToolsPopupCompletorModel();

      CompletorListener completorListener = new CompletorListener()
      {
         public void completionSelected(CompletionInfo completion, int replaceBegin, int keyCode, int modifiers)
         {
            onToolsPopupActionSelected(completion);
         }
      };


      _toolsCompletor = new Completor(_sqlEntryPanel.getTextComponent(), _toolsPopupCompletorModel, completorListener, new Color(255,204,204), true);

      sqlEntryPanel.getSession().addSimpleSessionListener(new SimpleSessionListener()
      {
         public void sessionClosed()
         {
            _toolsCompletor.disposePopup();
         }
      });


      _ctrlTCount = Preferences.userRoot().getInt(PREFS_KEY_CTRL_T_COUNT, 0);

      if(3 > _ctrlTCount)
      {
          // i18n[ToolsPopupController.toolspopupmsg=Please try out the Tools popup by hitting ctrl+t in the SQL Editor. Do it three times to stop this message.]
         _session.showMessage(s_stringMgr.getString("ToolsPopupController.toolspopupmsg"));
      }
   }

   private void onToolsPopupActionSelected(CompletionInfo completion)
   {
      final ToolsPopupCompletionInfo toExecute = (ToolsPopupCompletionInfo) completion;
      toExecute.getAction().actionPerformed(new ActionEvent(_sqlEntryPanel.getTextComponent(), _session.getIdentifier().hashCode(), "ToolsPopupSelected"));
   }


   public void showToolsPopup()
   {
      if(3 > _ctrlTCount)
      {
         int ctrlTCount = Preferences.userRoot().getInt(PREFS_KEY_CTRL_T_COUNT, 0);
         Preferences.userRoot().putInt(PREFS_KEY_CTRL_T_COUNT, ++ctrlTCount);
      }

      _toolsCompletor.show();
   }

   public void addAction(String selectionString, Action action)
   {
      _toolsPopupCompletorModel.addAction(selectionString, action);
   }
}
