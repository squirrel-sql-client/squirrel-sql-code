package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.AliasPropertiesController;

public class AliasPropertiesCommand
{
   private SQLAlias _selectedAlias;

   public AliasPropertiesCommand(SQLAlias selectedAlias)
   {
      _selectedAlias = selectedAlias;
   }

   public void execute()
   {
      // Cast is not so nice, but framework doesn't meet new requirements.
      AliasPropertiesController.showAliasProperties(_selectedAlias, Main.getApplication().getMainFrame());
   }
}
