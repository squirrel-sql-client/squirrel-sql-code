package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsButCurrentAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.Completor;
import net.sourceforge.squirrel_sql.fw.completion.CompletorListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;


public class ToolsPopupAction extends SquirrelAction
{
   private ToolsPopupCompletorModel _toolsPopupCompletorModel;
   private ISQLEntryPanel _sqlEntryPanel;
   private ISession _session;
   private Completor _toolsCompletor;
   private static final String PREFS_KEY_CTRL_T_COUNT = "squirrelSql_toolsPopup_ctrl_t_count";
   private int _ctrlTCount;

   public ToolsPopupAction(IApplication app, SQLPanel sqlPanel, ISession session)
   {
      super(app);
      _sqlEntryPanel = sqlPanel.getSQLEntryPanel();
      _session = session;

      _toolsPopupCompletorModel = new ToolsPopupCompletorModel();
      _toolsCompletor = new Completor((JTextComponent)_sqlEntryPanel.getTextComponent(), _toolsPopupCompletorModel, new Color(255,204,204), true);

      _toolsCompletor.addCodeCompletorListener
      (
         new CompletorListener()
         {
            public void completionSelected(CompletionInfo completion, int replaceBegin)
            {onToolsPopupActionSelected(completion);}
         }
      );

      ActionCollection ac = app.getActionCollection();

      addAction("undo", sqlPanel.getUndoAction());
      addAction("redo", sqlPanel.getRedoAction());
      addAction("runsql", ac.get(ExecuteSqlAction.class));
      addAction("fileopen", ac.get(FileOpenAction.class));
      addAction("filesave", ac.get(FileSaveAction.class));
      addAction("filesaveas", ac.get(FileSaveAsAction.class));

      addAction("tabnext", ac.get(GotoNextResultsTabAction.class));
      addAction("tabprevious", ac.get(GotoPreviousResultsTabAction.class));
      addAction("tabcloseall", ac.get(CloseAllSQLResultTabsAction.class));
      addAction("tabcloseallbutcur", ac.get(CloseAllSQLResultTabsButCurrentAction.class));

      _ctrlTCount = Preferences.userRoot().getInt(PREFS_KEY_CTRL_T_COUNT, 0);

      if(3 > _ctrlTCount)
      {
         _session.getMessageHandler().showMessage("Please try out the Tools popup by hitting ctrl+t in the SQL Editor. Do it three times to stop this message.");
      }
   }

   private void onToolsPopupActionSelected(CompletionInfo completion)
   {
      final ToolsPopupCompletionInfo toExecute = (ToolsPopupCompletionInfo) completion;
      toExecute.getAction().actionPerformed(new ActionEvent(_sqlEntryPanel.getTextComponent(), _session.getIdentifier().hashCode(), "ToolsPopupSelected"));
   }


   public void actionPerformed(ActionEvent evt)
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
