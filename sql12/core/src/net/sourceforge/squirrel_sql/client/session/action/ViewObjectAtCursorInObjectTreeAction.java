package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupController;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ObjectTreeSearch;


public class ViewObjectAtCursorInObjectTreeAction extends SquirrelAction  implements ISQLPanelAction
{
   public static final String VIEW_OBJECT_AT_CURSOR_INOBJECT_TREE_ACTION_BY_CTRL_MOUSECLICK =  "ViewObjectAtCursorInObjectTreeAction";

   private ISQLPanelAPI _panel;

   /**
    * Ctor specifying Application API.
    *
    * @param	app	Application API.
    */
   public ViewObjectAtCursorInObjectTreeAction(IApplication app)
   {
      super(app);
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
      setEnabled(null != _panel && _panel.isInMainSessionWindow());
   }

   /**
    * View the Object at cursor in the Object Tree
    *
    * @param	evt		Event being executed.
    */
   public synchronized void actionPerformed(ActionEvent evt)
   {
      if (_panel == null)
      {
         return;
      }

      if(false == shouldExecute(evt))
      {
         return;
      }

      String stringAtCursor = _panel.getSQLEntryPanel().getWordAtCursor();

      if (_panel.getSQLPanelSplitter().isSplit())
      {
         new ObjectTreeSearch().viewObjectInObjectTree(stringAtCursor, _panel.getSQLPanelSplitter().getObjectTreePanel());
      }
      else
      {
         new ObjectTreeSearch().viewObjectInObjectTree(stringAtCursor, _panel.getSession());
      }
   }

   private boolean shouldExecute(ActionEvent evt)
   {
//      System.out.println("evt = [" + evt + "]");
//      System.out.println("   evtModi = [" + evt.getModifiers() + "]");
//      System.out.println("   ctrlMask = [" + (evt.getModifiers() & InputEvent.CTRL_MASK) + "]");
//      System.out.println("   ctrlMaskDown = [" + (evt.getModifiers() & InputEvent.CTRL_DOWN_MASK) + "]");
//
//      System.out.println("   ctrlButton1 = [" + (evt.getModifiers() & InputEvent.BUTTON1_MASK) + "]");
//      System.out.println("   ctrlButton1Down = [" + (evt.getModifiers() & InputEvent.BUTTON1_DOWN_MASK) + "]");
//
//      System.out.println("   ctrlButton2 = [" + (evt.getModifiers() & InputEvent.BUTTON2_MASK) + "]");
//      System.out.println("   ctrlButton2Down = [" + (evt.getModifiers() & InputEvent.BUTTON2_DOWN_MASK) + "]");
//
//      System.out.println("   ctrlButton3 = [" + (evt.getModifiers() & InputEvent.BUTTON3_MASK) + "]");
//      System.out.println("   ctrlButton3Down = [" + (evt.getModifiers() & InputEvent.BUTTON3_DOWN_MASK) + "]");



      if(VIEW_OBJECT_AT_CURSOR_INOBJECT_TREE_ACTION_BY_CTRL_MOUSECLICK.equals(evt.getActionCommand()))
      {
         //System.out.println("CTRL + MOUSECLICK");
         return _panel.getSession().getProperties().getAllowCtrlMouseClickJumpToObjectTree();
      }
      else if(ToolsPopupController.TOOLS_POPUP_SELECTED_ACTION_COMMAND.equals(evt.getActionCommand()))
      {
         //System.out.println("TOOLS POPUP");
      }
      else // Right mouse menu or CTRL+B
      {
         int modi = evt.getModifiers();

         if((modi & InputEvent.CTRL_MASK) != 0 || (modi & InputEvent.CTRL_DOWN_MASK) != 0)
         {
            //System.out.println("CTRL+B");
            return _panel.getSession().getProperties().getAllowCtrlBJumpToObjectTree();

         }
         else
         {
            //System.out.println("RIGHT MOUSE MENU");
         }
      }

      return true;
   }
}
