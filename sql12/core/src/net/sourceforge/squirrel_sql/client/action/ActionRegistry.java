package net.sourceforge.squirrel_sql.client.action;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupController;
import net.sourceforge.squirrel_sql.client.mainframe.action.*;
import net.sourceforge.squirrel_sql.client.mainframe.action.findprefs.FindInPreferencesAction;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.action.ImportTableDataAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.CompareToClipboardAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.DBDiffCompareAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.DBDiffSelectAction;
import net.sourceforge.squirrel_sql.client.session.action.file.*;
import net.sourceforge.squirrel_sql.client.session.action.multicaret.CaretAddAction;
import net.sourceforge.squirrel_sql.client.session.action.multicaret.CaretRemoveAction;
import net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection.*;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.GitCommitSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionManageAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionOpenAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionSaveAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup.SaveAndManageGroupOfSavedSessionsAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.*;
import net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice.NewSQLWorksheetAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound.QuitSoundAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.*;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutManager;

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

      toolsPopupController.addAction("caretadd", ac.get(CaretAddAction.class), CaretAddAction.getToolsPopupDescription());
      toolsPopupController.addAction("caretremove", ac.get(CaretRemoveAction.class));

      toolsPopupController.addAction("format", ac.get(FormatSQLAction.class));

      toolsPopupController.addAction("compare", ac.get(CompareToClipboardAction.class));

      toolsPopupController.addAction("sqlhist", ac.get(OpenSqlHistoryAction.class));

      if (inMainSessionWindow)
      {
         toolsPopupController.addAction("viewinobjecttree", ac.get(ViewObjectAtCursorInObjectTreeAction.class));
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
      toolsPopupController.addAction("savemultiplesessions", ac.get(SaveAndManageGroupOfSavedSessionsAction.class));

      toolsPopupController.addAction("aliaspopup", ac.get(AliasPopUpMenuAction.class));
      toolsPopupController.addAction("sessionpopup", ac.get(SessionPopUpMenuAction.class));

      toolsPopupController.addAction("sql2table", ac.get(CreateTableOfCurrentSQLAction.class));
      toolsPopupController.addAction("sql2ins", ac.get(CreateDataScriptOfCurrentSQLAction.class));
      toolsPopupController.addAction("sql2insfile", ac.get(CreateInsertStatementsFileOfCurrentSQLAction.class));
      toolsPopupController.addAction("sql2file", ac.get(CreateFileOfCurrentSQLAction.class));

      toolsPopupController.addAction("import", ac.get(ImportTableDataAction.class));

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
