package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoRedoActionContext;
import net.sourceforge.squirrel_sql.fw.resources.Resources;

import javax.swing.JComponent;

public class UndoHandlerImpl implements IUndoHandler
{
   private SquirrelDefaultUndoManager _undoManager;
   private IUndoHandler _undoHandler;

   public UndoHandlerImpl(ISQLEntryPanel sqlEntryPanel)
   {
      if (!sqlEntryPanel.hasOwnUndoableManager())
      {
         _undoManager = new SquirrelDefaultUndoManager();
         sqlEntryPanel.setUndoManager(_undoManager);
      }
      else
      {
         _undoHandler = sqlEntryPanel.createUndoHandler();
      }

      Resources res = Main.getApplication().getResources();
      JComponent comp = sqlEntryPanel.getTextComponent();
      comp.registerKeyboardAction(getUndoAction(), res.getKeyStroke(getUndoAction()), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      comp.registerKeyboardAction(getRedoAction(), res.getKeyStroke(getRedoAction()), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      sqlEntryPanel.addUndoRedoActionsToSQLEntryAreaMenu(getUndoAction(), getRedoAction());
   }

   public UndoAction getUndoAction()
   {
      return (UndoAction) Main.getApplication().getActionCollection().get(UndoAction.class);
   }

   public RedoAction getRedoAction()
   {
      return (RedoAction) Main.getApplication().getActionCollection().get(RedoAction.class);
   }

   public UndoRedoActionContext getUndoRedoActionContext()
   {
      if( null != _undoHandler )
      {
         return new UndoRedoActionContext(_undoHandler.getUndoAction(), _undoHandler.getRedoAction());
      }
      else
      {
         return new UndoRedoActionContext(_undoManager);
      }
   }
}
