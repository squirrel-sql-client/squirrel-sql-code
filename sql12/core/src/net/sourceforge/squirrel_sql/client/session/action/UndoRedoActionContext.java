package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.session.mainpanel.SquirrelDefaultUndoManager;

import javax.swing.Action;

public class UndoRedoActionContext
{
   private Action _undoActionDelegate;
   private Action _redoActionDelegate;
   private SquirrelDefaultUndoManager undoManager;

   public UndoRedoActionContext(Action undoActionDelegate, Action redoActionDelegate)
   {
      _undoActionDelegate = undoActionDelegate;
      _redoActionDelegate = redoActionDelegate;
   }

   public UndoRedoActionContext(SquirrelDefaultUndoManager undoManager)
   {
      this.undoManager = undoManager;
   }

   public Action getUndoActionDelegate()
   {
      return _undoActionDelegate;
   }

   public Action getRedoActionDelegate()
   {
      return _redoActionDelegate;
   }

   public SquirrelDefaultUndoManager getUndoManager()
   {
      return undoManager;
   }
}
