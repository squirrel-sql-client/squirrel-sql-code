package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ObjectTreeSearch;

import javax.swing.text.JTextComponent;


public class ViewObjectAtCursorInObjectTreeAction extends SquirrelAction
   implements ISQLPanelAction
{
   private static final long serialVersionUID = 1L;

   /**
    * Current panel.
    */
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

      String stringAtCursor = _panel.getSQLEntryPanel().getWordAtCursor();

      new ObjectTreeSearch().viewObjectInObjectTree(stringAtCursor, _panel.getSession());
   }
}
