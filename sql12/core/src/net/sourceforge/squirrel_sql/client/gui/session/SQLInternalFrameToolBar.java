package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ChangeTrackAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteAllSqlsAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.GoToLastEditLocationAction;
import net.sourceforge.squirrel_sql.client.session.action.NextSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.PreviousSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.SelectSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleObjectTreeBesidesEditorAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileAppendAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileCloseAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileDetachAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileNewAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileOpenAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileOpenRecentAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FilePrintAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileReloadAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAsAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.SQLScriptMenuFactory;
import net.sourceforge.squirrel_sql.client.session.action.syntax.SyntaxMenuFactory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackTypeChooser;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;

/** The class representing the toolbar at the top of a sql internal frame*/
class SQLInternalFrameToolBar extends ToolBar
{
   SQLInternalFrameToolBar(ISession session, ISQLPanelAPI panel)
   {
      createGUI(session, panel);

      SessionColoringUtil.colorToolbar(session, this);
   }

   private void createGUI(ISession session, ISQLPanelAPI panel)
   {
      ActionCollection actions = session.getApplication().getActionCollection();
      setUseRolloverButtons(true);
      setFloatable(false);
      add(actions.get(ExecuteSqlAction.class));
      addSeparator();
      add(actions.get(ExecuteAllSqlsAction.class));
      addSeparator();
      add(actions.get(FileNewAction.class));
      add(actions.get(FileDetachAction.class));
      add(actions.get(FileOpenAction.class));
      add(actions.get(FileOpenRecentAction.class));
      add(actions.get(FileAppendAction.class));
      add(actions.get(FileSaveAction.class));
      add(actions.get(FileSaveAsAction.class));
      add(actions.get(FileCloseAction.class));
      add(actions.get(FilePrintAction.class));
      add(actions.get(FileReloadAction.class));

      add(new ChangeTrackTypeChooser((ChangeTrackAction) actions.get(ChangeTrackAction.class), session).getComponent());
      addSeparator();

      add(actions.get(PreviousSqlAction.class));
      add(actions.get(NextSqlAction.class));
      add(actions.get(SelectSqlAction.class));
      add(actions.get(GoToLastEditLocationAction.class));
      addSeparator();
      addToggleAction((IToggleAction) actions.get(ToggleObjectTreeBesidesEditorAction.class), session);

      addSeparator();
      SQLScriptMenuFactory.getSQLInternalFrameToolbarActions().forEach(a -> add(a));
      addSeparator();

      addSeparator();
      SyntaxMenuFactory.getSQLInternalFrameToolbarActions().forEach(a -> add(a));
      addSeparator();

   }
}
