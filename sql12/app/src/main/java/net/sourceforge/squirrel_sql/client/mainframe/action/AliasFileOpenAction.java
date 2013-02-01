package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.gui.recentfiles.RecentFilesController;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import java.awt.event.ActionEvent;

public class AliasFileOpenAction extends SquirrelAction
{
   private IAliasesList _aliasList;

   public AliasFileOpenAction(IApplication app, IAliasesList al)
   {
      super(app);
      _aliasList = al;
   }

   public void actionPerformed(ActionEvent e)
   {
      ISQLAlias selectedAlias = _aliasList.getSelectedAlias(null);

      if(null == selectedAlias)
      {
         return;
      }

      new RecentFilesController(getApplication().getMainFrame(), selectedAlias);

   }

}
