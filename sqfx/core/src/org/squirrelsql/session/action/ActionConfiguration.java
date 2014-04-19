package org.squirrelsql.session.action;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCodeCombination;
import org.squirrelsql.AppState;

public class ActionConfiguration
{
   private ActionScope _actionScope;
   private final Image _icon;
   private final String _text;
   private KeyCodeCombination _keyCodeCombination;
   private final int _actionConfigurationId;


   public ActionConfiguration(Image icon, String text, ActionScope actionScope, KeyCodeCombination keyCodeCombination)
   {
      ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // Note: ActionConfigurations are supposed to be an application wide state like in the StandardActionConfiguration enum.
      // If though two ActionConfiguration objects for the same action-function would be created this would result in two different ActionHandles with different listeners
      _actionConfigurationId = AppState.get().getActionMangerImpl().getNextActionConfigurationId();
      //
      ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


      _actionScope = actionScope;
      _icon = icon;
      _text = text;
      _keyCodeCombination = keyCodeCombination;
   }

   public ActionScope getActionScope()
   {
      return _actionScope;
   }

   public ImageView getIcon()
   {
      return new ImageView(_icon);
   }

   public String getText()
   {
      return _text;
   }

   public KeyCodeCombination getKeyCodeCombination()
   {
      return _keyCodeCombination;
   }


   public boolean matches(ActionConfiguration actionConfiguration)
   {
      return _actionConfigurationId == actionConfiguration._actionConfigurationId;
   }
}
