package org.squirrelsql.session.action;

import javafx.scene.control.Button;

public class ActionHandle
{
   private ActionConfiguration _actionConfiguration;
   private SqFxActionListener _sqFxActionListener;
   private Button _toolbarButton;

   public ActionHandle(ActionConfiguration actionConfiguration)
   {
      _actionConfiguration = actionConfiguration;
   }

   public void setOnAction(SqFxActionListener sqFxActionListener)
   {
      _sqFxActionListener = sqFxActionListener;
   }

   public void actionPerformed()
   {
      if(null != _sqFxActionListener)
      {
         _sqFxActionListener.actionPerformed();
      }

   }

   public void setToolbarButton(Button toolbarButton)
   {
      _toolbarButton = toolbarButton;
   }

   public ActionConfiguration getActionConfiguration()
   {
      return _actionConfiguration;
   }

   public void setDisable(boolean b)
   {
      if(null != _toolbarButton)
      {
         _toolbarButton.setDisable(b);
      }
   }
}
