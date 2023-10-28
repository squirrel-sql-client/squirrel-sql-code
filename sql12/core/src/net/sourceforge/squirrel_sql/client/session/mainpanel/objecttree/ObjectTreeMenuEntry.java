package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;

public class ObjectTreeMenuEntry extends JMenu
{
   private static final ILogger s_log = LoggerController.createLogger(ObjectTreeMenuEntry.class);

   private Action _action;
   private JMenu _menu;

   public ObjectTreeMenuEntry(Action action)
   {
      _action = action;
   }

   public ObjectTreeMenuEntry(JMenu menu)
   {
      _menu = menu;
   }

   public void addToPopup(JPopupMenu popUp)
   {
      if(null != _menu)
      {
         popUp.add(_menu);
      }
      else
      {
         JMenuItem menuItem = popUp.add(_action);

         if (StringUtilities.isEmpty(menuItem.getText(), true))
         {
            s_log.warn("Object tree popup menu item for action " + (null == _action ? "null" : _action.getClass().getName()) + " has not menu title.");
         }
      }
   }
}
