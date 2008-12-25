package net.sourceforge.squirrel_sql.client.gui.mainframe;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;

public class SquirrelDesktopManager
{
   private IApplication _app;

   public SquirrelDesktopManager(IApplication app)
   {
      _app = app;
   }

   public void activateWidget(IWidget f)
   {
      _app.getActionCollection().activationChanged(f);
   }
   public void deactivateWidget(IWidget f)
   {
      _app.getActionCollection().deactivationChanged(f);
   }
}
