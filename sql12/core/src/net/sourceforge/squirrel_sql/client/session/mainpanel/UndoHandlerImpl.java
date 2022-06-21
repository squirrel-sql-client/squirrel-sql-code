package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.fw.resources.Resources;

import javax.swing.Action;
import javax.swing.JComponent;

public class UndoHandlerImpl implements IUndoHandler
{
   private UndoAction _undoAction;
   private RedoAction _redoAction;

   public UndoHandlerImpl(ISQLEntryPanel entry)
   {
      _undoAction = (UndoAction) Main.getApplication().getActionCollection().get(UndoAction.class);
      _redoAction = (RedoAction) Main.getApplication().getActionCollection().get(RedoAction.class);

      Resources res = Main.getApplication().getResources();

      if (!entry.hasOwnUndoableManager())
      {
         SquirrelDefaultUndoManager undoManager = new SquirrelDefaultUndoManager();

         _undoAction.setUndoManager(undoManager);
         _redoAction.setUndoManager(undoManager);

         entry.setUndoManager(undoManager);
      }
      else
      {
         IUndoHandler undoHandler = entry.createUndoHandler();
         _undoAction.setDelegate(undoHandler.getUndoAction());
         _redoAction.setDelegate(undoHandler.getRedoAction());
      }

      JComponent comp = entry.getTextComponent();
      comp.registerKeyboardAction(_undoAction, res.getKeyStroke(_undoAction), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      comp.registerKeyboardAction(_redoAction, res.getKeyStroke(_redoAction), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      entry.addRedoUndoActionsToSQLEntryAreaMenu(_undoAction, _redoAction);
   }

   public Action getUndoAction()
   {
      return _undoAction;
   }

   public Action getRedoAction()
   {
      return _redoAction;
   }
}
