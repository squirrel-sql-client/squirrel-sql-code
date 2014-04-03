package org.squirrelsql.session.action;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ActionConfiguration
{


   private ActionScope _actionScope;
   private final Image _icon;
   private final String _text;
   private final boolean _onToolbar;


   public ActionConfiguration(Image icon, String text, ActionScope actionScope, boolean onToolbar)
   {
      _actionScope = actionScope;
      _icon = icon;
      _text = text;
      _onToolbar = onToolbar;
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

   public boolean isOnToolbar()
   {
      return _onToolbar;
   }
}
