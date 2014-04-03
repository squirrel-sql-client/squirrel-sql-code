package org.squirrelsql.session.action;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;

import java.util.HashMap;


public class ActionManager
{
   private final ToolBar _toolBar;

   private HashMap<ActionConfiguration, ActionHandle> _actionHandels = new HashMap<>();

   public ActionManager()
   {
      _toolBar = createToolbar();
   }


   private ToolBar createToolbar()
   {
      ToolBar ret = new ToolBar();

      for (ActionConfiguration actionConfiguration : StandardActionConfigurations.getToolbarConfigs())
      {
         if(actionConfiguration.isOnToolbar())
         {
            ActionHandle actionHandle = getActionHandle(actionConfiguration);

            Button b = new Button();
            actionHandle.setToolbarButton(b);

            b.setGraphic(actionConfiguration.getIcon());
            b.setTooltip(new Tooltip(actionConfiguration.getText()));

            b.setOnAction((e) -> actionHandle.actionPerformed());
            ret.getItems().add(b);
         }

      }
      return ret;
   }


   public ActionHandle getActionHandle(ActionConfiguration actionConfiguration)
   {
      ActionHandle actionHandle = _actionHandels.get(actionConfiguration);

      if(null == actionHandle)
      {
         actionHandle = new ActionHandle(actionConfiguration);
         _actionHandels.put(actionConfiguration, actionHandle);
      }

      return actionHandle;
   }

   public ToolBar getToolbar()
   {
      return _toolBar;
   }

   public ActionHandle getActionHandle(StandardActionConfigurations stdEnum)
   {
      return getActionHandle(stdEnum.getActionConfiguration());
   }

   public void setActionScope(ActionScope actionScope)
   {
      for (ActionHandle actionHandle : _actionHandels.values())
      {
         actionHandle.setDisable(actionHandle.getActionConfiguration().getActionScope() != actionScope);
      }
   }
}
