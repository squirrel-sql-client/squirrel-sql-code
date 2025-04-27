package net.sourceforge.squirrel_sql.client.action;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupController;
import net.sourceforge.squirrel_sql.client.mainframe.action.AboutAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPopUpMenuAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CloseAllButCurrentSessionsAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CloseAllSessionsAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DisplayPluginSummaryAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DumpApplicationAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExitAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.InstallDefaultDriversAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.MaximizeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.NewSessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.SavePreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.SessionPopUpMenuAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowLoadedDriversOnlyAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileHorizontalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileVerticalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewHelpAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewLogsAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.findprefs.FindInPreferencesAction;
import net.sourceforge.squirrel_sql.client.session.action.ChangeTrackAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsButCurrentAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsToLeftAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsToRightAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultWindowsAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseCurrentSQLResultTabAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseSessionWindowAction;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ConvertToStringBuilderAction;
import net.sourceforge.squirrel_sql.client.session.action.CopyQualifiedObjectNameAction;
import net.sourceforge.squirrel_sql.client.session.action.CopySimpleObjectNameAction;
import net.sourceforge.squirrel_sql.client.session.action.CopySqlAction;
import net.sourceforge.squirrel_sql.client.session.action.CutSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.DeleteCurrentLineAction;
import net.sourceforge.squirrel_sql.client.session.action.DeleteSelectedTablesAction;
import net.sourceforge.squirrel_sql.client.session.action.DeleteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.DumpSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.EditWhereColsAction;
import net.sourceforge.squirrel_sql.client.session.action.EscapeDateAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteAllSqlsAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.FilterObjectsAction;
import net.sourceforge.squirrel_sql.client.session.action.FindColumnsAction;
import net.sourceforge.squirrel_sql.client.session.action.FindColumnsInObjectTreeNodesAction;
import net.sourceforge.squirrel_sql.client.session.action.FormatSQLAction;
import net.sourceforge.squirrel_sql.client.session.action.GoToAliasSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.GoToLastEditLocationAction;
import net.sourceforge.squirrel_sql.client.session.action.GotoNextResultsTabAction;
import net.sourceforge.squirrel_sql.client.session.action.GotoPreviousResultsTabAction;
import net.sourceforge.squirrel_sql.client.session.action.InQuotesAction;
import net.sourceforge.squirrel_sql.client.session.action.NewAliasConnectionAction;
import net.sourceforge.squirrel_sql.client.session.action.NewObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.NextSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.NextSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.OpenSqlHistoryAction;
import net.sourceforge.squirrel_sql.client.session.action.PasteFromHistoryAction;
import net.sourceforge.squirrel_sql.client.session.action.PasteFromHistoryAltAcceleratorAction;
import net.sourceforge.squirrel_sql.client.session.action.PreviousSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.PreviousSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeItemAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.action.RemoveNewLinesAction;
import net.sourceforge.squirrel_sql.client.session.action.RemoveQuotesAction;
import net.sourceforge.squirrel_sql.client.session.action.RenameSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
import net.sourceforge.squirrel_sql.client.session.action.SearchInResultsAction;
import net.sourceforge.squirrel_sql.client.session.action.SelectSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.SessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.session.action.SetDefaultCatalogAction;
import net.sourceforge.squirrel_sql.client.session.action.ShowNativeSQLAction;
import net.sourceforge.squirrel_sql.client.session.action.ShowTableReferencesAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleAutoCommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabAnchoredAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabStickyAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleMinimizeResultsAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleObjectTreeBesidesEditorAction;
import net.sourceforge.squirrel_sql.client.session.action.ToolsPopupAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.action.ViewObjectAtCursorInObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.action.ImportTableDataAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.CompareToClipboardAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.DBDiffCompareAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.DBDiffSelectAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileAppendAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileCloseAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileDetachAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileNewAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileOpenAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileOpenRecentAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FilePrintAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileReloadAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAllAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAsAction;
import net.sourceforge.squirrel_sql.client.session.action.multicaret.CaretAddAction;
import net.sourceforge.squirrel_sql.client.session.action.multicaret.CaretRemoveAction;
import net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection.ApplyObjectTreeSelectionFromClipAction;
import net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection.ApplyStoredObjectTreeSelectionAction;
import net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection.CopyObjectTreeSelectionToClipAction;
import net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection.StoreObjectTreeSelectionAction;
import net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection.StoreObjectTreeSelectionNamedAction;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.GitCommitSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionManageAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionOpenAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionOpenManageDialogAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionSaveAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SaveAndManageGroupOfSavedSessionsAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateDataScriptAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateDataScriptOfCurrentSQLAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateFileOfCurrentSQLAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateFileOfSelectedTablesAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateInsertStatementsFileOfCurrentSQLAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateInsertStatementsFileOfSelectedTablesSQLAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateSelectScriptAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateTableOfCurrentSQLAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateTableScriptAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateTemplateDataScriptAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.DropTableScriptAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.CommentAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.CommentActionAltAccelerator;
import net.sourceforge.squirrel_sql.client.session.action.syntax.ConfigureAutoCorrectAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.DuplicateLineAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.FindAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.FindSelectedAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.GoToLineAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.MarkSelectedAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.RepeatLastFindAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.ReplaceAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.ToolsPopupHandler_I18n;
import net.sourceforge.squirrel_sql.client.session.action.syntax.UncommentAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.UncommentActionAltAccelerator;
import net.sourceforge.squirrel_sql.client.session.action.syntax.UnmarkAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirreLRSyntaxTextAreaUI;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.action.SquirrelCopyAsRtfAction;
import net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice.NewSQLWorksheetAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound.QuitSoundAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.CreateResultTabFrameAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.FindInResultAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.FindResultColumnAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.MarkDuplicatesToggleAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.RerunCurrentSQLResultTabAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader.MarkResultTabHeaderMatchingCurSqlAction;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutManager;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutUtil;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.Action;

