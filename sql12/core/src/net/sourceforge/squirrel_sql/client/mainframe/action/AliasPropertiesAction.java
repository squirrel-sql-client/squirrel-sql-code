package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import java.awt.event.ActionEvent;

public class AliasPropertiesAction  extends SquirrelAction
{
   private IAliasesList _aliasList;

   public AliasPropertiesAction(IApplication app, IAliasesList al)
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

      new AliasPropertiesCommand(selectedAlias, getApplication()).execute();
   }

}
