package org.squirrelsql.session.sql;

import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.action.SqFxActionListener;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.sql.bookmark.BookmarkManager;

public class ToolsPopupManager
{
   public ToolsPopupManager(SQLTextAreaServices sqlTextAreaServices, SessionTabContext sessionTabContext)
   {
      StdActionCfg.SHOW_TOOLS_POPUP.setAction(this::showToolsPopup);
   }

   private void showToolsPopup()
   {

   }
}
