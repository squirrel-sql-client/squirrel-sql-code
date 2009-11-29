package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import javax.swing.*;

public class UndoHandlerImpl implements IUndoHandler
{
   private UndoAction _undoAction;
   private RedoAction _redoAction;

   public UndoHandlerImpl(IApplication application, ISQLEntryPanel entry)
   {
      if (!entry.hasOwnUndoableManager())
      {
         SquirrelDefaultUndoManager undoManager = new SquirrelDefaultUndoManager();
         Resources res = application.getResources();
         _undoAction = new UndoAction(application, undoManager);
         _redoAction = new RedoAction(application, undoManager);

         JComponent comp = entry.getTextComponent();
         comp.registerKeyboardAction(_undoAction, res.getKeyStroke(_undoAction),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         comp.registerKeyboardAction(_redoAction, res.getKeyStroke(_redoAction),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

         entry.setUndoManager(undoManager);
      }
      else
      {
         IUndoHandler undoHandler = entry.createUndoHandler();
         _undoAction = new UndoAction(application, undoHandler.getUndoAction());
         _redoAction = new RedoAction(application, undoHandler.getRedoAction());
      }

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
