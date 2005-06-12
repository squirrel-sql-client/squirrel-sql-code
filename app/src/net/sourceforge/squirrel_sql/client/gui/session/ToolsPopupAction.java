package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.FileOpenAction;
import net.sourceforge.squirrel_sql.client.session.action.FileSaveAction;
import net.sourceforge.squirrel_sql.client.session.action.FileSaveAsAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.Completor;
import net.sourceforge.squirrel_sql.fw.completion.CompletorListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;


public class ToolsPopupAction extends SquirrelAction
{
   private ToolsPopupCompletorModel _toolsPopupCompletorModel;
   private ISQLEntryPanel _sqlEntryPanel;
   private ISession _session;
   private Completor _toolsCompletor;

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
   }

   private void onToolsPopupActionSelected(CompletionInfo completion)
   {
      final ToolsPopupCompletionInfo toExecute = (ToolsPopupCompletionInfo) completion;
      toExecute.getAction().actionPerformed(new ActionEvent(_sqlEntryPanel.getTextComponent(), _session.getIdentifier().hashCode(), "ToolsPopupSelected"));
   }


   public void actionPerformed(ActionEvent evt)
   {
      _toolsCompletor.show();
   }

   public void addAction(String selectionString, Action action)
   {
      _toolsPopupCompletorModel.addAction(selectionString, action);
   }
}