/**
 * Supposed to be the place where actions are
 * distributed to any necessary place.
 */
public class ActionRegistry
{
   private ActionCollection _actionCollection;

   public ActionRegistry()
   {
      _actionCollection = new ActionCollection();

      preloadActions(_actionCollection);

      _actionCollection.doAfterLoadInitalizations();
   }

   public void registerToolsPopupActions(ActionCollection ac, ToolsPopupController toolsPopupController, SQLPanel sqlPanel, boolean inMainSessionWindow)
   {
      toolsPopupController.addAction("undo", sqlPanel.getUndoAction());
      toolsPopupController.addAction("redo", sqlPanel.getRedoAction());
      toolsPopupController.addAction("runsql", ac.get(ExecuteSqlAction.class));
      toolsPopupController.addAction("runallsqls", ac.get(ExecuteAllSqlsAction.class));
      toolsPopupController.addAction("filenew", ac.get(FileNewAction.class));
      toolsPopupController.addAction("filedetach", ac.get(FileDetachAction.class));
      toolsPopupController.addAction("fileopen", ac.get(FileOpenAction.class));
      toolsPopupController.addAction("fileopenrecent", ac.get(FileOpenRecentAction.class));
      toolsPopupController.addAction("filesave", ac.get(FileSaveAction.class));
      toolsPopupController.addAction("filesaveas", ac.get(FileSaveAsAction.class));
      toolsPopupController.addAction("fileappend", ac.get(FileAppendAction.class));
      toolsPopupController.addAction("fileclose", ac.get(FileCloseAction.class));
      toolsPopupController.addAction("fileprint", ac.get(FilePrintAction.class));
      toolsPopupController.addAction("filereload", ac.get(FileReloadAction.class));

      toolsPopupController.addAction("tabnext", ac.get(GotoNextResultsTabAction.class));
      toolsPopupController.addAction("tabprevious", ac.get(GotoPreviousResultsTabAction.class));
      toolsPopupController.addAction("tabcloseall", ac.get(CloseAllSQLResultTabsAction.class));
      toolsPopupController.addAction("tabcloseallbutcur", ac.get(CloseAllSQLResultTabsButCurrentAction.class));
      toolsPopupController.addAction("tabclosealltoleft", ac.get(CloseAllSQLResultTabsToLeftAction.class));
      toolsPopupController.addAction("tabclosealltoRight", ac.get(CloseAllSQLResultTabsToRightAction.class));
      toolsPopupController.addAction("tabclosecur", ac.get(CloseCurrentSQLResultTabAction.class));
      toolsPopupController.addAction("tabsticky", ac.get(ToggleCurrentSQLResultTabStickyAction.class));
      toolsPopupController.addAction("tabanchored", ac.get(ToggleCurrentSQLResultTabAnchoredAction.class));

      toolsPopupController.addAction("minres", ac.get(ToggleMinimizeResultsAction.class));

      toolsPopupController.addAction("sqlprevious", ac.get(PreviousSqlAction.class));
      toolsPopupController.addAction("sqlnext", ac.get(NextSqlAction.class));
      toolsPopupController.addAction("sqlselect", ac.get(SelectSqlAction.class));
      toolsPopupController.addAction("sqllastedit", ac.get(SelectSqlAction.class));

      toolsPopupController.addAction("caretadd", ac.get(CaretAddAction.class));
      toolsPopupController.addAction("caretremove", ac.get(CaretRemoveAction.class));

      toolsPopupController.addAction("format", ac.get(FormatSQLAction.class));

      toolsPopupController.addAction("compare", ac.get(CompareToClipboardAction.class));

      toolsPopupController.addAction("sqlhist", ac.get(OpenSqlHistoryAction.class));

      if (inMainSessionWindow)
      {
         toolsPopupController.addAction("viewinobjecttree", ac.get(ViewObjectAtCursorInObjectTreeAction.class));
      }

      if(Main.getApplication().getSquirrelPreferences().isResultTabHeaderMarkCurrentSQLsHeader())
      {
         toolsPopupController.addAction("resulttabsql", ac.get(MarkResultTabHeaderMatchingCurSqlAction.class));
      }

      toolsPopupController.addAction("quote", ac.get(InQuotesAction.class));
      toolsPopupController.addAction("unquote", ac.get(RemoveQuotesAction.class));
      toolsPopupController.addAction("quotesb", ac.get(ConvertToStringBuilderAction.class));
      toolsPopupController.addAction("date", ac.get(EscapeDateAction.class));
      toolsPopupController.addAction("sqlcut", ac.get(CutSqlAction.class));
      toolsPopupController.addAction("sqlcopy", ac.get(CopySqlAction.class));
      toolsPopupController.addAction("sqldel", ac.get(DeleteSqlAction.class));
      toolsPopupController.addAction("remcurline", ac.get(DeleteCurrentLineAction.class));
      toolsPopupController.addAction("remnewlines", ac.get(RemoveNewLinesAction.class));
      toolsPopupController.addAction("pastehist", ac.get(PasteFromHistoryAction.class));

      toolsPopupController.addAction("objbesidessql", ac.get(ToggleObjectTreeBesidesEditorAction.class));

      toolsPopupController.addAction("changetrack", ac.get(ChangeTrackAction.class));

      toolsPopupController.addAction("savedsessionopen", ac.get(SessionOpenAction.class));
      toolsPopupController.addAction("savedsessionsave", ac.get(SessionSaveAction.class));
      toolsPopupController.addAction("savedsessioncommit", ac.get(GitCommitSessionAction.class));
      toolsPopupController.addAction("savegroup", ac.get(SaveAndManageGroupOfSavedSessionsAction.class));

      toolsPopupController.addAction("aliaspopup", ac.get(AliasPopUpMenuAction.class));
      toolsPopupController.addAction("sessionpopup", ac.get(SessionPopUpMenuAction.class));

      toolsPopupController.addAction("sql2table", ac.get(CreateTableOfCurrentSQLAction.class));
      toolsPopupController.addAction("sql2ins", ac.get(CreateDataScriptOfCurrentSQLAction.class));
      toolsPopupController.addAction("sql2insfile", ac.get(CreateInsertStatementsFileOfCurrentSQLAction.class));
      toolsPopupController.addAction("sql2file", ac.get(CreateFileOfCurrentSQLAction.class));

      toolsPopupController.addAction("import", ac.get(ImportTableDataAction.class));


      toolsPopupController.addAction(ToolsPopupHandler_I18n.FIND, ac.get(FindAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.FIND_SELECTED , ac.get(FindSelectedAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.REPEAT_LAST_FIND , ac.get(RepeatLastFindAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.MARK_SELECTED , ac.get(MarkSelectedAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.REPLACE, ac.get(ReplaceAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.UNMARK, ac.get(UnmarkAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.GO_TO_LINE, ac.get(GoToLineAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.AUTO_CORR, ac.get(ConfigureAutoCorrectAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.DUP_LINE, ac.get(DuplicateLineAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.COMMENT, ac.get(CommentAction.class));
      toolsPopupController.addAction(ToolsPopupHandler_I18n.UNCOMMENT, ac.get(UncommentAction.class));

      if (Main.getApplication().getSyntaxSQLEntryPanelFactoryProxy().isUseRSyntaxTextArea())
      {
         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) sqlPanel.getSQLPanelAPI().getSQLEntryPanel().getTextComponent();

         Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToUpperCaseKeyStroke()));
         sqlPanel.getSQLPanelAPI().addToToolsPopUp(ToolsPopupHandler_I18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToLowerCaseKeyStroke()));
         sqlPanel.getSQLPanelAPI().addToToolsPopUp(ToolsPopupHandler_I18n.TO_LOWER_CASE, toLowerAction);

         sqlPanel.getSQLPanelAPI().addToToolsPopUp(ToolsPopupHandler_I18n.COPY_AS_RTF, Main.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      }

   }

   public void preloadActions(ActionCollection actionCollection)
   {
      IApplication app = Main.getApplication();
      actionCollection.add(new AboutAction());
      actionCollection.add(new CascadeAction(app));
      actionCollection.add(new ToolsPopupAction(app));
      actionCollection.add(new CloseAllSessionsAction(app));
      actionCollection.add(new CloseAllButCurrentSessionsAction(app));
      actionCollection.add(new CloseAllSQLResultTabsAction(app));
      actionCollection.add(new CloseAllSQLResultTabsButCurrentAction(app));
      actionCollection.add(new CloseAllSQLResultTabsToLeftAction(app));
      actionCollection.add(new CloseAllSQLResultTabsToRightAction(app));
      actionCollection.add(new CloseCurrentSQLResultTabAction(app));
      actionCollection.add(new ToggleCurrentSQLResultTabStickyAction(app));
      actionCollection.add(new ToggleCurrentSQLResultTabAnchoredAction(app));
      actionCollection.add(new CloseAllSQLResultWindowsAction(app));
      actionCollection.add(new ToggleMinimizeResultsAction(app));
      actionCollection.add(new ViewObjectAtCursorInObjectTreeAction(app));
      actionCollection.add(new CloseSessionAction(app));
      actionCollection.add(new CloseSessionWindowAction(app));
      actionCollection.add(new CommitAction(app));
      actionCollection.add(new CopyQualifiedObjectNameAction(app));
      actionCollection.add(new CopySimpleObjectNameAction(app));
      actionCollection.add(new DisplayPluginSummaryAction(app));
      //this.add(new DropSelectedTablesAction(_app));
      actionCollection.add(new DeleteSelectedTablesAction(app));
      actionCollection.add(new ShowTableReferencesAction(app));
      actionCollection.add(new FindColumnsAction(app));
      actionCollection.add(new FindColumnsInObjectTreeNodesAction(app));
      actionCollection.add(new DumpApplicationAction(app));
      actionCollection.add(new SavePreferencesAction(app));
      actionCollection.add(new DumpSessionAction(app));
      actionCollection.add(new ExecuteSqlAction(app));
      actionCollection.add(new ExecuteAllSqlsAction(app));
      actionCollection.add(new ExitAction(app));
      actionCollection.add(new FileNewAction(app));
      actionCollection.add(new FileDetachAction(app));
      actionCollection.add(new FileOpenAction(app));
      actionCollection.add(new FileOpenRecentAction(app));
      actionCollection.add(new FileAppendAction(app));
      actionCollection.add(new FileSaveAction(app));
      actionCollection.add(new FileSaveAsAction());
      actionCollection.add(new FileCloseAction(app));
      actionCollection.add(new FilePrintAction(app));
      actionCollection.add(new FileReloadAction(app));
      actionCollection.add(new ChangeTrackAction(app));

      actionCollection.add(new SessionSaveAction(app));
      actionCollection.add(new GitCommitSessionAction(app));
      actionCollection.add(new SaveAndManageGroupOfSavedSessionsAction());
      actionCollection.add(new SessionOpenAction(app));
      actionCollection.add(new SessionManageAction(app));
      actionCollection.add(new SessionOpenManageDialogAction());

      actionCollection.add(new AliasPopUpMenuAction());
      actionCollection.add(new GlobalPreferencesAction(app));
      actionCollection.add(new GotoNextResultsTabAction(app));
      actionCollection.add(new GotoPreviousResultsTabAction(app));
      actionCollection.add(new InstallDefaultDriversAction(app));
      actionCollection.add(new MaximizeAction(app));
      actionCollection.add(new NewObjectTreeAction(app));
      actionCollection.add(new NewSQLWorksheetAction(app));
      actionCollection.add(new NewAliasConnectionAction(app));
      actionCollection.add(new NewSessionPropertiesAction(app));
      actionCollection.add(new FindInPreferencesAction());
      actionCollection.add(new SessionPopUpMenuAction());
      actionCollection.add(new NextSessionAction(app));
      actionCollection.add(new PreviousSessionAction(app));
      actionCollection.add(new ReconnectAction(app));
      actionCollection.add(new RefreshSchemaInfoAction(app));
      actionCollection.add(new RefreshObjectTreeItemAction(app));
      actionCollection.add(new RollbackAction(app));
      actionCollection.add(new SessionPropertiesAction(app));
      actionCollection.add(new FilterObjectsAction(app));
      actionCollection.add(new SetDefaultCatalogAction(app));
      actionCollection.add(new ShowLoadedDriversOnlyAction(app));
      actionCollection.add(new ShowNativeSQLAction(app));
      actionCollection.add(new SQLFilterAction(app));
      actionCollection.add(new EditWhereColsAction(app));
      actionCollection.add(new TileAction(app));
      actionCollection.add(new TileHorizontalAction(app));
      actionCollection.add(new TileVerticalAction(app));
      actionCollection.add(new ToggleAutoCommitAction(app));
      actionCollection.add(new ViewHelpAction(app));
      actionCollection.add(new ViewLogsAction(app));
      actionCollection.add(new PreviousSqlAction(app));
      actionCollection.add(new NextSqlAction(app));
      actionCollection.add(new SelectSqlAction(app));
      actionCollection.add(new GoToLastEditLocationAction(app));
      actionCollection.add(new OpenSqlHistoryAction(app));
      actionCollection.add(new FormatSQLAction(app));

      actionCollection.add(new RenameSessionAction(app));

      actionCollection.add(new GoToAliasSessionAction());

      actionCollection.add(new RerunCurrentSQLResultTabAction());
      actionCollection.add(new CreateResultTabFrameAction());
      actionCollection.add(new FindInResultAction());
      actionCollection.add(new FindResultColumnAction());
      actionCollection.add(new MarkDuplicatesToggleAction());

      actionCollection.add(new InQuotesAction(app));
      actionCollection.add(new RemoveQuotesAction(app));
      actionCollection.add(new ConvertToStringBuilderAction(app));

      actionCollection.add(new MarkResultTabHeaderMatchingCurSqlAction());

      actionCollection.add(new EscapeDateAction(app));
      actionCollection.add(new CutSqlAction(app));
      actionCollection.add(new CopySqlAction(app));
      actionCollection.add(new DeleteSqlAction(app));
      actionCollection.add(new DeleteCurrentLineAction(app));
      actionCollection.add(new RemoveNewLinesAction(app));
      actionCollection.add(new PasteFromHistoryAction(app));
      actionCollection.add(new PasteFromHistoryAltAcceleratorAction(app));

      actionCollection.add(new UndoAction());
      actionCollection.add(new RedoAction());


      actionCollection.add(new QuitSoundAction());

      actionCollection.add(new ToggleObjectTreeBesidesEditorAction());
      actionCollection.add(new FileSaveAllAction());

      actionCollection.add(new CaretAddAction());
      actionCollection.add(new CaretRemoveAction());

      actionCollection.add(new DBDiffSelectAction());
      actionCollection.add(new DBDiffCompareAction());
      actionCollection.add(new CompareToClipboardAction());

      actionCollection.add(new CreateDataScriptAction());
      actionCollection.add(new CreateTemplateDataScriptAction());
      actionCollection.add(new CreateDataScriptOfCurrentSQLAction());
      actionCollection.add(new CreateInsertStatementsFileOfCurrentSQLAction());
      actionCollection.add(new CreateTableOfCurrentSQLAction());
      actionCollection.add(new CreateTableScriptAction());
      actionCollection.add(new DropTableScriptAction());
      actionCollection.add(new CreateSelectScriptAction());
      actionCollection.add(new CreateFileOfCurrentSQLAction());
      actionCollection.add(new CreateFileOfSelectedTablesAction());
      actionCollection.add(new CreateInsertStatementsFileOfSelectedTablesSQLAction());

      actionCollection.add(new CopyObjectTreeSelectionToClipAction());
      actionCollection.add(new ApplyObjectTreeSelectionFromClipAction());
      actionCollection.add(new StoreObjectTreeSelectionAction());
      actionCollection.add(new StoreObjectTreeSelectionNamedAction());
      actionCollection.add(new ApplyStoredObjectTreeSelectionAction());

      actionCollection.add(new ImportTableDataAction());

      // Former Syntax-Plugin entries, BEGIN
      actionCollection.add(new SquirrelCopyAsRtfAction());
      actionCollection.add(new ConfigureAutoCorrectAction());
      actionCollection.add(new FindAction());
      actionCollection.add(new FindSelectedAction());
      actionCollection.add(new RepeatLastFindAction());
      actionCollection.add(new MarkSelectedAction());
      actionCollection.add(new ReplaceAction());
      actionCollection.add(new UnmarkAction());
      actionCollection.add(new GoToLineAction());
      actionCollection.add(new DuplicateLineAction());
      actionCollection.add(new CommentAction());
      actionCollection.add(new CommentActionAltAccelerator());
      actionCollection.add(new UncommentAction());
      actionCollection.add(new UncommentActionAltAccelerator());
      actionCollection.add(new SquirrelCopyAsRtfAction());
      // Former Syntax-Plugin entries, END

      actionCollection.add(new SearchInResultsAction());
   }

   public ActionCollection getActionCollection()
   {
      return _actionCollection;
   }

   public void loadActionKeys(ActionKeys[] actionKeys)
   {
      _actionCollection.loadActionKeys(actionKeys);
   }

   public void registerMissingActionsToShortcutManager()
   {
      ShortcutManager shortcutManager = Main.getApplication().getShortcutManager();

//      shortcutManager.registerAccelerator(UndoAction.class);
//      shortcutManager.registerAccelerator(RedoAction.class);

   }

}
