package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import java.awt.event.ActionEvent;

public class HQLBookmarksAction extends SquirrelAction
{
   private ISQLEntryPanel _entryPanel;

   public HQLBookmarksAction(IApplication app, Resources rsrc, ISQLEntryPanel entryPanel)
   {
      super(app, rsrc);
      _entryPanel = entryPanel;
   }


   public void actionPerformed(ActionEvent e)
   {
      BookmarksAccessor.selectBookmark(_entryPanel);      
   }
}
