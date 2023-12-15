package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.AliasPropertiesController;

public class AliasPropertiesCommand
{
   private SQLAlias _selectedAlias;
   private IApplication _app;

   public AliasPropertiesCommand(SQLAlias selectedAlias, IApplication app)
   {
      _selectedAlias = selectedAlias;
      _app = app;
   }

   public void execute()
   {
      // Cast is not so nice, but framework doesn't meet new requirements.
      AliasPropertiesController.showAliasProperties(_selectedAlias);
   }
}
