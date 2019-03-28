package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupController;

import java.awt.event.ActionEvent;

public class HQLToolsPopUpAction  extends SquirrelAction
{
   private ToolsPopupController _toolsPopupController;

   public HQLToolsPopUpAction(HibernatePluginResources resources, ToolsPopupController toolsPopupController, IApplication application)
   {
      super(application, resources);
      _toolsPopupController = toolsPopupController;
   }


   public void actionPerformed(ActionEvent e)
   {
      _toolsPopupController.showToolsPopup();
   }
}
