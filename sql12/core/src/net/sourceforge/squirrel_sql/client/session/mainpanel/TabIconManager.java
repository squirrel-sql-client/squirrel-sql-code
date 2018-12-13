package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabAnchoredAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabStickyAction;

import javax.swing.Action;
import javax.swing.ImageIcon;

public class TabIconManager
{

   private final ImageIcon _stickyIcon;
   private final ImageIcon _anchorIcon;

   public TabIconManager()
   {
      ActionCollection actionCollection = Main.getApplication().getActionCollection();
      _stickyIcon = (ImageIcon) actionCollection.get(ToggleCurrentSQLResultTabStickyAction.class).getValue(Action.SMALL_ICON);

      _anchorIcon = (ImageIcon) actionCollection.get(ToggleCurrentSQLResultTabAnchoredAction.class).getValue(Action.SMALL_ICON);
   }

   ImageIcon getStickyIcon()
   {
      return _stickyIcon;
   }

   ImageIcon getAnchorIcon()
   {
      return _anchorIcon;
   }

}
