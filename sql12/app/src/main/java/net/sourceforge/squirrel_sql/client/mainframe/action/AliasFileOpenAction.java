package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.gui.recentfiles.RecentFilesController;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;
import java.io.File;

public class AliasFileOpenAction extends SquirrelAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasFileOpenAction.class);

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
         getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("AliasFileOpenAction.noAliasSelected"));
         return;
      }

      File fileToOpen = new RecentFilesController(getApplication(), selectedAlias).getFileToOpen();

      System.out.println("fileToOpen = " + fileToOpen);
   }

}
