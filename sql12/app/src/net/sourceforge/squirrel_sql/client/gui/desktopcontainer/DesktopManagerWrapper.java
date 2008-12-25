package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.mainframe.SquirrelDesktopManager;

import javax.swing.*;

public class DesktopManagerWrapper extends DefaultDesktopManager
{
   private SquirrelDesktopManager _squirrelDesktopManager;

   public DesktopManagerWrapper(SquirrelDesktopManager squirrelDesktopManager)
   {
      _squirrelDesktopManager = squirrelDesktopManager;
   }

   public void activateFrame(JInternalFrame f)
   {
      super.activateFrame(f);

      if(f instanceof InternalFrameDelegate)
      {
         InternalFrameDelegate d = (InternalFrameDelegate) f;
         _squirrelDesktopManager.activateWidget(d.getWidget());
      }
   }
   public void deactivateFrame(JInternalFrame f)
   {
      super.deactivateFrame(f);

      if(f instanceof InternalFrameDelegate)
      {
         InternalFrameDelegate d = (InternalFrameDelegate) f;
         _squirrelDesktopManager.deactivateWidget(d.getWidget());
      }
   }
}
